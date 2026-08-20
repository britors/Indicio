package br.com.avoren.indicio.data.caso

import br.com.avoren.indicio.data.caso.dto.CasoDto
import br.com.avoren.indicio.data.caso.dto.CasoV2Dto
import br.com.avoren.indicio.data.caso.dto.CatalogoDto
import br.com.avoren.indicio.data.caso.dto.CatalogoV2Dto
import br.com.avoren.indicio.data.caso.dto.paraDominio
import br.com.avoren.indicio.domain.caso.ErroCarga
import br.com.avoren.indicio.domain.caso.RepositorioCasos
import br.com.avoren.indicio.domain.caso.ResultadoCarga
import br.com.avoren.indicio.domain.model.caso.Caso
import br.com.avoren.indicio.domain.model.caso.Catalogo
import br.com.avoren.indicio.domain.model.caso.ResumoCaso
import br.com.avoren.indicio.domain.model.caso.RevisaoCaso
import br.com.avoren.indicio.domain.validacao.ProblemaValidacao
import br.com.avoren.indicio.domain.validacao.ValidadorCaso
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Carrega casos locais v1 e v2 e entrega somente modelos puros ao domínio. */
class RepositorioCasosJson(
    private val fonte: FonteCasos,
    private val validador: ValidadorCaso = ValidadorCaso(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : RepositorioCasos {

    private val json = Json {
        ignoreUnknownKeys = false
        isLenient = false
        explicitNulls = false
    }

    override suspend fun catalogo(): ResultadoCarga<Catalogo> = withContext(dispatcher) {
        when (val resultado = carregarCatalogo()) {
            is ResultadoCarga.Falha -> resultado
            is ResultadoCarga.Sucesso -> ResultadoCarga.Sucesso(
                Catalogo(resultado.valor.entradas.map(EntradaCatalogo::resumo)),
            )
        }
    }

    override suspend fun caso(id: String): ResultadoCarga<Caso> = withContext(dispatcher) {
        val catalogo = when (val resultado = carregarCatalogo()) {
            is ResultadoCarga.Falha -> return@withContext resultado
            is ResultadoCarga.Sucesso -> resultado.valor
        }
        val entrada = catalogo.entradas.firstOrNull { it.resumo.id == id }
            ?: return@withContext falha(ErroCarga.CasoDesconhecido(id))
        val caminho = entrada.arquivo
        if (!entrada.resumo.disponivel || caminho.isNullOrBlank()) {
            return@withContext falha(ErroCarga.CasoIndisponivel(id))
        }

        val texto = lerOuFalhar(caminho)
            ?: return@withContext falha(ErroCarga.ArquivoNaoEncontrado(caminho))
        val versao = extrairInteiro(texto, "versaoEsquema")
            ?: return@withContext jsonInvalido(caminho, "versaoEsquema ausente ou inválida")

        val resultadoCaso = when (versao) {
            VERSAO_CASO_1 -> decodificar(texto, caminho) { dto: CasoDto -> dto.paraDominio() }
            VERSAO_CASO_2 -> decodificar(texto, caminho) { dto: CasoV2Dto -> dto.paraDominio() }
            else -> return@withContext falha(
                ErroCarga.VersaoIncompativel(caminho, versao, VERSAO_CASO_ATUAL),
            )
        }
        val caso = when (resultadoCaso) {
            is ResultadoCarga.Falha -> return@withContext resultadoCaso
            is ResultadoCarga.Sucesso -> resultadoCaso.valor
        }

        val casoComRevisao = if (versao == VERSAO_CASO_1) {
            caso.copy(revisao = entrada.revisao ?: RevisaoCaso.V1)
        } else {
            caso
        }
        val problemas = validador.validar(casoComRevisao) +
            coerenciaComCatalogo(casoComRevisao, entrada)
        if (problemas.isNotEmpty()) {
            return@withContext falha(ErroCarga.GrafoInvalido(casoComRevisao.id, problemas))
        }

        ResultadoCarga.Sucesso(casoComRevisao)
    }

    private fun carregarCatalogo(): ResultadoCarga<CatalogoCarregado> {
        val texto = lerOuFalhar(CAMINHO_CATALOGO)
            ?: return falha(ErroCarga.ArquivoNaoEncontrado(CAMINHO_CATALOGO))
        val versaoNova = extrairInteiro(texto, "versaoCatalogo")
        val versaoLegada = extrairInteiro(texto, "versaoEsquema")

        val resultadoEntradas = when {
            versaoNova != null -> carregarCatalogoV2(texto, versaoNova)
            versaoLegada != null -> carregarCatalogoV1(texto, versaoLegada)
            else -> return jsonInvalido(
                CAMINHO_CATALOGO,
                "versaoCatalogo ou versaoEsquema ausente",
            )
        }
        val entradas = when (resultadoEntradas) {
            is ResultadoCarga.Falha -> return resultadoEntradas
            is ResultadoCarga.Sucesso -> resultadoEntradas.valor
        }

        val problemas = validarCatalogo(entradas)
        return if (problemas.isEmpty()) {
            ResultadoCarga.Sucesso(CatalogoCarregado(entradas))
        } else {
            falha(ErroCarga.GrafoInvalido(CAMINHO_CATALOGO, problemas))
        }
    }

    private fun carregarCatalogoV2(
        texto: String,
        versao: Int,
    ): ResultadoCarga<List<EntradaCatalogo>> {
        if (versao != VERSAO_CATALOGO) {
            return falha(
                ErroCarga.VersaoIncompativel(CAMINHO_CATALOGO, versao, VERSAO_CATALOGO),
            )
        }
        val resultado = decodificar(texto, CAMINHO_CATALOGO) { dto: CatalogoV2Dto -> dto }
        val dto = when (resultado) {
            is ResultadoCarga.Falha -> return resultado
            is ResultadoCarga.Sucesso -> resultado.valor
        }
        return ResultadoCarga.Sucesso(
            dto.casos.map { resumo ->
                EntradaCatalogo(
                    resumo = resumo.paraDominio(),
                    arquivo = resumo.arquivo,
                    revisao = if (resumo.versaoEsquema != null && resumo.versaoConteudo != null) {
                        RevisaoCaso(resumo.versaoEsquema, resumo.versaoConteudo)
                    } else {
                        null
                    },
                )
            },
        )
    }

    private fun carregarCatalogoV1(
        texto: String,
        versao: Int,
    ): ResultadoCarga<List<EntradaCatalogo>> {
        if (versao != VERSAO_CASO_1) {
            return falha(
                ErroCarga.VersaoIncompativel(CAMINHO_CATALOGO, versao, VERSAO_CASO_1),
            )
        }
        val resultado = decodificar(texto, CAMINHO_CATALOGO) { dto: CatalogoDto -> dto }
        val dto = when (resultado) {
            is ResultadoCarga.Falha -> return resultado
            is ResultadoCarga.Sucesso -> resultado.valor
        }
        return ResultadoCarga.Sucesso(
            dto.casos.map { resumo ->
                EntradaCatalogo(
                    resumo = resumo.paraDominio(),
                    arquivo = resumo.arquivo,
                    revisao = RevisaoCaso.V1.takeIf { resumo.disponivel },
                )
            },
        )
    }

    private inline fun <reified T, R> decodificar(
        texto: String,
        caminho: String,
        transformar: (T) -> R,
    ): ResultadoCarga<R> = try {
        ResultadoCarga.Sucesso(transformar(json.decodeFromString<T>(texto)))
    } catch (erro: SerializationException) {
        jsonInvalido(caminho, erro.message.orEmpty())
    } catch (erro: IllegalArgumentException) {
        jsonInvalido(caminho, erro.message.orEmpty())
    }

    private fun extrairInteiro(texto: String, campo: String): Int? = try {
        json.parseToJsonElement(texto).jsonObject[campo]?.jsonPrimitive?.intOrNull
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }

    private fun validarCatalogo(entradas: List<EntradaCatalogo>): List<ProblemaValidacao> = buildList {
        entradas.groupingBy { it.resumo.id }
            .eachCount()
            .filterValues { it > 1 }
            .keys
            .sorted()
            .forEach { repetido ->
                add(problemaCatalogo(repetido, "casos[].id", "identificador de caso repetido no catálogo"))
            }

        entradas.forEach { entrada ->
            val resumo = entrada.resumo
            if (resumo.id.isBlank()) add(problemaCatalogo(resumo.id, "id", "o caso precisa de um identificador"))
            if (resumo.titulo.isBlank()) add(problemaCatalogo(resumo.id, "titulo", "o caso precisa de um título"))
            if (resumo.sinopse.isBlank()) add(problemaCatalogo(resumo.id, "sinopse", "o caso precisa de uma sinopse"))
            if (resumo.disponivel && entrada.arquivo.isNullOrBlank()) {
                add(problemaCatalogo(resumo.id, "arquivo", "caso disponível precisa apontar para um arquivo"))
            }
            if (resumo.disponivel && entrada.revisao == null) {
                add(problemaCatalogo(resumo.id, "versões", "caso disponível precisa declarar suas versões"))
            }
            entrada.revisao?.let { revisao ->
                if (revisao.esquema !in VERSOES_CASO_SUPORTADAS) {
                    add(problemaCatalogo(resumo.id, "versaoEsquema", "versão de caso não suportada"))
                }
                if (revisao.conteudo < 1) {
                    add(problemaCatalogo(resumo.id, "versaoConteudo", "a versão de conteúdo precisa ser positiva"))
                }
            }
        }
    }

    private fun coerenciaComCatalogo(
        caso: Caso,
        entrada: EntradaCatalogo,
    ): List<ProblemaValidacao> = buildList {
        val resumo = entrada.resumo
        if (caso.id != resumo.id) {
            add(problemaCaso(caso, "id", "o identificador difere do catálogo (\"${resumo.id}\")"))
        }
        if (caso.titulo != resumo.titulo) add(problemaCaso(caso, "titulo", "o título difere do catálogo"))
        if (caso.sinopse != resumo.sinopse) add(problemaCaso(caso, "sinopse", "a sinopse difere do catálogo"))
        if (caso.categoria != resumo.categoria) {
            add(problemaCaso(caso, "categoria", "a categoria difere do catálogo (\"${resumo.categoria.rotulo}\")"))
        }
        if (entrada.revisao != null && caso.revisao != entrada.revisao) {
            add(problemaCaso(caso, "versões", "as versões diferem do catálogo"))
        }
    }

    private fun lerOuFalhar(caminho: String): String? = try {
        fonte.ler(caminho)
    } catch (_: IOException) {
        null
    }

    private fun problemaCatalogo(casoId: String, campo: String, mensagem: String) =
        ProblemaValidacao(casoId = casoId, campo = campo, mensagem = mensagem)

    private fun problemaCaso(caso: Caso, campo: String, mensagem: String) =
        ProblemaValidacao(casoId = caso.id, campo = campo, mensagem = mensagem)

    private fun jsonInvalido(caminho: String, causa: String) =
        falha(ErroCarga.JsonInvalido(caminho, causa))

    private fun falha(erro: ErroCarga) = ResultadoCarga.Falha(erro)

    private data class EntradaCatalogo(
        val resumo: ResumoCaso,
        val arquivo: String?,
        val revisao: RevisaoCaso?,
    )

    private data class CatalogoCarregado(val entradas: List<EntradaCatalogo>)

    companion object {
        const val VERSAO_CASO_1 = 1
        const val VERSAO_CASO_2 = 2
        const val VERSAO_CASO_ATUAL = VERSAO_CASO_2
        const val VERSAO_CATALOGO = 2
        val VERSOES_CASO_SUPORTADAS = setOf(VERSAO_CASO_1, VERSAO_CASO_2)

        const val CAMINHO_CATALOGO = "casos/catalogo.json"
    }
}

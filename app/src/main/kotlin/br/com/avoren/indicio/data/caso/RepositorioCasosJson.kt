package br.com.avoren.indicio.data.caso

import br.com.avoren.indicio.domain.caso.ErroCarga
import br.com.avoren.indicio.domain.caso.RepositorioCasos
import br.com.avoren.indicio.domain.caso.ResultadoCarga
import br.com.avoren.indicio.domain.model.caso.Caso
import br.com.avoren.indicio.domain.model.caso.Catalogo
import br.com.avoren.indicio.domain.model.caso.ResumoCaso
import br.com.avoren.indicio.domain.validacao.ProblemaValidacao
import br.com.avoren.indicio.domain.validacao.ValidadorCaso
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Carrega catálogo e casos a partir de arquivos JSON locais.
 *
 * O JSON é lido em modo estrito: campos desconhecidos são erro, e não silêncio.
 * Isso transforma um engano de digitação em um caso novo numa mensagem clara,
 * em vez de uma cena que simplesmente não aparece.
 */
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
        val texto = lerOuFalhar(CAMINHO_CATALOGO)
            ?: return@withContext falha(ErroCarga.ArquivoNaoEncontrado(CAMINHO_CATALOGO))

        val catalogo = try {
            json.decodeFromString<Catalogo>(texto)
        } catch (erro: SerializationException) {
            return@withContext falha(
                ErroCarga.JsonInvalido(CAMINHO_CATALOGO, erro.message.orEmpty()),
            )
        } catch (erro: IllegalArgumentException) {
            return@withContext falha(
                ErroCarga.JsonInvalido(CAMINHO_CATALOGO, erro.message.orEmpty()),
            )
        }

        if (catalogo.versaoEsquema != VERSAO_ESQUEMA) {
            return@withContext falha(
                ErroCarga.VersaoIncompativel(CAMINHO_CATALOGO, catalogo.versaoEsquema, VERSAO_ESQUEMA),
            )
        }

        val problemas = validarCatalogo(catalogo)
        if (problemas.isNotEmpty()) {
            return@withContext falha(ErroCarga.GrafoInvalido(CAMINHO_CATALOGO, problemas))
        }

        ResultadoCarga.Sucesso(catalogo)
    }

    override suspend fun caso(id: String): ResultadoCarga<Caso> {
        val resumo = when (val resultado = catalogo()) {
            is ResultadoCarga.Falha -> return resultado
            is ResultadoCarga.Sucesso -> resultado.valor.resumo(id)
                ?: return falha(ErroCarga.CasoDesconhecido(id))
        }

        val caminho = resumo.arquivo
        if (!resumo.disponivel || caminho.isNullOrBlank()) {
            return falha(ErroCarga.CasoIndisponivel(id))
        }

        return withContext(dispatcher) {
            val texto = lerOuFalhar(caminho)
                ?: return@withContext falha(ErroCarga.ArquivoNaoEncontrado(caminho))

            val caso = try {
                json.decodeFromString<Caso>(texto)
            } catch (erro: SerializationException) {
                return@withContext falha(ErroCarga.JsonInvalido(caminho, erro.message.orEmpty()))
            } catch (erro: IllegalArgumentException) {
                return@withContext falha(ErroCarga.JsonInvalido(caminho, erro.message.orEmpty()))
            }

            if (caso.versaoEsquema != VERSAO_ESQUEMA) {
                return@withContext falha(
                    ErroCarga.VersaoIncompativel(caminho, caso.versaoEsquema, VERSAO_ESQUEMA),
                )
            }

            val problemas = validador.validar(caso) + coerenciaComCatalogo(caso, resumo)
            if (problemas.isNotEmpty()) {
                return@withContext falha(ErroCarga.GrafoInvalido(caso.id, problemas))
            }

            ResultadoCarga.Sucesso(caso)
        }
    }

    private fun lerOuFalhar(caminho: String): String? = try {
        fonte.ler(caminho)
    } catch (_: IOException) {
        null
    }

    private fun validarCatalogo(catalogo: Catalogo): List<ProblemaValidacao> = buildList {
        catalogo.casos
            .groupingBy(ResumoCaso::id)
            .eachCount()
            .filterValues { it > 1 }
            .keys
            .sorted()
            .forEach { repetido ->
                add(
                    ProblemaValidacao(
                        casoId = repetido,
                        campo = "casos[].id",
                        mensagem = "identificador de caso repetido no catálogo",
                    ),
                )
            }

        catalogo.casos.forEach { resumo ->
            if (resumo.id.isBlank()) {
                add(problemaCatalogo(resumo, "id", "o caso precisa de um identificador"))
            }
            if (resumo.titulo.isBlank()) {
                add(problemaCatalogo(resumo, "titulo", "o caso precisa de um título"))
            }
            if (resumo.disponivel && resumo.arquivo.isNullOrBlank()) {
                add(
                    problemaCatalogo(
                        resumo,
                        "arquivo",
                        "caso marcado como disponível precisa apontar para um arquivo",
                    ),
                )
            }
        }
    }

    private fun coerenciaComCatalogo(caso: Caso, resumo: ResumoCaso): List<ProblemaValidacao> = buildList {
        if (caso.id != resumo.id) {
            add(
                ProblemaValidacao(
                    casoId = caso.id,
                    campo = "id",
                    mensagem = "o identificador difere do catálogo (\"${resumo.id}\")",
                ),
            )
        }
        if (caso.categoria != resumo.categoria) {
            add(
                ProblemaValidacao(
                    casoId = caso.id,
                    campo = "categoria",
                    mensagem = "a categoria difere do catálogo (\"${resumo.categoria.rotulo}\")",
                ),
            )
        }
    }

    private fun problemaCatalogo(resumo: ResumoCaso, campo: String, mensagem: String) =
        ProblemaValidacao(casoId = resumo.id, campo = campo, mensagem = mensagem)

    private fun falha(erro: ErroCarga) = ResultadoCarga.Falha(erro)

    companion object {
        /** Versão do esquema JSON entendida por esta versão do aplicativo. */
        const val VERSAO_ESQUEMA = 1

        const val CAMINHO_CATALOGO = "casos/catalogo.json"
    }
}

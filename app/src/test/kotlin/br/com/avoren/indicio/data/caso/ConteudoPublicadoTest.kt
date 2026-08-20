package br.com.avoren.indicio.data.caso

import br.com.avoren.indicio.domain.caso.ResultadoCarga
import br.com.avoren.indicio.domain.model.caso.Caso
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Escolha
import br.com.avoren.indicio.domain.model.caso.TipoCena
import br.com.avoren.indicio.fake.FonteCasosDeArquivo
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Protege o conteúdo que realmente vai para o aplicativo.
 *
 * Falha se um caso publicado deixar de carregar, de validar ou de respeitar as
 * regras editoriais verificáveis automaticamente.
 */
class ConteudoPublicadoTest {

    private val repositorio = RepositorioCasosJson(
        fonte = FonteCasosDeArquivo(),
        dispatcher = UnconfinedTestDispatcher(),
    )

    private suspend fun casosPublicados(): List<Caso> {
        val catalogo = (repositorio.catalogo() as ResultadoCarga.Sucesso).valor
        return catalogo.disponiveis().map { resumo ->
            val resultado = repositorio.caso(resumo.id)
            assertTrue(
                "O caso \"${resumo.id}\" não passou na validação: $resultado",
                resultado is ResultadoCarga.Sucesso,
            )
            (resultado as ResultadoCarga.Sucesso).valor
        }
    }

    @Test
    fun `o catalogo publicado carrega e valida`() = runTest {
        val resultado = repositorio.catalogo()

        assertTrue("Catálogo inválido: $resultado", resultado is ResultadoCarga.Sucesso)
    }

    @Test
    fun `todo caso disponivel carrega e valida`() = runTest {
        assertTrue(casosPublicados().isNotEmpty())
    }

    @Test
    fun `cada caso tem ao menos doze cenas`() = runTest {
        casosPublicados().forEach { caso ->
            assertTrue(
                "O caso \"${caso.id}\" tem ${caso.cenas.size} cenas; o mínimo é 12.",
                caso.cenas.size >= 12,
            )
        }
    }

    @Test
    fun `cada caso tem entre dois e tres finais positivos`() = runTest {
        casosPublicados().forEach { caso ->
            val finais = caso.cenas.count { it.tipo == TipoCena.FINAL }
            assertTrue("O caso \"${caso.id}\" tem $finais finais; o esperado é 2 ou 3.", finais in 2..3)
        }
    }

    @Test
    fun `todo final e alcancavel a partir da cena inicial`() = runTest {
        casosPublicados().forEach { caso ->
            val alcancadas = alcancaveis(caso)
            caso.cenas.filter { it.tipo == TipoCena.FINAL }.forEach { final ->
                assertTrue(
                    "O final \"${final.id}\" do caso \"${caso.id}\" não é alcançável.",
                    final.id in alcancadas,
                )
            }
        }
    }

    @Test
    fun `nenhuma escolha leva de volta a propria cena`() = runTest {
        casosPublicados().forEach { caso ->
            caso.cenas.forEach { cena ->
                cena.escolhas.forEach { escolha ->
                    assertEquals(
                        "A escolha \"${escolha.id}\" volta para a própria cena \"${cena.id}\".",
                        false,
                        escolha.proximaCena == cena.id,
                    )
                }
            }
        }
    }

    @Test
    fun `todo caso tem ao menos tres pistas distintas`() = runTest {
        casosPublicados().forEach { caso ->
            assertTrue(
                "O caso \"${caso.id}\" tem ${caso.pistas().size} pista(s).",
                caso.pistas().size >= 3,
            )
        }
    }

    @Test
    fun `a taca longa mantem duracao editorial equilibrada em todo percurso`() = runTest {
        val caso = casosPublicados().single { it.id == "taca-desaparecida" }
        val comprimentos = comprimentosAteFinal(caso, caso.cenaInicial)
        val palavras = palavrasAteFinal(caso, caso.cenaInicial)

        assertEquals(2, caso.revisao.esquema)
        assertEquals(4, caso.revisao.conteudo)
        assertEquals(6, caso.etapas.size)
        assertEquals(124, caso.cenas.count { it.tipo == TipoCena.COMUM })
        assertEquals(3, caso.cenas.count { it.tipo == TipoCena.FINAL })
        assertTrue(caso.cenas.filter { it.tipo == TipoCena.COMUM }.all { it.escolhas.size == 3 })
        assertEquals(42..42, comprimentos)
        assertTrue(
            "O menor percurso tem apenas ${palavras.first} palavras.",
            palavras.first >= 5_500,
        )
        assertTrue(
            "A diferença entre percursos é de ${palavras.last - palavras.first} palavras.",
            palavras.last - palavras.first <= 300,
        )
    }

    @Test
    fun `nenhum termo de violencia, punicao ou marca real aparece no conteudo`() = runTest {
        casosPublicados().forEach { caso ->
            val texto = textoCompleto(caso).lowercase()
            TERMOS_PROIBIDOS.forEach { termo ->
                // Palavra inteira: "retirou" não pode acusar o termo "tiro".
                val ocorrencia = Regex("\\b${Regex.escape(termo)}\\b")
                assertTrue(
                    "O caso \"${caso.id}\" contém o termo proibido \"$termo\".",
                    !ocorrencia.containsMatchIn(texto),
                )
            }
        }
    }

    @Test
    fun `toda descricao acessivel e informativa`() = runTest {
        casosPublicados().forEach { caso ->
            caso.cenas.forEach { cena ->
                assertTrue(
                    "A descrição acessível da cena \"${cena.id}\" é curta demais.",
                    cena.imagem.descricaoAcessivel.length >= 30,
                )
            }
        }
    }

    private fun alcancaveis(caso: Caso): Set<String> {
        val vistas = mutableSetOf(caso.cenaInicial)
        val fila = ArrayDeque(listOf(caso.cenaInicial))
        while (fila.isNotEmpty()) {
            val cena = caso.cena(fila.removeFirst()) ?: continue
            cena.escolhas.forEach { escolha ->
                if (vistas.add(escolha.proximaCena)) fila += escolha.proximaCena
            }
        }
        return vistas
    }

    private fun comprimentosAteFinal(
        caso: Caso,
        cenaInicial: String,
    ): IntRange {
        val memo = mutableMapOf<String, IntRange>()

        fun calcular(cenaId: String): IntRange = memo.getOrPut(cenaId) {
            val cena = requireNotNull(caso.cena(cenaId))
            if (cena.tipo == TipoCena.FINAL) {
                0..0
            } else {
                val destinos = cena.escolhas.map { escolha -> calcular(escolha.proximaCena) }
                (1 + destinos.minOf(IntRange::first))..(1 + destinos.maxOf(IntRange::last))
            }
        }

        return calcular(cenaInicial)
    }

    private fun palavrasAteFinal(
        caso: Caso,
        cenaInicial: String,
    ): IntRange {
        val memo = mutableMapOf<String, IntRange>()

        fun calcular(cenaId: String): IntRange = memo.getOrPut(cenaId) {
            val cena = requireNotNull(caso.cena(cenaId))
            val palavrasDaCena = cena.texto.trim().split(Regex("\\s+")).size
            if (cena.tipo == TipoCena.FINAL) {
                palavrasDaCena..palavrasDaCena
            } else {
                val destinos = cena.escolhas.map { escolha -> calcular(escolha.proximaCena) }
                (palavrasDaCena + destinos.minOf(IntRange::first))..
                    (palavrasDaCena + destinos.maxOf(IntRange::last))
            }
        }

        return calcular(cenaInicial)
    }

    private fun textoCompleto(caso: Caso): String = buildString {
        append(caso.titulo).append(' ').append(caso.sinopse).append(' ')
        caso.cenas.forEach { cena: Cena ->
            append(cena.texto).append(' ')
            append(cena.narracao.orEmpty()).append(' ')
            append(cena.imagem.descricaoAcessivel).append(' ')
            cena.pista?.let { append(it.titulo).append(' ').append(it.descricao).append(' ') }
            cena.escolhas.forEach { escolha: Escolha -> append(escolha.texto).append(' ') }
            cena.desfecho?.let {
                append(it.titulo).append(' ').append(it.mensagem).append(' ').append(it.explicacaoPistas)
            }
        }
    }

    private companion object {
        /**
         * Termos que contrariam as regras editoriais: violência, punição,
         * linguagem de fracasso, alegações médicas e marcas reais.
         */
        val TERMOS_PROIBIDOS = listOf(
            "fifa", "cbf", "conmebol", "uefa",
            "copa do mundo", "copa do brasil", "copa américa",
            "libertadores", "champions", "brasileirão", "mundial de clubes",
            "morte", "morreu", "sangue", "arma", "faca", "tiro",
            "game over", "você perdeu", "derrota", "fracasso", "punição",
            "demência", "alzheimer", "tratamento", "terapia", "cura",
        )
    }
}

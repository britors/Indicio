package br.com.avoren.indicio.domain.validacao

import br.com.avoren.indicio.fake.CasoFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadorCasoTest {

    private val validador = ValidadorCaso()

    private fun campos(caso: br.com.avoren.indicio.domain.model.caso.Caso): List<String> =
        validador.validar(caso).map { it.campo }

    @Test
    fun `caso valido nao produz problemas`() {
        assertEquals(emptyList<ProblemaValidacao>(), validador.validar(CasoFixtures.casoValido()))
    }

    @Test
    fun `identificadores de cena duplicados sao reportados`() {
        val caso = CasoFixtures.casoValido(
            cenas = listOf(
                CasoFixtures.cenaComum("abertura", "encerramento", "encerramento"),
                CasoFixtures.cenaComum("abertura", "encerramento", "encerramento"),
                CasoFixtures.cenaFinal("encerramento"),
            ),
        )

        val problemas = validador.validar(caso)

        assertTrue(
            problemas.any { it.cenaId == "abertura" && it.campo == "id" },
        )
    }

    @Test
    fun `cena inicial inexistente e reportada`() {
        val caso = CasoFixtures.casoValido().copy(cenaInicial = "nao-existe")

        val problemas = validador.validar(caso)

        assertTrue(problemas.any { it.campo == "cenaInicial" })
    }

    @Test
    fun `referencia de proxima cena inexistente e reportada`() {
        val caso = CasoFixtures.casoValido(
            cenas = listOf(
                CasoFixtures.cenaComum("abertura", "fantasma", "encerramento"),
                CasoFixtures.cenaFinal("encerramento"),
            ),
        )

        val problemas = validador.validar(caso)

        assertTrue(
            problemas.any { it.cenaId == "abertura" && it.campo.endsWith("proximaCena") },
        )
    }

    @Test
    fun `cena legada sem exatamente duas escolhas e reportada`() {
        val cena = CasoFixtures.cenaComum("abertura", "encerramento", "encerramento")
        val caso = CasoFixtures.casoValido(
            cenas = listOf(
                cena.copy(escolhas = cena.escolhas.take(1)),
                CasoFixtures.cenaFinal("encerramento"),
            ),
        )

        val problemas = validador.validar(caso)

        assertTrue(problemas.any { it.cenaId == "abertura" && it.campo == "escolhas" })
    }

    @Test
    fun `cena nao final sem saida e reportada`() {
        val cena = CasoFixtures.cenaComum("abertura", "encerramento", "encerramento")
        val caso = CasoFixtures.casoValido(
            cenas = listOf(
                cena.copy(escolhas = emptyList()),
                CasoFixtures.cenaFinal("encerramento"),
            ),
        )

        val problemas = validador.validar(caso)

        assertTrue(
            problemas.any {
                it.cenaId == "abertura" && it.campo == "escolhas" && it.mensagem.contains("sem saída")
            },
        )
    }

    @Test
    fun `final sem metadados de conclusao e reportado`() {
        val caso = CasoFixtures.casoValido(
            cenas = listOf(
                CasoFixtures.cenaComum("abertura", "encerramento", "encerramento"),
                CasoFixtures.cenaFinal("encerramento", desfecho = null),
            ),
        )

        val problemas = validador.validar(caso)

        assertTrue(problemas.any { it.cenaId == "encerramento" && it.campo == "desfecho" })
    }

    @Test
    fun `cena inalcancavel e reportada`() {
        val caso = CasoFixtures.casoValido(
            cenas = listOf(
                CasoFixtures.cenaComum("abertura", "encerramento", "encerramento"),
                CasoFixtures.cenaComum("esquecida", "encerramento", "encerramento"),
                CasoFixtures.cenaFinal("encerramento"),
            ),
        )

        val problemas = validador.validar(caso)

        assertTrue(
            problemas.any {
                it.cenaId == "esquecida" && it.mensagem.contains("não é alcançável")
            },
        )
    }

    @Test
    fun `texto ausente e reportado`() {
        val cena = CasoFixtures.cenaComum("abertura", "encerramento", "encerramento")
        val caso = CasoFixtures.casoValido(
            cenas = listOf(cena.copy(texto = "   "), CasoFixtures.cenaFinal("encerramento")),
        )

        assertTrue(campos(caso).contains("texto"))
    }

    @Test
    fun `imagem ausente e reportada`() {
        val cena = CasoFixtures.cenaComum("abertura", "encerramento", "encerramento")
        val caso = CasoFixtures.casoValido(
            cenas = listOf(
                cena.copy(imagem = CasoFixtures.imagem(recurso = "")),
                CasoFixtures.cenaFinal("encerramento"),
            ),
        )

        assertTrue(campos(caso).contains("imagem.recurso"))
    }

    @Test
    fun `descricao acessivel ausente e reportada`() {
        val cena = CasoFixtures.cenaComum("abertura", "encerramento", "encerramento")
        val caso = CasoFixtures.casoValido(
            cenas = listOf(
                cena.copy(imagem = cena.imagem.copy(descricaoAcessivel = "")),
                CasoFixtures.cenaFinal("encerramento"),
            ),
        )

        assertTrue(campos(caso).contains("imagem.descricaoAcessivel"))
    }

    @Test
    fun `pista repetida com conteudos diferentes e reportada`() {
        val divergente = CasoFixtures.pista().copy(descricao = "Outra descrição para a mesma pista.")
        val caso = CasoFixtures.casoValido(
            cenas = listOf(
                CasoFixtures.cenaComum("abertura", "sala", "encerramento", pista = CasoFixtures.pista()),
                CasoFixtures.cenaComum("sala", "encerramento", "encerramento", pista = divergente),
                CasoFixtures.cenaFinal("encerramento"),
            ),
        )

        val problemas = validador.validar(caso)

        assertTrue(problemas.any { it.campo == "pistas" })
    }

    @Test
    fun `cena final com escolhas e reportada`() {
        val final = CasoFixtures.cenaFinal("encerramento")
        val caso = CasoFixtures.casoValido(
            cenas = listOf(
                CasoFixtures.cenaComum("abertura", "encerramento", "encerramento"),
                final.copy(escolhas = listOf(CasoFixtures.escolha("extra", "abertura"))),
            ),
        )

        val problemas = validador.validar(caso)

        assertTrue(problemas.any { it.cenaId == "encerramento" && it.campo == "escolhas" })
    }

    @Test
    fun `todos os problemas sao coletados de uma vez`() {
        val caso = CasoFixtures.casoValido().copy(
            titulo = "",
            sinopse = "",
            cenaInicial = "nao-existe",
        )

        val problemas = validador.validar(caso)

        assertTrue(problemas.size >= 3)
        assertTrue(problemas.map { it.campo }.containsAll(listOf("titulo", "sinopse", "cenaInicial")))
    }

    @Test
    fun `mensagem legivel aponta caso cena e campo`() {
        val problema = ProblemaValidacao(
            casoId = "caso-exemplo",
            cenaId = "abertura",
            campo = "escolhas[0].proximaCena",
            mensagem = "a cena \"fantasma\" não existe",
        )

        assertEquals(
            "caso \"caso-exemplo\", cena \"abertura\", campo \"escolhas[0].proximaCena\": " +
                "a cena \"fantasma\" não existe",
            problema.mensagemLegivel(),
        )
    }
}

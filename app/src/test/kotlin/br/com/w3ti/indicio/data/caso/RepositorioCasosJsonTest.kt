package br.com.w3ti.indicio.data.caso

import br.com.w3ti.indicio.domain.caso.ErroCarga
import br.com.w3ti.indicio.domain.caso.ResultadoCarga
import br.com.w3ti.indicio.domain.model.caso.RevisaoCaso
import br.com.w3ti.indicio.fake.FonteCasosEmMemoria
import java.io.File
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositorioCasosJsonTest {

    private fun repositorio(vararg arquivos: Pair<String, String>) = RepositorioCasosJson(
        fonte = FonteCasosEmMemoria(arquivos.toMap()),
        dispatcher = UnconfinedTestDispatcher(),
    )

    @Test
    fun `caso valido e desserializado e entregue ao dominio`() = runTest {
        val repositorio = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to CATALOGO_VALIDO,
            CAMINHO_CASO to CASO_VALIDO,
        )

        val resultado = repositorio.caso("caso-exemplo")

        assertTrue(resultado is ResultadoCarga.Sucesso)
        val caso = (resultado as ResultadoCarga.Sucesso).valor
        assertEquals("caso-exemplo", caso.id)
        assertEquals("abertura", caso.cenaInicial)
        assertEquals(2, caso.cenas.size)
        assertEquals("Seguir pela sala", caso.cena("abertura")?.escolhas?.first()?.texto)
    }

    @Test
    fun `narracao ausente cai para o texto da cena`() = runTest {
        val repositorio = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to CATALOGO_VALIDO,
            CAMINHO_CASO to CASO_VALIDO,
        )

        val caso = (repositorio.caso("caso-exemplo") as ResultadoCarga.Sucesso).valor

        assertEquals(caso.cena("abertura")?.texto, caso.cena("abertura")?.textoNarrado)
    }

    @Test
    fun `catalogo ausente falha de modo controlado`() = runTest {
        val resultado = repositorio().catalogo()

        assertTrue(resultado is ResultadoCarga.Falha)
        assertTrue((resultado as ResultadoCarga.Falha).erro is ErroCarga.ArquivoNaoEncontrado)
    }

    @Test
    fun `json malformado falha de modo controlado`() = runTest {
        val resultado = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to "{ isso nao e json",
        ).catalogo()

        assertTrue(resultado is ResultadoCarga.Falha)
        assertTrue((resultado as ResultadoCarga.Falha).erro is ErroCarga.JsonInvalido)
    }

    @Test
    fun `campo desconhecido no caso e reportado como json invalido`() = runTest {
        val resultado = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to CATALOGO_VALIDO,
            CAMINHO_CASO to CASO_VALIDO.replace("\"sinopse\"", "\"sinopsee\""),
        ).caso("caso-exemplo")

        assertTrue(resultado is ResultadoCarga.Falha)
        assertTrue((resultado as ResultadoCarga.Falha).erro is ErroCarga.JsonInvalido)
    }

    @Test
    fun `versao de esquema incompativel falha de modo controlado`() = runTest {
        val resultado = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to CATALOGO_VALIDO.replace(
                "\"versaoEsquema\": 1",
                "\"versaoEsquema\": 99",
            ),
        ).catalogo()

        assertTrue(resultado is ResultadoCarga.Falha)
        val erro = (resultado as ResultadoCarga.Falha).erro
        assertTrue(erro is ErroCarga.VersaoIncompativel)
        assertEquals(99, (erro as ErroCarga.VersaoIncompativel).encontrada)
    }

    @Test
    fun `grafo invalido falha com problemas legiveis`() = runTest {
        val resultado = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to CATALOGO_VALIDO,
            CAMINHO_CASO to CASO_VALIDO.replace("\"proximaCena\": \"encerramento\"", "\"proximaCena\": \"fantasma\""),
        ).caso("caso-exemplo")

        assertTrue(resultado is ResultadoCarga.Falha)
        val erro = (resultado as ResultadoCarga.Falha).erro
        assertTrue(erro is ErroCarga.GrafoInvalido)
        assertTrue(erro.detalhe.contains("fantasma"))
        assertTrue(erro.detalhe.contains("abertura"))
    }

    @Test
    fun `caso fora do catalogo e reportado`() = runTest {
        val resultado = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to CATALOGO_VALIDO,
        ).caso("outro-caso")

        assertTrue(resultado is ResultadoCarga.Falha)
        assertTrue((resultado as ResultadoCarga.Falha).erro is ErroCarga.CasoDesconhecido)
    }

    @Test
    fun `caso marcado como indisponivel nao e carregado`() = runTest {
        val resultado = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to CATALOGO_VALIDO.replace(
                "\"disponivel\": true",
                "\"disponivel\": false",
            ),
        ).caso("caso-exemplo")

        assertTrue(resultado is ResultadoCarga.Falha)
        assertTrue((resultado as ResultadoCarga.Falha).erro is ErroCarga.CasoIndisponivel)
    }

    @Test
    fun `catalogo com ids repetidos e rejeitado`() = runTest {
        val duplicado = CATALOGO_VALIDO.replace(
            "\"casos\": [",
            "\"casos\": [ { \"id\": \"caso-exemplo\", \"titulo\": \"Repetido\", " +
                "\"sinopse\": \"...\", \"categoria\": \"futebol\", \"disponivel\": false },",
        )

        val resultado = repositorio(RepositorioCasosJson.CAMINHO_CATALOGO to duplicado).catalogo()

        assertTrue(resultado is ResultadoCarga.Falha)
        assertTrue((resultado as ResultadoCarga.Falha).erro is ErroCarga.GrafoInvalido)
    }

    @Test
    fun `catalogo separa casos disponiveis dos futuros`() = runTest {
        val catalogo = (
            repositorio(RepositorioCasosJson.CAMINHO_CATALOGO to CATALOGO_COM_FUTURO).catalogo()
                as ResultadoCarga.Sucesso
            ).valor

        assertEquals(2, catalogo.casos.size)
        assertEquals(listOf("caso-exemplo"), catalogo.disponiveis().map { it.id })
    }

    @Test
    fun `dois casos disponiveis sao carregados pelo proprio identificador`() = runTest {
        val repositorio = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to CATALOGO_COM_DOIS_DISPONIVEIS,
            CAMINHO_CASO to CASO_VALIDO,
            CAMINHO_SEGUNDO_CASO to SEGUNDO_CASO_VALIDO,
        )

        val catalogo = (repositorio.catalogo() as ResultadoCarga.Sucesso).valor

        assertEquals(listOf("segundo-caso", "caso-exemplo"), catalogo.disponiveis().map { it.id })
        assertTrue(repositorio.caso("caso-exemplo") is ResultadoCarga.Sucesso)
        assertTrue(repositorio.caso("segundo-caso") is ResultadoCarga.Sucesso)
    }

    @Test
    fun `identificador divergente entre caso e catalogo e reportado`() = runTest {
        val resultado = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to CATALOGO_VALIDO,
            CAMINHO_CASO to CASO_VALIDO.replace("\"id\": \"caso-exemplo\"", "\"id\": \"outro-id\""),
        ).caso("caso-exemplo")

        assertTrue(resultado is ResultadoCarga.Falha)
        val erro = (resultado as ResultadoCarga.Falha).erro
        assertTrue(erro is ErroCarga.GrafoInvalido)
        assertTrue(erro.detalhe.contains("difere do catálogo"))
    }

    @Test
    fun `catalogo v2 carrega casos v1 e v2 simultaneamente`() = runTest {
        val casoV2 = fixture("catalogo-fora-de-ordem.json")
        val catalogoMisto = fixture("catalogo.json").replace(
            "\"casos\": [",
            "\"casos\": [" +
                "{\"id\":\"caso-exemplo\",\"titulo\":\"Caso de exemplo\"," +
                "\"sinopse\":\"Uma sinopse curta.\",\"categoria\":\"misterios_policiais\"," +
                "\"arquivo\":\"$CAMINHO_CASO\",\"disponivel\":true," +
                "\"versaoEsquema\":1,\"versaoConteudo\":1},",
        )
        val repositorio = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to catalogoMisto,
            CAMINHO_CASO to CASO_VALIDO,
            "casos/catalogo-fora-de-ordem.json" to casoV2,
            "casos/transmissao-incompleta.json" to fixture("transmissao-incompleta.json"),
        )

        val catalogo = (repositorio.catalogo() as ResultadoCarga.Sucesso).valor
        val legado = (repositorio.caso("caso-exemplo") as ResultadoCarga.Sucesso).valor
        val longo = (repositorio.caso("catalogo-fora-de-ordem") as ResultadoCarga.Sucesso).valor

        assertEquals(3, catalogo.disponiveis().size)
        assertEquals(RevisaoCaso.V1, legado.revisao)
        assertEquals(RevisaoCaso(2, 1), longo.revisao)
        assertEquals(3, longo.etapas.size)
        assertEquals(4, longo.caderno.pistas.size)
    }

    @Test
    fun `os dois fixtures v2 passam pelo carregamento e validacao de dominio`() = runTest {
        val repositorio = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to fixture("catalogo.json"),
            "casos/catalogo-fora-de-ordem.json" to fixture("catalogo-fora-de-ordem.json"),
            "casos/transmissao-incompleta.json" to fixture("transmissao-incompleta.json"),
        )

        assertTrue(repositorio.caso("catalogo-fora-de-ordem") is ResultadoCarga.Sucesso)
        assertTrue(repositorio.caso("transmissao-incompleta") is ResultadoCarga.Sucesso)
    }

    @Test
    fun `revisao divergente entre catalogo e caso v2 e rejeitada`() = runTest {
        val catalogo = fixture("catalogo.json").replace(
            "\"versaoConteudo\": 1",
            "\"versaoConteudo\": 2",
        )
        val resultado = repositorio(
            RepositorioCasosJson.CAMINHO_CATALOGO to catalogo,
            "casos/catalogo-fora-de-ordem.json" to fixture("catalogo-fora-de-ordem.json"),
            "casos/transmissao-incompleta.json" to fixture("transmissao-incompleta.json"),
        ).caso("catalogo-fora-de-ordem")

        assertTrue(resultado is ResultadoCarga.Falha)
        assertTrue((resultado as ResultadoCarga.Falha).erro.detalhe.contains("versões diferem"))
    }

    private fun fixture(nome: String): String =
        File("../docs/exemplos/esquema-v2/$nome").readText()

    private companion object {
        const val CAMINHO_CASO = "casos/caso-exemplo.json"
        const val CAMINHO_SEGUNDO_CASO = "casos/segundo-caso.json"

        val CATALOGO_VALIDO = """
            {
              "versaoEsquema": 1,
              "casos": [
                {
                  "id": "caso-exemplo",
                  "titulo": "Caso de exemplo",
                  "sinopse": "Uma sinopse curta.",
                  "categoria": "misterios_policiais",
                  "arquivo": "casos/caso-exemplo.json",
                  "disponivel": true
                }
              ]
            }
        """.trimIndent()

        val CATALOGO_COM_FUTURO = CATALOGO_VALIDO.replace(
            "\"casos\": [",
            "\"casos\": [ { \"id\": \"caso-futuro\", \"titulo\": \"Ainda em preparação\", " +
                "\"sinopse\": \"Em breve.\", \"categoria\": \"faroeste\", \"disponivel\": false },",
        )

        val CATALOGO_COM_DOIS_DISPONIVEIS = CATALOGO_VALIDO.replace(
            "\"casos\": [",
            "\"casos\": [ { \"id\": \"segundo-caso\", \"titulo\": \"Segundo caso\", " +
                "\"sinopse\": \"Outra investigação.\", \"categoria\": \"futebol\", " +
                "\"arquivo\": \"$CAMINHO_SEGUNDO_CASO\", \"disponivel\": true },",
        )

        val CASO_VALIDO = """
            {
              "versaoEsquema": 1,
              "id": "caso-exemplo",
              "titulo": "Caso de exemplo",
              "sinopse": "Uma sinopse curta.",
              "categoria": "misterios_policiais",
              "cenaInicial": "abertura",
              "cenas": [
                {
                  "id": "abertura",
                  "tipo": "comum",
                  "texto": "A investigação começa em uma sala silenciosa.",
                  "imagem": {
                    "recurso": "cena_abertura",
                    "descricaoAcessivel": "Sala com uma mesa e uma janela aberta."
                  },
                  "escolhas": [
                    {
                      "id": "abertura-sala",
                      "texto": "Seguir pela sala",
                      "proximaCena": "encerramento",
                      "pista": {
                        "id": "janela-aberta",
                        "titulo": "A janela estava aberta",
                        "descricao": "Ninguém mencionou a janela até agora."
                      }
                    },
                    {
                      "id": "abertura-corredor",
                      "texto": "Seguir pelo corredor",
                      "proximaCena": "encerramento"
                    }
                  ]
                },
                {
                  "id": "encerramento",
                  "tipo": "final",
                  "texto": "As peças se encaixam com calma.",
                  "imagem": {
                    "recurso": "cena_encerramento",
                    "descricaoAcessivel": "A sala novamente organizada."
                  },
                  "desfecho": {
                    "titulo": "Caso resolvido",
                    "mensagem": "A investigação chega a um encerramento tranquilo.",
                    "explicacaoPistas": "A janela aberta explicava o caminho percorrido."
                  }
                }
              ]
            }
        """.trimIndent()

        val SEGUNDO_CASO_VALIDO = CASO_VALIDO
            .replace("\"id\": \"caso-exemplo\"", "\"id\": \"segundo-caso\"")
            .replace("\"titulo\": \"Caso de exemplo\"", "\"titulo\": \"Segundo caso\"")
            .replace("\"sinopse\": \"Uma sinopse curta.\"", "\"sinopse\": \"Outra investigação.\"")
            .replace("\"categoria\": \"misterios_policiais\"", "\"categoria\": \"futebol\"")
    }
}

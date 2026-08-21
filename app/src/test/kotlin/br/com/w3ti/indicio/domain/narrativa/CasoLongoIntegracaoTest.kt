package br.com.w3ti.indicio.domain.narrativa

import br.com.w3ti.indicio.data.caso.RepositorioCasosJson
import br.com.w3ti.indicio.domain.caso.ResultadoCarga
import br.com.w3ti.indicio.domain.model.caso.Caso
import br.com.w3ti.indicio.domain.model.caso.RevisaoCaso
import br.com.w3ti.indicio.domain.model.sessao.ProgressoCaso
import br.com.w3ti.indicio.fake.FonteCasosEmMemoria
import java.io.File
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CasoLongoIntegracaoTest {

    private val mecanismo = MecanismoNarrativo()

    @Test
    fun `percurso v2 deriva etapa objetivo caderno e retomada`() = runTest {
        val caso = carregar("catalogo-fora-de-ordem")
        val inicial = requireNotNull(mecanismo.iniciar(caso))

        assertEquals("A ordem interrompida", inicial.etapaAtual(caso)?.titulo)
        assertEquals("Entender como os cartões estavam organizados", inicial.objetivoAtual(caso)?.texto)
        assertEquals(listOf("etiqueta-azul"), inicial.pistas.map { it.id })
        assertEquals(listOf("sala-de-consulta"), inicial.cadernoRevelado(caso).locais.map { it.local.id })

        val avancada = aplicar(
            caso,
            inicial,
            "chegada-a",
            "bibliotecaria-a",
            "lista-a",
        )

        assertEquals("Cada cartão em seu lugar", avancada.etapaAtual(caso)?.titulo)
        assertEquals("reconstruir-ordem", avancada.objetivoAtual(caso)?.id)
        assertEquals(listOf("bibliotecaria", "orientador-oficina"),
            avancada.cadernoRevelado(caso).pessoas.map { it.pessoa.id })
        assertEquals(listOf("series-separadas", "oficina-usou-copias", "copias-marcadas"),
            avancada.lembrancasParaRetomada().map { it.id })
    }

    @Test
    fun `reconstrucao v2 e deterministica e atualiza revisao compativel`() = runTest {
        val caso = carregar("transmissao-incompleta")
        val original = aplicar(
            caso,
            requireNotNull(mecanismo.iniciar(caso)),
            "primeira-escuta-a",
            "fim-do-rolo-a",
            "tecnico-a",
            "chave-retorno-a",
        )
        val revisado = caso.copy(revisao = RevisaoCaso(2, 2))

        val resultado = mecanismo.reconstruir(revisado, original.progresso())

        assertTrue(resultado is ResultadoReconstrucao.Sucesso)
        val reconstruida = (resultado as ResultadoReconstrucao.Sucesso).sessao
        assertEquals(original.copy(revisao = RevisaoCaso(2, 2)), reconstruida)
        assertTrue(reconstruida.concluida)
    }

    @Test
    fun `progresso de outro esquema nao e convertido automaticamente`() = runTest {
        val caso = carregar("catalogo-fora-de-ordem")
        val legado = ProgressoCaso(
            casoId = caso.id,
            escolhas = listOf("chegada-a"),
            revisao = RevisaoCaso.V1,
        )

        assertTrue(
            mecanismo.reconstruir(caso, legado) is ResultadoReconstrucao.ProgressoIncompativel,
        )
    }

    @Test
    fun `dois casos mantem sessoes e cadernos independentes`() = runTest {
        val catalogo = carregar("catalogo-fora-de-ordem")
        val transmissao = carregar("transmissao-incompleta")
        val sessaoCatalogo = aplicar(
            catalogo,
            requireNotNull(mecanismo.iniciar(catalogo)),
            "chegada-b",
        )
        val sessaoTransmissao = aplicar(
            transmissao,
            requireNotNull(mecanismo.iniciar(transmissao)),
            "primeira-escuta-a",
        )

        assertEquals("catalogo-fora-de-ordem", sessaoCatalogo.casoId)
        assertEquals("transmissao-incompleta", sessaoTransmissao.casoId)
        assertEquals(listOf("etiqueta-azul", "faixa-de-papel"), sessaoCatalogo.pistas.map { it.id })
        assertEquals(listOf("rolo-sem-corte"), sessaoTransmissao.pistas.map { it.id })
    }

    private suspend fun carregar(id: String): Caso {
        val repositorio = RepositorioCasosJson(
            fonte = FonteCasosEmMemoria(
                mapOf(
                    RepositorioCasosJson.CAMINHO_CATALOGO to fixture("catalogo.json"),
                    "casos/catalogo-fora-de-ordem.json" to fixture("catalogo-fora-de-ordem.json"),
                    "casos/transmissao-incompleta.json" to fixture("transmissao-incompleta.json"),
                ),
            ),
            dispatcher = UnconfinedTestDispatcher(),
        )
        return (repositorio.caso(id) as ResultadoCarga.Sucesso).valor
    }

    private fun aplicar(
        caso: Caso,
        inicial: br.com.w3ti.indicio.domain.model.sessao.SessaoInvestigacao,
        vararg escolhas: String,
    ) = escolhas.fold(inicial) { sessao, escolha ->
        (mecanismo.escolher(caso, sessao, escolha) as ResultadoEscolha.Aplicada).sessao
    }

    private fun fixture(nome: String): String =
        File("../docs/exemplos/esquema-v2/$nome").readText()
}

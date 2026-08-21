package br.com.w3ti.indicio.ui.investigacao

import br.com.w3ti.indicio.application.investigacao.DecidirExibicaoDaRetomada
import br.com.w3ti.indicio.application.investigacao.InvestigacaoCarregada
import br.com.w3ti.indicio.data.caso.RepositorioCasosJson
import br.com.w3ti.indicio.domain.caso.ResultadoCarga
import br.com.w3ti.indicio.domain.model.caso.Caso
import br.com.w3ti.indicio.domain.model.sessao.ProgressoSalvo
import br.com.w3ti.indicio.domain.model.sessao.SessaoInvestigacao
import br.com.w3ti.indicio.domain.narrativa.MecanismoNarrativo
import br.com.w3ti.indicio.domain.narrativa.ResultadoEscolha
import br.com.w3ti.indicio.fake.FonteCasosEmMemoria
import java.io.File
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProjetorInvestigacaoTest {
    private val mecanismo = MecanismoNarrativo()
    private val projetor = ProjetorInvestigacao(
        DecidirExibicaoDaRetomada(intervaloMinimoMillis = 1_000),
    )

    @Test
    fun `estado parcial exibe apenas registros revelados e oculta etapas futuras`() = runTest {
        val caso = carregarCaso()
        val sessao = avancar(caso, requireNotNull(mecanismo.iniciar(caso)), "chegada-a")

        val estado = projetar(caso, sessao, atualizadoEm = 1_000, agora = 2_000)

        assertTrue(estado.exibirRetomada)
        assertEquals("A ordem interrompida", estado.retomada?.etapa)
        assertEquals(listOf("etiqueta-azul"), estado.caderno.pistas.map { it.id })
        assertEquals(listOf("bibliotecaria"), estado.caderno.pessoas.map { it.id })
        assertEquals(1, estado.caderno.conversas.size)
        assertEquals(SituacaoEtapa.ATUAL, estado.etapas[0].situacao)
        assertEquals(SituacaoEtapa.FUTURA, estado.etapas[1].situacao)
        assertNull(estado.etapas[1].titulo)
        assertNull(estado.etapas[1].descricao)
    }

    @Test
    fun `saida curta pula retomada e sessao concluida marca todas as etapas`() = runTest {
        val caso = carregarCaso()
        val inicial = requireNotNull(mecanismo.iniciar(caso))
        val curta = projetar(caso, inicial, atualizadoEm = 1_500, agora = 2_000)
        val concluida = avancar(
            caso,
            inicial,
            "chegada-a",
            "bibliotecaria-a",
            "lista-a",
            "orientador-a",
        )
        val final = projetar(caso, concluida, atualizadoEm = 1_000, agora = 2_000)

        assertFalse(curta.exibirRetomada)
        assertTrue(final.concluida)
        assertFalse(final.exibirRetomada)
        assertTrue(final.etapas.all { it.situacao == SituacaoEtapa.CONCLUIDA })
    }

    private fun projetar(
        caso: Caso,
        sessao: SessaoInvestigacao,
        atualizadoEm: Long,
        agora: Long,
    ) = projetor(
        InvestigacaoCarregada(
            caso = caso,
            sessao = sessao,
            progressoSalvo = ProgressoSalvo(
                casoId = caso.id,
                cenaAtual = sessao.cenaAtual,
                escolhas = sessao.caminho,
                pistasDescobertas = sessao.pistas.map { it.id },
                desfechoAlcancado = sessao.desfecho?.let { sessao.cenaAtual },
                atualizadoEm = atualizadoEm,
                revisao = caso.revisao,
            ),
        ),
        agora = agora,
    )

    private suspend fun carregarCaso(): Caso {
        val repositorio = RepositorioCasosJson(
            fonte = FonteCasosEmMemoria(
                mapOf(
                    RepositorioCasosJson.CAMINHO_CATALOGO to fixture("catalogo.json"),
                    "casos/catalogo-fora-de-ordem.json" to fixture("catalogo-fora-de-ordem.json"),
                ),
            ),
            dispatcher = UnconfinedTestDispatcher(),
        )
        return (repositorio.caso("catalogo-fora-de-ordem") as ResultadoCarga.Sucesso).valor
    }

    private fun avancar(
        caso: Caso,
        inicial: SessaoInvestigacao,
        vararg escolhas: String,
    ) = escolhas.fold(inicial) { sessao, escolha ->
        (mecanismo.escolher(caso, sessao, escolha) as ResultadoEscolha.Aplicada).sessao
    }

    private fun fixture(nome: String): String =
        File("../docs/exemplos/esquema-v2/$nome").readText()
}

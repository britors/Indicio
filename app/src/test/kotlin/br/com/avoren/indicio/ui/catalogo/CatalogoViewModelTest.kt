package br.com.avoren.indicio.ui.catalogo

import app.cash.turbine.test
import br.com.avoren.indicio.application.catalogo.SituacaoCasoCatalogo
import br.com.avoren.indicio.domain.model.caso.Catalogo
import br.com.avoren.indicio.domain.model.caso.Categoria
import br.com.avoren.indicio.domain.model.caso.ResumoCaso
import br.com.avoren.indicio.domain.model.sessao.ProgressoSalvo
import br.com.avoren.indicio.fake.RepositorioCasosFalso
import br.com.avoren.indicio.fake.RepositorioProgressoFalso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val resumo = ResumoCaso(
        id = "caso",
        titulo = "Caso",
        sinopse = "Sinopse",
        categoria = Categoria.MISTERIOS_POLICIAIS,
        disponivel = true,
    )

    @Before
    fun instalarDispatcher() = Dispatchers.setMain(dispatcher)

    @After
    fun restaurarDispatcher() = Dispatchers.resetMain()

    @Test
    fun `combina catalogo e progresso observado`() = runTest(dispatcher) {
        val viewModel = criarViewModel(RepositorioProgressoFalso(progresso()))

        val conteudo = viewModel.estado.filterIsInstance<EstadoCatalogo.Conteudo>().first()
        val caso = conteudo.grupos.flatMap(GrupoDeCategoria::casos).single()

        assertEquals(SituacaoCasoCatalogo.EM_ANDAMENTO, caso.situacao)
        assertEquals(900L, caso.ultimoAcessoEm)
    }

    @Test
    fun `reinicio bem sucedido emite destino do caso`() = runTest(dispatcher) {
        val viewModel = criarViewModel(RepositorioProgressoFalso(progresso()))

        viewModel.eventos.test {
            viewModel.reiniciar(resumo.id)
            assertEquals(EventoCatalogo.CasoReiniciado(resumo.id), awaitItem())
        }
    }

    @Test
    fun `falha ao reiniciar vira mensagem de interface`() = runTest(dispatcher) {
        val viewModel = criarViewModel(
            RepositorioProgressoFalso(progresso(), falhaAoGravar = "armazenamento indisponível"),
        )

        viewModel.eventos.test {
            viewModel.reiniciar(resumo.id)
            assertEquals(
                EventoCatalogo.FalhaAoReiniciar("armazenamento indisponível"),
                awaitItem(),
            )
        }
    }

    private fun criarViewModel(progresso: RepositorioProgressoFalso) = CatalogoViewModel(
        repositorioCasos = RepositorioCasosFalso(catalogo = Catalogo(listOf(resumo))),
        repositorioProgresso = progresso,
    )

    private fun progresso() = ProgressoSalvo(
        casoId = resumo.id,
        cenaAtual = "cena",
        escolhas = listOf("escolha"),
        pistasDescobertas = emptyList(),
        desfechoAlcancado = null,
        atualizadoEm = 900L,
    )
}

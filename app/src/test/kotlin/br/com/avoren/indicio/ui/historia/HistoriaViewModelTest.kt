package br.com.avoren.indicio.ui.historia

import app.cash.turbine.test
import br.com.avoren.indicio.domain.caso.ErroCarga
import br.com.avoren.indicio.domain.model.sessao.ProgressoSalvo
import br.com.avoren.indicio.fake.CasoFixtures
import br.com.avoren.indicio.fake.RepositorioCasosFalso
import br.com.avoren.indicio.fake.RepositorioProgressoFalso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HistoriaViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun instalarDispatcher() = Dispatchers.setMain(dispatcher)

    @After
    fun removerDispatcher() = Dispatchers.resetMain()

    private fun viewModel(
        repositorio: RepositorioCasosFalso = RepositorioCasosFalso(),
        progresso: RepositorioProgressoFalso = RepositorioProgressoFalso(),
    ) = HistoriaViewModel(repositorio, progresso)

    @Test
    fun `abrir um caso apresenta a cena inicial`() = runTest {
        val vm = viewModel()

        vm.abrir(CasoFixtures.ID)

        val estado = vm.estado.value
        assertTrue(estado is EstadoHistoria.EmCurso)
        assertEquals("abertura", (estado as EstadoHistoria.EmCurso).cena.id)
        assertEquals("Caso de exemplo", estado.tituloCaso)
        assertTrue(estado.escolhasHabilitadas)
    }

    @Test
    fun `falha de carregamento vira estado de falha, nao excecao`() = runTest {
        val vm = viewModel(RepositorioCasosFalso(erro = ErroCarga.ArquivoNaoEncontrado("casos/x.json")))

        vm.abrir(CasoFixtures.ID)

        assertTrue(vm.estado.value is EstadoHistoria.Falha)
    }

    @Test
    fun `escolher avanca a cena e acumula a pista`() = runTest {
        val vm = viewModel()
        vm.abrir(CasoFixtures.ID)

        vm.escolher("abertura-a")

        val estado = vm.estado.value as EstadoHistoria.EmCurso
        assertEquals("sala", estado.cena.id)
        assertEquals(listOf(CasoFixtures.pista()), estado.pistas)
    }

    @Test
    fun `pista revelada e anunciada como evento`() = runTest {
        val vm = viewModel()
        vm.abrir(CasoFixtures.ID)

        vm.eventos.test {
            vm.escolher("abertura-a")

            val evento = awaitItem()
            assertTrue(evento is EventoHistoria.PistasReveladas)
            assertEquals(listOf(CasoFixtures.pista()), (evento as EventoHistoria.PistasReveladas).pistas)
        }
    }

    @Test
    fun `escolha invalida e ignorada sem alterar o estado`() = runTest {
        val vm = viewModel()
        vm.abrir(CasoFixtures.ID)
        val antes = vm.estado.value

        vm.eventos.test {
            vm.escolher("corredor-a")

            assertEquals(EventoHistoria.EscolhaIgnorada, awaitItem())
        }
        assertEquals(antes, vm.estado.value)
    }

    @Test
    fun `chegar ao final produz estado concluido com desfecho e pistas`() = runTest {
        val vm = viewModel()
        vm.abrir(CasoFixtures.ID)

        vm.escolher("abertura-a")
        vm.escolher("sala-a")

        val estado = vm.estado.value
        assertTrue(estado is EstadoHistoria.Concluida)
        estado as EstadoHistoria.Concluida
        assertEquals(CasoFixtures.desfecho(), estado.desfecho)
        assertEquals(listOf(CasoFixtures.pista()), estado.pistas)
    }

    @Test
    fun `reiniciar volta a cena inicial sem pistas`() = runTest {
        val vm = viewModel()
        vm.abrir(CasoFixtures.ID)
        vm.escolher("abertura-a")

        vm.reiniciar()

        val estado = vm.estado.value as EstadoHistoria.EmCurso
        assertEquals("abertura", estado.cena.id)
        assertEquals(emptyList<Any>(), estado.pistas)
    }

    @Test
    fun `abrir com progresso salvo retoma onde parou`() = runTest {
        val vm = viewModel(progresso = progressoSalvo(listOf("abertura-a", "sala-b")))

        vm.abrir(CasoFixtures.ID)

        val estado = vm.estado.value as EstadoHistoria.EmCurso
        assertEquals("corredor", estado.cena.id)
        assertEquals(listOf(CasoFixtures.pista()), estado.pistas)
    }

    @Test
    fun `abrir sem retomar ignora o progresso salvo`() = runTest {
        val vm = viewModel(progresso = progressoSalvo(listOf("abertura-a", "sala-b")))

        vm.abrir(CasoFixtures.ID, retomar = false)

        assertEquals("abertura", (vm.estado.value as EstadoHistoria.EmCurso).cena.id)
    }

    @Test
    fun `progresso incompativel aguarda confirmacao antes de recomecar`() = runTest {
        val vm = viewModel(progresso = progressoSalvo(listOf("escolha-que-sumiu")))

        vm.abrir(CasoFixtures.ID)

        assertTrue(vm.estado.value is EstadoHistoria.AtualizacaoNecessaria)

        vm.reiniciar()

        assertEquals("abertura", (vm.estado.value as EstadoHistoria.EmCurso).cena.id)
    }

    @Test
    fun `abertura e cada escolha gravam o progresso antes da proxima interacao`() = runTest {
        val armazenamento = RepositorioProgressoFalso()
        val vm = viewModel(progresso = armazenamento)
        vm.abrir(CasoFixtures.ID)

        vm.escolher("abertura-a")
        vm.escolher("sala-b")

        assertEquals(3, armazenamento.salvamentos)
        assertEquals(
            listOf("abertura-a", "sala-b"),
            armazenamento.progresso(CasoFixtures.ID)?.escolhas,
        )
    }

    @Test
    fun `falha de gravacao e anunciada sem perder o estado em memoria`() = runTest {
        val vm = viewModel(
            progresso = RepositorioProgressoFalso(falhaAoGravar = "disco cheio"),
        )
        vm.abrir(CasoFixtures.ID)

        vm.eventos.test {
            vm.escolher("abertura-a")

            val eventos = listOf(awaitItem(), awaitItem())
            assertTrue(eventos.any { it is EventoHistoria.FalhaAoSalvar })
        }
        assertEquals("sala", (vm.estado.value as EstadoHistoria.EmCurso).cena.id)
    }

    @Test
    fun `reiniciar limpa o progresso gravado`() = runTest {
        val armazenamento = progressoSalvo(listOf("abertura-a"))
        val vm = viewModel(progresso = armazenamento)
        vm.abrir(CasoFixtures.ID)

        vm.reiniciar()

        assertNull(armazenamento.progresso(CasoFixtures.ID))
        assertEquals("abertura", (vm.estado.value as EstadoHistoria.EmCurso).cena.id)
    }

    @Test
    fun `abrir um caso ja concluido comeca uma investigacao nova`() = runTest {
        val vm = viewModel(
            progresso = progressoSalvo(
                escolhas = listOf("abertura-b", "corredor-a"),
                desfecho = "encerramento",
            ),
        )

        vm.abrir(CasoFixtures.ID)

        val estado = vm.estado.value
        assertTrue("Esperava recomeçar, mas veio $estado", estado is EstadoHistoria.EmCurso)
        assertEquals("abertura", (estado as EstadoHistoria.EmCurso).cena.id)
        assertEquals(emptyList<Any>(), estado.pistas)
    }

    private fun progressoSalvo(
        escolhas: List<String>,
        desfecho: String? = null,
    ) = RepositorioProgressoFalso(
        ProgressoSalvo(
            casoId = CasoFixtures.ID,
            cenaAtual = "irrelevante",
            escolhas = escolhas,
            pistasDescobertas = emptyList(),
            desfechoAlcancado = desfecho,
            atualizadoEm = 1L,
        ),
    )
}

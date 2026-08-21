package br.com.w3ti.indicio.ui.historia

import br.com.w3ti.indicio.domain.narracao.EstadoNarracao
import br.com.w3ti.indicio.fake.CasoFixtures
import br.com.w3ti.indicio.fake.NarradorFalso
import br.com.w3ti.indicio.fake.RepositorioCasosFalso
import br.com.w3ti.indicio.fake.RepositorioProgressoFalso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NarracaoTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun instalarDispatcher() = Dispatchers.setMain(dispatcher)

    @After
    fun removerDispatcher() = Dispatchers.resetMain()

    private fun viewModel(narrador: NarradorFalso?) = HistoriaViewModel(
        repositorioCasos = RepositorioCasosFalso(),
        repositorioProgresso = RepositorioProgressoFalso(),
        narrador = narrador,
    )

    @Test
    fun `narrar le o texto da cena atual`() = runTest {
        val narrador = NarradorFalso()
        val vm = viewModel(narrador)
        vm.abrir(CasoFixtures.ID)

        vm.alternarNarracao()

        assertEquals(listOf("Texto narrativo da cena abertura."), narrador.textosFalados)
        assertEquals(EstadoNarracao.FALANDO, vm.estadoNarracao.value)
    }

    @Test
    fun `tocar de novo enquanto fala interrompe a narracao`() = runTest {
        val narrador = NarradorFalso()
        val vm = viewModel(narrador)
        vm.abrir(CasoFixtures.ID)

        vm.alternarNarracao()
        vm.alternarNarracao()

        assertEquals(1, narrador.textosFalados.size)
        assertEquals(EstadoNarracao.PRONTO, vm.estadoNarracao.value)
    }

    @Test
    fun `trocar de cena interrompe a fala anterior`() = runTest {
        val narrador = NarradorFalso()
        val vm = viewModel(narrador)
        vm.abrir(CasoFixtures.ID)
        vm.alternarNarracao()

        vm.escolher("abertura-a")

        assertEquals(EstadoNarracao.PRONTO, vm.estadoNarracao.value)
    }

    @Test
    fun `narrar apos trocar de cena le o texto novo`() = runTest {
        val narrador = NarradorFalso()
        val vm = viewModel(narrador)
        vm.abrir(CasoFixtures.ID)

        vm.escolher("abertura-a")
        vm.alternarNarracao()

        assertEquals(listOf("Texto narrativo da cena sala."), narrador.textosFalados)
    }

    @Test
    fun `silenciar interrompe a fala sem encerrar o mecanismo`() = runTest {
        val narrador = NarradorFalso()
        val vm = viewModel(narrador)
        vm.abrir(CasoFixtures.ID)
        vm.alternarNarracao()

        vm.silenciar()

        assertTrue(narrador.paradas > 0)
        assertTrue("O mecanismo não deveria ser encerrado", !narrador.encerrado)
        assertEquals(EstadoNarracao.PRONTO, vm.estadoNarracao.value)
    }

    @Test
    fun `sem voz instalada o caso continua jogavel ate o final`() = runTest {
        val narrador = NarradorFalso(EstadoNarracao.INDISPONIVEL)
        val vm = viewModel(narrador)
        vm.abrir(CasoFixtures.ID)

        vm.alternarNarracao()
        vm.escolher("abertura-b")
        vm.escolher("corredor-a")

        assertEquals(emptyList<String>(), narrador.textosFalados)
        assertTrue(vm.estado.value is EstadoHistoria.Concluida)
    }

    @Test
    fun `sem narrador o estado e indisponivel e nada quebra`() = runTest {
        val vm = viewModel(narrador = null)
        vm.abrir(CasoFixtures.ID)

        vm.alternarNarracao()
        vm.silenciar()

        assertEquals(EstadoNarracao.INDISPONIVEL, vm.estadoNarracao.value)
        assertTrue(vm.estado.value is EstadoHistoria.EmCurso)
    }
}

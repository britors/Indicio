package br.com.avoren.indicio.ui.inicio

import br.com.avoren.indicio.domain.model.IdentidadeApp
import br.com.avoren.indicio.fake.RepositorioIdentidadeFalso
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class InicioViewModelTest {

    @Test
    fun `estado inicial reflete a identidade do repositorio`() = runTest {
        val repositorio = RepositorioIdentidadeFalso(
            IdentidadeApp(
                nome = "Indício",
                slogan = "Toda escolha revela uma pista.",
                versao = "9.9.9",
            ),
        )

        val viewModel = InicioViewModel(repositorio)

        assertEquals(
            EstadoInicio(
                nome = "Indício",
                slogan = "Toda escolha revela uma pista.",
                versao = "9.9.9",
            ),
            viewModel.estado.value,
        )
    }

    @Test
    fun `identidade e consultada uma unica vez`() = runTest {
        val repositorio = RepositorioIdentidadeFalso()

        val viewModel = InicioViewModel(repositorio)
        repeat(3) { viewModel.estado.value }

        assertEquals(1, repositorio.vezesConsultado)
    }
}

package br.com.w3ti.indicio.application.caso

import br.com.w3ti.indicio.domain.model.caso.Catalogo
import br.com.w3ti.indicio.domain.model.caso.Categoria
import br.com.w3ti.indicio.domain.model.caso.ResumoCaso
import br.com.w3ti.indicio.domain.model.sessao.ProgressoSalvo
import br.com.w3ti.indicio.fake.CasoFixtures
import br.com.w3ti.indicio.fake.RepositorioCasosFalso
import br.com.w3ti.indicio.fake.RepositorioProgressoFalso
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ObterCasoParaContinuarTest {

    private fun resumo(disponivel: Boolean = true) = ResumoCaso(
        id = CasoFixtures.ID,
        titulo = "Caso de exemplo",
        sinopse = "Uma sinopse curta.",
        categoria = Categoria.MISTERIOS_POLICIAIS,
        disponivel = disponivel,
    )

    private fun progresso(
        casoId: String = CasoFixtures.ID,
        desfecho: String? = null,
    ) = ProgressoSalvo(
        casoId = casoId,
        cenaAtual = "sala",
        escolhas = listOf("abertura-a"),
        pistasDescobertas = listOf("pista-exemplo"),
        desfechoAlcancado = desfecho,
        atualizadoEm = 10L,
    )

    private fun usoDe(
        progresso: ProgressoSalvo?,
        catalogo: Catalogo = Catalogo(listOf(resumo())),
    ) = ObterCasoParaContinuar(
        repositorioCasos = RepositorioCasosFalso(catalogo = catalogo),
        repositorioProgresso = RepositorioProgressoFalso(progresso),
    )

    @Test
    fun `oferece o caso quando ha progresso valido`() = runTest {
        val resultado = usoDe(progresso()).invoke().first()

        assertNotNull(resultado)
        assertEquals(CasoFixtures.ID, resultado?.resumo?.id)
        assertEquals(listOf("abertura-a"), resultado?.progresso?.escolhas)
    }

    @Test
    fun `nao oferece nada quando nao ha progresso`() = runTest {
        assertNull(usoDe(progresso = null).invoke().first())
    }

    @Test
    fun `nao oferece caso ja concluido`() = runTest {
        assertNull(usoDe(progresso(desfecho = "encerramento")).invoke().first())
    }

    @Test
    fun `nao oferece caso removido do catalogo`() = runTest {
        val resultado = usoDe(progresso(), catalogo = Catalogo(emptyList())).invoke().first()

        assertNull(resultado)
    }

    @Test
    fun `nao oferece caso que deixou de estar disponivel`() = runTest {
        val catalogo = Catalogo(listOf(resumo(disponivel = false)))

        assertNull(usoDe(progresso(), catalogo).invoke().first())
    }
}

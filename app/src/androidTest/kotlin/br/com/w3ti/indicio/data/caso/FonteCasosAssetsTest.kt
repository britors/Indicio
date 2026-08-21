package br.com.w3ti.indicio.data.caso

import androidx.test.platform.app.InstrumentationRegistry
import br.com.w3ti.indicio.domain.caso.ResultadoCarga
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Garante que o catálogo empacotado no APK é lido e validado de verdade,
 * exercitando o caminho real de assets em vez de um dublê.
 */
class FonteCasosAssetsTest {

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun catalogoEmpacotadoCarregaEValida() = runBlocking {
        val repositorio = RepositorioCasosJson(FonteCasosAssets(contexto.assets))

        val resultado = repositorio.catalogo()

        assertTrue(
            "Falha ao carregar o catálogo: $resultado",
            resultado is ResultadoCarga.Sucesso,
        )
        val catalogo = (resultado as ResultadoCarga.Sucesso).valor
        assertTrue(catalogo.casos.isNotEmpty())
    }

    @Test
    fun todoCasoDisponivelNoCatalogoCarregaEValida() = runBlocking {
        val repositorio = RepositorioCasosJson(FonteCasosAssets(contexto.assets))
        val catalogo = (repositorio.catalogo() as ResultadoCarga.Sucesso).valor

        catalogo.disponiveis().forEach { resumo ->
            val resultado = repositorio.caso(resumo.id)
            assertTrue(
                "O caso \"${resumo.id}\" do catálogo não passou na validação: $resultado",
                resultado is ResultadoCarga.Sucesso,
            )
        }
    }
}

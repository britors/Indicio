package br.com.w3ti.indicio.data.caso

import br.com.w3ti.indicio.domain.caso.ResultadoCarga
import br.com.w3ti.indicio.domain.model.caso.Caso
import br.com.w3ti.indicio.fake.FonteCasosDeArquivo
import java.io.File
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Garante que toda cena publicada tem arte de carta.
 *
 * O nome do recurso vem do JSON e só é resolvido em tempo de execução, então o
 * compilador não acusa um nome errado: sem este teste, uma arte faltando ou um
 * erro de digitação só apareceria como carta sem imagem no aparelho.
 */
class ArteDasCartasTest {

    private val repositorio = RepositorioCasosJson(
        fonte = FonteCasosDeArquivo(),
        dispatcher = UnconfinedTestDispatcher(),
    )

    private val drawable = File("src/main/res/drawable")

    private suspend fun casosPublicados(): List<Caso> {
        val catalogo = (repositorio.catalogo() as ResultadoCarga.Sucesso).valor
        return catalogo.disponiveis().map { resumo ->
            (repositorio.caso(resumo.id) as ResultadoCarga.Sucesso).valor
        }
    }

    @Test
    fun `toda cena publicada tem arte de carta`() = runTest {
        casosPublicados().forEach { caso ->
            caso.cenas.forEach { cena ->
                val arte = File(drawable, "${cena.imagem.recurso}.xml")
                assertTrue(
                    "A cena \"${cena.id}\" do caso \"${caso.id}\" aponta para " +
                        "\"${cena.imagem.recurso}\", que não existe em ${drawable.path}.",
                    arte.isFile,
                )
            }
        }
    }

    @Test
    fun `toda capa publicada aponta para uma arte local`() = runTest {
        val catalogo = (repositorio.catalogo() as ResultadoCarga.Sucesso).valor

        catalogo.disponiveis().forEach { resumo ->
            val recurso = requireNotNull(resumo.imagem).recurso
            assertTrue(
                "A capa do caso \"${resumo.id}\" aponta para \"$recurso\", que não existe.",
                File(drawable, "$recurso.xml").isFile,
            )
        }
    }

    @Test
    fun `o verso comum das cartas existe`() {
        assertTrue(File(drawable, "carta_verso.xml").isFile)
    }

    @Test
    fun `nenhuma arte de cena ficou orfa`() = runTest {
        val usadas = casosPublicados()
            .flatMap { caso -> caso.cenas.map { it.imagem.recurso } }
            .toSet()

        val existentes = drawable.listFiles()
            .orEmpty()
            .filter { it.name.startsWith("cena_") && it.name.endsWith(".xml") }
            .map { it.name.removeSuffix(".xml") }

        val orfas = existentes - usadas
        assertTrue("Arte de cena sem nenhuma cena que a use: $orfas", orfas.isEmpty())
    }
}

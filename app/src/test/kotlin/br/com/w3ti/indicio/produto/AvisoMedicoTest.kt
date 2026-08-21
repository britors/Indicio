package br.com.w3ti.indicio.produto

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * O aviso de saúde definido pelo produto deve existir uma única vez e viver
 * somente na tela Sobre.
 *
 * A verificação é estática, sobre o código-fonte, porque a regra é sobre onde o
 * texto pode aparecer — não sobre o que uma tela específica renderiza.
 */
class AvisoMedicoTest {

    private val fontes = File("src/main")

    private fun arquivos(extensao: String): List<File> =
        fontes.walkTopDown().filter { it.isFile && it.extension == extensao }.toList()

    @Test
    fun `o texto do aviso existe uma unica vez, em strings xml`() {
        val ocorrencias = arquivos("xml").sumOf { arquivo ->
            Regex(Regex.escape(TEXTO_DO_AVISO)).findAll(arquivo.readText()).count()
        }

        assertEquals(1, ocorrencias)
    }

    @Test
    fun `o texto do aviso nao aparece literalmente em codigo Kotlin`() {
        val comOTexto = arquivos("kt").filter { it.readText().contains(TEXTO_DO_AVISO) }

        assertEquals(emptyList<File>(), comOTexto)
    }

    @Test
    fun `o recurso do aviso e usado somente pela tela Sobre`() {
        val usos = arquivos("kt")
            .filter { it.readText().contains(RECURSO_DO_AVISO) }
            .map { it.name }
            .sorted()

        assertEquals(listOf("TelaSobre.kt"), usos)
    }

    @Test
    fun `o texto do aviso nao aparece em nenhum caso publicado`() {
        val assets = File("src/main/assets")
        val comOTexto = assets.walkTopDown()
            .filter { it.isFile && it.extension == "json" }
            .filter { it.readText().contains("acompanhamento médico") }
            .map { it.name }
            .toList()

        assertTrue("Aviso médico encontrado em conteúdo: $comOTexto", comOTexto.isEmpty())
    }

    private companion object {
        const val TEXTO_DO_AVISO =
            "Indício é uma experiência de entretenimento e estímulo cognitivo. " +
                "Não substitui avaliação, tratamento ou acompanhamento médico."

        const val RECURSO_DO_AVISO = "R.string.sobre_aviso_medico"
    }
}

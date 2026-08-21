package br.com.w3ti.indicio.arquitetura

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/** Protege a regra de dependência da Clean Architecture sem framework externo. */
class DependenciasArquiteturaisTest {

    private val raiz = File("src/main/kotlin/br/com/w3ti/indicio")

    @Test
    fun `o dominio nao conhece frameworks nem camadas externas`() {
        val proibidos = listOf(
            "import android.",
            "import androidx.",
            "import kotlinx.serialization.",
            "import br.com.w3ti.indicio.data.",
            "import br.com.w3ti.indicio.di.",
            "import br.com.w3ti.indicio.navegacao.",
            "import br.com.w3ti.indicio.ui.",
        )

        assertSemImportsProibidos("domain", proibidos)
    }

    @Test
    fun `casos de uso dependem apenas do dominio`() {
        val proibidos = listOf(
            "import android.",
            "import androidx.",
            "import kotlinx.serialization.",
            "import br.com.w3ti.indicio.data.",
            "import br.com.w3ti.indicio.di.",
            "import br.com.w3ti.indicio.navegacao.",
            "import br.com.w3ti.indicio.ui.",
        )

        assertSemImportsProibidos("application", proibidos)
    }

    @Test
    fun `dados nao conhecem interface nem navegacao`() {
        assertSemImportsProibidos(
            diretorio = "data",
            proibidos = listOf(
                "import br.com.w3ti.indicio.navegacao.",
                "import br.com.w3ti.indicio.ui.",
            ),
        )
    }

    @Test
    fun `interface nao conhece infraestrutura nem composicao`() {
        assertSemImportsProibidos(
            diretorio = "ui",
            proibidos = listOf(
                "import br.com.w3ti.indicio.data.",
                "import br.com.w3ti.indicio.di.",
                "import br.com.w3ti.indicio.navegacao.",
            ),
        )
    }

    private fun assertSemImportsProibidos(
        diretorio: String,
        proibidos: List<String>,
    ) {
        val violacoes = File(raiz, diretorio)
            .walkTopDown()
            .filter(File::isFile)
            .filter { it.extension == "kt" }
            .flatMap { arquivo ->
                arquivo.readLines().mapIndexedNotNull { indice, linha ->
                    val importProibido = proibidos.firstOrNull(linha::startsWith)
                    if (importProibido == null) null
                    else "${arquivo.relativeTo(raiz).path}:${indice + 1}: $linha"
                }
            }
            .toList()

        assertTrue(
            "Dependências arquiteturais proibidas:\n${violacoes.joinToString("\n")}",
            violacoes.isEmpty(),
        )
    }
}

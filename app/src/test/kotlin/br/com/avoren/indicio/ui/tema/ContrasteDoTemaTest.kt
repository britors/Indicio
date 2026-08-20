package br.com.avoren.indicio.ui.tema

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Contraste da paleta, conferido sobre o esquema de cores real.
 *
 * O produto é feito para quem enxerga pouco, então contraste é requisito, não
 * acabamento. O teste lê [EsquemaClaro] diretamente: trocar uma cor no tema sem
 * conferir o contraste passa a quebrar o build.
 */
class ContrasteDoTemaTest {

    private val esquema: ColorScheme = EsquemaClaro

    @Test
    fun `texto atende o minimo AA de 4,5 para 1 sobre o proprio fundo`() {
        val pares = listOf(
            Trio("onPrimary sobre primary", esquema.onPrimary, esquema.primary),
            Trio("onPrimaryContainer sobre primaryContainer", esquema.onPrimaryContainer, esquema.primaryContainer),
            Trio("onSecondary sobre secondary", esquema.onSecondary, esquema.secondary),
            Trio("onSecondaryContainer sobre secondaryContainer", esquema.onSecondaryContainer, esquema.secondaryContainer),
            Trio("onBackground sobre background", esquema.onBackground, esquema.background),
            Trio("onSurface sobre surface", esquema.onSurface, esquema.surface),
            Trio("onSurfaceVariant sobre surfaceVariant", esquema.onSurfaceVariant, esquema.surfaceVariant),
            Trio("onError sobre error", esquema.onError, esquema.error),
            // Títulos de seção usam `primary` como cor de texto.
            Trio("primary sobre background", esquema.primary, esquema.background),
            Trio("primary sobre surfaceVariant", esquema.primary, esquema.surfaceVariant),
            // A carta de escolha põe onSurface sobre surfaceVariant.
            Trio("onSurface sobre surfaceVariant", esquema.onSurface, esquema.surfaceVariant),
        )

        verificar(pares, minimo = MINIMO_TEXTO)
    }

    @Test
    fun `contorno de controle atende o minimo de 3 para 1`() {
        // WCAG 1.4.11: o que delimita um controle precisa ser percebido. É o
        // caso da borda dos botões delineados e da carta de escolha, que não
        // têm preenchimento próprio para se distinguir do fundo.
        val pares = listOf(
            Trio("outline sobre background", esquema.outline, esquema.background),
            Trio("outline sobre surfaceVariant", esquema.outline, esquema.surfaceVariant),
            // `outlineVariant` é o que o Material usa na borda do
            // OutlinedButton nesta versão, e por isso também precisa passar.
            Trio("outlineVariant sobre background", esquema.outlineVariant, esquema.background),
            Trio("outlineVariant sobre surfaceVariant", esquema.outlineVariant, esquema.surfaceVariant),
            Trio("secondary sobre background", esquema.secondary, esquema.background),
            Trio("secondary sobre surfaceVariant", esquema.secondary, esquema.surfaceVariant),
        )

        verificar(pares, minimo = MINIMO_NAO_TEXTO)
    }

    @Test
    fun `nenhum contorno herda a cor padrao do Material`() {
        // Um token que o tema esquece de definir cai no padrão do Material, que
        // é cinza-lilás e não pertence a esta paleta. Foi assim que a borda dos
        // botões ficou em 1,58:1 sem ninguém notar: `outlineVariant` nunca fora
        // definido. Este teste falha se um contorno voltar a ser herdado.
        val nossos = setOf(BordaSepia, BordaSuave)

        assertTrue(
            "outline não vem da paleta do Indício: ${esquema.outline}",
            esquema.outline in nossos,
        )
        assertTrue(
            "outlineVariant não vem da paleta do Indício: ${esquema.outlineVariant}",
            esquema.outlineVariant in nossos,
        )
    }

    private fun verificar(pares: List<Trio>, minimo: Double) {
        val falhas = pares
            .map { it to contraste(it.frente, it.fundo) }
            .filter { (_, razao) -> razao < minimo }
            .map { (par, razao) -> "${par.nome}: %.2f:1".format(razao) }

        assertTrue(
            "Abaixo do mínimo de $minimo:1 — ${falhas.joinToString("; ")}",
            falhas.isEmpty(),
        )
    }

    private data class Trio(val nome: String, val frente: Color, val fundo: Color)

    private fun contraste(a: Color, b: Color): Double {
        val la = luminanciaRelativa(a)
        val lb = luminanciaRelativa(b)
        return (maxOf(la, lb) + 0.05) / (minOf(la, lb) + 0.05)
    }

    /** Luminância relativa da WCAG 2.1. */
    private fun luminanciaRelativa(cor: Color): Double {
        fun canal(valor: Float): Double {
            val v = valor.toDouble()
            return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * canal(cor.red) + 0.7152 * canal(cor.green) + 0.0722 * canal(cor.blue)
    }

    private companion object {
        const val MINIMO_TEXTO = 4.5
        const val MINIMO_NAO_TEXTO = 3.0
    }
}

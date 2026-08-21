package br.com.w3ti.indicio.ui.tema

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Contraste da paleta, conferido sobre o esquema de cores real.
 *
 * O produto é feito para quem enxerga pouco, então contraste é requisito, não
 * acabamento. O teste lê os dois esquemas diretamente: trocar uma cor no tema
 * sem conferir o contraste passa a quebrar o build.
 */
class ContrasteDoTemaTest {

    private val esquemas = listOf(
        "claro" to EsquemaClaro,
        "escuro" to EsquemaEscuro,
    )

    @Test
    fun `texto atende o minimo AA de 4,5 para 1 sobre o proprio fundo`() {
        val pares = esquemas.flatMap { (nome, esquema) ->
            listOf(
                Trio("$nome: onPrimary sobre primary", esquema.onPrimary, esquema.primary),
                Trio(
                    "$nome: onPrimaryContainer sobre primaryContainer",
                    esquema.onPrimaryContainer,
                    esquema.primaryContainer,
                ),
                Trio("$nome: onSecondary sobre secondary", esquema.onSecondary, esquema.secondary),
                Trio(
                    "$nome: onSecondaryContainer sobre secondaryContainer",
                    esquema.onSecondaryContainer,
                    esquema.secondaryContainer,
                ),
                Trio("$nome: onBackground sobre background", esquema.onBackground, esquema.background),
                Trio("$nome: onSurface sobre surface", esquema.onSurface, esquema.surface),
                Trio(
                    "$nome: onSurfaceVariant sobre surfaceVariant",
                    esquema.onSurfaceVariant,
                    esquema.surfaceVariant,
                ),
                Trio("$nome: onSurface sobre surfaceDim", esquema.onSurface, esquema.surfaceDim),
                Trio("$nome: onSurface sobre surfaceBright", esquema.onSurface, esquema.surfaceBright),
                Trio(
                    "$nome: onSurface sobre surfaceContainerLowest",
                    esquema.onSurface,
                    esquema.surfaceContainerLowest,
                ),
                Trio(
                    "$nome: onSurface sobre surfaceContainerLow",
                    esquema.onSurface,
                    esquema.surfaceContainerLow,
                ),
                Trio(
                    "$nome: onSurface sobre surfaceContainer",
                    esquema.onSurface,
                    esquema.surfaceContainer,
                ),
                Trio(
                    "$nome: onSurface sobre surfaceContainerHigh",
                    esquema.onSurface,
                    esquema.surfaceContainerHigh,
                ),
                Trio(
                    "$nome: onSurface sobre surfaceContainerHighest",
                    esquema.onSurface,
                    esquema.surfaceContainerHighest,
                ),
                Trio(
                    "$nome: inverseOnSurface sobre inverseSurface",
                    esquema.inverseOnSurface,
                    esquema.inverseSurface,
                ),
                Trio("$nome: onError sobre error", esquema.onError, esquema.error),
                // Títulos de seção usam `primary` como cor de texto.
                Trio("$nome: primary sobre background", esquema.primary, esquema.background),
                Trio("$nome: primary sobre surfaceVariant", esquema.primary, esquema.surfaceVariant),
                // A carta de escolha põe onSurface sobre surfaceVariant.
                Trio("$nome: onSurface sobre surfaceVariant", esquema.onSurface, esquema.surfaceVariant),
                // A marca e os rótulos de retomada usam o acento no painel azul.
                Trio(
                    "$nome: tertiary sobre primaryContainer",
                    esquema.tertiary,
                    esquema.primaryContainer,
                ),
            )
        }

        verificar(pares, minimo = MINIMO_TEXTO)
    }

    @Test
    fun `contorno de controle atende o minimo de 3 para 1`() {
        // WCAG 1.4.11: o que delimita um controle precisa ser percebido. É o
        // caso da borda dos botões delineados e da carta de escolha, que não
        // têm preenchimento próprio para se distinguir do fundo.
        val pares = esquemas.flatMap { (nome, esquema) ->
            listOf(
                Trio("$nome: outline sobre background", esquema.outline, esquema.background),
                Trio("$nome: outline sobre surfaceVariant", esquema.outline, esquema.surfaceVariant),
                // `outlineVariant` é o que o Material usa na borda do
                // OutlinedButton nesta versão, e por isso também precisa passar.
                Trio(
                    "$nome: outlineVariant sobre background",
                    esquema.outlineVariant,
                    esquema.background,
                ),
                Trio(
                    "$nome: outlineVariant sobre surfaceVariant",
                    esquema.outlineVariant,
                    esquema.surfaceVariant,
                ),
                Trio("$nome: secondary sobre background", esquema.secondary, esquema.background),
                Trio("$nome: secondary sobre surfaceVariant", esquema.secondary, esquema.surfaceVariant),
            )
        }

        verificar(pares, minimo = MINIMO_NAO_TEXTO)
    }

    @Test
    fun `nenhum contorno herda a cor padrao do Material`() {
        // Um token que o tema esquece de definir cai no padrão do Material, que
        // é cinza-lilás e não pertence a esta paleta. Foi assim que a borda dos
        // botões ficou em 1,58:1 sem ninguém notar: `outlineVariant` nunca fora
        // definido. Este teste falha se um contorno voltar a ser herdado.
        val nossos = setOf(BordaControleClara, BordaControleEscura)

        esquemas.forEach { (nome, esquema) ->
            assertTrue(
                "$nome: outline não vem da paleta do Indício: ${esquema.outline}",
                esquema.outline in nossos,
            )
            assertTrue(
                "$nome: outlineVariant não vem da paleta do Indício: ${esquema.outlineVariant}",
                esquema.outlineVariant in nossos,
            )
        }
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

package br.com.avoren.indicio.ui.tema

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.com.avoren.indicio.domain.model.preferencias.TamanhoTexto

/**
 * Fator aplicado sobre a tipografia base.
 *
 * Não existe escala menor que 1: o menor tamanho oferecido pelo produto já é
 * confortável, e "grande" é o padrão.
 */
internal val TamanhoTexto.escala: Float
    get() = when (this) {
        TamanhoTexto.GRANDE -> 1.0f
        TamanhoTexto.MUITO_GRANDE -> 1.25f
    }

/**
 * Tipografia base, com corpo de texto maior que o padrão do Material 3 e
 * entrelinha generosa.
 *
 * Títulos crescem menos que o corpo do texto: o que precisa de leitura longa é
 * a narrativa, e ampliar títulos na mesma proporção expulsaria o conteúdo da
 * tela sem ganho de conforto.
 */
internal fun tipografiaIndicio(tamanho: TamanhoTexto): Typography {
    val corpo = tamanho.escala
    val titulo = 1f + (corpo - 1f) / 2f

    return Typography(
        displaySmall = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 40.sp * titulo,
            lineHeight = 48.sp * titulo,
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 30.sp * titulo,
            lineHeight = 38.sp * titulo,
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp * titulo,
            lineHeight = 32.sp * titulo,
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp * titulo,
            lineHeight = 28.sp * titulo,
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp * corpo,
            lineHeight = 30.sp * corpo,
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp * corpo,
            lineHeight = 28.sp * corpo,
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp * titulo,
            lineHeight = 28.sp * titulo,
        ),
    )
}

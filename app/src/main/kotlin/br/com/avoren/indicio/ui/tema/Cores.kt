package br.com.avoren.indicio.ui.tema

import androidx.compose.ui.graphics.Color

/**
 * Paleta base de romance policial clássico: papel/creme/sépia, azul-marinho e
 * dourado envelhecido. Os valores definitivos e a verificação de contraste são
 * responsabilidade da issue de identidade visual.
 */
internal val AzulMarinho = Color(0xFF1B2A41)
internal val AzulMarinhoClaro = Color(0xFF2E4665)
internal val DouradoEnvelhecido = Color(0xFF8A6A1F)
internal val DouradoClaro = Color(0xFFF3E4BE)
internal val PapelCreme = Color(0xFFFBF6EC)
internal val PapelSepia = Color(0xFFF1E7D6)
internal val TintaSepia = Color(0xFF2B2118)
internal val TintaSuave = Color(0xFF544636)
/**
 * Contorno de controle: a borda que identifica um botão delineado.
 *
 * Escuro o bastante para atender o mínimo de 3:1 da WCAG 1.4.11 tanto sobre
 * [PapelCreme] (3,8:1) quanto sobre [PapelSepia] (3,4:1). O tom anterior,
 * mais claro, ficava em 1,8:1 e 1,6:1 — o contorno do botão praticamente não
 * existia para quem enxerga pouco.
 */
internal val BordaSepia = Color(0xFF8F7A5B)

/** Contorno decorativo: divisórias e filetes que não delimitam controle. */
internal val BordaSuave = Color(0xFFCBB99B)
internal val Branco = Color(0xFFFFFFFF)
internal val VermelhoDiscreto = Color(0xFF7A2E22)

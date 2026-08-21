package br.com.avoren.indicio.ui.historia

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.avoren.indicio.domain.model.caso.Imagem
import br.com.avoren.indicio.ui.carta.PROPORCAO_DA_CARTA
import br.com.avoren.indicio.ui.comum.IlustracaoNarrativa

/** Mantém o vocabulário da história sobre a ilustração narrativa compartilhada. */
@Composable
internal fun IlustracaoDaCena(
    imagem: Imagem,
    modifier: Modifier = Modifier,
    proporcao: Float = PROPORCAO_DA_CARTA,
) {
    IlustracaoNarrativa(
        imagem = imagem,
        proporcao = proporcao,
        modifier = modifier,
    )
}

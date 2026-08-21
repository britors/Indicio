package br.com.w3ti.indicio.ui.historia

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.w3ti.indicio.domain.model.caso.Imagem
import br.com.w3ti.indicio.ui.carta.PROPORCAO_DA_CARTA
import br.com.w3ti.indicio.ui.comum.IlustracaoNarrativa

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

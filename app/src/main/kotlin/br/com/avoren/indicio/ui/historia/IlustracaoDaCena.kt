package br.com.avoren.indicio.ui.historia

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.domain.model.caso.Imagem

/**
 * Ilustração da cena.
 *
 * O nome do recurso vem do JSON do caso, então precisa ser resolvido em tempo
 * de execução. Enquanto a arte de um caso não existir, a descrição acessível é
 * apresentada como texto no lugar da imagem: a cena continua compreensível e a
 * ausência fica visível, em vez de virar um espaço vazio.
 */
@Composable
internal fun IlustracaoDaCena(
    imagem: Imagem,
    modifier: Modifier = Modifier,
) {
    val recursos = LocalResources.current
    val pacote = LocalContext.current.packageName

    // O nome do recurso vem do JSON, então só pode ser resolvido em tempo de
    // execução. É o preço de manter os casos fora do código: sem isto, cada
    // história nova exigiria editar Kotlin.
    @SuppressLint("DiscouragedApi")
    val idDoRecurso = remember(imagem.recurso, recursos) {
        recursos.getIdentifier(imagem.recurso, "drawable", pacote)
    }

    val forma = RoundedCornerShape(12.dp)

    if (idDoRecurso != 0) {
        Image(
            painter = painterResource(idDoRecurso),
            contentDescription = imagem.descricaoAcessivel,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(PROPORCAO)
                .clip(forma)
                .border(1.dp, MaterialTheme.colorScheme.outline, forma),
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(forma)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, forma)
                .semantics { contentDescription = imagem.descricaoAcessivel },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = imagem.descricaoAcessivel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp),
            )
        }
    }
}

private const val PROPORCAO = 4f / 3f

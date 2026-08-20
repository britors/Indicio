package br.com.avoren.indicio.ui.carta

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.ui.tema.AlturaMinimaBotao

/**
 * Uma escolha, apresentada como carta a jogar.
 *
 * Continua sendo um botão para efeitos de acessibilidade: papel de botão,
 * largura inteira e a altura mínima de 64 dp exigida pelo produto. A carta é a
 * aparência, nunca o único meio de escolher — não há gesto obrigatório.
 */
@Composable
internal fun CartaDeEscolha(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
) {
    val corDaBorda = if (habilitado) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Surface(
        onClick = onClick,
        enabled = habilitado,
        shape = FormaDaEscolha,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(2.dp, corDaBorda),
        shadowElevation = if (habilitado) 3.dp else 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AlturaMinimaBotao)
            .semantics { role = Role.Button },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
        ) {
            MarcaDaCarta(cor = corDaBorda)

            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

/** Losango dourado: a mesma marca que aparece nos cantos do verso. */
@Composable
private fun MarcaDaCarta(cor: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .rotate(45f)
            .background(cor, RoundedCornerShape(2.dp)),
    )
}

private val FormaDaEscolha = RoundedCornerShape(14.dp)

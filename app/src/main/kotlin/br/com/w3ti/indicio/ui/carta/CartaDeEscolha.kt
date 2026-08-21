package br.com.w3ti.indicio.ui.carta

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import br.com.w3ti.indicio.ui.tema.AlturaMinimaBotao
import br.com.w3ti.indicio.ui.comum.IconesIndicio
import br.com.w3ti.indicio.ui.tema.EspacamentoIndicio
import br.com.w3ti.indicio.ui.tema.FormasIndicio

/**
 * Uma escolha, apresentada como carta a jogar.
 *
 * Continua sendo um botão para efeitos de acessibilidade: papel de botão,
 * largura inteira e a altura mínima de 64 dp exigida pelo produto. A carta é a
 * aparência, nunca o único meio de escolher — não há gesto obrigatório.
 */
@Composable
internal fun CartaDeEscolha(
    numero: Int,
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
) {
    Surface(
        onClick = onClick,
        enabled = habilitado,
        shape = FormasIndicio.controle,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AlturaMinimaBotao)
            .semantics { role = Role.Button },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
            modifier = Modifier.padding(
                horizontal = EspacamentoIndicio.medio,
                vertical = EspacamentoIndicio.pequeno,
            ),
        ) {
            Surface(
                shape = FormasIndicio.pequena,
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ) {
                Text(
                    text = numero.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(
                        horizontal = EspacamentoIndicio.medio,
                        vertical = EspacamentoIndicio.pequeno,
                    ),
                )
            }

            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector = IconesIndicio.avancar,
                contentDescription = null,
            )
        }
    }
}

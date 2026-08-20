package br.com.avoren.indicio.ui.historia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.R
import br.com.avoren.indicio.domain.narracao.EstadoNarracao
import br.com.avoren.indicio.ui.comum.IconesIndicio
import br.com.avoren.indicio.ui.tema.AlturaMinimaBotao
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio

/**
 * Controle de narração da cena.
 *
 * Sem voz utilizável no aparelho, no lugar do botão aparece um aviso curto e
 * não bloqueante: nada some da tela e a história segue por leitura.
 *
 * O botão anuncia estado e ação separadamente para tecnologia assistiva — o
 * rótulo diz o que o toque faz, e a descrição de estado diz o que está
 * acontecendo agora.
 */
@Composable
internal fun ControleDeNarracao(
    estado: EstadoNarracao,
    onAlternar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (estado) {
        EstadoNarracao.PREPARANDO -> Unit

        EstadoNarracao.INDISPONIVEL -> Text(
            text = stringResource(R.string.historia_sem_voz),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.fillMaxWidth(),
        )

        EstadoNarracao.PRONTO, EstadoNarracao.FALANDO -> {
            val falando = estado == EstadoNarracao.FALANDO
            val rotulo = stringResource(
                if (falando) R.string.historia_parar_narracao else R.string.historia_ouvir,
            )
            val situacao = stringResource(
                if (falando) R.string.historia_narrando else R.string.historia_ouvir,
            )

            Surface(
                onClick = onAlternar,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = AlturaMinimaBotao)
                    .semantics {
                        stateDescription = situacao
                        liveRegion = LiveRegionMode.Polite
                        onClick(label = rotulo, action = null)
                    },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (falando) {
                                    IconesIndicio.fechar
                                } else {
                                    IconesIndicio.continuar
                                },
                                contentDescription = null,
                            )
                        }
                    }
                    Text(text = rotulo, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

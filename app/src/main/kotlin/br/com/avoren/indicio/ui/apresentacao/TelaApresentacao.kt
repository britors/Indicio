package br.com.avoren.indicio.ui.apresentacao

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.R
import br.com.avoren.indicio.ui.tema.LocalReducaoDeMovimentos
import br.com.avoren.indicio.ui.tema.TemaIndicio
import kotlinx.coroutines.delay

/**
 * Apresentação da marca.
 *
 * A transição é discreta e ignorável: qualquer toque na tela avança na hora, e
 * a espera é curta. Com redução de movimentos ligada, a apresentação passa
 * quase imediatamente.
 */
@Composable
fun TelaApresentacao(
    onConcluir: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val concluir by rememberUpdatedState(onConcluir)
    val semMovimento = LocalReducaoDeMovimentos.current

    LaunchedEffect(semMovimento) {
        delay(if (semMovimento) ESPERA_REDUZIDA else ESPERA_PADRAO)
        concluir()
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                onClickLabel = stringResource(R.string.apresentacao_pular),
                onClick = { concluir() },
            ),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.app_nome),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.app_slogan),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private const val ESPERA_PADRAO = 1_600L
private const val ESPERA_REDUZIDA = 300L

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PreviaApresentacao() {
    TemaIndicio { TelaApresentacao(onConcluir = {}) }
}

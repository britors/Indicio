package br.com.avoren.indicio.ui.descanso

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.R
import br.com.avoren.indicio.ui.comum.RotuloEditorial
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio
import br.com.avoren.indicio.ui.tema.TemaIndicio
import kotlin.math.ceil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/** Tela modal que protege os três minutos de descanso da investigação. */
@Composable
fun TelaDescanso(
    tempoRestante: Duration,
    duracaoTotal: Duration,
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = true) { /* A pausa termina somente ao fim da contagem. */ }

    val segundos = ceil(tempoRestante.inWholeMilliseconds / 1_000.0).toInt().coerceAtLeast(0)
    val minutos = segundos / SEGUNDOS_POR_MINUTO
    val segundosDoMinuto = segundos % SEGUNDOS_POR_MINUTO
    val relogio = "%02d:%02d".format(minutos, segundosDoMinuto)
    val descricaoRelogio = stringResource(
        R.string.descanso_tempo_acessivel,
        minutos,
        segundosDoMinuto,
    )
    val titulo = stringResource(R.string.descanso_titulo)
    val progresso = if (duracaoTotal.isPositive()) {
        (tempoRestante / duracaoTotal).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                    }
                }
            }
            .semantics { paneTitle = titulo },
        color = MaterialTheme.colorScheme.background,
    ) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { espacamento ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(espacamento)
                    .verticalScroll(rememberScrollState())
                    .padding(EspacamentoIndicio.destaque),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                RotuloEditorial(texto = stringResource(R.string.descanso_rotulo))
                Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))
                Text(
                    text = stringResource(R.string.descanso_mensagem),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(EspacamentoIndicio.destaque))
                Box(
                    modifier = Modifier
                        .size(TAMANHO_RELOGIO)
                        .clearAndSetSemantics { contentDescription = descricaoRelogio },
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        progress = { progresso },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 10.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    )
                    Text(
                        text = relogio,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.height(EspacamentoIndicio.destaque))
                Text(
                    text = stringResource(R.string.descanso_retorno_automatico),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** Lembrete breve que não bloqueia escolhas, leitura nem navegação. */
@Composable
fun LembreteDescanso(
    onDispensar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Snackbar(
        modifier = modifier
            .padding(EspacamentoIndicio.padrao)
            .semantics { liveRegion = LiveRegionMode.Polite },
        actionOnNewLine = true,
        action = {
            TextButton(onClick = onDispensar) {
                Text(stringResource(R.string.descanso_lembrete_entendi))
            }
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.minimo)) {
            Text(
                text = stringResource(R.string.descanso_lembrete_titulo),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.descanso_lembrete_mensagem),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private const val SEGUNDOS_POR_MINUTO = 60
private val TAMANHO_RELOGIO = 220.dp

@Preview(showBackground = true)
@Composable
private fun PreviaDescanso() {
    TemaIndicio {
        TelaDescanso(
            tempoRestante = 2.minutes,
            duracaoTotal = 3.minutes,
        )
    }
}

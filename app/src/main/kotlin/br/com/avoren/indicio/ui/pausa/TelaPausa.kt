package br.com.avoren.indicio.ui.pausa

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.R
import br.com.avoren.indicio.ui.comum.BarraDoTopo
import br.com.avoren.indicio.ui.comum.BotaoPrincipal
import br.com.avoren.indicio.ui.comum.BotaoSecundario
import br.com.avoren.indicio.ui.tema.TemaIndicio

/**
 * Pausa da história.
 *
 * Voltar ao início não pede confirmação: o progresso já está gravado e o jogador
 * pode retomar pelo botão "Continuar". Reiniciar o caso, sim — é a única ação
 * daqui que descarta o caminho percorrido.
 */
@Composable
fun TelaPausa(
    onContinuar: () -> Unit,
    onConfiguracoes: () -> Unit,
    onReiniciar: () -> Unit,
    onVoltarAoInicio: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var confirmandoReinicio by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { BarraDoTopo(titulo = stringResource(R.string.pausa_titulo)) },
    ) { espacamento ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
        ) {
            BotaoPrincipal(
                texto = stringResource(R.string.pausa_continuar),
                onClick = onContinuar,
            )
            Spacer(modifier = Modifier.height(16.dp))

            BotaoSecundario(
                texto = stringResource(R.string.pausa_configuracoes),
                onClick = onConfiguracoes,
            )
            Spacer(modifier = Modifier.height(16.dp))

            BotaoSecundario(
                texto = stringResource(R.string.pausa_reiniciar),
                onClick = { confirmandoReinicio = true },
            )
            Spacer(modifier = Modifier.height(16.dp))

            BotaoSecundario(
                texto = stringResource(R.string.pausa_voltar_inicio),
                onClick = onVoltarAoInicio,
            )
        }
    }

    if (confirmandoReinicio) {
        AlertDialog(
            onDismissRequest = { confirmandoReinicio = false },
            title = { Text(stringResource(R.string.pausa_reiniciar_confirmar_titulo)) },
            text = {
                Text(
                    text = stringResource(R.string.pausa_reiniciar_confirmar_texto),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmandoReinicio = false
                        onReiniciar()
                    },
                ) {
                    Text(stringResource(R.string.pausa_reiniciar_confirmar_sim))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmandoReinicio = false }) {
                    Text(stringResource(R.string.pausa_cancelar))
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviaPausa() {
    TemaIndicio {
        TelaPausa(
            onContinuar = {},
            onConfiguracoes = {},
            onReiniciar = {},
            onVoltarAoInicio = {},
        )
    }
}

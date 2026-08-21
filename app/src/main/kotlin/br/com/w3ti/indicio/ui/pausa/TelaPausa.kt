package br.com.w3ti.indicio.ui.pausa

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import br.com.w3ti.indicio.R
import br.com.w3ti.indicio.ui.comum.BotaoPrincipal
import br.com.w3ti.indicio.ui.comum.BotaoSecundario
import br.com.w3ti.indicio.ui.comum.IconesIndicio
import br.com.w3ti.indicio.ui.comum.TituloDeTela
import br.com.w3ti.indicio.ui.tema.EspacamentoIndicio
import br.com.w3ti.indicio.ui.tema.TemaIndicio

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
    onAbrirEtapas: () -> Unit = {},
    onAbrirCaderno: () -> Unit = {},
    temEtapas: Boolean = false,
) {
    var confirmandoReinicio by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { espacamento ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = EspacamentoIndicio.margemDaTela,
                    vertical = EspacamentoIndicio.grande,
                ),
        ) {
            TituloDeTela(texto = stringResource(R.string.pausa_titulo))
            Spacer(modifier = Modifier.height(EspacamentoIndicio.destaque))

            BotaoPrincipal(
                texto = stringResource(R.string.pausa_continuar),
                icone = IconesIndicio.continuar,
                onClick = onContinuar,
            )
            Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))

            if (temEtapas) {
                BotaoSecundario(
                    texto = stringResource(R.string.pausa_etapas),
                    icone = IconesIndicio.lista,
                    onClick = onAbrirEtapas,
                )
                Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))
            }

            BotaoSecundario(
                texto = stringResource(R.string.pausa_caderno),
                icone = IconesIndicio.pesquisar,
                onClick = onAbrirCaderno,
            )
            Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))

            BotaoSecundario(
                texto = stringResource(R.string.pausa_configuracoes),
                icone = IconesIndicio.configuracoes,
                onClick = onConfiguracoes,
            )
            Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))

            BotaoSecundario(
                texto = stringResource(R.string.pausa_reiniciar),
                icone = IconesIndicio.reiniciar,
                onClick = { confirmandoReinicio = true },
            )
            Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))

            BotaoSecundario(
                texto = stringResource(R.string.pausa_voltar_inicio),
                icone = IconesIndicio.inicio,
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
                    Icon(imageVector = IconesIndicio.reiniciar, contentDescription = null)
                    Text(
                        text = stringResource(R.string.pausa_reiniciar_confirmar_sim),
                        modifier = Modifier.padding(start = EspacamentoIndicio.pequeno),
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmandoReinicio = false }) {
                    Icon(imageVector = IconesIndicio.fechar, contentDescription = null)
                    Text(
                        text = stringResource(R.string.pausa_cancelar),
                        modifier = Modifier.padding(start = EspacamentoIndicio.pequeno),
                    )
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
            onAbrirEtapas = {},
            onAbrirCaderno = {},
            temEtapas = true,
            onConfiguracoes = {},
            onReiniciar = {},
            onVoltarAoInicio = {},
        )
    }
}

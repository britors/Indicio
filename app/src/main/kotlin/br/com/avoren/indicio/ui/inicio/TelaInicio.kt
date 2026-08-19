package br.com.avoren.indicio.ui.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.avoren.indicio.R
import br.com.avoren.indicio.di.ContainerAplicacao
import br.com.avoren.indicio.ui.comum.BotaoPrincipal
import br.com.avoren.indicio.ui.comum.BotaoSecundario
import br.com.avoren.indicio.ui.tema.TemaIndicio

/**
 * Tela inicial: o ponto de partida de toda sessão.
 */
@Composable
fun TelaInicio(
    container: ContainerAplicacao,
    onContinuar: (String) -> Unit,
    onEscolherCaso: () -> Unit,
    onConfiguracoes: () -> Unit,
    onSobre: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InicioViewModel = viewModel(factory = InicioViewModel.fabrica(container)),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    ConteudoInicio(
        estado = estado,
        onContinuar = onContinuar,
        onEscolherCaso = onEscolherCaso,
        onConfiguracoes = onConfiguracoes,
        onSobre = onSobre,
        modifier = modifier,
    )
}

@Composable
internal fun ConteudoInicio(
    estado: EstadoInicio,
    onContinuar: (String) -> Unit,
    onEscolherCaso: () -> Unit,
    onConfiguracoes: () -> Unit,
    onSobre: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { espacamento ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = estado.nome,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() },
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = estado.slogan,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(40.dp))

            // "Continuar" só existe quando há uma sessão realmente retomável;
            // um botão desabilitado sem explicação seria uma promessa vazia.
            estado.casoParaContinuar?.let { casoId ->
                BotaoPrincipal(
                    texto = stringResource(R.string.inicio_continuar),
                    descricaoAcessivel = estado.tituloParaContinuar?.let {
                        stringResource(R.string.inicio_continuar_caso, it)
                    },
                    onClick = { onContinuar(casoId) },
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            BotaoPrincipal(
                texto = stringResource(R.string.inicio_escolher_caso),
                onClick = onEscolherCaso,
            )

            Spacer(modifier = Modifier.height(16.dp))

            BotaoSecundario(
                texto = stringResource(R.string.inicio_configuracoes),
                onClick = onConfiguracoes,
            )

            Spacer(modifier = Modifier.height(16.dp))

            BotaoSecundario(
                texto = stringResource(R.string.inicio_sobre),
                onClick = onSobre,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.inicio_versao, estado.versao),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviaTelaInicio() {
    TemaIndicio {
        ConteudoInicio(
            estado = EstadoInicio(
                nome = "Indício",
                slogan = "Toda escolha revela uma pista.",
                versao = "0.1.0",
                casoParaContinuar = "taca-desaparecida",
                tituloParaContinuar = "O Mistério da Taça Desaparecida",
            ),
            onContinuar = {},
            onEscolherCaso = {},
            onConfiguracoes = {},
            onSobre = {},
        )
    }
}

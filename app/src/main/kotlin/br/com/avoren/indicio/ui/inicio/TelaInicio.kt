package br.com.avoren.indicio.ui.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.avoren.indicio.R
import br.com.avoren.indicio.application.caso.ObterCasoParaContinuar
import br.com.avoren.indicio.domain.repositorio.RepositorioIdentidade
import br.com.avoren.indicio.ui.comum.BotaoPrincipal
import br.com.avoren.indicio.ui.comum.BotaoSobreDestaque
import br.com.avoren.indicio.ui.comum.IconesIndicio
import br.com.avoren.indicio.ui.comum.MarcaIndicio
import br.com.avoren.indicio.ui.comum.RotuloEditorial
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio
import br.com.avoren.indicio.ui.tema.FormasIndicio
import br.com.avoren.indicio.ui.tema.TemaIndicio

/**
 * Tela inicial: o ponto de partida de toda sessão.
 */
@Composable
fun TelaInicio(
    repositorioIdentidade: RepositorioIdentidade,
    obterCasoParaContinuar: ObterCasoParaContinuar,
    onContinuar: (String) -> Unit,
    onEscolherCaso: () -> Unit,
    onConfiguracoes: () -> Unit,
    onSobre: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InicioViewModel = viewModel(
        factory = InicioViewModel.fabrica(repositorioIdentidade, obterCasoParaContinuar),
    ),
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
                .padding(
                    horizontal = EspacamentoIndicio.margemDaTela,
                    vertical = EspacamentoIndicio.extraGrande,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            MarcaIndicio()

            Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))

            Text(
                text = estado.nome,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() },
            )

            Spacer(modifier = Modifier.height(EspacamentoIndicio.minimo))

            Text(
                text = estado.slogan,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(EspacamentoIndicio.extraGrande))

            // "Continuar" só existe quando há uma sessão realmente retomável;
            // um botão desabilitado sem explicação seria uma promessa vazia.
            estado.casoParaContinuar?.let { casoId ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = FormasIndicio.cartao,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(EspacamentoIndicio.grande),
                        verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
                    ) {
                        RotuloEditorial(
                            texto = stringResource(R.string.inicio_retomada_rotulo),
                            cor = MaterialTheme.colorScheme.tertiary,
                        )
                        Text(
                            text = estado.tituloParaContinuar.orEmpty(),
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.semantics { heading() },
                        )
                        Text(
                            text = stringResource(R.string.inicio_retomada_apoio),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        BotaoSobreDestaque(
                            texto = stringResource(R.string.inicio_continuar),
                            descricaoAcessivel = estado.tituloParaContinuar?.let {
                                stringResource(R.string.inicio_continuar_caso, it)
                            },
                            onClick = { onContinuar(casoId) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(EspacamentoIndicio.medio))
            }

            BotaoPrincipal(
                texto = stringResource(
                    if (estado.podeContinuar) {
                        R.string.inicio_escolher_outro_caso
                    } else {
                        R.string.inicio_escolher_caso
                    },
                ),
                icone = IconesIndicio.pesquisar,
                onClick = onEscolherCaso,
                modifier = Modifier,
            )

            Spacer(modifier = Modifier.height(EspacamentoIndicio.medio))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
            ) {
                AcaoCompacta(
                    texto = stringResource(R.string.inicio_configuracoes),
                    icone = IconesIndicio.configuracoes,
                    onClick = onConfiguracoes,
                    modifier = Modifier.weight(1f),
                )
                AcaoCompacta(
                    texto = stringResource(R.string.inicio_sobre),
                    icone = IconesIndicio.informacao,
                    onClick = onSobre,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))

            Text(
                text = stringResource(R.string.inicio_versao, estado.versao),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AcaoCompacta(
    texto: String,
    icone: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(AlturaCompacta),
        shape = FormasIndicio.controle,
        color = Color.Transparent,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AlturaVisualAcaoCompacta),
                shape = FormasIndicio.controle,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = icone,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = texto,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = EspacamentoIndicio.pequeno),
                    )
                }
            }
        }
    }
}

private val AlturaCompacta = br.com.avoren.indicio.ui.tema.AlturaMinimaBotao
private val AlturaVisualAcaoCompacta = 52.dp

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

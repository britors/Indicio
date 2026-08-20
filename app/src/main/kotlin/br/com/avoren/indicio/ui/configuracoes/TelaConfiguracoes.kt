package br.com.avoren.indicio.ui.configuracoes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.avoren.indicio.ui.tema.BordaSuave
import br.com.avoren.indicio.R
import br.com.avoren.indicio.domain.model.preferencias.Preferencias
import br.com.avoren.indicio.domain.model.preferencias.TamanhoTexto
import br.com.avoren.indicio.ui.comum.TituloDeTela
import br.com.avoren.indicio.ui.tema.AlturaMinimaBotao
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio
import br.com.avoren.indicio.ui.tema.TemaIndicio

/**
 * Configurações de leitura e conforto.
 *
 * Cada controle grava no DataStore imediatamente; não existe botão "salvar",
 * que seria mais um passo para errar. O efeito é visível na própria tela: o
 * texto de exemplo muda de tamanho junto com a escolha.
 */
@Composable
fun TelaConfiguracoes(
    viewModel: ConfiguracoesViewModel,
    modifier: Modifier = Modifier,
) {
    val preferencias by viewModel.preferencias.collectAsStateWithLifecycle()

    ConteudoConfiguracoes(
        preferencias = preferencias,
        onTamanhoTexto = viewModel::definirTamanhoTexto,
        onReduzirMovimentos = viewModel::definirReducaoDeMovimentos,
        modifier = modifier,
    )
}

@Composable
internal fun ConteudoConfiguracoes(
    preferencias: Preferencias,
    onTamanhoTexto: (TamanhoTexto) -> Unit,
    onReduzirMovimentos: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            TituloDeTela(texto = stringResource(R.string.configuracoes_titulo))

            Spacer(modifier = Modifier.height(EspacamentoIndicio.destaque))

            Text(
                text = stringResource(R.string.configuracoes_tamanho_texto),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() },
            )

            Spacer(modifier = Modifier.height(EspacamentoIndicio.pequeno))

            Column(modifier = Modifier.selectableGroup()) {
                OpcaoDeTamanho(
                    rotulo = stringResource(R.string.configuracoes_tamanho_grande),
                    selecionada = preferencias.tamanhoTexto == TamanhoTexto.GRANDE,
                    onSelecionar = { onTamanhoTexto(TamanhoTexto.GRANDE) },
                )
                OpcaoDeTamanho(
                    rotulo = stringResource(R.string.configuracoes_tamanho_muito_grande),
                    selecionada = preferencias.tamanhoTexto == TamanhoTexto.MUITO_GRANDE,
                    onSelecionar = { onTamanhoTexto(TamanhoTexto.MUITO_GRANDE) },
                )
            }

            Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))

            Text(
                text = stringResource(R.string.configuracoes_exemplo),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(EspacamentoIndicio.extraGrande))
            HorizontalDivider(color = BordaSuave)
            Spacer(modifier = Modifier.height(EspacamentoIndicio.extraGrande))

            Text(
                text = stringResource(R.string.configuracoes_movimento),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() },
            )

            Spacer(modifier = Modifier.height(EspacamentoIndicio.pequeno))

            // A linha inteira alterna a preferência: obrigar o toque a acertar
            // o próprio interruptor seria um alvo pequeno demais.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = AlturaMinimaBotao)
                    .toggleable(
                        value = preferencias.reduzirMovimentos,
                        onValueChange = onReduzirMovimentos,
                        role = Role.Switch,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.configuracoes_reduzir_movimentos),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = stringResource(R.string.configuracoes_reduzir_movimentos_apoio),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = preferencias.reduzirMovimentos,
                    onCheckedChange = null,
                )
            }
        }
    }
}

@Composable
private fun OpcaoDeTamanho(
    rotulo: String,
    selecionada: Boolean,
    onSelecionar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AlturaMinimaBotao)
            .selectable(
                selected = selecionada,
                onClick = onSelecionar,
                role = Role.RadioButton,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selecionada, onClick = null)
        Text(
            text = rotulo,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviaConfiguracoes() {
    TemaIndicio {
        ConteudoConfiguracoes(
            preferencias = Preferencias(),
            onTamanhoTexto = {},
            onReduzirMovimentos = {},
        )
    }
}

package br.com.avoren.indicio.ui.investigacao

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import br.com.avoren.indicio.R
import br.com.avoren.indicio.ui.comum.BarraDoTopo
import br.com.avoren.indicio.ui.comum.BotaoPrincipal
import br.com.avoren.indicio.ui.comum.CartaoDeRegistro
import br.com.avoren.indicio.ui.comum.EstadoDoRegistro
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio

@Composable
internal fun ConteudoEtapas(
    estado: EstadoInvestigacao.Conteudo,
    onContinuar: () -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraDoTopo(
                titulo = stringResource(R.string.etapas_titulo),
                onVoltar = onVoltar,
            )
        },
    ) { espacamento ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(espacamento),
            contentPadding = PaddingValues(
                horizontal = EspacamentoIndicio.margemDaTela,
                vertical = EspacamentoIndicio.grande,
            ),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.padrao),
        ) {
            item {
                Text(
                    text = stringResource(R.string.etapas_reveladas),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics { heading() },
                )
            }

            if (estado.etapas.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.etapas_vazio),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                items(estado.etapas, key = EtapaUi::id) { etapa ->
                    CartaoDeRegistro(
                        marcador = etapa.numero.toString().padStart(2, '0'),
                        titulo = etapa.titulo ?: stringResource(R.string.etapas_futura_titulo),
                        descricao = etapa.descricao ?: stringResource(R.string.etapas_futura_descricao),
                        estado = when (etapa.situacao) {
                            SituacaoEtapa.CONCLUIDA -> EstadoDoRegistro.CONCLUIDO
                            SituacaoEtapa.ATUAL -> EstadoDoRegistro.ATUAL
                            SituacaoEtapa.FUTURA -> EstadoDoRegistro.FUTURO
                        },
                        selo = when (etapa.situacao) {
                            SituacaoEtapa.CONCLUIDA -> stringResource(R.string.etapas_concluida)
                            SituacaoEtapa.ATUAL -> stringResource(R.string.etapas_atual)
                            SituacaoEtapa.FUTURA -> null
                        },
                    )
                }
            }

            if (!estado.concluida) {
                item {
                    BotaoPrincipal(
                        texto = stringResource(R.string.etapas_continuar),
                        onClick = onContinuar,
                    )
                }
            }
        }
    }
}

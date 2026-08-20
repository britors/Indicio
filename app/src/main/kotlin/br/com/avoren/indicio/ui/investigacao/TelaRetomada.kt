package br.com.avoren.indicio.ui.investigacao

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import br.com.avoren.indicio.R
import br.com.avoren.indicio.ui.comum.BarraDoTopo
import br.com.avoren.indicio.ui.comum.BotaoPrincipal
import br.com.avoren.indicio.ui.comum.BotaoSecundario
import br.com.avoren.indicio.ui.comum.PainelDeObjetivo
import br.com.avoren.indicio.ui.comum.RotuloEditorial
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio
import br.com.avoren.indicio.ui.tema.FormasIndicio

@Composable
internal fun ConteudoRetomada(
    estado: EstadoInvestigacao.Conteudo,
    onContinuar: () -> Unit,
    onAbrirCaderno: () -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val retomada = requireNotNull(estado.retomada)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraDoTopo(
                titulo = stringResource(R.string.retomada_titulo),
                onVoltar = onVoltar,
            )
        },
    ) { espacamento ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = EspacamentoIndicio.margemDaTela,
                vertical = EspacamentoIndicio.grande,
            ),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.padrao),
        ) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = FormasIndicio.cartao,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(EspacamentoIndicio.grande),
                        verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
                    ) {
                        RotuloEditorial(texto = stringResource(R.string.retomada_etapa))
                        Text(
                            text = retomada.etapa,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.semantics { heading() },
                        )
                        Text(text = retomada.resumo, style = MaterialTheme.typography.bodyLarge)

                        if (retomada.lembrancas.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.retomada_lembrar),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            retomada.lembrancas.forEach { lembranca ->
                                Text(text = "• $lembranca", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            estado.objetivoAtual?.let { objetivo ->
                item {
                    PainelDeObjetivo(
                        rotulo = stringResource(R.string.retomada_proximo_objetivo),
                        texto = objetivo.texto,
                    )
                }
            }

            item {
                BotaoPrincipal(
                    texto = stringResource(R.string.retomada_continuar),
                    onClick = onContinuar,
                )
            }
            item {
                BotaoSecundario(
                    texto = pluralStringResource(
                        R.plurals.retomada_abrir_caderno,
                        estado.caderno.totalRegistros,
                        estado.caderno.totalRegistros,
                    ),
                    onClick = onAbrirCaderno,
                )
            }
        }
    }
}

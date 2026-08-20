package br.com.avoren.indicio.ui.sobre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.R
import br.com.avoren.indicio.ui.comum.BotaoSecundario
import br.com.avoren.indicio.ui.comum.IconesIndicio
import br.com.avoren.indicio.ui.comum.TituloDeTela
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio
import br.com.avoren.indicio.ui.tema.TemaIndicio

private const val URL_REPOSITORIO = "https://github.com/britors/Indicio"

/**
 * Tela Sobre.
 *
 * É o único lugar do aplicativo onde o aviso de saúde aparece. Ele não deve ser
 * repetido em nenhuma outra tela, nem em textos de casos.
 */
@Composable
fun TelaSobre(
    versao: String,
    modifier: Modifier = Modifier,
    onAbrirGithub: (() -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current

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
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.grande),
        ) {
            TituloDeTela(texto = stringResource(R.string.sobre_titulo))

            Text(
                text = stringResource(R.string.sobre_descricao),
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = stringResource(R.string.sobre_offline),
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = stringResource(R.string.sobre_ficcao),
                style = MaterialTheme.typography.bodyMedium,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            ) {
                Text(
                    text = stringResource(R.string.sobre_aviso_medico),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp),
                )
            }

            Text(
                text = stringResource(R.string.sobre_licenca),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = stringResource(R.string.sobre_criador),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            BotaoSecundario(
                texto = stringResource(R.string.sobre_github),
                icone = IconesIndicio.compartilhar,
                onClick = onAbrirGithub ?: { uriHandler.openUri(URL_REPOSITORIO) },
            )

            Text(
                text = stringResource(R.string.sobre_versao, versao),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviaSobre() {
    TemaIndicio { TelaSobre(versao = "0.1.0") }
}

package br.com.w3ti.indicio.ui.historia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.w3ti.indicio.R
import br.com.w3ti.indicio.ui.comum.BotaoPrincipal
import br.com.w3ti.indicio.ui.comum.BotaoSecundario
import br.com.w3ti.indicio.ui.comum.IconesIndicio

/** Confirma antes de substituir um progresso que não combina mais com o caso. */
@Composable
internal fun ConteudoAtualizacaoNecessaria(
    tituloCaso: String,
    onReiniciar: () -> Unit,
    onVoltarAoCatalogo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.historia_atualizada_titulo),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.historia_atualizada_mensagem, tituloCaso),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
        )
        BotaoPrincipal(
            texto = stringResource(R.string.historia_atualizada_recomecar),
            icone = IconesIndicio.reiniciar,
            onClick = onReiniciar,
        )
        BotaoSecundario(
            texto = stringResource(R.string.conclusao_voltar_catalogo),
            icone = IconesIndicio.lista,
            onClick = onVoltarAoCatalogo,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

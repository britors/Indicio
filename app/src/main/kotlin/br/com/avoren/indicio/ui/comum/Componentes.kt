package br.com.avoren.indicio.ui.comum

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.R
import br.com.avoren.indicio.ui.tema.AlturaMinimaBotao

/**
 * Botão de ação padrão do aplicativo.
 *
 * Ocupa a largura disponível e respeita a altura mínima de 64 dp exigida pelo
 * produto, para que o alvo de toque seja confortável.
 */
@Composable
fun BotaoPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    descricaoAcessivel: String? = null,
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AlturaMinimaBotao),
        contentPadding = ButtonDefaults.ContentPadding,
    ) {
        Text(
            text = descricaoAcessivel ?: texto,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
        )
    }
}

/** Variante secundária, para ações que não são o caminho principal da tela. */
@Composable
fun BotaoSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AlturaMinimaBotao),
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraDoTopo(
    titulo: String,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() },
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = modifier,
    )
}

@Composable
fun ConteudoCarregando(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Estado de falha com caminho de recuperação.
 *
 * A mensagem é neutra e não culpa o jogador; o detalhe técnico fica nos logs.
 */
@Composable
fun ConteudoDeFalha(
    onTentarNovamente: () -> Unit,
    modifier: Modifier = Modifier,
    mensagem: String = stringResource(R.string.comum_falha_conteudo),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = mensagem,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        BotaoPrincipal(
            texto = stringResource(R.string.comum_tentar_novamente),
            onClick = onTentarNovamente,
            modifier = Modifier.padding(top = 24.dp),
        )
    }
}

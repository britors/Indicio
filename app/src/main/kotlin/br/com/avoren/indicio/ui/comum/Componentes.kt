package br.com.avoren.indicio.ui.comum

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.R
import br.com.avoren.indicio.ui.tema.AlturaMinimaBotao
import br.com.avoren.indicio.ui.tema.BordaSuave
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio
import br.com.avoren.indicio.ui.tema.FormasIndicio

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
            .heightIn(min = AlturaMinimaBotao)
            .semantics(mergeDescendants = true) {
                descricaoAcessivel?.let { contentDescription = it }
            },
        shape = FormasIndicio.pilula,
        contentPadding = ButtonDefaults.ContentPadding,
    ) {
        Text(
            text = texto,
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
        shape = FormasIndicio.pilula,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
    onVoltar: (() -> Unit)? = null,
    acao: (@Composable () -> Unit)? = null,
) {
    Column {
        TopAppBar(
            navigationIcon = {
                onVoltar?.let {
                    BotaoIcone(
                        textoAcessivel = stringResource(R.string.comum_voltar),
                        onClick = it,
                        modifier = Modifier.padding(start = EspacamentoIndicio.minimo),
                    ) {
                        Text(text = "←", style = MaterialTheme.typography.titleMedium)
                    }
                }
            },
            title = {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics { heading() },
                )
            },
            actions = { acao?.invoke() },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = modifier,
        )
        HorizontalDivider(color = BordaSuave)
    }
}

/** Ação quadrada usada nas barras, com alvo de toque confortável. */
@Composable
fun BotaoIcone(
    textoAcessivel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    conteudo: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = FormasIndicio.controle,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .size(AlturaMinimaBotao)
            .semantics(mergeDescendants = true) { contentDescription = textoAcessivel },
    ) {
        Box(contentAlignment = Alignment.Center) { conteudo() }
    }
}

/** Marca gráfica do Indício, desenhada localmente e sem dependência de ícones. */
@Composable
fun MarcaIndicio(modifier: Modifier = Modifier) {
    val dourado = MaterialTheme.colorScheme.secondary
    Box(
        modifier = modifier
            .size(58.dp)
            .clip(FormasIndicio.controle)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(36.dp)) {
            drawCircle(
                color = dourado,
                radius = size.minDimension * 0.31f,
                center = Offset(size.width * 0.45f, size.height * 0.42f),
                style = Stroke(width = size.minDimension * 0.13f),
            )
            drawLine(
                color = dourado,
                start = Offset(size.width * 0.64f, size.height * 0.61f),
                end = Offset(size.width * 0.86f, size.height * 0.84f),
                strokeWidth = size.minDimension * 0.13f,
                cap = StrokeCap.Round,
            )
        }
    }
}

/** Cabeçalho editorial das telas que não usam barra de navegação. */
@Composable
fun TituloDeTela(
    texto: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = texto,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.semantics { heading() },
    )
}

/** Botão claro para ações colocadas sobre painéis escuros. */
@Composable
fun BotaoSobreDestaque(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    descricaoAcessivel: String? = null,
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        shape = FormasIndicio.controle,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AlturaMinimaBotao)
            .semantics(mergeDescendants = true) {
                descricaoAcessivel?.let { contentDescription = it }
            },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = EspacamentoIndicio.pequeno),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = texto, style = MaterialTheme.typography.labelLarge)
            Text(text = "→", style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Rótulo curto em caixa alta, recorrente em cartões e estados. */
@Composable
fun RotuloEditorial(
    texto: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = texto.uppercase(),
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize * 0.72f,
        ),
        color = MaterialTheme.colorScheme.secondary,
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
            .padding(EspacamentoIndicio.extraGrande),
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
            modifier = Modifier.padding(top = EspacamentoIndicio.extraGrande),
        )
    }
}

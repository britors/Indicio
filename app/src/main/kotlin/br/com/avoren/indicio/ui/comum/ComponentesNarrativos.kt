package br.com.avoren.indicio.ui.comum

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.R
import br.com.avoren.indicio.domain.model.caso.Imagem
import br.com.avoren.indicio.domain.narracao.EstadoNarracao
import br.com.avoren.indicio.ui.tema.AlturaMinimaBotao
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio
import br.com.avoren.indicio.ui.tema.FormasIndicio

/** Arte local compartilhada pelas cenas e por suas capas no catálogo. */
@Composable
internal fun IlustracaoNarrativa(
    imagem: Imagem,
    proporcao: Float,
    modifier: Modifier = Modifier,
) {
    val recursos = LocalResources.current
    val pacote = LocalContext.current.packageName

    @SuppressLint("DiscouragedApi")
    val idDoRecurso = remember(imagem.recurso, recursos) {
        recursos.getIdentifier(imagem.recurso, "drawable", pacote)
    }
    val forma = RoundedCornerShape(10.dp)

    if (idDoRecurso != 0) {
        Image(
            painter = painterResource(idDoRecurso),
            contentDescription = imagem.descricaoAcessivel,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .aspectRatio(proporcao)
                .clip(forma)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), forma),
        )
    } else {
        Box(
            modifier = modifier
                .heightIn(min = ALTURA_MINIMA_SEM_ARTE)
                .clip(forma)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f), forma)
                .semantics { contentDescription = imagem.descricaoAcessivel },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = imagem.descricaoAcessivel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

/** Ação de ouvir/parar usada em qualquer conteúdo narrativo. */
@Composable
internal fun ControleDeNarracao(
    estado: EstadoNarracao,
    onAlternar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (estado) {
        EstadoNarracao.PREPARANDO -> Unit

        EstadoNarracao.INDISPONIVEL -> Text(
            text = stringResource(R.string.historia_sem_voz),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.fillMaxWidth(),
        )

        EstadoNarracao.PRONTO, EstadoNarracao.FALANDO -> {
            val falando = estado == EstadoNarracao.FALANDO
            val rotulo = stringResource(
                if (falando) R.string.historia_parar_narracao else R.string.historia_ouvir,
            )
            val situacao = stringResource(
                if (falando) R.string.historia_narrando else R.string.historia_ouvir,
            )

            Surface(
                onClick = onAlternar,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = AlturaMinimaBotao)
                    .semantics {
                        stateDescription = situacao
                        liveRegion = LiveRegionMode.Polite
                        onClick(label = rotulo, action = null)
                    },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (falando) IconesIndicio.fechar else IconesIndicio.continuar,
                                contentDescription = null,
                            )
                        }
                    }
                    Text(text = rotulo, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

/** Texto em duas linhas que pode ser expandido sem sair do card. */
@Composable
internal fun PainelDeTextoRecolhivel(
    chave: String,
    texto: String,
    modifier: Modifier = Modifier,
) {
    var expandido by rememberSaveable(chave) { mutableStateOf(false) }
    var possuiMaisDeDuasLinhas by remember(chave, texto) { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = FormasIndicio.pequena,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)),
    ) {
        Column {
            if (possuiMaisDeDuasLinhas) {
                val rotulo = stringResource(
                    if (expandido) R.string.historia_recolher_texto else R.string.historia_expandir_texto,
                )
                val situacao = stringResource(
                    if (expandido) R.string.historia_texto_expandido else R.string.historia_texto_recolhido,
                )

                Surface(
                    onClick = { expandido = !expandido },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = AlturaMinimaBotao)
                        .semantics {
                            role = Role.Button
                            stateDescription = situacao
                        },
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = EspacamentoIndicio.padrao),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = rotulo, style = MaterialTheme.typography.labelLarge)
                        Icon(
                            imageVector = IconesIndicio.avancar,
                            contentDescription = null,
                            modifier = Modifier.rotate(if (expandido) -90f else 90f),
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
            }

            Text(
                text = texto,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = if (expandido) Int.MAX_VALUE else DUAS_LINHAS,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { resultado ->
                    if (!expandido) possuiMaisDeDuasLinhas = resultado.hasVisualOverflow
                },
                modifier = Modifier
                    .padding(EspacamentoIndicio.padrao)
                    .semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
    }
}

private const val DUAS_LINHAS = 2
private val ALTURA_MINIMA_SEM_ARTE = 180.dp

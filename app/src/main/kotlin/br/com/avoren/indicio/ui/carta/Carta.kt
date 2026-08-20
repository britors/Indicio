package br.com.avoren.indicio.ui.carta

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.R
import br.com.avoren.indicio.ui.tema.ElevacaoIndicio
import br.com.avoren.indicio.ui.tema.FormasIndicio
import br.com.avoren.indicio.ui.tema.LocalReducaoDeMovimentos

/** Proporção de carta de baralho, em retrato. */
internal const val PROPORCAO_DA_CARTA = 2f / 3f

internal val FormaDaCarta = FormasIndicio.cartao

/**
 * Frente editorial comum das cartas, capaz de crescer com texto ampliado.
 */
@Composable
internal fun Carta(
    modifier: Modifier = Modifier,
    conteudo: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        shape = FormaDaCarta,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        shadowElevation = ElevacaoIndicio.cartao,
        modifier = modifier,
    ) {
        Column(content = conteudo)
    }
}

/**
 * Verso comum a todas as cartas.
 *
 * Não tem semântica: é decoração durante a virada, e o conteúdo real já está
 * exposto pela frente da carta.
 */
@Composable
internal fun VersoDaCarta(modifier: Modifier = Modifier) {
    Surface(
        shape = FormaDaCarta,
        color = MaterialTheme.colorScheme.primary,
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.secondary),
        shadowElevation = ElevacaoIndicio.cartao,
        modifier = modifier.clearAndSetSemantics { },
    ) {
        Image(
            painter = painterResource(R.drawable.carta_verso),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * Distribui uma carta nova: ela chega virada para baixo e é revelada.
 *
 * A frente permanece composta durante toda a virada, por dois motivos: a carta
 * mantém a altura estável, e o leitor de tela anuncia o texto da cena assim que
 * ele existe, sem esperar a animação terminar.
 *
 * Com "reduzir movimentos", a carta nova simplesmente aparece.
 */
@Composable
internal fun CartaDistribuida(
    chave: Any,
    modifier: Modifier = Modifier,
    frente: @Composable () -> Unit,
) {
    // Lido por `rememberUpdatedState`, e não como chave do efeito: mudar a
    // preferência não é cena nova, e não pode redistribuir a carta na tela.
    val semMovimento by rememberUpdatedState(LocalReducaoDeMovimentos.current)
    val angulo = remember { Animatable(0f) }
    var chaveExibida by remember { mutableStateOf(chave) }
    var jaDistribuiu by remember { mutableStateOf(false) }

    // Avaliado na composição, não em efeito: a carta nova precisa nascer
    // coberta. Se a cobertura dependesse do `LaunchedEffect`, haveria uma
    // janela — visível no emulador — em que o texto da cena nova já estava
    // legível antes de a carta virar, estragando a revelação.
    val recemChegada = chaveExibida != chave

    LaunchedEffect(chave) {
        if (!jaDistribuiu) {
            jaDistribuiu = true
            chaveExibida = chave
            return@LaunchedEffect
        }
        if (semMovimento) {
            chaveExibida = chave
            angulo.snapTo(0f)
            return@LaunchedEffect
        }
        // A ordem importa: o ângulo cobre antes de `recemChegada` ser desligado,
        // para que não sobre nenhum quadro com a frente à mostra.
        angulo.snapTo(MEIA_VOLTA)
        chaveExibida = chave
        angulo.animateTo(0f, tween(DURACAO_DA_VIRADA))
    }

    val versoPeloAngulo by remember { derivedStateOf { angulo.value > QUARTO_DE_VOLTA } }
    val mostrandoVerso = versoPeloAngulo || (recemChegada && !semMovimento)

    Box(
        modifier = modifier.graphicsLayer {
            rotationY = angulo.value
            cameraDistance = DISTANCIA_DA_CAMERA * density
        },
    ) {
        frente()

        if (mostrandoVerso) {
            // Contra-rotação: sem ela, o verso apareceria espelhado.
            VersoDaCarta(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { rotationY = MEIA_VOLTA },
            )
        }
    }
}

private const val MEIA_VOLTA = 180f
private const val QUARTO_DE_VOLTA = 90f
private const val DISTANCIA_DA_CAMERA = 16f
private const val DURACAO_DA_VIRADA = 460

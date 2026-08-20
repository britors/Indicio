package br.com.avoren.indicio.ui.historia

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.R
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Escolha
import br.com.avoren.indicio.domain.model.caso.Imagem
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.narracao.EstadoNarracao
import br.com.avoren.indicio.ui.comum.BarraDoTopo
import br.com.avoren.indicio.ui.comum.BotaoPrincipal
import br.com.avoren.indicio.ui.comum.BotaoSecundario
import br.com.avoren.indicio.ui.tema.LocalReducaoDeMovimentos
import br.com.avoren.indicio.ui.tema.TemaIndicio

/**
 * Tela narrativa: ilustração, trecho curto, controle de narração e as duas
 * escolhas grandes.
 */
@Composable
internal fun ConteudoHistoria(
    estado: EstadoHistoria.EmCurso,
    estadoNarracao: EstadoNarracao,
    onEscolher: (String) -> Unit,
    onAlternarNarracao: () -> Unit,
    onPausar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val semMovimento = LocalReducaoDeMovimentos.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { BarraDoTopo(titulo = estado.tituloCaso) },
    ) { espacamento ->
        // A transição entre cenas é um esmaecimento curto, sem deslocamento.
        // Com redução de movimentos, a troca é imediata.
        AnimatedContent(
            targetState = estado,
            transitionSpec = {
                val duracao = if (semMovimento) 0 else DURACAO_DA_TRANSICAO
                fadeIn(tween(duracao)) togetherWith fadeOut(tween(duracao))
            },
            label = "cena",
        ) { atual ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(espacamento)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
            ) {
                IlustracaoDaCena(imagem = atual.cena.imagem)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = atual.cena.texto,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )

                Spacer(modifier = Modifier.height(24.dp))

                ControleDeNarracao(
                    estado = estadoNarracao,
                    onAlternar = onAlternarNarracao,
                )

                Spacer(modifier = Modifier.height(32.dp))

                atual.cena.escolhas.forEach { escolha ->
                    BotaoPrincipal(
                        texto = escolha.texto,
                        onClick = { onEscolher(escolha.id) },
                        habilitado = atual.escolhasHabilitadas,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                PainelDePistas(pistas = atual.pistas)
                Spacer(modifier = Modifier.height(24.dp))

                BotaoSecundario(
                    texto = stringResource(R.string.historia_pausar),
                    onClick = onPausar,
                )
            }
        }
    }
}

private const val DURACAO_DA_TRANSICAO = 220

/**
 * Pistas acumuladas.
 *
 * Sempre textual: nenhuma pista é comunicada só por ícone ou cor.
 */
@Composable
internal fun PainelDePistas(
    pistas: List<Pista>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        Text(
            text = stringResource(R.string.historia_pistas),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.semantics { heading() },
        )

        if (pistas.isEmpty()) {
            Text(
                text = stringResource(R.string.historia_pistas_nenhuma),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            pistas.forEach { pista ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = pista.titulo, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = pista.descricao,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviaHistoria() {
    TemaIndicio {
        ConteudoHistoria(
            estado = EstadoHistoria.EmCurso(
                tituloCaso = "O Mistério da Taça Desaparecida",
                cena = Cena(
                    id = "vitrine",
                    texto = "A vitrine está intacta. O pedestal, porém, não está centralizado.",
                    imagem = Imagem("cena_vitrine", "Vitrine de vidro sobre um pedestal deslocado."),
                    escolhas = listOf(
                        Escolha("a", "Examinar o chão", "po"),
                        Escolha("b", "Olhar o forro", "forro"),
                    ),
                ),
                pistas = listOf(
                    Pista("pedestal", "O pedestal fora do lugar", "Está à esquerda da marca no piso."),
                ),
            ),
            estadoNarracao = EstadoNarracao.PRONTO,
            onEscolher = {},
            onAlternarNarracao = {},
            onPausar = {},
        )
    }
}

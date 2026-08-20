package br.com.avoren.indicio.ui.historia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.ui.tema.BordaSuave
import br.com.avoren.indicio.R
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Escolha
import br.com.avoren.indicio.domain.model.caso.Imagem
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.narracao.EstadoNarracao
import br.com.avoren.indicio.ui.carta.Carta
import br.com.avoren.indicio.ui.carta.CartaDeEscolha
import br.com.avoren.indicio.ui.carta.CartaDistribuida
import br.com.avoren.indicio.ui.carta.PROPORCAO_DA_CARTA
import br.com.avoren.indicio.ui.comum.BarraDoTopo
import br.com.avoren.indicio.ui.comum.BotaoSecundario
import br.com.avoren.indicio.ui.tema.TemaIndicio

/**
 * Tela narrativa, no formato de jogo de cartas.
 *
 * Cada cena é uma carta distribuída: chega virada para baixo e é revelada. As
 * escolhas são as cartas que o jogador pode jogar em seguida.
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
    val rolagem = rememberScrollState()

    // Sem isto, a carta nova é distribuída acima da dobra: quem acabou de jogar
    // continua olhando o fim da tela e não vê a carta ser virada. Reposiciona
    // de imediato, em vez de rolar animado, para não comer o começo da virada.
    LaunchedEffect(estado.cena.id) {
        rolagem.scrollTo(0)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { BarraDoTopo(titulo = estado.tituloCaso) },
    ) { espacamento ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
                .verticalScroll(rolagem)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            // A identidade da cena é a chave da virada: cena nova, carta nova.
            CartaDistribuida(chave = estado.cena.id, modifier = Modifier.fillMaxWidth()) {
                Carta {
                    // A arte é limitada pela largura da carta E pela altura da
                    // janela. Só pela largura, em paisagem a carta fica larga
                    // demais e a arte, em retrato, sozinha ocupa mais que a tela
                    // inteira — o texto e as escolhas somem abaixo da dobra.
                    val alturaDaJanela = with(LocalDensity.current) {
                        LocalWindowInfo.current.containerSize.height.toDp()
                    }

                    BoxWithConstraints(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        val pelaLargura = maxWidth * FRACAO_DA_ARTE
                        val pelaAltura = alturaDaJanela * FRACAO_DA_ALTURA_DA_ARTE * PROPORCAO_DA_CARTA

                        IlustracaoDaCena(
                            imagem = estado.cena.imagem,
                            modifier = Modifier.width(minOf(pelaLargura, pelaAltura)),
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = estado.cena.texto,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ControleDeNarracao(
                        estado = estadoNarracao,
                        onAlternar = onAlternarNarracao,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.historia_escolhas),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() },
            )

            Spacer(modifier = Modifier.height(14.dp))

            estado.cena.escolhas.forEach { escolha ->
                CartaDeEscolha(
                    texto = escolha.texto,
                    onClick = { onEscolher(escolha.id) },
                    habilitado = estado.escolhasHabilitadas,
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.height(18.dp))
            PainelDePistas(pistas = estado.pistas)
            Spacer(modifier = Modifier.height(24.dp))

            BotaoSecundario(
                texto = stringResource(R.string.historia_pausar),
                onClick = onPausar,
            )
        }
    }
}

/**
 * Quanto da largura da carta a arte ocupa.
 *
 * Deixa margem para a moldura respirar e impede que a ilustração, em retrato,
 * empurre o texto da cena para fora da primeira tela.
 */
private const val FRACAO_DA_ARTE = 0.62f

/**
 * Teto da arte em relação à altura da janela.
 *
 * Em retrato não chega a valer — a regra de largura é sempre mais apertada. É
 * em paisagem, e em tela larga, que ele impede a arte de empurrar o texto e as
 * escolhas para fora da vista.
 */
private const val FRACAO_DA_ALTURA_DA_ARTE = 0.5f

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
        HorizontalDivider(color = BordaSuave)

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

package br.com.avoren.indicio.ui.historia

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import br.com.avoren.indicio.R
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Escolha
import br.com.avoren.indicio.domain.model.caso.Imagem
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.narracao.EstadoNarracao
import br.com.avoren.indicio.ui.carta.Carta
import br.com.avoren.indicio.ui.carta.CartaDeEscolha
import br.com.avoren.indicio.ui.carta.CartaDistribuida
import br.com.avoren.indicio.ui.comum.BarraDoTopo
import br.com.avoren.indicio.ui.comum.BotaoIcone
import br.com.avoren.indicio.ui.comum.BotaoSecundario
import br.com.avoren.indicio.ui.comum.RotuloEditorial
import br.com.avoren.indicio.ui.tema.ElevacaoIndicio
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio
import br.com.avoren.indicio.ui.tema.FormasIndicio
import br.com.avoren.indicio.ui.tema.TemaIndicio

/** Tela narrativa em que a cena e as escolhas formam um jogo de cartas. */
@Composable
internal fun ConteudoHistoria(
    estado: EstadoHistoria.EmCurso,
    estadoNarracao: EstadoNarracao,
    onEscolher: (String) -> Unit,
    onAlternarNarracao: () -> Unit,
    onPausar: () -> Unit,
    modifier: Modifier = Modifier,
    onAbrirEtapas: () -> Unit = {},
    onAbrirCaderno: () -> Unit = {},
) {
    val rolagem = rememberScrollState()

    LaunchedEffect(estado.cena.id) { rolagem.scrollTo(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraDoTopo(
                titulo = estado.tituloCaso,
                acao = {
                    BotaoIcone(
                        textoAcessivel = stringResource(R.string.historia_pausar),
                        onClick = onPausar,
                        modifier = Modifier.padding(end = EspacamentoIndicio.minimo),
                    ) {
                        Text(text = "Ⅱ", style = MaterialTheme.typography.titleMedium)
                    }
                },
            )
        },
    ) { espacamento ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
                .verticalScroll(rolagem)
                .padding(
                    horizontal = EspacamentoIndicio.margemDaTela,
                    vertical = EspacamentoIndicio.padrao,
                ),
        ) {
            CartaDistribuida(chave = estado.cena.id, modifier = Modifier.fillMaxWidth()) {
                CartaDaCena(
                    cena = estado.cena,
                    estadoNarracao = estadoNarracao,
                    onAlternarNarracao = onAlternarNarracao,
                )
            }

            Spacer(modifier = Modifier.height(EspacamentoIndicio.grande))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = stringResource(R.string.historia_escolhas),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() },
                )
                Text(
                    text = stringResource(R.string.historia_escolhas_apoio),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = EspacamentoIndicio.medio),
                )
            }

            Spacer(modifier = Modifier.height(EspacamentoIndicio.medio))

            estado.cena.escolhas.forEachIndexed { indice, escolha ->
                CartaDeEscolha(
                    numero = indice + 1,
                    texto = escolha.texto,
                    onClick = { onEscolher(escolha.id) },
                    habilitado = estado.escolhasHabilitadas,
                )
                Spacer(modifier = Modifier.height(EspacamentoIndicio.pequeno))
            }

            Spacer(modifier = Modifier.height(EspacamentoIndicio.pequeno))
            if (estado.temInvestigacaoLonga) {
                BotaoSecundario(
                    texto = stringResource(R.string.historia_ver_etapas),
                    onClick = onAbrirEtapas,
                )
                Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))
            }
            PainelDePistas(
                pistas = estado.pistas,
                onAbrirCaderno = onAbrirCaderno,
            )
            Spacer(modifier = Modifier.height(EspacamentoIndicio.extraGrande))
        }
    }
}

@Composable
private fun CartaDaCena(
    cena: Cena,
    estadoNarracao: EstadoNarracao,
    onAlternarNarracao: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Carta(modifier = modifier) {
        Box {
            val alturaDaJanela = with(LocalDensity.current) {
                LocalWindowInfo.current.containerSize.height.toDp()
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                val larguraPelaAltura = alturaDaJanela * FRACAO_DA_ALTURA_DA_ARTE * PROPORCAO_ARTE_CENA
                IlustracaoDaCena(
                    imagem = cena.imagem,
                    proporcao = PROPORCAO_ARTE_CENA,
                    modifier = Modifier.width(minOf(maxWidth, larguraPelaAltura)),
                )
            }

            if (cena.pista != null) {
                Surface(
                    modifier = Modifier.padding(EspacamentoIndicio.medio),
                    shape = FormasIndicio.pequena,
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    RotuloEditorial(
                        texto = stringResource(R.string.historia_nova_pista),
                        modifier = Modifier.padding(
                            horizontal = EspacamentoIndicio.medio,
                            vertical = EspacamentoIndicio.pequeno,
                        ),
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(EspacamentoIndicio.grande),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
        ) {
            Text(
                text = cena.texto,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )

            ControleDeNarracao(
                estado = estadoNarracao,
                onAlternar = onAlternarNarracao,
            )
        }
    }
}

/** Caderno resumido, seguido do conteúdo textual que já foi descoberto. */
@Composable
internal fun PainelDePistas(
    pistas: List<Pista>,
    modifier: Modifier = Modifier,
    onAbrirCaderno: () -> Unit = {},
) {
    Surface(
        onClick = onAbrirCaderno,
        modifier = modifier.fillMaxWidth(),
        shape = FormasIndicio.controle,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        shadowElevation = ElevacaoIndicio.controle,
    ) {
        Column(
            modifier = Modifier.padding(EspacamentoIndicio.padrao),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.historia_caderno),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = stringResource(R.string.historia_abrir_caderno),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            if (pistas.isEmpty()) {
                Text(
                    text = stringResource(R.string.historia_pistas_nenhuma),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                pistas.forEach { pista ->
                    Column(verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.minimo)) {
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
}

private const val PROPORCAO_ARTE_CENA = 16f / 9f
private const val FRACAO_DA_ALTURA_DA_ARTE = 0.46f

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
                    pista = Pista("pedestal", "O pedestal fora do lugar", "Está à esquerda da marca."),
                    escolhas = listOf(
                        Escolha("a", "Examinar o chão em volta do pedestal", "po"),
                        Escolha("b", "Olhar para o forro, acima da vitrine", "forro"),
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
            onAbrirEtapas = {},
            onAbrirCaderno = {},
        )
    }
}

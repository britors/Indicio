package br.com.avoren.indicio.ui.historia

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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.R
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Imagem
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.ui.comum.BarraDoTopo
import br.com.avoren.indicio.ui.comum.BotaoPrincipal
import br.com.avoren.indicio.ui.comum.BotaoSecundario
import br.com.avoren.indicio.ui.tema.TemaIndicio

/**
 * Tela narrativa: o trecho da história e as duas escolhas.
 *
 * A ilustração da cena e a narração por voz chegam com a issue da experiência
 * jogável; por ora a descrição acessível da imagem é apresentada como texto,
 * o que mantém o caso inteiramente jogável e não esconde o que falta.
 */
@Composable
internal fun ConteudoHistoria(
    estado: EstadoHistoria.EmCurso,
    onEscolher: (String) -> Unit,
    onPausar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { BarraDoTopo(titulo = estado.tituloCaso) },
    ) { espacamento ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            Text(
                text = estado.cena.imagem.descricaoAcessivel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = estado.cena.texto,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )

            Spacer(modifier = Modifier.height(32.dp))

            estado.cena.escolhas.forEach { escolha ->
                BotaoPrincipal(
                    texto = escolha.texto,
                    onClick = { onEscolher(escolha.id) },
                    habilitado = estado.escolhasHabilitadas,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
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
                    escolhas = emptyList(),
                ),
                pistas = listOf(
                    Pista("pedestal", "O pedestal fora do lugar", "Está à esquerda da marca no piso."),
                ),
            ),
            onEscolher = {},
            onPausar = {},
        )
    }
}

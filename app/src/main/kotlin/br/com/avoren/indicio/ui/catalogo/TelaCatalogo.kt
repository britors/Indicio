package br.com.avoren.indicio.ui.catalogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.avoren.indicio.R
import br.com.avoren.indicio.di.ContainerAplicacao
import br.com.avoren.indicio.domain.model.caso.Categoria
import br.com.avoren.indicio.domain.model.caso.ResumoCaso
import br.com.avoren.indicio.ui.comum.BarraDoTopo
import br.com.avoren.indicio.ui.comum.BotaoPrincipal
import br.com.avoren.indicio.ui.comum.ConteudoCarregando
import br.com.avoren.indicio.ui.comum.ConteudoDeFalha
import br.com.avoren.indicio.ui.tema.TemaIndicio

/**
 * Catálogo agrupado pelas cinco categorias previstas.
 */
@Composable
fun TelaCatalogo(
    container: ContainerAplicacao,
    onAbrirCaso: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CatalogoViewModel = viewModel(factory = CatalogoViewModel.fabrica(container)),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    ConteudoCatalogo(
        estado = estado,
        onAbrirCaso = onAbrirCaso,
        onTentarNovamente = viewModel::carregar,
        modifier = modifier,
    )
}

@Composable
internal fun ConteudoCatalogo(
    estado: EstadoCatalogo,
    onAbrirCaso: (String) -> Unit,
    onTentarNovamente: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { BarraDoTopo(titulo = stringResource(R.string.catalogo_titulo)) },
    ) { espacamento ->
        when (estado) {
            is EstadoCatalogo.Carregando -> ConteudoCarregando(Modifier.padding(espacamento))

            is EstadoCatalogo.Falha -> ConteudoDeFalha(
                onTentarNovamente = onTentarNovamente,
                modifier = Modifier.padding(espacamento),
            )

            is EstadoCatalogo.Conteudo -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(espacamento),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                estado.grupos.forEach { grupo ->
                    item(key = "categoria-${grupo.categoria.name}") {
                        Text(
                            text = grupo.categoria.rotulo,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.semantics { heading() },
                        )
                    }

                    if (grupo.casos.isEmpty()) {
                        item(key = "vazia-${grupo.categoria.name}") {
                            Text(
                                text = stringResource(R.string.catalogo_categoria_vazia),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        items(grupo.casos, key = { it.id }) { resumo ->
                            CartaoDeCaso(resumo = resumo, onAbrir = { onAbrirCaso(resumo.id) })
                        }
                    }
                }
            }
        }
    }
}

/**
 * Casos futuros aparecem com a mesma dignidade dos disponíveis, mas sem botão e
 * com um rótulo textual — a diferença nunca é comunicada apenas por cor.
 */
@Composable
private fun CartaoDeCaso(
    resumo: ResumoCaso,
    onAbrir: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = resumo.titulo,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = resumo.sinopse,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (resumo.disponivel) {
                BotaoPrincipal(
                    texto = stringResource(R.string.catalogo_abrir, resumo.titulo),
                    onClick = onAbrir,
                )
            } else {
                Text(
                    text = stringResource(R.string.catalogo_em_preparacao),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviaCatalogo() {
    TemaIndicio {
        ConteudoCatalogo(
            estado = EstadoCatalogo.Conteudo(
                listOf(
                    GrupoDeCategoria(
                        Categoria.FUTEBOL,
                        listOf(
                            ResumoCaso(
                                id = "taca-desaparecida",
                                titulo = "O Mistério da Taça Desaparecida",
                                sinopse = "Uma taça some horas antes da exposição.",
                                categoria = Categoria.FUTEBOL,
                                arquivo = "casos/taca-desaparecida.json",
                                disponivel = true,
                            ),
                        ),
                    ),
                    GrupoDeCategoria(Categoria.FAROESTE, emptyList()),
                ),
            ),
            onAbrirCaso = {},
            onTentarNovamente = {},
        )
    }
}

package br.com.avoren.indicio.ui.catalogo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.avoren.indicio.R
import br.com.avoren.indicio.domain.caso.RepositorioCasos
import br.com.avoren.indicio.domain.model.caso.Categoria
import br.com.avoren.indicio.domain.model.caso.ResumoCaso
import br.com.avoren.indicio.ui.comum.BarraDoTopo
import br.com.avoren.indicio.ui.comum.BotaoPrincipal
import br.com.avoren.indicio.ui.comum.ConteudoCarregando
import br.com.avoren.indicio.ui.comum.ConteudoDeFalha
import br.com.avoren.indicio.ui.comum.MarcaIndicio
import br.com.avoren.indicio.ui.comum.RotuloEditorial
import br.com.avoren.indicio.ui.tema.ElevacaoIndicio
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio
import br.com.avoren.indicio.ui.tema.FormasIndicio
import br.com.avoren.indicio.ui.tema.TemaIndicio

/** Catálogo visualmente filtrável, sem esconder as categorias planejadas. */
@Composable
fun TelaCatalogo(
    repositorioCasos: RepositorioCasos,
    onAbrirCaso: (String) -> Unit,
    modifier: Modifier = Modifier,
    onVoltar: (() -> Unit)? = null,
    viewModel: CatalogoViewModel = viewModel(factory = CatalogoViewModel.fabrica(repositorioCasos)),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    ConteudoCatalogo(
        estado = estado,
        onAbrirCaso = onAbrirCaso,
        onTentarNovamente = viewModel::carregar,
        onVoltar = onVoltar,
        modifier = modifier,
    )
}

@Composable
internal fun ConteudoCatalogo(
    estado: EstadoCatalogo,
    onAbrirCaso: (String) -> Unit,
    onTentarNovamente: () -> Unit,
    modifier: Modifier = Modifier,
    onVoltar: (() -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraDoTopo(
                titulo = stringResource(R.string.catalogo_titulo),
                onVoltar = onVoltar,
            )
        },
    ) { espacamento ->
        when (estado) {
            is EstadoCatalogo.Carregando -> ConteudoCarregando(Modifier.padding(espacamento))

            is EstadoCatalogo.Falha -> ConteudoDeFalha(
                onTentarNovamente = onTentarNovamente,
                modifier = Modifier.padding(espacamento),
            )

            is EstadoCatalogo.Conteudo -> CatalogoCarregado(
                grupos = estado.grupos,
                onAbrirCaso = onAbrirCaso,
                modifier = Modifier.padding(espacamento),
            )
        }
    }
}

internal const val TAG_LISTA_CATALOGO = "catalogo-lista"

@Composable
private fun CatalogoCarregado(
    grupos: List<GrupoDeCategoria>,
    onAbrirCaso: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var categoriaSelecionada by remember { mutableStateOf<Categoria?>(null) }
    val casos = grupos.flatMap(GrupoDeCategoria::casos)
    val visiveis = casos.filter { categoriaSelecionada == null || it.categoria == categoriaSelecionada }
    val disponiveis = visiveis.filter(ResumoCaso::disponivel)
    val categoriasEmPreparacao = grupos
        .filter { grupo -> grupo.casos.isEmpty() || grupo.casos.any { !it.disponivel } }
        .map(GrupoDeCategoria::categoria)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag(TAG_LISTA_CATALOGO),
        contentPadding = PaddingValues(vertical = EspacamentoIndicio.extraGrande),
        verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.grande),
    ) {
        item {
            Column(
                modifier = Modifier.padding(horizontal = EspacamentoIndicio.margemDaTela),
                verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.pequeno),
            ) {
                Text(
                    text = stringResource(R.string.catalogo_chamada),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = stringResource(R.string.catalogo_apoio),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = EspacamentoIndicio.margemDaTela),
                horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.pequeno),
            ) {
                item {
                    FiltroCategoria(
                        rotulo = stringResource(R.string.catalogo_todos),
                        selecionado = categoriaSelecionada == null,
                        onClick = { categoriaSelecionada = null },
                    )
                }
                items(Categoria.entries, key = Categoria::name) { categoria ->
                    FiltroCategoria(
                        rotulo = categoria.rotulo,
                        selecionado = categoriaSelecionada == categoria,
                        onClick = { categoriaSelecionada = categoria },
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = EspacamentoIndicio.margemDaTela),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.catalogo_disponivel_agora),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.catalogo_contagem,
                        disponiveis.size,
                        disponiveis.size,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (disponiveis.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.catalogo_categoria_vazia),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = EspacamentoIndicio.margemDaTela),
                )
            }
        } else {
            items(disponiveis, key = ResumoCaso::id) { resumo ->
                CartaoDeCaso(
                    resumo = resumo,
                    onAbrir = { onAbrirCaso(resumo.id) },
                    modifier = Modifier.padding(horizontal = EspacamentoIndicio.margemDaTela),
                )
            }
        }

        if (categoriasEmPreparacao.isNotEmpty()) {
            item {
                PainelEmPreparacao(
                    categorias = categoriasEmPreparacao,
                    modifier = Modifier.padding(horizontal = EspacamentoIndicio.margemDaTela),
                )
            }
        }
    }
}

@Composable
private fun FiltroCategoria(
    rotulo: String,
    selecionado: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = FormasIndicio.controle,
        color = if (selecionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (selecionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        border = if (selecionado) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = EspacamentoIndicio.padrao, vertical = EspacamentoIndicio.medio),
        )
    }
}

@Composable
private fun CartaoDeCaso(
    resumo: ResumoCaso,
    onAbrir: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = FormasIndicio.cartao,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
        shadowElevation = ElevacaoIndicio.cartao,
    ) {
        Row(
            modifier = Modifier.padding(EspacamentoIndicio.medio),
            horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.padrao),
        ) {
            Box(
                modifier = Modifier
                    .width(102.dp)
                    .height(154.dp)
                    .clip(FormasIndicio.controle)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                MarcaIndicio(modifier = Modifier.size(48.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.pequeno),
            ) {
                RotuloEditorial(texto = resumo.categoria.rotulo)
                Text(
                    text = resumo.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = resumo.sinopse,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                BotaoPrincipal(
                    texto = stringResource(R.string.catalogo_abrir_curto),
                    descricaoAcessivel = stringResource(R.string.catalogo_abrir, resumo.titulo),
                    onClick = onAbrir,
                )
            }
        }
    }
}

@Composable
private fun PainelEmPreparacao(
    categorias: List<Categoria>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = FormasIndicio.cartao,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(EspacamentoIndicio.padrao),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
        ) {
            RotuloEditorial(texto = stringResource(R.string.catalogo_em_preparacao_sem_previsao))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.pequeno)) {
                items(categorias, key = Categoria::name) { categoria ->
                    Surface(shape = FormasIndicio.pequena, color = MaterialTheme.colorScheme.surface) {
                        Text(
                            text = categoria.rotulo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(
                                horizontal = EspacamentoIndicio.medio,
                                vertical = EspacamentoIndicio.pequeno,
                            ),
                        )
                    }
                }
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
                                sinopse = "Uma taça desaparece de uma sala aparentemente trancada.",
                                categoria = Categoria.FUTEBOL,
                                disponivel = true,
                            ),
                        ),
                    ),
                    GrupoDeCategoria(Categoria.FAROESTE, emptyList()),
                ),
            ),
            onAbrirCaso = {},
            onTentarNovamente = {},
            onVoltar = {},
        )
    }
}

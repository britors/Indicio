package br.com.avoren.indicio.ui.catalogo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.avoren.indicio.R
import br.com.avoren.indicio.application.catalogo.CasoDoCatalogo
import br.com.avoren.indicio.application.catalogo.SituacaoCasoCatalogo
import br.com.avoren.indicio.domain.armazenamento.RepositorioProgresso
import br.com.avoren.indicio.domain.caso.RepositorioCasos
import br.com.avoren.indicio.domain.model.caso.Categoria
import br.com.avoren.indicio.domain.model.caso.ResumoCaso
import br.com.avoren.indicio.domain.narracao.EstadoNarracao
import br.com.avoren.indicio.domain.narracao.Narrador
import br.com.avoren.indicio.ui.comum.BarraDoTopo
import br.com.avoren.indicio.ui.comum.ControleDeNarracao
import br.com.avoren.indicio.ui.comum.IconesIndicio
import br.com.avoren.indicio.ui.comum.ConteudoCarregando
import br.com.avoren.indicio.ui.comum.ConteudoDeFalha
import br.com.avoren.indicio.ui.comum.IlustracaoNarrativa
import br.com.avoren.indicio.ui.comum.MarcaIndicio
import br.com.avoren.indicio.ui.comum.PainelDeTextoRecolhivel
import br.com.avoren.indicio.ui.comum.RotuloEditorial
import br.com.avoren.indicio.ui.tema.ElevacaoIndicio
import br.com.avoren.indicio.ui.tema.AlturaMinimaBotao
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio
import br.com.avoren.indicio.ui.tema.FormasIndicio
import br.com.avoren.indicio.ui.tema.TemaIndicio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Catálogo visualmente filtrável, sem esconder as categorias planejadas. */
@Composable
fun TelaCatalogo(
    repositorioCasos: RepositorioCasos,
    repositorioProgresso: RepositorioProgresso,
    criarNarrador: () -> Narrador,
    onAbrirCaso: (String) -> Unit,
    onCasoReiniciado: (String) -> Unit,
    modifier: Modifier = Modifier,
    onVoltar: (() -> Unit)? = null,
    viewModel: CatalogoViewModel = viewModel(
        factory = CatalogoViewModel.fabrica(repositorioCasos, repositorioProgresso),
    ),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val narrador = remember { criarNarrador() }
    val estadoNarracao by narrador.estado.collectAsStateWithLifecycle()
    var casoNarradoId by remember { mutableStateOf<String?>(null) }
    val proprietario = LocalLifecycleOwner.current

    LaunchedEffect(estadoNarracao) {
        if (estadoNarracao == EstadoNarracao.PRONTO) casoNarradoId = null
    }

    DisposableEffect(proprietario, narrador) {
        val observador = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_PAUSE) {
                narrador.parar()
                casoNarradoId = null
            }
        }
        proprietario.lifecycle.addObserver(observador)
        onDispose {
            proprietario.lifecycle.removeObserver(observador)
            narrador.encerrar()
        }
    }

    LaunchedEffect(viewModel, onCasoReiniciado) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                is EventoCatalogo.CasoReiniciado -> onCasoReiniciado(evento.casoId)
                is EventoCatalogo.FalhaAoReiniciar -> snackbarHostState.showSnackbar(evento.mensagem)
            }
        }
    }

    ConteudoCatalogo(
        estado = estado,
        onAbrirCaso = onAbrirCaso,
        onReiniciarCaso = viewModel::reiniciar,
        onTentarNovamente = viewModel::carregar,
        estadoNarracao = estadoNarracao,
        casoNarradoId = casoNarradoId,
        onAlternarNarracao = { resumo ->
            if (casoNarradoId == resumo.id && estadoNarracao == EstadoNarracao.FALANDO) {
                narrador.parar()
                casoNarradoId = null
            } else {
                casoNarradoId = resumo.id
                narrador.falar(resumo.sinopse)
            }
        },
        onVoltar = onVoltar,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
internal fun ConteudoCatalogo(
    estado: EstadoCatalogo,
    onAbrirCaso: (String) -> Unit,
    onReiniciarCaso: (String) -> Unit = {},
    onTentarNovamente: () -> Unit,
    modifier: Modifier = Modifier,
    onVoltar: (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    estadoNarracao: EstadoNarracao = EstadoNarracao.PREPARANDO,
    casoNarradoId: String? = null,
    onAlternarNarracao: (ResumoCaso) -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                onReiniciarCaso = onReiniciarCaso,
                estadoNarracao = estadoNarracao,
                casoNarradoId = casoNarradoId,
                onAlternarNarracao = onAlternarNarracao,
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
    onReiniciarCaso: (String) -> Unit,
    estadoNarracao: EstadoNarracao,
    casoNarradoId: String?,
    onAlternarNarracao: (ResumoCaso) -> Unit,
    modifier: Modifier = Modifier,
) {
    var categoriaSelecionada by remember { mutableStateOf<Categoria?>(null) }
    var casoParaReiniciar by remember { mutableStateOf<CasoDoCatalogo?>(null) }
    val casos = grupos.flatMap(GrupoDeCategoria::casos)
    val visiveis = casos.filter {
        categoriaSelecionada == null || it.resumo.categoria == categoriaSelecionada
    }
    val disponiveis = visiveis.filter { it.resumo.disponivel }
    val categoriasEmPreparacao = grupos
        .filter { grupo -> grupo.casos.isEmpty() || grupo.casos.any { !it.resumo.disponivel } }
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
                        icone = IconesIndicio.lista,
                        selecionado = categoriaSelecionada == null,
                        onClick = { categoriaSelecionada = null },
                    )
                }
                items(Categoria.entries, key = Categoria::name) { categoria ->
                    FiltroCategoria(
                        rotulo = categoria.rotulo,
                        icone = categoria.icone,
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
            items(disponiveis, key = { caso -> caso.resumo.id }) { caso ->
                CartaoDeCaso(
                    caso = caso,
                    onAbrir = { onAbrirCaso(caso.resumo.id) },
                    onReiniciar = { casoParaReiniciar = caso },
                    estadoNarracao = if (
                        estadoNarracao == EstadoNarracao.FALANDO && casoNarradoId != caso.resumo.id
                    ) {
                        EstadoNarracao.PRONTO
                    } else {
                        estadoNarracao
                    },
                    onAlternarNarracao = { onAlternarNarracao(caso.resumo) },
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

    casoParaReiniciar?.let { caso ->
        AlertDialog(
            onDismissRequest = { casoParaReiniciar = null },
            title = { Text(stringResource(R.string.catalogo_reiniciar_confirmar_titulo)) },
            text = {
                Text(
                    text = stringResource(
                        R.string.catalogo_reiniciar_confirmar_texto,
                        caso.resumo.titulo,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        casoParaReiniciar = null
                        onReiniciarCaso(caso.resumo.id)
                    },
                ) {
                    Icon(imageVector = IconesIndicio.reiniciar, contentDescription = null)
                    Text(
                        text = stringResource(R.string.catalogo_reiniciar_confirmar_sim),
                        modifier = Modifier.padding(start = EspacamentoIndicio.pequeno),
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { casoParaReiniciar = null }) {
                    Icon(imageVector = IconesIndicio.fechar, contentDescription = null)
                    Text(
                        text = stringResource(R.string.pausa_cancelar),
                        modifier = Modifier.padding(start = EspacamentoIndicio.pequeno),
                    )
                }
            },
        )
    }
}

@Composable
private fun FiltroCategoria(
    rotulo: String,
    icone: ImageVector,
    selecionado: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = FormasIndicio.controle,
        color = if (selecionado) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        contentColor = if (selecionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = EspacamentoIndicio.padrao,
                vertical = EspacamentoIndicio.medio,
            ),
            horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.pequeno),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = icone, contentDescription = null)
            Text(text = rotulo, style = MaterialTheme.typography.labelLarge)
        }
    }
}

private val Categoria.icone: ImageVector
    get() = when (this) {
        Categoria.FUTEBOL -> IconesIndicio.futebol
        Categoria.MISTERIOS_POLICIAIS -> IconesIndicio.pesquisar
        Categoria.FAROESTE -> IconesIndicio.local
        Categoria.ROMANCES_CLASSICOS -> IconesIndicio.romance
        Categoria.CULTURA_POPULAR_ANTIGA -> IconesIndicio.cultura
    }

@Composable
private fun CartaoDeCaso(
    caso: CasoDoCatalogo,
    onAbrir: () -> Unit,
    onReiniciar: () -> Unit,
    estadoNarracao: EstadoNarracao,
    onAlternarNarracao: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resumo = caso.resumo
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = FormasIndicio.cartao,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
        shadowElevation = ElevacaoIndicio.cartao,
    ) {
        Column {
            resumo.imagem?.let { imagem ->
                IlustracaoNarrativa(
                    imagem = imagem,
                    proporcao = PROPORCAO_CAPA,
                    modifier = Modifier.fillMaxWidth(),
                )
            } ?: Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = ALTURA_MINIMA_CAPA),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    MarcaIndicio(modifier = Modifier.size(58.dp))
                }
            }

            Column(
                modifier = Modifier.padding(EspacamentoIndicio.grande),
                verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
            ) {
                RotuloEditorial(texto = resumo.categoria.rotulo)
                Text(
                    text = resumo.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() },
                )

                ControleDeNarracao(
                    estado = estadoNarracao,
                    onAlternar = onAlternarNarracao,
                )

                PainelDeTextoRecolhivel(
                    chave = resumo.id,
                    texto = resumo.sinopse,
                )

                EstadoDoCaso(caso)
                Text(
                    text = caso.ultimoAcessoEm?.let { instante ->
                        stringResource(
                            R.string.catalogo_ultimo_acesso,
                            formatarDataEHora(instante),
                        )
                    } ?: stringResource(R.string.catalogo_ultimo_acesso_nunca),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                AcoesDoCaso(caso = caso, onAbrir = onAbrir, onReiniciar = onReiniciar)
            }
        }
    }
}

@Composable
private fun AcoesDoCaso(
    caso: CasoDoCatalogo,
    onAbrir: () -> Unit,
    onReiniciar: () -> Unit,
) {
    val titulo = caso.resumo.titulo
    when {
        caso.podeRetomar -> Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.pequeno),
        ) {
            BotaoDeAcaoDoCaso(
                texto = stringResource(R.string.catalogo_retomar),
                icone = IconesIndicio.continuar,
                descricaoAcessivel = stringResource(R.string.catalogo_retomar_caso, titulo),
                destaque = true,
                onClick = onAbrir,
                modifier = Modifier.weight(1f),
            )
            BotaoDeAcaoDoCaso(
                texto = stringResource(R.string.catalogo_reiniciar),
                icone = IconesIndicio.reiniciar,
                descricaoAcessivel = stringResource(R.string.catalogo_reiniciar_caso, titulo),
                destaque = false,
                onClick = onReiniciar,
                modifier = Modifier.weight(1f),
            )
        }
        caso.podeReiniciar -> BotaoDeAcaoDoCaso(
            texto = stringResource(R.string.catalogo_reiniciar),
            icone = IconesIndicio.reiniciar,
            descricaoAcessivel = stringResource(R.string.catalogo_reiniciar_caso, titulo),
            destaque = true,
            onClick = onReiniciar,
            modifier = Modifier.fillMaxWidth(),
        )
        else -> BotaoDeAcaoDoCaso(
            texto = stringResource(R.string.catalogo_abrir_curto),
            icone = IconesIndicio.continuar,
            descricaoAcessivel = stringResource(R.string.catalogo_abrir, titulo),
            destaque = true,
            onClick = onAbrir,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun BotaoDeAcaoDoCaso(
    texto: String,
    icone: ImageVector,
    descricaoAcessivel: String,
    destaque: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = AlturaMinimaBotao)
            .semantics(mergeDescendants = true) {
                contentDescription = descricaoAcessivel
            },
        shape = FormasIndicio.controle,
        colors = if (destaque) {
            ButtonDefaults.buttonColors()
        } else {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        },
        contentPadding = PaddingValues(
            horizontal = EspacamentoIndicio.medio,
            vertical = EspacamentoIndicio.pequeno,
        ),
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = EspacamentoIndicio.pequeno),
        )
    }
}

@Composable
private fun EstadoDoCaso(caso: CasoDoCatalogo) {
    val texto = when (caso.situacao) {
        SituacaoCasoCatalogo.NAO_INICIADO -> stringResource(R.string.catalogo_estado_nao_iniciado)
        SituacaoCasoCatalogo.EM_ANDAMENTO -> stringResource(R.string.catalogo_estado_em_andamento)
        SituacaoCasoCatalogo.RESOLVIDO -> stringResource(R.string.catalogo_estado_resolvido)
    }
    val resolvido = caso.situacao == SituacaoCasoCatalogo.RESOLVIDO

    Column(verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.minimo)) {
        Surface(
            shape = FormasIndicio.pilula,
            color = if (resolvido) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            },
            contentColor = if (resolvido) {
                MaterialTheme.colorScheme.onTertiaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        ) {
            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(
                    horizontal = EspacamentoIndicio.medio,
                    vertical = EspacamentoIndicio.minimo,
                ),
            )
        }
        if (resolvido && caso.emAndamento) {
            Text(
                text = stringResource(R.string.catalogo_estado_nova_investigacao),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatarDataEHora(instante: Long): String =
    SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.forLanguageTag("pt-BR")).format(Date(instante))

private const val PROPORCAO_CAPA = 16f / 9f
private val ALTURA_MINIMA_CAPA = 180.dp

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
                            CasoDoCatalogo(
                                resumo = ResumoCaso(
                                    id = "taca-desaparecida",
                                    titulo = "O Mistério da Taça Desaparecida",
                                    sinopse = "Uma taça desaparece de uma sala aparentemente trancada.",
                                    categoria = Categoria.FUTEBOL,
                                    disponivel = true,
                                ),
                                situacao = SituacaoCasoCatalogo.EM_ANDAMENTO,
                                emAndamento = true,
                                ultimoAcessoEm = System.currentTimeMillis(),
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

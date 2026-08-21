package br.com.avoren.indicio.ui.historia

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.avoren.indicio.domain.armazenamento.RepositorioProgresso
import br.com.avoren.indicio.domain.caso.RepositorioCasos
import br.com.avoren.indicio.domain.dica.RepositorioDicas
import br.com.avoren.indicio.domain.narracao.Narrador
import br.com.avoren.indicio.ui.comum.ConteudoCarregando
import br.com.avoren.indicio.ui.comum.ConteudoDeFalha
import br.com.avoren.indicio.ui.dica.DicaViewModel

/**
 * Destino de navegação da história.
 *
 * Decide entre carregamento, falha, cena em curso e conclusão. A conclusão é um
 * estado da mesma sessão, e não um destino separado: assim o progresso não
 * precisa ser recarregado nem duplicado entre dois ViewModels.
 */
@Composable
fun DestinoHistoria(
    repositorioCasos: RepositorioCasos,
    repositorioProgresso: RepositorioProgresso,
    repositorioDicas: RepositorioDicas,
    criarNarrador: () -> Narrador,
    casoId: String,
    retomar: Boolean,
    onPausar: (Boolean) -> Unit,
    onAbrirEtapas: () -> Unit,
    onAbrirCaderno: () -> Unit,
    onConfiguracoes: () -> Unit,
    onVoltarAoCatalogo: () -> Unit,
    emDescanso: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: HistoriaViewModel = viewModel(
        factory = HistoriaViewModel.fabrica(
            repositorioCasos = repositorioCasos,
            repositorioProgresso = repositorioProgresso,
            criarNarrador = criarNarrador,
        ),
    ),
    dicaViewModel: DicaViewModel = viewModel(factory = DicaViewModel.fabrica(repositorioDicas)),
) {
    LaunchedEffect(casoId, retomar) {
        viewModel.abrir(casoId, retomar = retomar)
    }

    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val estadoNarracao by viewModel.estadoNarracao.collectAsStateWithLifecycle()
    val estadoDica by dicaViewModel.estado.collectAsStateWithLifecycle()
    val historiaEmCurso = estado as? EstadoHistoria.EmCurso

    LaunchedEffect(casoId, historiaEmCurso?.cena?.id) {
        historiaEmCurso?.let { atual ->
            dicaViewModel.carregar(
                casoId = casoId,
                cenaId = atual.cena.id,
                escolhas = atual.cena.escolhas,
                escolhaSugerida = atual.escolhaSugerida,
            )
        }
    }

    LaunchedEffect(emDescanso) {
        if (emDescanso) viewModel.silenciar()
    }

    // Sair da tela ou levá-la a segundo plano interrompe a fala; o mecanismo em
    // si só é liberado quando o ViewModel morre.
    val proprietario = LocalLifecycleOwner.current
    DisposableEffect(proprietario) {
        val observador = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_PAUSE) viewModel.silenciar()
        }
        proprietario.lifecycle.addObserver(observador)
        onDispose {
            proprietario.lifecycle.removeObserver(observador)
            viewModel.silenciar()
        }
    }

    // O botão do sistema abre a pausa em vez de sair da história sem aviso.
    BackHandler(enabled = estado is EstadoHistoria.EmCurso && !emDescanso) {
        val emCurso = estado as? EstadoHistoria.EmCurso
        onPausar(emCurso?.temInvestigacaoLonga == true)
    }

    when (val atual = estado) {
        is EstadoHistoria.Carregando -> ConteudoCarregando(modifier)

        is EstadoHistoria.Falha -> ConteudoDeFalha(
            onTentarNovamente = { viewModel.abrir(casoId, retomar = retomar) },
            modifier = modifier,
        )

        is EstadoHistoria.AtualizacaoNecessaria -> ConteudoAtualizacaoNecessaria(
            tituloCaso = atual.tituloCaso,
            onReiniciar = viewModel::reiniciar,
            onVoltarAoCatalogo = onVoltarAoCatalogo,
            modifier = modifier,
        )

        is EstadoHistoria.EmCurso -> ConteudoHistoria(
            estado = atual,
            estadoNarracao = estadoNarracao,
            onEscolher = viewModel::escolher,
            onAlternarNarracao = viewModel::alternarNarracao,
            onConfiguracoes = onConfiguracoes,
            onAbrirEtapas = onAbrirEtapas,
            onAbrirCaderno = onAbrirCaderno,
            bloquearMenu = emDescanso,
            estadoDica = estadoDica,
            onRevelarDica = dicaViewModel::revelar,
            onRecarregarDica = dicaViewModel::recarregar,
            modifier = modifier,
        )

        is EstadoHistoria.Concluida -> ConteudoConclusao(
            estado = atual,
            onJogarNovamente = viewModel::reiniciar,
            onVoltarAoCatalogo = onVoltarAoCatalogo,
            modifier = modifier,
        )
    }
}

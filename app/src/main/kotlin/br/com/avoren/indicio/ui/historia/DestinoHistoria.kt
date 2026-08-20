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
import br.com.avoren.indicio.di.ContainerAplicacao
import br.com.avoren.indicio.ui.comum.ConteudoCarregando
import br.com.avoren.indicio.ui.comum.ConteudoDeFalha

/**
 * Destino de navegação da história.
 *
 * Decide entre carregamento, falha, cena em curso e conclusão. A conclusão é um
 * estado da mesma sessão, e não um destino separado: assim o progresso não
 * precisa ser recarregado nem duplicado entre dois ViewModels.
 */
@Composable
fun DestinoHistoria(
    container: ContainerAplicacao,
    casoId: String,
    retomar: Boolean,
    onPausar: () -> Unit,
    onVoltarAoCatalogo: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoriaViewModel = viewModel(factory = HistoriaViewModel.fabrica(container)),
) {
    LaunchedEffect(casoId, retomar) {
        viewModel.abrir(casoId, retomar = retomar)
    }

    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val estadoNarracao by viewModel.estadoNarracao.collectAsStateWithLifecycle()

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
    BackHandler(enabled = estado is EstadoHistoria.EmCurso, onBack = onPausar)

    when (val atual = estado) {
        is EstadoHistoria.Carregando -> ConteudoCarregando(modifier)

        is EstadoHistoria.Falha -> ConteudoDeFalha(
            onTentarNovamente = { viewModel.abrir(casoId, retomar = retomar) },
            modifier = modifier,
        )

        is EstadoHistoria.EmCurso -> ConteudoHistoria(
            estado = atual,
            estadoNarracao = estadoNarracao,
            onEscolher = viewModel::escolher,
            onAlternarNarracao = viewModel::alternarNarracao,
            onPausar = onPausar,
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

package br.com.avoren.indicio.navegacao

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.com.avoren.indicio.application.descanso.EstadoCicloDeDescanso
import br.com.avoren.indicio.di.ContainerAplicacao
import br.com.avoren.indicio.ui.apresentacao.TelaApresentacao
import br.com.avoren.indicio.ui.catalogo.TelaCatalogo
import br.com.avoren.indicio.ui.configuracoes.ConfiguracoesViewModel
import br.com.avoren.indicio.ui.configuracoes.TelaConfiguracoes
import br.com.avoren.indicio.ui.descanso.DescansoViewModel
import br.com.avoren.indicio.ui.descanso.LembreteDescanso
import br.com.avoren.indicio.ui.descanso.TelaDescanso
import br.com.avoren.indicio.ui.historia.DestinoHistoria
import br.com.avoren.indicio.ui.investigacao.DestinoCaderno
import br.com.avoren.indicio.ui.investigacao.DestinoEtapas
import br.com.avoren.indicio.ui.investigacao.DestinoRetomada
import br.com.avoren.indicio.ui.inicio.TelaInicio
import br.com.avoren.indicio.ui.pausa.TelaPausa
import br.com.avoren.indicio.ui.sobre.TelaSobre
import br.com.avoren.indicio.ui.tema.TemaIndicio
import kotlinx.coroutines.delay

/**
 * Raiz do aplicativo: tema e grafo de navegação.
 *
 * As preferências são observadas aqui porque o tema inteiro depende delas, e
 * não apenas a tela de configurações.
 */
@Composable
fun AppIndicio(container: ContainerAplicacao) {
    val configuracoesViewModel: ConfiguracoesViewModel = viewModel(
        factory = ConfiguracoesViewModel.fabrica(container.repositorioPreferencias),
    )
    val preferencias by configuracoesViewModel.preferencias.collectAsStateWithLifecycle()
    val descansoViewModel: DescansoViewModel = viewModel(factory = DescansoViewModel.fabrica())
    val estadoDescanso by descansoViewModel.estado.collectAsStateWithLifecycle()
    val proprietario = LocalLifecycleOwner.current

    DisposableEffect(proprietario, descansoViewModel) {
        val observador = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_START -> descansoViewModel.retomar()
                Lifecycle.Event.ON_STOP -> descansoViewModel.pausar()
                else -> Unit
            }
        }
        proprietario.lifecycle.addObserver(observador)
        if (proprietario.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            descansoViewModel.retomar()
        }
        onDispose {
            proprietario.lifecycle.removeObserver(observador)
            descansoViewModel.pausar()
        }
    }

    LaunchedEffect(descansoViewModel) {
        while (true) {
            descansoViewModel.atualizar()
            delay(INTERVALO_ATUALIZACAO_DESCANSO_MILLIS)
        }
    }

    TemaIndicio(preferencias = preferencias) {
        val navController = rememberNavController()
        var pistasNaoLidasPorCaso by remember { mutableStateOf(emptyMap<String, Int>()) }
        val descanso = estadoDescanso as? EstadoCicloDeDescanso.EmDescanso
        val mostrarLembrete = estadoDescanso == EstadoCicloDeDescanso.LembreteVisual

        fun abrirCaderno(casoId: String) {
            pistasNaoLidasPorCaso = pistasNaoLidasPorCaso - casoId
            navController.navigate(Rota.Caderno(casoId))
        }

        fun limparPistasNaoLidas(casoId: String) {
            pistasNaoLidasPorCaso = pistasNaoLidasPorCaso - casoId
        }

        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Rota.Apresentacao,
                modifier = if (descanso == null) {
                    Modifier
                } else {
                    Modifier.clearAndSetSemantics { }
                },
            ) {

            composable<Rota.Apresentacao> {
                TelaApresentacao(
                    onConcluir = {
                        navController.navigate(Rota.Inicio) {
                            // A apresentação não volta pelo botão do sistema.
                            popUpTo(Rota.Apresentacao) { inclusive = true }
                        }
                    },
                )
            }

            composable<Rota.Inicio> {
                TelaInicio(
                    repositorioIdentidade = container.repositorioIdentidade,
                    obterCasoParaContinuar = container.obterCasoParaContinuar,
                    onContinuar = { casoId ->
                        navController.navigate(Rota.Retomada(casoId))
                    },
                    onEscolherCaso = { navController.navigate(Rota.Catalogo) },
                    onConfiguracoes = { navController.navigate(Rota.Configuracoes) },
                    onSobre = { navController.navigate(Rota.Sobre) },
                )
            }

            composable<Rota.Catalogo> {
                TelaCatalogo(
                    repositorioCasos = container.repositorioCasos,
                    onAbrirCaso = { casoId ->
                        navController.navigate(Rota.Retomada(casoId))
                    },
                    onVoltar = { navController.popBackStack() },
                )
            }

            composable<Rota.Historia> { entrada ->
                val rota = entrada.toRoute<Rota.Historia>()

                DestinoHistoria(
                    repositorioCasos = container.repositorioCasos,
                    repositorioProgresso = container.repositorioProgresso,
                    repositorioDicas = container.repositorioDicas,
                    criarNarrador = container::criarNarrador,
                    casoId = rota.casoId,
                    retomar = rota.retomar,
                    onPausar = { temEtapas ->
                        navController.navigate(Rota.Pausa(rota.casoId, temEtapas))
                    },
                    onAbrirEtapas = { navController.navigate(Rota.Etapas(rota.casoId)) },
                    onAbrirCaderno = { abrirCaderno(rota.casoId) },
                    pistasNaoLidas = pistasNaoLidasPorCaso[rota.casoId] ?: 0,
                    onPistasReveladas = { pistas ->
                        pistasNaoLidasPorCaso = pistasNaoLidasPorCaso + (
                            rota.casoId to ((pistasNaoLidasPorCaso[rota.casoId] ?: 0) + pistas.size)
                        )
                    },
                    onReiniciarCaso = { limparPistasNaoLidas(rota.casoId) },
                    onConfiguracoes = { navController.navigate(Rota.Configuracoes) },
                    onVoltarAoCatalogo = { navController.irParaCatalogo() },
                    emDescanso = descanso != null,
                )
            }

            composable<Rota.Retomada> { entrada ->
                val rota = entrada.toRoute<Rota.Retomada>()
                DestinoRetomada(
                    casoId = rota.casoId,
                    repositorioCasos = container.repositorioCasos,
                    repositorioProgresso = container.repositorioProgresso,
                    onContinuar = {
                        navController.navigate(Rota.Historia(rota.casoId, retomar = true)) {
                            popUpTo(rota) { inclusive = true }
                        }
                    },
                    onAbrirCaderno = { abrirCaderno(rota.casoId) },
                    onVoltar = { navController.popBackStack() },
                )
            }

            composable<Rota.Etapas> { entrada ->
                val rota = entrada.toRoute<Rota.Etapas>()
                DestinoEtapas(
                    casoId = rota.casoId,
                    repositorioCasos = container.repositorioCasos,
                    repositorioProgresso = container.repositorioProgresso,
                    onContinuar = { navController.retomarHistoria(rota.casoId) },
                    onVoltar = { navController.popBackStack() },
                )
            }

            composable<Rota.Caderno> { entrada ->
                val rota = entrada.toRoute<Rota.Caderno>()
                DestinoCaderno(
                    casoId = rota.casoId,
                    repositorioCasos = container.repositorioCasos,
                    repositorioProgresso = container.repositorioProgresso,
                    onVoltar = { navController.popBackStack() },
                )
            }

            composable<Rota.Pausa> { entrada ->
                val rota = entrada.toRoute<Rota.Pausa>()

                TelaPausa(
                    onContinuar = { navController.popBackStack() },
                    onAbrirEtapas = { navController.navigate(Rota.Etapas(rota.casoId)) },
                    onAbrirCaderno = { abrirCaderno(rota.casoId) },
                    temEtapas = rota.temEtapas,
                    onConfiguracoes = { navController.navigate(Rota.Configuracoes) },
                    onReiniciar = {
                        limparPistasNaoLidas(rota.casoId)
                        navController.navigate(Rota.Historia(rota.casoId, retomar = false)) {
                            popUpTo(Rota.Historia(rota.casoId, retomar = true)) { inclusive = true }
                        }
                    },
                    onVoltarAoInicio = { navController.irParaInicio() },
                )
            }

            composable<Rota.Configuracoes> {
                TelaConfiguracoes(viewModel = configuracoesViewModel)
            }

            composable<Rota.Sobre> {
                val identidade = container.repositorioIdentidade.identidade()
                TelaSobre(versao = identidade.versao)
            }
            }

            if (descanso != null) {
                TelaDescanso(
                    tempoRestante = descanso.tempoRestante,
                    duracaoTotal = descanso.duracaoTotal,
                )
            }

            if (mostrarLembrete) {
                LembreteDescanso(
                    onDispensar = descansoViewModel::dispensarLembrete,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding(),
                )
            }
        }
    }
}

private const val INTERVALO_ATUALIZACAO_DESCANSO_MILLIS = 250L

/**
 * Volta ao início limpando o que houver acima dele, para que o botão do
 * sistema não reabra a história já encerrada.
 */
private fun NavHostController.irParaInicio() {
    navigate(Rota.Inicio) {
        popUpTo(Rota.Inicio) { inclusive = false }
        launchSingleTop = true
    }
}

private fun NavHostController.irParaCatalogo() {
    navigate(Rota.Catalogo) {
        popUpTo(Rota.Inicio) { inclusive = false }
        launchSingleTop = true
    }
}

private fun NavHostController.retomarHistoria(casoId: String) {
    if (!popBackStack<Rota.Historia>(inclusive = false)) {
        navigate(Rota.Historia(casoId, retomar = true))
    }
}

package br.com.avoren.indicio.navegacao

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.com.avoren.indicio.di.ContainerAplicacao
import br.com.avoren.indicio.ui.apresentacao.TelaApresentacao
import br.com.avoren.indicio.ui.catalogo.TelaCatalogo
import br.com.avoren.indicio.ui.configuracoes.ConfiguracoesViewModel
import br.com.avoren.indicio.ui.configuracoes.TelaConfiguracoes
import br.com.avoren.indicio.ui.historia.DestinoHistoria
import br.com.avoren.indicio.ui.investigacao.DestinoCaderno
import br.com.avoren.indicio.ui.investigacao.DestinoEtapas
import br.com.avoren.indicio.ui.investigacao.DestinoRetomada
import br.com.avoren.indicio.ui.inicio.TelaInicio
import br.com.avoren.indicio.ui.pausa.TelaPausa
import br.com.avoren.indicio.ui.sobre.TelaSobre
import br.com.avoren.indicio.ui.tema.TemaIndicio

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

    TemaIndicio(preferencias = preferencias) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Rota.Apresentacao) {

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
                    criarNarrador = container::criarNarrador,
                    casoId = rota.casoId,
                    retomar = rota.retomar,
                    onPausar = { temEtapas ->
                        navController.navigate(Rota.Pausa(rota.casoId, temEtapas))
                    },
                    onAbrirEtapas = { navController.navigate(Rota.Etapas(rota.casoId)) },
                    onAbrirCaderno = { navController.navigate(Rota.Caderno(rota.casoId)) },
                    onVoltarAoCatalogo = { navController.irParaCatalogo() },
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
                    onAbrirCaderno = { navController.navigate(Rota.Caderno(rota.casoId)) },
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
                    onAbrirCaderno = { navController.navigate(Rota.Caderno(rota.casoId)) },
                    temEtapas = rota.temEtapas,
                    onConfiguracoes = { navController.navigate(Rota.Configuracoes) },
                    onReiniciar = {
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
    }
}

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

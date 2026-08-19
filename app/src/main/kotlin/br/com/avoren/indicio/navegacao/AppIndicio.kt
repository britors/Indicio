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
        factory = ConfiguracoesViewModel.fabrica(container),
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
                    container = container,
                    onContinuar = { casoId ->
                        navController.navigate(Rota.Historia(casoId, retomar = true))
                    },
                    onEscolherCaso = { navController.navigate(Rota.Catalogo) },
                    onConfiguracoes = { navController.navigate(Rota.Configuracoes) },
                    onSobre = { navController.navigate(Rota.Sobre) },
                )
            }

            composable<Rota.Catalogo> {
                TelaCatalogo(
                    container = container,
                    onAbrirCaso = { casoId ->
                        navController.navigate(Rota.Historia(casoId, retomar = true))
                    },
                )
            }

            composable<Rota.Historia> { entrada ->
                val rota = entrada.toRoute<Rota.Historia>()

                DestinoHistoria(
                    container = container,
                    casoId = rota.casoId,
                    retomar = rota.retomar,
                    onPausar = { navController.navigate(Rota.Pausa(rota.casoId)) },
                    onVoltarAoCatalogo = { navController.irParaCatalogo() },
                )
            }

            composable<Rota.Pausa> { entrada ->
                val rota = entrada.toRoute<Rota.Pausa>()

                TelaPausa(
                    onContinuar = { navController.popBackStack() },
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

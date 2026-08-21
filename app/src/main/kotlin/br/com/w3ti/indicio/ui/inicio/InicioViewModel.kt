package br.com.w3ti.indicio.ui.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.w3ti.indicio.application.caso.ObterCasoParaContinuar
import br.com.w3ti.indicio.domain.repositorio.RepositorioIdentidade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado observável da tela inicial.
 *
 * [casoParaContinuar] só é preenchido quando existe uma sessão realmente
 * retomável; é ele que habilita o botão "Continuar".
 */
data class EstadoInicio(
    val nome: String = "",
    val slogan: String = "",
    val versao: String = "",
    val casoParaContinuar: String? = null,
    val tituloParaContinuar: String? = null,
) {
    val podeContinuar: Boolean get() = casoParaContinuar != null
}

/**
 * ViewModel da tela inicial.
 *
 * Segue fluxo unidirecional: a interface apenas observa [estado] e envia
 * eventos; nenhuma decisão de navegação é tomada aqui.
 */
class InicioViewModel(
    repositorioIdentidade: RepositorioIdentidade,
    obterCasoParaContinuar: ObterCasoParaContinuar? = null,
) : ViewModel() {

    private val _estado = MutableStateFlow(
        with(repositorioIdentidade.identidade()) {
            EstadoInicio(nome = nome, slogan = slogan, versao = versao)
        },
    )
    val estado: StateFlow<EstadoInicio> = _estado.asStateFlow()

    init {
        obterCasoParaContinuar?.let { obter ->
            viewModelScope.launch {
                obter().collect { retomavel ->
                    _estado.value = _estado.value.copy(
                        casoParaContinuar = retomavel?.resumo?.id,
                        tituloParaContinuar = retomavel?.resumo?.titulo,
                    )
                }
            }
        }
    }

    companion object {
        fun fabrica(
            repositorioIdentidade: RepositorioIdentidade,
            obterCasoParaContinuar: ObterCasoParaContinuar,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    InicioViewModel(
                        repositorioIdentidade = repositorioIdentidade,
                        obterCasoParaContinuar = obterCasoParaContinuar,
                    )
                }
            }
    }
}

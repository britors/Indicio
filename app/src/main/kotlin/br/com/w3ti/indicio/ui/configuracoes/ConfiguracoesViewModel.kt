package br.com.w3ti.indicio.ui.configuracoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.w3ti.indicio.domain.armazenamento.RepositorioPreferencias
import br.com.w3ti.indicio.domain.model.preferencias.Preferencias
import br.com.w3ti.indicio.domain.model.preferencias.TamanhoTexto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Preferências de leitura e conforto.
 *
 * Vive na raiz da navegação: o tema inteiro depende dele, e não só a tela de
 * configurações.
 */
class ConfiguracoesViewModel(
    private val repositorio: RepositorioPreferencias,
) : ViewModel() {

    val preferencias: StateFlow<Preferencias> = repositorio.preferencias.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TEMPO_DE_ESPERA),
        initialValue = Preferencias(),
    )

    fun definirTamanhoTexto(tamanho: TamanhoTexto) {
        viewModelScope.launch { repositorio.definirTamanhoTexto(tamanho) }
    }

    fun definirReducaoDeMovimentos(reduzir: Boolean) {
        viewModelScope.launch { repositorio.definirReducaoDeMovimentos(reduzir) }
    }

    companion object {
        private const val TEMPO_DE_ESPERA = 5_000L

        fun fabrica(repositorio: RepositorioPreferencias): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { ConfiguracoesViewModel(repositorio) }
            }
    }
}

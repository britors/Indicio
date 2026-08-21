package br.com.w3ti.indicio.ui.descanso

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.w3ti.indicio.application.descanso.CicloDeDescanso
import br.com.w3ti.indicio.application.descanso.EstadoCicloDeDescanso
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Adapta o ciclo de descanso ao relógio monotônico e ao ciclo de vida da UI. */
class DescansoViewModel internal constructor(
    private val ciclo: CicloDeDescanso,
    private val agora: () -> Duration,
) : ViewModel() {
    private val _estado = MutableStateFlow(ciclo.estado)
    val estado: StateFlow<EstadoCicloDeDescanso> = _estado.asStateFlow()

    fun retomar() = publicar(ciclo.retomar(agora()))

    fun pausar() = publicar(ciclo.pausar(agora()))

    fun atualizar() = publicar(ciclo.atualizar(agora()))

    fun dispensarLembrete() = publicar(ciclo.dispensarLembrete())

    private fun publicar(novoEstado: EstadoCicloDeDescanso) {
        _estado.value = novoEstado
    }

    companion object {
        fun fabrica(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DescansoViewModel(
                    ciclo = CicloDeDescanso(),
                    agora = { SystemClock.elapsedRealtime().milliseconds },
                )
            }
        }
    }
}

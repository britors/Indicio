package br.com.avoren.indicio.fake

import br.com.avoren.indicio.domain.narracao.EstadoNarracao
import br.com.avoren.indicio.domain.narracao.Narrador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Narrador em memória, com disponibilidade controlável.
 */
class NarradorFalso(
    inicial: EstadoNarracao = EstadoNarracao.PRONTO,
) : Narrador {

    private val _estado = MutableStateFlow(inicial)
    override val estado: StateFlow<EstadoNarracao> = _estado

    val textosFalados = mutableListOf<String>()
    var paradas: Int = 0
        private set
    var encerrado: Boolean = false
        private set

    override fun falar(texto: String) {
        if (_estado.value == EstadoNarracao.INDISPONIVEL || encerrado) return
        textosFalados += texto
        _estado.value = EstadoNarracao.FALANDO
    }

    override fun parar() {
        paradas++
        if (_estado.value == EstadoNarracao.FALANDO) _estado.value = EstadoNarracao.PRONTO
    }

    override fun encerrar() {
        encerrado = true
        _estado.value = EstadoNarracao.INDISPONIVEL
    }
}

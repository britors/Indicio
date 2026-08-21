package br.com.w3ti.indicio.data.narracao

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import br.com.w3ti.indicio.domain.narracao.EstadoNarracao
import br.com.w3ti.indicio.domain.narracao.Narrador
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Narração pelo TextToSpeech do Android, sem rede.
 *
 * Tenta português do Brasil; se o aparelho não tiver voz, idioma ou mecanismo
 * utilizável, o narrador fica em [EstadoNarracao.INDISPONIVEL] e o restante do
 * aplicativo segue funcionando por completo.
 */
class NarradorTextToSpeech(
    context: Context,
) : Narrador {

    private val _estado = MutableStateFlow(EstadoNarracao.PREPARANDO)
    override val estado: StateFlow<EstadoNarracao> = _estado.asStateFlow()

    private var encerrado = false

    private val motor = TextToSpeech(context.applicationContext) { status ->
        _estado.value = when {
            encerrado -> EstadoNarracao.INDISPONIVEL
            status != TextToSpeech.SUCCESS -> EstadoNarracao.INDISPONIVEL
            !idiomaUtilizavel() -> EstadoNarracao.INDISPONIVEL
            else -> EstadoNarracao.PRONTO
        }
    }

    init {
        motor.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    if (!encerrado) _estado.value = EstadoNarracao.FALANDO
                }

                override fun onDone(utteranceId: String?) = voltarAoRepouso()

                @Deprecated("Substituído pela sobrecarga com código de erro")
                override fun onError(utteranceId: String?) = voltarAoRepouso()

                override fun onError(utteranceId: String?, errorCode: Int) = voltarAoRepouso()

                override fun onStop(utteranceId: String?, interrupted: Boolean) = voltarAoRepouso()
            },
        )
    }

    private fun idiomaUtilizavel(): Boolean {
        val resultado = motor.setLanguage(Locale.forLanguageTag(IDIOMA))
        return resultado != TextToSpeech.LANG_MISSING_DATA &&
            resultado != TextToSpeech.LANG_NOT_SUPPORTED
    }

    private fun voltarAoRepouso() {
        if (!encerrado && _estado.value == EstadoNarracao.FALANDO) {
            _estado.value = EstadoNarracao.PRONTO
        }
    }

    override fun falar(texto: String) {
        if (encerrado || _estado.value == EstadoNarracao.INDISPONIVEL) return
        if (texto.isBlank()) return

        motor.speak(texto, TextToSpeech.QUEUE_FLUSH, null, IDENTIFICADOR_DA_FALA)
    }

    override fun parar() {
        if (encerrado) return
        motor.stop()
        voltarAoRepouso()
    }

    override fun encerrar() {
        if (encerrado) return
        encerrado = true
        motor.stop()
        motor.shutdown()
        _estado.value = EstadoNarracao.INDISPONIVEL
    }

    private companion object {
        const val IDIOMA = "pt-BR"
        const val IDENTIFICADOR_DA_FALA = "cena"
    }
}

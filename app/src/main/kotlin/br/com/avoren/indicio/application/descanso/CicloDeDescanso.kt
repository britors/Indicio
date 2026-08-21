package br.com.avoren.indicio.application.descanso

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/** Estado do ciclo que intercala investigação e pausas de descanso. */
sealed interface EstadoCicloDeDescanso {
    data object EmUso : EstadoCicloDeDescanso

    data class EmDescanso(
        val tempoRestante: Duration,
        val duracaoTotal: Duration,
    ) : EstadoCicloDeDescanso
}

/**
 * Política temporal do lembrete de descanso.
 *
 * Somente o uso em primeiro plano conta para os 45 minutos. Depois que a pausa
 * começa, seus cinco minutos continuam correndo mesmo com o aplicativo em
 * segundo plano, pois esse tempo já constitui descanso da tela.
 */
class CicloDeDescanso(
    private val intervaloDeUso: Duration = INTERVALO_DE_USO_PADRAO,
    private val duracaoDoDescanso: Duration = DURACAO_DO_DESCANSO_PADRAO,
) {
    var estado: EstadoCicloDeDescanso = EstadoCicloDeDescanso.EmUso
        private set

    private var emPrimeiroPlano = false
    private var usoAcumulado = Duration.ZERO
    private var inicioDoUso = Duration.ZERO
    private var fimDoDescanso = Duration.ZERO

    init {
        require(intervaloDeUso.isPositive()) { "o intervalo de uso precisa ser positivo" }
        require(duracaoDoDescanso.isPositive()) { "a duração do descanso precisa ser positiva" }
    }

    fun retomar(agora: Duration): EstadoCicloDeDescanso {
        if (emPrimeiroPlano) return atualizar(agora)

        emPrimeiroPlano = true
        if (estado is EstadoCicloDeDescanso.EmUso) inicioDoUso = agora
        return atualizar(agora)
    }

    fun pausar(agora: Duration): EstadoCicloDeDescanso {
        if (!emPrimeiroPlano) return atualizar(agora)

        atualizar(agora)
        if (estado is EstadoCicloDeDescanso.EmUso) {
            usoAcumulado += agora - inicioDoUso
        }
        emPrimeiroPlano = false
        return estado
    }

    fun atualizar(agora: Duration): EstadoCicloDeDescanso {
        when (estado) {
            EstadoCicloDeDescanso.EmUso -> atualizarUso(agora)
            is EstadoCicloDeDescanso.EmDescanso -> atualizarDescanso(agora)
        }
        return estado
    }

    private fun atualizarUso(agora: Duration) {
        if (!emPrimeiroPlano) return

        val usoTotal = usoAcumulado + (agora - inicioDoUso)
        if (usoTotal >= intervaloDeUso) {
            usoAcumulado = Duration.ZERO
            fimDoDescanso = agora + duracaoDoDescanso
            estado = EstadoCicloDeDescanso.EmDescanso(
                tempoRestante = duracaoDoDescanso,
                duracaoTotal = duracaoDoDescanso,
            )
        }
    }

    private fun atualizarDescanso(agora: Duration) {
        val restante = fimDoDescanso - agora
        if (restante.isPositive()) {
            estado = EstadoCicloDeDescanso.EmDescanso(
                tempoRestante = restante,
                duracaoTotal = duracaoDoDescanso,
            )
        } else {
            estado = EstadoCicloDeDescanso.EmUso
            usoAcumulado = Duration.ZERO
            if (emPrimeiroPlano) inicioDoUso = agora
        }
    }

    companion object {
        val INTERVALO_DE_USO_PADRAO = 45.minutes
        val DURACAO_DO_DESCANSO_PADRAO = 5.minutes
    }
}

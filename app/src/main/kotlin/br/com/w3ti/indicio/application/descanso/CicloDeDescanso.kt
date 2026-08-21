package br.com.w3ti.indicio.application.descanso

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/** Estado do ciclo que intercala investigação e pausas de descanso. */
sealed interface EstadoCicloDeDescanso {
    data object EmUso : EstadoCicloDeDescanso

    /** Aviso não bloqueante para uma breve mudança de foco visual. */
    data object LembreteVisual : EstadoCicloDeDescanso

    data class EmDescanso(
        val tempoRestante: Duration,
        val duracaoTotal: Duration,
    ) : EstadoCicloDeDescanso
}

/**
 * Política temporal do lembrete de descanso.
 *
 * Somente o uso em primeiro plano conta para os intervalos. Aos 20 minutos há
 * um lembrete não bloqueante; aos 30 começa a pausa de três minutos. Depois que
 * a pausa começa, sua contagem continua mesmo com o aplicativo em segundo
 * plano, pois esse tempo já constitui descanso da tela.
 */
class CicloDeDescanso(
    private val intervaloDoLembrete: Duration = INTERVALO_DO_LEMBRETE_PADRAO,
    private val intervaloDeUso: Duration = INTERVALO_DE_USO_PADRAO,
    private val duracaoDoDescanso: Duration = DURACAO_DO_DESCANSO_PADRAO,
) {
    var estado: EstadoCicloDeDescanso = EstadoCicloDeDescanso.EmUso
        private set

    private var emPrimeiroPlano = false
    private var usoAcumulado = Duration.ZERO
    private var inicioDoUso = Duration.ZERO
    private var fimDoDescanso = Duration.ZERO
    private var lembreteExibidoNesteCiclo = false

    init {
        require(intervaloDoLembrete.isPositive()) { "o intervalo do lembrete precisa ser positivo" }
        require(intervaloDeUso.isPositive()) { "o intervalo de uso precisa ser positivo" }
        require(intervaloDoLembrete < intervaloDeUso) {
            "o lembrete precisa acontecer antes do descanso"
        }
        require(duracaoDoDescanso.isPositive()) { "a duração do descanso precisa ser positiva" }
    }

    fun retomar(agora: Duration): EstadoCicloDeDescanso {
        if (emPrimeiroPlano) return atualizar(agora)

        emPrimeiroPlano = true
        if (estado !is EstadoCicloDeDescanso.EmDescanso) inicioDoUso = agora
        return atualizar(agora)
    }

    fun pausar(agora: Duration): EstadoCicloDeDescanso {
        if (!emPrimeiroPlano) return atualizar(agora)

        atualizar(agora)
        if (estado !is EstadoCicloDeDescanso.EmDescanso) {
            usoAcumulado += agora - inicioDoUso
        }
        emPrimeiroPlano = false
        return estado
    }

    fun atualizar(agora: Duration): EstadoCicloDeDescanso {
        when (estado) {
            EstadoCicloDeDescanso.EmUso,
            EstadoCicloDeDescanso.LembreteVisual,
            -> atualizarUso(agora)
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
        } else if (usoTotal >= intervaloDoLembrete && !lembreteExibidoNesteCiclo) {
            lembreteExibidoNesteCiclo = true
            estado = EstadoCicloDeDescanso.LembreteVisual
        }
    }

    fun dispensarLembrete(): EstadoCicloDeDescanso {
        if (estado == EstadoCicloDeDescanso.LembreteVisual) {
            estado = EstadoCicloDeDescanso.EmUso
        }
        return estado
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
            lembreteExibidoNesteCiclo = false
            if (emPrimeiroPlano) inicioDoUso = agora
        }
    }

    companion object {
        val INTERVALO_DO_LEMBRETE_PADRAO = 20.minutes
        val INTERVALO_DE_USO_PADRAO = 30.minutes
        val DURACAO_DO_DESCANSO_PADRAO = 3.minutes
    }
}

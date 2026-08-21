package br.com.avoren.indicio.application.descanso

import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CicloDeDescansoTest {

    @Test
    fun iniciaCincoMinutosDeDescansoAposQuarentaECincoMinutosDeUso() {
        val ciclo = CicloDeDescanso()

        ciclo.retomar(0.minutes)
        assertEquals(EstadoCicloDeDescanso.EmUso, ciclo.atualizar(44.minutes + 59.seconds))

        val descanso = ciclo.atualizar(45.minutes)

        assertEquals(
            EstadoCicloDeDescanso.EmDescanso(
                tempoRestante = 5.minutes,
                duracaoTotal = 5.minutes,
            ),
            descanso,
        )
    }

    @Test
    fun tempoEmSegundoPlanoNaoContaComoUso() {
        val ciclo = CicloDeDescanso()

        ciclo.retomar(0.minutes)
        ciclo.pausar(30.minutes)
        ciclo.retomar(90.minutes)

        assertEquals(EstadoCicloDeDescanso.EmUso, ciclo.atualizar(104.minutes + 59.seconds))
        assertTrue(ciclo.atualizar(105.minutes) is EstadoCicloDeDescanso.EmDescanso)
    }

    @Test
    fun descansoContinuaEnquantoOAplicativoEstaEmSegundoPlano() {
        val ciclo = CicloDeDescanso()

        ciclo.retomar(0.minutes)
        ciclo.atualizar(45.minutes)
        ciclo.pausar(45.minutes)

        assertEquals(
            EstadoCicloDeDescanso.EmDescanso(
                tempoRestante = 1.minutes,
                duracaoTotal = 5.minutes,
            ),
            ciclo.atualizar(49.minutes),
        )
        assertEquals(EstadoCicloDeDescanso.EmUso, ciclo.atualizar(50.minutes))
    }

    @Test
    fun iniciaNovoCicloDeUsoQuandoODescansoTermina() {
        val ciclo = CicloDeDescanso()

        ciclo.retomar(0.minutes)
        ciclo.atualizar(45.minutes)
        ciclo.atualizar(50.minutes)

        assertEquals(EstadoCicloDeDescanso.EmUso, ciclo.atualizar(94.minutes + 59.seconds))
        assertTrue(ciclo.atualizar(95.minutes) is EstadoCicloDeDescanso.EmDescanso)
    }
}

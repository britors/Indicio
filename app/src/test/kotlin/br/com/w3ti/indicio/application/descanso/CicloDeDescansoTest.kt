package br.com.w3ti.indicio.application.descanso

import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CicloDeDescansoTest {

    @Test
    fun mostraLembreteVisualAosVinteMinutosSemBloquearOCiclo() {
        val ciclo = CicloDeDescanso()

        ciclo.retomar(0.minutes)
        assertEquals(EstadoCicloDeDescanso.EmUso, ciclo.atualizar(19.minutes + 59.seconds))
        assertEquals(EstadoCicloDeDescanso.LembreteVisual, ciclo.atualizar(20.minutes))

        assertEquals(EstadoCicloDeDescanso.EmUso, ciclo.dispensarLembrete())
        assertEquals(EstadoCicloDeDescanso.EmUso, ciclo.atualizar(29.minutes + 59.seconds))
    }

    @Test
    fun iniciaTresMinutosDeDescansoAposTrintaMinutosDeUso() {
        val ciclo = CicloDeDescanso()

        ciclo.retomar(0.minutes)

        val descanso = ciclo.atualizar(30.minutes)

        assertEquals(
            EstadoCicloDeDescanso.EmDescanso(
                tempoRestante = 3.minutes,
                duracaoTotal = 3.minutes,
            ),
            descanso,
        )
    }

    @Test
    fun tempoEmSegundoPlanoNaoContaComoUso() {
        val ciclo = CicloDeDescanso()

        ciclo.retomar(0.minutes)
        ciclo.pausar(15.minutes)
        ciclo.retomar(90.minutes)

        assertTrue(ciclo.atualizar(104.minutes + 59.seconds) !is EstadoCicloDeDescanso.EmDescanso)
        assertTrue(ciclo.atualizar(105.minutes) is EstadoCicloDeDescanso.EmDescanso)
    }

    @Test
    fun descansoContinuaEnquantoOAplicativoEstaEmSegundoPlano() {
        val ciclo = CicloDeDescanso()

        ciclo.retomar(0.minutes)
        ciclo.atualizar(30.minutes)
        ciclo.pausar(30.minutes)

        assertEquals(
            EstadoCicloDeDescanso.EmDescanso(
                tempoRestante = 1.minutes,
                duracaoTotal = 3.minutes,
            ),
            ciclo.atualizar(32.minutes),
        )
        assertEquals(EstadoCicloDeDescanso.EmUso, ciclo.atualizar(33.minutes))
    }

    @Test
    fun iniciaNovoCicloDeUsoQuandoODescansoTermina() {
        val ciclo = CicloDeDescanso()

        ciclo.retomar(0.minutes)
        ciclo.atualizar(30.minutes)
        ciclo.atualizar(33.minutes)

        assertEquals(EstadoCicloDeDescanso.LembreteVisual, ciclo.atualizar(53.minutes))
        ciclo.dispensarLembrete()
        assertEquals(EstadoCicloDeDescanso.EmUso, ciclo.atualizar(62.minutes + 59.seconds))
        assertTrue(ciclo.atualizar(63.minutes) is EstadoCicloDeDescanso.EmDescanso)
    }
}

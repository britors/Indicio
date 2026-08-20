package br.com.avoren.indicio.application.investigacao

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DecidirExibicaoDaRetomadaTest {
    private val politica = DecidirExibicaoDaRetomada(intervaloMinimoMillis = 1_000)

    @Test
    fun `intervalo igual ou maior que o limite abre retomada`() {
        assertTrue(politica(atualizadoEm = 1_000, agora = 2_000))
        assertTrue(politica(atualizadoEm = 1_000, agora = 2_001))
    }

    @Test
    fun `saida curta ou relogio inconsistente nao interrompe o retorno`() {
        assertFalse(politica(atualizadoEm = 1_001, agora = 2_000))
        assertFalse(politica(atualizadoEm = 3_000, agora = 2_000))
        assertFalse(politica(atualizadoEm = null, agora = 2_000))
    }
}

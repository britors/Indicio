package br.com.w3ti.indicio.domain.narrativa

import br.com.w3ti.indicio.domain.model.caso.Caso
import br.com.w3ti.indicio.domain.model.sessao.ProgressoCaso
import br.com.w3ti.indicio.fake.CasoFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MecanismoNarrativoTest {

    private val mecanismo = MecanismoNarrativo()

    /**
     * Grafo com ramificação, reencontro e pistas:
     *
     * abertura --a--> sala (pista) --a--> encerramento
     *          --b--> corredor       --b--> corredor
     *                 corredor --a/b--> encerramento
     */
    private val caso: Caso = CasoFixtures.casoValido()

    private fun sessaoInicial() = requireNotNull(mecanismo.iniciar(caso))

    private fun aplicar(vararg escolhas: String) = escolhas.fold(sessaoInicial()) { sessao, id ->
        (mecanismo.escolher(caso, sessao, id) as ResultadoEscolha.Aplicada).sessao
    }

    @Test
    fun `iniciar abre o caso na cena inicial sem caminho percorrido`() {
        val sessao = sessaoInicial()

        assertEquals("abertura", sessao.cenaAtual)
        assertEquals(emptyList<String>(), sessao.caminho)
        assertEquals(emptyList<Any>(), sessao.pistas)
        assertTrue(!sessao.concluida)
    }

    @Test
    fun `escolha leva a cena declarada no caso`() {
        val resultado = mecanismo.escolher(caso, sessaoInicial(), "abertura-a")

        assertTrue(resultado is ResultadoEscolha.Aplicada)
        assertEquals("sala", (resultado as ResultadoEscolha.Aplicada).sessao.cenaAtual)
    }

    @Test
    fun `caminhos alternativos se reencontram na mesma cena`() {
        val porSala = aplicar("abertura-a", "sala-b")
        val direto = aplicar("abertura-b")

        assertEquals("corredor", porSala.cenaAtual)
        assertEquals(direto.cenaAtual, porSala.cenaAtual)
        assertEquals(listOf("abertura-a", "sala-b"), porSala.caminho)
    }

    @Test
    fun `pista da cena e recolhida ao chegar nela`() {
        val resultado = mecanismo.escolher(caso, sessaoInicial(), "abertura-a")

        val aplicada = resultado as ResultadoEscolha.Aplicada
        assertEquals(listOf(CasoFixtures.pista()), aplicada.pistasReveladas)
        assertEquals(listOf(CasoFixtures.pista()), aplicada.sessao.pistas)
    }

    @Test
    fun `pistas nao se repetem ao passar duas vezes pela mesma cena`() {
        val sessao = aplicar("abertura-a", "sala-b", "corredor-a")

        assertEquals(1, sessao.pistas.count { it.id == CasoFixtures.pista().id })
    }

    @Test
    fun `pistas permanecem disponiveis na conclusao`() {
        val sessao = aplicar("abertura-a", "sala-a")

        assertTrue(sessao.concluida)
        assertEquals(listOf(CasoFixtures.pista()), sessao.pistas)
    }

    @Test
    fun `cena final registra o desfecho declarado no caso`() {
        val sessao = aplicar("abertura-b", "corredor-a")

        assertEquals(CasoFixtures.desfecho(), sessao.desfecho)
        assertTrue(sessao.concluida)
    }

    @Test
    fun `escolha de outra cena e recusada`() {
        val resultado = mecanismo.escolher(caso, sessaoInicial(), "corredor-a")

        assertTrue(resultado is ResultadoEscolha.Recusada)
        assertEquals(
            MotivoRecusa.ESCOLHA_INDISPONIVEL,
            (resultado as ResultadoEscolha.Recusada).motivo,
        )
    }

    @Test
    fun `toque duplo na mesma escolha nao avanca duas vezes`() {
        val sessao = sessaoInicial()

        val primeiro = mecanismo.escolher(caso, sessao, "abertura-a") as ResultadoEscolha.Aplicada
        val segundo = mecanismo.escolher(caso, primeiro.sessao, "abertura-a")

        assertTrue(segundo is ResultadoEscolha.Recusada)
        assertEquals(listOf("abertura-a"), primeiro.sessao.caminho)
    }

    @Test
    fun `sessao concluida nao aceita novas escolhas`() {
        val concluida = aplicar("abertura-b", "corredor-a")

        val resultado = mecanismo.escolher(caso, concluida, "corredor-b")

        assertTrue(resultado is ResultadoEscolha.Recusada)
        assertEquals(MotivoRecusa.SESSAO_CONCLUIDA, (resultado as ResultadoEscolha.Recusada).motivo)
    }

    @Test
    fun `escolha recusada devolve a sessao intacta`() {
        val sessao = sessaoInicial()

        val resultado = mecanismo.escolher(caso, sessao, "inexistente") as ResultadoEscolha.Recusada

        assertEquals(sessao, resultado.sessao)
    }

    @Test
    fun `reiniciar descarta caminho e pistas`() {
        val avancada = aplicar("abertura-a", "sala-a")

        val reiniciada = requireNotNull(mecanismo.reiniciar(caso))

        assertEquals("abertura", reiniciada.cenaAtual)
        assertEquals(emptyList<String>(), reiniciada.caminho)
        assertEquals(emptyList<Any>(), reiniciada.pistas)
        assertTrue(avancada.concluida)
    }

    @Test
    fun `caso sem cena inicial valida nao inicia`() {
        assertNull(mecanismo.iniciar(caso.copy(cenaInicial = "fantasma")))
    }

    @Test
    fun `progresso reconstroi a mesma sessao deterministicamente`() {
        val original = aplicar("abertura-a", "sala-b", "corredor-a")

        val reconstruida = mecanismo.reconstruir(caso, original.progresso())

        assertTrue(reconstruida is ResultadoReconstrucao.Sucesso)
        assertEquals(original, (reconstruida as ResultadoReconstrucao.Sucesso).sessao)
    }

    @Test
    fun `progresso vazio reconstroi a cena inicial`() {
        val reconstruida = mecanismo.reconstruir(caso, ProgressoCaso(caso.id))

        assertEquals(sessaoInicial(), (reconstruida as ResultadoReconstrucao.Sucesso).sessao)
    }

    @Test
    fun `progresso com escolha que nao existe mais e reportado`() {
        val progresso = ProgressoCaso(caso.id, listOf("abertura-a", "escolha-removida"))

        val resultado = mecanismo.reconstruir(caso, progresso)

        assertTrue(resultado is ResultadoReconstrucao.ProgressoIncompativel)
        assertEquals(1, (resultado as ResultadoReconstrucao.ProgressoIncompativel).passo)
        assertEquals("escolha-removida", resultado.escolhaId)
    }

    @Test
    fun `progresso de outro caso e recusado`() {
        val resultado = mecanismo.reconstruir(caso, ProgressoCaso("outro-caso", listOf("abertura-a")))

        assertTrue(resultado is ResultadoReconstrucao.ProgressoIncompativel)
    }
}

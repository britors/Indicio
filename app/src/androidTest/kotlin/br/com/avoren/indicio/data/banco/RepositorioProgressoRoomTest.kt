package br.com.avoren.indicio.data.banco

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import br.com.avoren.indicio.domain.armazenamento.ResultadoArmazenamento
import br.com.avoren.indicio.fake.CasoFixtures
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.model.sessao.SessaoInvestigacao
import br.com.avoren.indicio.domain.narrativa.MecanismoNarrativo
import br.com.avoren.indicio.domain.narrativa.ResultadoEscolha
import br.com.avoren.indicio.domain.narrativa.ResultadoReconstrucao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Exercita a persistência real contra um banco Room em memória.
 */
class RepositorioProgressoRoomTest {

    private lateinit var banco: BancoIndicio
    private lateinit var repositorio: RepositorioProgressoRoom

    private val caso = CasoFixtures.casoValido()
    private val mecanismo = MecanismoNarrativo()

    @Before
    fun abrirBanco() {
        val contexto = InstrumentationRegistry.getInstrumentation().context
        banco = Room.inMemoryDatabaseBuilder(contexto, BancoIndicio::class.java)
            .allowMainThreadQueries()
            .build()
        repositorio = RepositorioProgressoRoom(
            progressoDao = banco.progressoDao(),
            conclusaoDao = banco.conclusaoDao(),
            agora = { 1_000L },
        )
    }

    @After
    fun fecharBanco() = banco.close()

    private fun sessaoApos(vararg escolhas: String): SessaoInvestigacao =
        escolhas.fold(requireNotNull(mecanismo.iniciar(caso))) { sessao, id ->
            (mecanismo.escolher(caso, sessao, id) as ResultadoEscolha.Aplicada).sessao
        }

    @Test
    fun salvaERestauraCenaEPistas() = runBlocking {
        val sessao = sessaoApos("abertura-a", "sala-b")

        assertTrue(repositorio.salvar(sessao).bemSucedido)

        val salvo = requireNotNull(repositorio.progresso(caso.id))
        assertEquals("corredor", salvo.cenaAtual)
        assertEquals(listOf("abertura-a", "sala-b"), salvo.escolhas)
        assertEquals(sessao.pistas.map(Pista::id), salvo.pistasDescobertas)
        assertNull(salvo.desfechoAlcancado)
    }

    @Test
    fun progressoSalvoReconstroiExatamenteAMesmaSessao() = runBlocking {
        val original = sessaoApos("abertura-a", "sala-b", "corredor-a")
        repositorio.salvar(original)

        val salvo = requireNotNull(repositorio.progresso(caso.id))
        val reconstruida = mecanismo.reconstruir(caso, salvo.paraReconstrucao())

        assertTrue(reconstruida is ResultadoReconstrucao.Sucesso)
        assertEquals(original, (reconstruida as ResultadoReconstrucao.Sucesso).sessao)
    }

    @Test
    fun salvarDuasVezesMantemUmUnicoRegistroPorCaso() = runBlocking {
        repositorio.salvar(sessaoApos("abertura-a"))
        repositorio.salvar(sessaoApos("abertura-a", "sala-b"))

        val salvo = requireNotNull(repositorio.progresso(caso.id))
        assertEquals("corredor", salvo.cenaAtual)
        assertEquals(2, salvo.escolhas.size)
    }

    @Test
    fun concluirRegistraNoHistorico() = runBlocking {
        val concluida = sessaoApos("abertura-b", "corredor-a")

        repositorio.salvar(concluida, tituloDesfecho = "Caso resolvido")

        val historico = repositorio.historico().first()
        assertEquals(1, historico.size)
        assertEquals("Caso resolvido", historico.first().tituloDesfecho)
        assertEquals("encerramento", historico.first().cenaFinal)
    }

    @Test
    fun reiniciarApagaOProgressoEPreservaOHistorico() = runBlocking {
        repositorio.salvar(sessaoApos("abertura-b", "corredor-a"), tituloDesfecho = "Caso resolvido")

        assertTrue(repositorio.reiniciar(caso.id).bemSucedido)

        assertNull(repositorio.progresso(caso.id))
        assertEquals(1, repositorio.historico().first().size)
    }

    @Test
    fun maisRecenteAcompanhaAUltimaSessaoMexida() = runBlocking {
        assertNull(repositorio.maisRecente().first())

        repositorio.salvar(sessaoApos("abertura-a"))

        assertEquals(caso.id, repositorio.maisRecente().first()?.casoId)
    }

    @Test
    fun escritaEmBancoFechadoViraFalhaDescritiva() = runBlocking {
        banco.close()

        val resultado = repositorio.salvar(sessaoApos("abertura-a"))

        assertTrue(resultado is ResultadoArmazenamento.Falha)
        assertTrue((resultado as ResultadoArmazenamento.Falha).causa.contains("salvar o progresso"))
    }

    @Test
    fun leituraEmBancoFechadoNaoDerrubaOAplicativo() = runBlocking {
        banco.close()

        assertNull(repositorio.progresso(caso.id))
    }
}

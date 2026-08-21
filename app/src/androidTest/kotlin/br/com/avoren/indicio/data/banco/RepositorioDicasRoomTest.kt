package br.com.avoren.indicio.data.banco

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import br.com.avoren.indicio.domain.armazenamento.ResultadoArmazenamento
import br.com.avoren.indicio.domain.dica.DicaRegistrada
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Protege a cota transacional por caso contra regressões no adaptador Room. */
class RepositorioDicasRoomTest {

    private lateinit var banco: BancoIndicio
    private lateinit var repositorio: RepositorioDicasRoom

    @Before
    fun abrirBanco() {
        val contexto = InstrumentationRegistry.getInstrumentation().context
        banco = Room.inMemoryDatabaseBuilder(contexto, BancoIndicio::class.java)
            .allowMainThreadQueries()
            .build()
        repositorio = RepositorioDicasRoom(banco.dicaDao())
    }

    @After
    fun fecharBanco() = banco.close()

    @Test
    fun cotaDeTresEIndependenteParaCadaCaso() = runBlocking {
        repeat(LIMITE) { indice ->
            assertTrue(registrar("caso-a", "cena-$indice"))
        }

        assertFalse(registrar("caso-a", "cena-extra"))
        assertTrue(registrar("caso-b", "cena-0"))
        assertEquals(LIMITE, quantidade("caso-a"))
        assertEquals(1, quantidade("caso-b"))
    }

    private suspend fun registrar(casoId: String, cenaId: String): Boolean {
        val resultado = repositorio.registrarSeDisponivel(
            dica = DicaRegistrada(casoId, cenaId, "escolha", USADA_EM),
            inicioDaSemana = INICIO_DA_SEMANA,
            limite = LIMITE,
        )
        return (resultado as ResultadoArmazenamento.Sucesso).valor
    }

    private suspend fun quantidade(casoId: String): Int {
        val resultado = repositorio.quantidadeDoCasoDesde(casoId, INICIO_DA_SEMANA)
        return (resultado as ResultadoArmazenamento.Sucesso).valor
    }

    private companion object {
        const val LIMITE = 3
        const val INICIO_DA_SEMANA = 1_000L
        const val USADA_EM = 2_000L
    }
}

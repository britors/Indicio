package br.com.avoren.indicio.data.preferencias

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import br.com.avoren.indicio.domain.model.preferencias.TamanhoTexto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Exercita o DataStore real em arquivo temporário, incluindo a releitura das
 * preferências depois de o armazenamento ser reaberto — o equivalente a
 * fechar e reabrir o aplicativo.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RepositorioPreferenciasDataStoreTest {

    @get:Rule
    val pasta = TemporaryFolder()

    private lateinit var escopo: CoroutineScope
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repositorio: RepositorioPreferenciasDataStore

    @Before
    fun abrir() {
        escopo = CoroutineScope(UnconfinedTestDispatcher() + Job())
        dataStore = criarDataStore()
        repositorio = RepositorioPreferenciasDataStore(dataStore)
    }

    @After
    fun fechar() = escopo.cancel()

    private fun criarDataStore(): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = escopo,
        produceFile = { pasta.root.resolve("preferencias.preferences_pb") },
    )

    @Test
    fun preferenciasComecamNosValoresMaisConfortaveis() = runBlocking {
        val preferencias = repositorio.preferencias.first()

        assertEquals(TamanhoTexto.GRANDE, preferencias.tamanhoTexto)
        assertEquals(false, preferencias.reduzirMovimentos)
    }

    @Test
    fun tamanhoDoTextoEGravadoELido() = runBlocking {
        assertTrue(repositorio.definirTamanhoTexto(TamanhoTexto.MUITO_GRANDE).bemSucedido)

        assertEquals(TamanhoTexto.MUITO_GRANDE, repositorio.preferencias.first().tamanhoTexto)
    }

    @Test
    fun reducaoDeMovimentosEGravadaELida() = runBlocking {
        assertTrue(repositorio.definirReducaoDeMovimentos(true).bemSucedido)

        assertTrue(repositorio.preferencias.first().reduzirMovimentos)
    }

    @Test
    fun preferenciasSaoAplicadasNovamenteAposReiniciar() = runBlocking {
        repositorio.definirTamanhoTexto(TamanhoTexto.MUITO_GRANDE)
        repositorio.definirReducaoDeMovimentos(true)

        // Encerrar o escopo libera o arquivo, como o processo do app ao morrer;
        // um novo DataStore sobre o mesmo arquivo representa a reabertura.
        escopo.coroutineContext.job.cancelAndJoin()
        escopo = CoroutineScope(UnconfinedTestDispatcher() + Job())

        val depoisDeReiniciar = RepositorioPreferenciasDataStore(criarDataStore())
        val preferencias = depoisDeReiniciar.preferencias.first()

        assertEquals(TamanhoTexto.MUITO_GRANDE, preferencias.tamanhoTexto)
        assertTrue(preferencias.reduzirMovimentos)
    }

    @Test
    fun chaveDesconhecidaCaiParaOPadrao() {
        assertEquals(TamanhoTexto.GRANDE, TamanhoTexto.porChave("minusculo"))
        assertEquals(TamanhoTexto.GRANDE, TamanhoTexto.porChave(null))
    }
}

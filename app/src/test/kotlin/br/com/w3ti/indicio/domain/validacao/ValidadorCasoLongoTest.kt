package br.com.w3ti.indicio.domain.validacao

import br.com.w3ti.indicio.data.caso.RepositorioCasosJson
import br.com.w3ti.indicio.domain.caso.ResultadoCarga
import br.com.w3ti.indicio.domain.model.caso.Caso
import br.com.w3ti.indicio.domain.model.caso.Revelacoes
import br.com.w3ti.indicio.fake.FonteCasosEmMemoria
import java.io.File
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadorCasoLongoTest {

    private val validador = ValidadorCaso()

    @Test
    fun `cena longa sem exatamente duas escolhas e reportada`() = runTest {
        val caso = carregarValido()
        val primeira = caso.cenas.first()
        val escolhaExtra = primeira.escolhas.last().copy(
            id = "escolha-extra",
            texto = "Terceira escolha",
        )
        val invalido = substituir(caso, primeira.copy(escolhas = primeira.escolhas + escolhaExtra))

        assertTrue(
            validador.validar(invalido).any {
                it.cenaId == primeira.id &&
                    it.campo == "escolhas" &&
                    it.mensagem.contains("exatamente 2")
            },
        )
    }

    @Test
    fun `referencia de caderno inexistente e reportada`() = runTest {
        val caso = carregarValido()
        val primeira = caso.cenas.first()
        val invalido = caso.copy(
            cenas = listOf(
                primeira.copy(
                    revelacoes = primeira.revelacoes.copy(
                        conversas = primeira.revelacoes.conversas + "conversa-ausente",
                    ),
                ),
            ) + caso.cenas.drop(1),
        )

        assertTrue(validador.validar(invalido).any { it.campo.contains("revelacoes.conversas") })
    }

    @Test
    fun `transicao para etapa anterior e ciclo sao reportados`() = runTest {
        val caso = carregarValido()
        val orientador = requireNotNull(caso.cena("orientador"))
        val escolha = orientador.escolhas.first().copy(proximaCena = caso.cenaInicial)
        val invalido = substituir(caso, orientador.copy(escolhas = listOf(escolha) + orientador.escolhas.drop(1)))

        val problemas = validador.validar(invalido)

        assertTrue(problemas.any { it.mensagem.contains("voltar nem saltar") })
        assertTrue(problemas.any { it.mensagem.contains("ciclos") })
    }

    @Test
    fun `conversa antes da pessoa e reportada`() = runTest {
        val caso = carregarValido()
        val primeira = caso.cenas.first()
        val invalido = substituir(
            caso,
            primeira.copy(
                revelacoes = Revelacoes(conversas = listOf("conversa-orientador-copias")),
            ),
        )

        assertTrue(validador.validar(invalido).any { it.mensagem.contains("antes de sua pessoa") })
    }

    @Test
    fun `id de escolha precisa ser unico no caso inteiro`() = runTest {
        val caso = carregarValido()
        val gaveta = requireNotNull(caso.cena("gaveta"))
        val repetida = gaveta.escolhas.first().copy(id = "chegada-a")
        val invalido = substituir(caso, gaveta.copy(escolhas = listOf(repetida) + gaveta.escolhas.drop(1)))

        assertTrue(validador.validar(invalido).any { it.campo == "escolhas[].id" })
    }

    private fun substituir(caso: Caso, cena: br.com.w3ti.indicio.domain.model.caso.Cena) =
        caso.copy(cenas = caso.cenas.map { if (it.id == cena.id) cena else it })

    private suspend fun carregarValido(): Caso {
        val repositorio = RepositorioCasosJson(
            fonte = FonteCasosEmMemoria(
                mapOf(
                    RepositorioCasosJson.CAMINHO_CATALOGO to fixture("catalogo.json"),
                    "casos/catalogo-fora-de-ordem.json" to fixture("catalogo-fora-de-ordem.json"),
                    "casos/transmissao-incompleta.json" to fixture("transmissao-incompleta.json"),
                ),
            ),
            dispatcher = UnconfinedTestDispatcher(),
        )
        return (repositorio.caso("catalogo-fora-de-ordem") as ResultadoCarga.Sucesso).valor
    }

    private fun fixture(nome: String) =
        File("../docs/exemplos/esquema-v2/$nome").readText()
}

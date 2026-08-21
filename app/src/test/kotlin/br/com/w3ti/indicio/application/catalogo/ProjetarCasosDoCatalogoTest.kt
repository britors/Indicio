package br.com.w3ti.indicio.application.catalogo

import br.com.w3ti.indicio.domain.model.caso.Catalogo
import br.com.w3ti.indicio.domain.model.caso.Categoria
import br.com.w3ti.indicio.domain.model.caso.ResumoCaso
import br.com.w3ti.indicio.domain.model.sessao.ConclusaoRegistrada
import br.com.w3ti.indicio.domain.model.sessao.ProgressoSalvo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProjetarCasosDoCatalogoTest {

    private val resumo = ResumoCaso(
        id = "caso",
        titulo = "Caso",
        sinopse = "Sinopse",
        categoria = Categoria.MISTERIOS_POLICIAIS,
        disponivel = true,
    )
    private val projetar = ProjetarCasosDoCatalogo()

    @Test
    fun `caso sem registro aparece como nao iniciado`() {
        val caso = projetar(Catalogo(listOf(resumo)), emptyList(), emptyList()).single()

        assertEquals(SituacaoCasoCatalogo.NAO_INICIADO, caso.situacao)
        assertFalse(caso.podeRetomar)
        assertFalse(caso.podeReiniciar)
        assertNull(caso.ultimoAcessoEm)
    }

    @Test
    fun `progresso aberto permite retomar e reiniciar`() {
        val caso = projetar(
            Catalogo(listOf(resumo)),
            listOf(progresso(atualizadoEm = 200L)),
            emptyList(),
        ).single()

        assertEquals(SituacaoCasoCatalogo.EM_ANDAMENTO, caso.situacao)
        assertTrue(caso.podeRetomar)
        assertTrue(caso.podeReiniciar)
        assertEquals(200L, caso.ultimoAcessoEm)
    }

    @Test
    fun `historico mantem resolvido durante uma nova investigacao`() {
        val caso = projetar(
            Catalogo(listOf(resumo)),
            listOf(progresso(atualizadoEm = 300L)),
            listOf(conclusao(concluidoEm = 100L)),
        ).single()

        assertEquals(SituacaoCasoCatalogo.RESOLVIDO, caso.situacao)
        assertTrue(caso.emAndamento)
        assertTrue(caso.podeRetomar)
        assertEquals(300L, caso.ultimoAcessoEm)
    }

    @Test
    fun `caso resolvido usa a conclusao mais recente como ultimo acesso`() {
        val caso = projetar(
            Catalogo(listOf(resumo)),
            emptyList(),
            listOf(conclusao(100L), conclusao(500L)),
        ).single()

        assertEquals(SituacaoCasoCatalogo.RESOLVIDO, caso.situacao)
        assertFalse(caso.podeRetomar)
        assertTrue(caso.podeReiniciar)
        assertEquals(500L, caso.ultimoAcessoEm)
    }

    private fun progresso(atualizadoEm: Long) = ProgressoSalvo(
        casoId = resumo.id,
        cenaAtual = "cena",
        escolhas = listOf("escolha"),
        pistasDescobertas = emptyList(),
        desfechoAlcancado = null,
        atualizadoEm = atualizadoEm,
    )

    private fun conclusao(concluidoEm: Long) = ConclusaoRegistrada(
        casoId = resumo.id,
        cenaFinal = "fim",
        tituloDesfecho = "Resolvido",
        pistas = emptyList(),
        concluidoEm = concluidoEm,
    )
}

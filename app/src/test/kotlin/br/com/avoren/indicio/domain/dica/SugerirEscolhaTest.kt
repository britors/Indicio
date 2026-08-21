package br.com.avoren.indicio.domain.dica

import br.com.avoren.indicio.domain.model.caso.Caso
import br.com.avoren.indicio.domain.model.caso.Categoria
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Escolha
import br.com.avoren.indicio.domain.model.caso.Imagem
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.model.sessao.SessaoInvestigacao
import org.junit.Assert.assertEquals
import org.junit.Test

class SugerirEscolhaTest {

    @Test
    fun recomendaOCaminhoMaisCurtoAteUmaDescobertaNova() {
        val escolhaLonga = Escolha("longa", "Examinar o corredor", "corredor")
        val escolhaCurta = Escolha("curta", "Conversar com a curadora", "curadora")
        val caso = caso(
            Cena("inicio", texto = "Início", imagem = imagem, escolhas = listOf(escolhaLonga, escolhaCurta)),
            Cena("corredor", texto = "Corredor", imagem = imagem, escolhas = listOf(Escolha("seguir", "Seguir", "pista-longa"))),
            Cena("pista-longa", texto = "Pista", imagem = imagem, pista = Pista("p1", "Pista 1", "Descrição")),
            Cena("curadora", texto = "Curadora", imagem = imagem, pista = Pista("p2", "Pista 2", "Descrição")),
        )

        val sugestao = SugerirEscolha().executar(caso, SessaoInvestigacao("caso", "inicio"))

        assertEquals("curta", sugestao?.id)
    }

    @Test
    fun ignoraPistaJaConhecidaAoCompararOsCaminhos() {
        val conhecida = Pista("conhecida", "Conhecida", "Já descoberta")
        val nova = Pista("nova", "Nova", "Ainda oculta")
        val caso = caso(
            Cena(
                "inicio",
                texto = "Início",
                imagem = imagem,
                escolhas = listOf(
                    Escolha("a", "Voltar ao conhecido", "conhecida"),
                    Escolha("b", "Procurar outra fonte", "intermediaria"),
                ),
            ),
            Cena("conhecida", texto = "Conhecida", imagem = imagem, pista = conhecida),
            Cena("intermediaria", texto = "Intermediária", imagem = imagem, escolhas = listOf(Escolha("seguir", "Seguir", "nova"))),
            Cena("nova", texto = "Nova", imagem = imagem, pista = nova),
        )
        val sessao = SessaoInvestigacao("caso", "inicio", pistas = listOf(conhecida))

        assertEquals("b", SugerirEscolha().executar(caso, sessao)?.id)
    }

    private fun caso(vararg cenas: Cena) = Caso(
        id = "caso",
        titulo = "Caso",
        sinopse = "Sinopse",
        categoria = Categoria.FUTEBOL,
        cenaInicial = "inicio",
        cenas = cenas.toList(),
    )

    private companion object {
        val imagem = Imagem("arte", "Descrição")
    }
}

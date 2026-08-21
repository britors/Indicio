package br.com.w3ti.indicio.fake

import br.com.w3ti.indicio.domain.model.caso.Caso
import br.com.w3ti.indicio.domain.model.caso.Categoria
import br.com.w3ti.indicio.domain.model.caso.Cena
import br.com.w3ti.indicio.domain.model.caso.Desfecho
import br.com.w3ti.indicio.domain.model.caso.Escolha
import br.com.w3ti.indicio.domain.model.caso.Imagem
import br.com.w3ti.indicio.domain.model.caso.Pista
import br.com.w3ti.indicio.domain.model.caso.TipoCena

/**
 * Caso mínimo, válido e propositalmente genérico.
 *
 * Os testes de validação partem dele e introduzem um defeito por vez, de modo
 * que cada teste demonstre exatamente uma regra.
 */
object CasoFixtures {

    const val ID = "caso-exemplo"

    fun imagem(recurso: String = "cena_exemplo") = Imagem(
        recurso = recurso,
        descricaoAcessivel = "Descrição acessível da cena.",
    )

    fun pista(id: String = "pista-exemplo") = Pista(
        id = id,
        titulo = "Uma observação",
        descricao = "Algo no ambiente não está no lugar de sempre.",
    )

    fun desfecho() = Desfecho(
        titulo = "Caso resolvido",
        mensagem = "A investigação chega a um encerramento tranquilo.",
        explicacaoPistas = "As observações reunidas apontavam para a mesma explicação.",
    )

    fun escolha(
        id: String,
        proximaCena: String,
        pista: Pista? = null,
    ) = Escolha(
        id = id,
        texto = "Seguir por este caminho",
        proximaCena = proximaCena,
        pista = pista,
    )

    fun cenaComum(
        id: String,
        primeiraPara: String,
        segundaPara: String,
        pista: Pista? = null,
    ) = Cena(
        id = id,
        tipo = TipoCena.COMUM,
        texto = "Texto narrativo da cena $id.",
        imagem = imagem(),
        pista = pista,
        escolhas = listOf(
            escolha("$id-a", primeiraPara),
            escolha("$id-b", segundaPara),
        ),
    )

    fun cenaFinal(id: String, desfecho: Desfecho? = desfecho()) = Cena(
        id = id,
        tipo = TipoCena.FINAL,
        texto = "Texto narrativo do encerramento.",
        imagem = imagem(),
        desfecho = desfecho,
    )

    /**
     * Grafo válido: abertura ramifica em dois caminhos que se reencontram no
     * mesmo final positivo.
     */
    fun casoValido(cenas: List<Cena>? = null) = Caso(
        id = ID,
        titulo = "Caso de exemplo",
        sinopse = "Uma sinopse curta para os testes.",
        categoria = Categoria.MISTERIOS_POLICIAIS,
        cenaInicial = "abertura",
        cenas = cenas ?: listOf(
            cenaComum("abertura", primeiraPara = "sala", segundaPara = "corredor"),
            cenaComum("sala", primeiraPara = "encerramento", segundaPara = "corredor", pista = pista()),
            cenaComum("corredor", primeiraPara = "encerramento", segundaPara = "encerramento"),
            cenaFinal("encerramento"),
        ),
    )
}

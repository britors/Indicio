package br.com.w3ti.indicio.domain.model.caso

/**
 * Ilustração de uma cena.
 *
 * [descricaoAcessivel] é obrigatória: nenhuma pista pode depender apenas da
 * imagem, e leitores de tela precisam de um texto equivalente.
 */
data class Imagem(
    val recurso: String,
    val descricaoAcessivel: String,
)

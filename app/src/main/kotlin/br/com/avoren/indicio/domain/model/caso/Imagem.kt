package br.com.avoren.indicio.domain.model.caso

import kotlinx.serialization.Serializable

/**
 * Ilustração de uma cena.
 *
 * [descricaoAcessivel] é obrigatória: nenhuma pista pode depender apenas da
 * imagem, e leitores de tela precisam de um texto equivalente.
 */
@Serializable
data class Imagem(
    val recurso: String,
    val descricaoAcessivel: String,
)

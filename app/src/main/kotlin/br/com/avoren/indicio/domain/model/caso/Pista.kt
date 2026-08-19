package br.com.avoren.indicio.domain.model.caso

import kotlinx.serialization.Serializable

/**
 * Pista descoberta pelo jogador.
 *
 * A mesma pista pode ser alcançada por caminhos diferentes; nesse caso o [id]
 * se repete no arquivo e o conteúdo precisa ser idêntico.
 */
@Serializable
data class Pista(
    val id: String,
    val titulo: String,
    val descricao: String,
)

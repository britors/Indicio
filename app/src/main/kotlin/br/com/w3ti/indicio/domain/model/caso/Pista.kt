package br.com.w3ti.indicio.domain.model.caso

/**
 * Pista descoberta pelo jogador.
 *
 * A mesma pista pode ser alcançada por caminhos diferentes; nesse caso o [id]
 * se repete no arquivo e o conteúdo precisa ser idêntico.
 */
data class Pista(
    val id: String,
    val titulo: String,
    val descricao: String,
    val relevancia: String? = null,
)

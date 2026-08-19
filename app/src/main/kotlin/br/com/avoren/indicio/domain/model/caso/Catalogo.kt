package br.com.avoren.indicio.domain.model.caso

import kotlinx.serialization.Serializable

/**
 * Entrada do catálogo.
 *
 * Casos ainda não escritos aparecem com [disponivel] falso e sem [arquivo],
 * para que o catálogo indique o que virá sem prometer o que não existe.
 */
@Serializable
data class ResumoCaso(
    val id: String,
    val titulo: String,
    val sinopse: String,
    val categoria: Categoria,
    val arquivo: String? = null,
    val disponivel: Boolean = false,
)

/** Índice de todos os casos conhecidos pelo aplicativo. */
@Serializable
data class Catalogo(
    val versaoEsquema: Int,
    val casos: List<ResumoCaso>,
) {
    fun disponiveis(): List<ResumoCaso> = casos.filter(ResumoCaso::disponivel)

    fun porCategoria(): Map<Categoria, List<ResumoCaso>> =
        Categoria.entries.associateWith { categoria ->
            casos.filter { it.categoria == categoria }
        }

    fun resumo(id: String): ResumoCaso? = casos.firstOrNull { it.id == id }
}

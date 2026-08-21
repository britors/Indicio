package br.com.w3ti.indicio.domain.model.caso

/**
 * Entrada do catálogo.
 *
 * Casos ainda não escritos aparecem com [disponivel] falso e sem [arquivo],
 * para que o catálogo indique o que virá sem prometer o que não existe.
 */
data class ResumoCaso(
    val id: String,
    val titulo: String,
    val sinopse: String,
    val categoria: Categoria,
    val disponivel: Boolean = false,
    val revisao: RevisaoCaso? = null,
    val imagem: Imagem? = null,
)

/** Índice de todos os casos conhecidos pelo aplicativo. */
data class Catalogo(
    val casos: List<ResumoCaso>,
) {
    fun disponiveis(): List<ResumoCaso> = casos.filter(ResumoCaso::disponivel)

    fun porCategoria(): Map<Categoria, List<ResumoCaso>> =
        Categoria.entries.associateWith { categoria ->
            casos.filter { it.categoria == categoria }
        }

    fun resumo(id: String): ResumoCaso? = casos.firstOrNull { it.id == id }
}

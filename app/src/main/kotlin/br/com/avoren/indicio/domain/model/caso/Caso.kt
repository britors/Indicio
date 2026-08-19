package br.com.avoren.indicio.domain.model.caso

import kotlinx.serialization.Serializable

/**
 * Um caso completo, carregado de um único arquivo JSON.
 *
 * O grafo da história vive inteiramente nos dados: o código nunca decide para
 * onde uma escolha leva, apenas segue [Escolha.proximaCena].
 */
@Serializable
data class Caso(
    val versaoEsquema: Int,
    val id: String,
    val titulo: String,
    val sinopse: String,
    val categoria: Categoria,
    val cenaInicial: String,
    val cenas: List<Cena>,
) {
    /**
     * Índice por identificador. Casos com ids repetidos são rejeitados pelo
     * validador antes de chegarem ao domínio, portanto o índice é confiável.
     */
    private val indice: Map<String, Cena> by lazy { cenas.associateBy(Cena::id) }

    fun cena(id: String): Cena? = indice[id]

    /** Todas as pistas declaradas no arquivo, sem repetição de identificador. */
    fun pistas(): List<Pista> = cenas
        .flatMap { cena -> listOfNotNull(cena.pista) + cena.escolhas.mapNotNull(Escolha::pista) }
        .distinctBy(Pista::id)
}

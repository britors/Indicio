package br.com.avoren.indicio.domain.model.caso

/**
 * Um caso completo, carregado de um único arquivo JSON.
 *
 * O grafo da história vive inteiramente nos dados: o código nunca decide para
 * onde uma escolha leva, apenas segue [Escolha.proximaCena].
 */
data class Caso(
    val id: String,
    val titulo: String,
    val sinopse: String,
    val categoria: Categoria,
    val cenaInicial: String,
    val cenas: List<Cena>,
    val revisao: RevisaoCaso = RevisaoCaso.V1,
    val etapas: List<Etapa> = emptyList(),
    val caderno: CadernoCaso = CadernoCaso(),
    val lembrancas: List<Lembranca> = emptyList(),
) {
    /**
     * Índice por identificador. Casos com ids repetidos são rejeitados pelo
     * validador antes de chegarem ao domínio, portanto o índice é confiável.
     */
    private val indice: Map<String, Cena> by lazy { cenas.associateBy(Cena::id) }

    fun cena(id: String): Cena? = indice[id]

    fun etapa(id: String): Etapa? = etapas.firstOrNull { it.id == id }

    fun objetivo(id: String): Objetivo? = etapas
        .flatMap(Etapa::objetivos)
        .firstOrNull { it.id == id }

    fun lembranca(id: String): Lembranca? = lembrancas.firstOrNull { it.id == id }

    /** Todas as pistas declaradas no arquivo, sem repetição de identificador. */
    fun pistas(): List<Pista> = (
        caderno.pistas + cenas
            .flatMap { cena -> listOfNotNull(cena.pista) + cena.escolhas.mapNotNull(Escolha::pista) }
        )
        .distinctBy(Pista::id)
}

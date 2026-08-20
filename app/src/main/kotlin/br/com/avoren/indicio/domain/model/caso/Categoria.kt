package br.com.avoren.indicio.domain.model.caso

/**
 * Categorias previstas para o catálogo.
 *
 * O domínio conhece a categoria e seu vocabulário, mas não conhece como ela é
 * representada em JSON. O [rotulo] é o texto exibido; o produto é publicado
 * apenas em português do Brasil.
 */
enum class Categoria(val rotulo: String) {
    FUTEBOL("Futebol"),

    MISTERIOS_POLICIAIS("Mistérios policiais"),

    FAROESTE("Faroeste"),

    ROMANCES_CLASSICOS("Romances clássicos"),

    CULTURA_POPULAR_ANTIGA("Desenhos e cultura popular antigos"),
}

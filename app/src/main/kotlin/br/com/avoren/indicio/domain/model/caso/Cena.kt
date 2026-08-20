package br.com.avoren.indicio.domain.model.caso

/** Natureza da cena dentro do grafo da história. */
enum class TipoCena {
    /** Cena narrativa comum: exige exatamente duas escolhas. */
    COMUM,

    /** Cena terminal: exige metadados de conclusão e não tem escolhas. */
    FINAL,
}

/**
 * Uma cena da história.
 *
 * O texto narrado pode diferir do texto exibido quando a leitura em voz alta
 * precisa de pontuação ou fraseado próprios; por padrão narra-se o mesmo texto.
 */
data class Cena(
    val id: String,
    val tipo: TipoCena = TipoCena.COMUM,
    val texto: String,
    val imagem: Imagem,
    val narracao: String? = null,
    val pista: Pista? = null,
    val escolhas: List<Escolha> = emptyList(),
    val desfecho: Desfecho? = null,
    val etapaId: String? = null,
    val objetivoId: String? = null,
    val pontoDePausa: Boolean = false,
    val revelacoes: Revelacoes = Revelacoes(),
) {
    /** Texto entregue ao mecanismo de narração. */
    val textoNarrado: String
        get() = narracao?.takeIf { it.isNotBlank() } ?: texto
}

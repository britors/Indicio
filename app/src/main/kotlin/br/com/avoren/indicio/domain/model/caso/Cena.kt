package br.com.avoren.indicio.domain.model.caso

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Natureza da cena dentro do grafo da história. */
@Serializable
enum class TipoCena {
    /** Cena narrativa comum: exige exatamente duas escolhas. */
    @SerialName("comum")
    COMUM,

    /** Cena terminal: exige metadados de conclusão e não tem escolhas. */
    @SerialName("final")
    FINAL,
}

/**
 * Uma cena da história.
 *
 * O texto narrado pode diferir do texto exibido quando a leitura em voz alta
 * precisa de pontuação ou fraseado próprios; por padrão narra-se o mesmo texto.
 */
@Serializable
data class Cena(
    val id: String,
    val tipo: TipoCena = TipoCena.COMUM,
    val texto: String,
    val imagem: Imagem,
    val narracao: String? = null,
    val pista: Pista? = null,
    val escolhas: List<Escolha> = emptyList(),
    val desfecho: Desfecho? = null,
) {
    /** Texto entregue ao mecanismo de narração. */
    val textoNarrado: String
        get() = narracao?.takeIf { it.isNotBlank() } ?: texto
}

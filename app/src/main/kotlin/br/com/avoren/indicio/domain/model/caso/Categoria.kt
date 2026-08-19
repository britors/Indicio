package br.com.avoren.indicio.domain.model.caso

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Categorias previstas para o catálogo.
 *
 * O identificador serializado é estável e nunca deve mudar: ele aparece nos
 * arquivos JSON dos casos. O [rotulo] é o texto exibido; o produto é publicado
 * apenas em português do Brasil.
 */
@Serializable
enum class Categoria(val rotulo: String) {
    @SerialName("futebol")
    FUTEBOL("Futebol"),

    @SerialName("misterios_policiais")
    MISTERIOS_POLICIAIS("Mistérios policiais"),

    @SerialName("faroeste")
    FAROESTE("Faroeste"),

    @SerialName("romances_classicos")
    ROMANCES_CLASSICOS("Romances clássicos"),

    @SerialName("cultura_popular_antiga")
    CULTURA_POPULAR_ANTIGA("Desenhos e cultura popular antigos"),
}

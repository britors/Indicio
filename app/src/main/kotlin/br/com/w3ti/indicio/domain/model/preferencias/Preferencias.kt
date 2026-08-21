package br.com.w3ti.indicio.domain.model.preferencias

/**
 * Tamanho do texto narrativo.
 *
 * O produto oferece apenas dois tamanhos, ambos confortáveis; não existe uma
 * opção "pequena".
 */
enum class TamanhoTexto(val chave: String) {
    GRANDE("grande"),
    MUITO_GRANDE("muito_grande"),
    ;

    companion object {
        fun porChave(chave: String?): TamanhoTexto =
            entries.firstOrNull { it.chave == chave } ?: GRANDE
    }
}

/** Preferências de leitura e conforto. */
data class Preferencias(
    val tamanhoTexto: TamanhoTexto = TamanhoTexto.GRANDE,
    val reduzirMovimentos: Boolean = false,
)

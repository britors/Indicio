package br.com.w3ti.indicio.domain.validacao

/**
 * Um problema encontrado na estrutura de um caso.
 *
 * Cada problema aponta o caso, a cena (quando aplicável) e o campo, para que
 * quem escreve histórias localize o erro sem ler o código.
 */
data class ProblemaValidacao(
    val casoId: String,
    val cenaId: String? = null,
    val campo: String,
    val mensagem: String,
) {
    fun mensagemLegivel(): String = buildString {
        append("caso \"").append(casoId).append('"')
        if (cenaId != null) {
            append(", cena \"").append(cenaId).append('"')
        }
        append(", campo \"").append(campo).append("\": ").append(mensagem)
    }
}

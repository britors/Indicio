package br.com.w3ti.indicio.domain.model.caso

/** Identifica o contrato e a revisão narrativa usados por um caso. */
data class RevisaoCaso(
    val esquema: Int,
    val conteudo: Int,
) {
    companion object {
        val V1 = RevisaoCaso(esquema = 1, conteudo = 1)
    }
}

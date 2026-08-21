package br.com.w3ti.indicio.domain.model.sessao

import br.com.w3ti.indicio.domain.model.caso.RevisaoCaso

/**
 * Progresso salvável de um caso.
 *
 * Guarda apenas a sequência de escolhas aplicadas: cena atual e pistas são
 * derivadas ao reproduzir essa sequência sobre o caso. Assim o salvamento não
 * duplica informação que já está no JSON e continua correto se a redação de
 * uma cena mudar.
 */
data class ProgressoCaso(
    val casoId: String,
    val escolhas: List<String> = emptyList(),
    val revisao: RevisaoCaso = RevisaoCaso.V1,
) {
    val iniciado: Boolean get() = escolhas.isNotEmpty()
}

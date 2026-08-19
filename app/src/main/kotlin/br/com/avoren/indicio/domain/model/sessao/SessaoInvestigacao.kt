package br.com.avoren.indicio.domain.model.sessao

import br.com.avoren.indicio.domain.model.caso.Desfecho
import br.com.avoren.indicio.domain.model.caso.Pista

/**
 * Estado completo de uma investigação em andamento.
 *
 * É imutável: cada escolha produz uma nova sessão, o que torna a transição
 * atômica e o estado reproduzível a partir de [progresso].
 */
data class SessaoInvestigacao(
    val casoId: String,
    val cenaAtual: String,
    val caminho: List<String> = emptyList(),
    val pistas: List<Pista> = emptyList(),
    val desfecho: Desfecho? = null,
) {
    /** Uma sessão concluída chegou a um final positivo e não aceita escolhas. */
    val concluida: Boolean get() = desfecho != null

    fun progresso(): ProgressoCaso = ProgressoCaso(casoId = casoId, escolhas = caminho)

    /**
     * Acrescenta a pista, se ainda não descoberta.
     *
     * A ordem de descoberta é preservada: é ela que a tela de conclusão usa
     * para retomar o raciocínio na sequência em que o jogador o construiu.
     */
    internal fun comPista(pista: Pista?): SessaoInvestigacao = when {
        pista == null -> this
        pistas.any { it.id == pista.id } -> this
        else -> copy(pistas = pistas + pista)
    }
}

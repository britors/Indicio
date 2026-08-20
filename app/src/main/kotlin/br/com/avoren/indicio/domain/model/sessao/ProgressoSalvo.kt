package br.com.avoren.indicio.domain.model.sessao

import br.com.avoren.indicio.domain.model.caso.RevisaoCaso

/**
 * Progresso como está gravado no aparelho.
 *
 * [paraReconstrucao] devolve o mínimo necessário para o mecanismo narrativo
 * reproduzir a sessão; os demais campos existem para decidir sobre "Continuar"
 * e montar o histórico sem abrir o JSON do caso.
 */
data class ProgressoSalvo(
    val casoId: String,
    val cenaAtual: String,
    val escolhas: List<String>,
    val pistasDescobertas: List<String>,
    val desfechoAlcancado: String?,
    val atualizadoEm: Long,
    val revisao: RevisaoCaso = RevisaoCaso.V1,
) {
    val concluido: Boolean get() = desfechoAlcancado != null

    fun paraReconstrucao(): ProgressoCaso = ProgressoCaso(
        casoId = casoId,
        escolhas = escolhas,
        revisao = revisao,
    )
}

/** Uma conclusão já registrada no histórico. */
data class ConclusaoRegistrada(
    val casoId: String,
    val cenaFinal: String,
    val tituloDesfecho: String,
    val pistas: List<String>,
    val concluidoEm: Long,
    val revisao: RevisaoCaso = RevisaoCaso.V1,
)

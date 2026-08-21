package br.com.w3ti.indicio.application.catalogo

import br.com.w3ti.indicio.domain.model.caso.Catalogo
import br.com.w3ti.indicio.domain.model.caso.ResumoCaso
import br.com.w3ti.indicio.domain.model.sessao.ConclusaoRegistrada
import br.com.w3ti.indicio.domain.model.sessao.ProgressoSalvo

/** Estado editorial de uma investigação no catálogo. */
enum class SituacaoCasoCatalogo {
    NAO_INICIADO,
    EM_ANDAMENTO,
    RESOLVIDO,
}

/**
 * Projeção de leitura usada pelo catálogo.
 *
 * [emAndamento] permanece separado de [situacao] porque um caso já resolvido
 * pode estar sendo investigado novamente. Assim, o histórico não é apagado
 * nem escondido durante uma nova tentativa.
 */
data class CasoDoCatalogo(
    val resumo: ResumoCaso,
    val situacao: SituacaoCasoCatalogo,
    val emAndamento: Boolean,
    val ultimoAcessoEm: Long?,
) {
    val podeRetomar: Boolean get() = emAndamento
    val podeReiniciar: Boolean get() = situacao != SituacaoCasoCatalogo.NAO_INICIADO || emAndamento
}

/** Consolida catálogo, progresso atual e histórico sem levar regras para a UI. */
class ProjetarCasosDoCatalogo {

    operator fun invoke(
        catalogo: Catalogo,
        progressos: List<ProgressoSalvo>,
        conclusoes: List<ConclusaoRegistrada>,
    ): List<CasoDoCatalogo> {
        val progressoPorCaso = progressos.associateBy(ProgressoSalvo::casoId)
        val conclusoesPorCaso = conclusoes.groupBy(ConclusaoRegistrada::casoId)

        return catalogo.casos.map { resumo ->
            val progresso = progressoPorCaso[resumo.id]
            val conclusoesDoCaso = conclusoesPorCaso[resumo.id].orEmpty()
            val resolvido = progresso?.concluido == true || conclusoesDoCaso.isNotEmpty()
            val emAndamento = progresso != null && !progresso.concluido
            val ultimoAcesso = listOfNotNull(
                progresso?.atualizadoEm,
                conclusoesDoCaso.maxOfOrNull(ConclusaoRegistrada::concluidoEm),
            ).maxOrNull()

            CasoDoCatalogo(
                resumo = resumo,
                situacao = when {
                    resolvido -> SituacaoCasoCatalogo.RESOLVIDO
                    emAndamento -> SituacaoCasoCatalogo.EM_ANDAMENTO
                    else -> SituacaoCasoCatalogo.NAO_INICIADO
                },
                emAndamento = emAndamento,
                ultimoAcessoEm = ultimoAcesso,
            )
        }
    }
}

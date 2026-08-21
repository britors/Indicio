package br.com.w3ti.indicio.application.investigacao

import br.com.w3ti.indicio.domain.armazenamento.RepositorioProgresso
import br.com.w3ti.indicio.domain.caso.ErroCarga
import br.com.w3ti.indicio.domain.caso.RepositorioCasos
import br.com.w3ti.indicio.domain.caso.ResultadoCarga
import br.com.w3ti.indicio.domain.model.caso.Caso
import br.com.w3ti.indicio.domain.model.sessao.ProgressoSalvo
import br.com.w3ti.indicio.domain.model.sessao.SessaoInvestigacao
import br.com.w3ti.indicio.domain.narrativa.MecanismoNarrativo
import br.com.w3ti.indicio.domain.narrativa.ResultadoReconstrucao

/** Dados reconstruídos necessários às telas auxiliares de uma investigação. */
data class InvestigacaoCarregada(
    val caso: Caso,
    val sessao: SessaoInvestigacao,
    val progressoSalvo: ProgressoSalvo?,
)

sealed interface ResultadoCarregamentoInvestigacao {
    data class Sucesso(val investigacao: InvestigacaoCarregada) : ResultadoCarregamentoInvestigacao
    data class Falha(val erro: ErroCarga) : ResultadoCarregamentoInvestigacao
    data object ProgressoIncompativel : ResultadoCarregamentoInvestigacao
}

/**
 * Carrega caso e progresso e entrega uma sessão reconstruída pelo domínio.
 *
 * A apresentação não repete a reprodução do grafo e tampouco conhece a fonte
 * JSON ou o banco. Sem progresso, a projeção corresponde à cena inicial.
 */
class CarregarInvestigacao(
    private val repositorioCasos: RepositorioCasos,
    private val repositorioProgresso: RepositorioProgresso,
    private val mecanismo: MecanismoNarrativo = MecanismoNarrativo(),
) {
    suspend operator fun invoke(casoId: String): ResultadoCarregamentoInvestigacao {
        val caso = when (val resultado = repositorioCasos.caso(casoId)) {
            is ResultadoCarga.Falha -> return ResultadoCarregamentoInvestigacao.Falha(resultado.erro)
            is ResultadoCarga.Sucesso -> resultado.valor
        }
        val salvo = repositorioProgresso.progresso(casoId)
        val sessao = if (salvo == null) {
            mecanismo.iniciar(caso)
        } else {
            when (val resultado = mecanismo.reconstruir(caso, salvo.paraReconstrucao())) {
                is ResultadoReconstrucao.Sucesso -> resultado.sessao
                is ResultadoReconstrucao.ProgressoIncompativel -> {
                    return ResultadoCarregamentoInvestigacao.ProgressoIncompativel
                }
            }
        }

        return if (sessao == null) {
            ResultadoCarregamentoInvestigacao.Falha(
                ErroCarga.GrafoInvalido(caso.id, emptyList()),
            )
        } else {
            ResultadoCarregamentoInvestigacao.Sucesso(
                InvestigacaoCarregada(caso, sessao, salvo),
            )
        }
    }
}

/** Política temporal que evita interromper retornos feitos após saídas curtas. */
class DecidirExibicaoDaRetomada(
    private val intervaloMinimoMillis: Long = INTERVALO_PADRAO_MILLIS,
) {
    init {
        require(intervaloMinimoMillis >= 0) { "O intervalo de retomada não pode ser negativo." }
    }

    operator fun invoke(atualizadoEm: Long?, agora: Long): Boolean =
        atualizadoEm != null && agora >= atualizadoEm &&
            agora - atualizadoEm >= intervaloMinimoMillis

    companion object {
        const val INTERVALO_PADRAO_MILLIS: Long = 30L * 60L * 1_000L
    }
}

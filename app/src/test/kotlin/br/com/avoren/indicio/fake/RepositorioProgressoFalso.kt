package br.com.avoren.indicio.fake

import br.com.avoren.indicio.domain.armazenamento.RepositorioProgresso
import br.com.avoren.indicio.domain.armazenamento.ResultadoArmazenamento
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.model.sessao.ConclusaoRegistrada
import br.com.avoren.indicio.domain.model.sessao.ProgressoSalvo
import br.com.avoren.indicio.domain.model.sessao.SessaoInvestigacao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Progresso em memória, com falha de escrita simulável.
 */
class RepositorioProgressoFalso(
    inicial: ProgressoSalvo? = null,
    private val falhaAoGravar: String? = null,
) : RepositorioProgresso {

    private val armazenado = MutableStateFlow(inicial)
    private val conclusoes = MutableStateFlow(emptyList<ConclusaoRegistrada>())

    var salvamentos: Int = 0
        private set

    override fun maisRecente(): Flow<ProgressoSalvo?> = armazenado

    override suspend fun progresso(casoId: String): ProgressoSalvo? =
        armazenado.value?.takeIf { it.casoId == casoId }

    override suspend fun salvar(
        sessao: SessaoInvestigacao,
        tituloDesfecho: String?,
    ): ResultadoArmazenamento<Unit> {
        salvamentos++
        falhaAoGravar?.let { return ResultadoArmazenamento.Falha(it) }

        armazenado.value = ProgressoSalvo(
            casoId = sessao.casoId,
            cenaAtual = sessao.cenaAtual,
            escolhas = sessao.caminho,
            pistasDescobertas = sessao.pistas.map(Pista::id),
            desfechoAlcancado = sessao.desfecho?.let { sessao.cenaAtual },
            atualizadoEm = salvamentos.toLong(),
        )
        if (sessao.concluida) {
            conclusoes.value += ConclusaoRegistrada(
                casoId = sessao.casoId,
                cenaFinal = sessao.cenaAtual,
                tituloDesfecho = tituloDesfecho.orEmpty(),
                pistas = sessao.pistas.map(Pista::id),
                concluidoEm = salvamentos.toLong(),
            )
        }
        return ResultadoArmazenamento.Sucesso(Unit)
    }

    override suspend fun reiniciar(casoId: String): ResultadoArmazenamento<Unit> {
        falhaAoGravar?.let { return ResultadoArmazenamento.Falha(it) }
        armazenado.value = null
        return ResultadoArmazenamento.Sucesso(Unit)
    }

    override fun historico(): Flow<List<ConclusaoRegistrada>> = conclusoes.map { it }
}

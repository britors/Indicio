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

    private val armazenados = MutableStateFlow(
        inicial?.let { mapOf(it.casoId to it) }.orEmpty(),
    )
    private val conclusoes = MutableStateFlow(emptyList<ConclusaoRegistrada>())

    var salvamentos: Int = 0
        private set

    override fun progressos(): Flow<List<ProgressoSalvo>> = armazenados.map { mapa ->
        mapa.values.sortedByDescending(ProgressoSalvo::atualizadoEm)
    }

    override fun maisRecente(): Flow<ProgressoSalvo?> = armazenados.map { mapa ->
        mapa.values.maxByOrNull(ProgressoSalvo::atualizadoEm)
    }

    override suspend fun progresso(casoId: String): ProgressoSalvo? =
        armazenados.value[casoId]

    override suspend fun salvar(
        sessao: SessaoInvestigacao,
        tituloDesfecho: String?,
    ): ResultadoArmazenamento<Unit> {
        salvamentos++
        falhaAoGravar?.let { return ResultadoArmazenamento.Falha(it) }

        val salvo = ProgressoSalvo(
            casoId = sessao.casoId,
            cenaAtual = sessao.cenaAtual,
            escolhas = sessao.caminho,
            pistasDescobertas = sessao.pistas.map(Pista::id),
            desfechoAlcancado = sessao.desfecho?.let { sessao.cenaAtual },
            atualizadoEm = salvamentos.toLong(),
            revisao = sessao.revisao,
        )
        armazenados.value += sessao.casoId to salvo
        if (sessao.concluida) {
            conclusoes.value += ConclusaoRegistrada(
                casoId = sessao.casoId,
                cenaFinal = sessao.cenaAtual,
                tituloDesfecho = tituloDesfecho.orEmpty(),
                pistas = sessao.pistas.map(Pista::id),
                concluidoEm = salvamentos.toLong(),
                revisao = sessao.revisao,
            )
        }
        return ResultadoArmazenamento.Sucesso(Unit)
    }

    override suspend fun reiniciar(casoId: String): ResultadoArmazenamento<Unit> {
        falhaAoGravar?.let { return ResultadoArmazenamento.Falha(it) }
        armazenados.value -= casoId
        return ResultadoArmazenamento.Sucesso(Unit)
    }

    override fun historico(): Flow<List<ConclusaoRegistrada>> = conclusoes.map { it }
}

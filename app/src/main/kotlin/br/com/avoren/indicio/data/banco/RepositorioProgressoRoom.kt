package br.com.avoren.indicio.data.banco

import br.com.avoren.indicio.domain.armazenamento.RepositorioProgresso
import br.com.avoren.indicio.domain.armazenamento.ResultadoArmazenamento
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.model.caso.RevisaoCaso
import br.com.avoren.indicio.domain.model.sessao.ConclusaoRegistrada
import br.com.avoren.indicio.domain.model.sessao.ProgressoSalvo
import br.com.avoren.indicio.domain.model.sessao.SessaoInvestigacao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Persistência de progresso e histórico com Room.
 *
 * Toda operação de escrita é envolvida em um bloco protegido: um banco cheio,
 * corrompido ou indisponível vira uma [ResultadoArmazenamento.Falha] descritiva
 * em vez de derrubar a sessão em andamento.
 */
class RepositorioProgressoRoom(
    private val progressoDao: ProgressoDao,
    private val conclusaoDao: ConclusaoDao,
    private val agora: () -> Long = System::currentTimeMillis,
) : RepositorioProgresso {

    override fun maisRecente(): Flow<ProgressoSalvo?> = progressoDao.maisRecente()
        .map { entidade -> entidade?.paraDominio() }
        .catch { emit(null) }

    override suspend fun progresso(casoId: String): ProgressoSalvo? = try {
        progressoDao.porCaso(casoId)?.paraDominio()
    } catch (erro: Exception) {
        // Leitura falha é tratada como "sem progresso": o jogador recomeça,
        // em vez de encontrar o aplicativo travado.
        null
    }

    override suspend fun salvar(
        sessao: SessaoInvestigacao,
        tituloDesfecho: String?,
    ): ResultadoArmazenamento<Unit> = protegido("salvar o progresso") {
        val instante = agora()

        progressoDao.salvar(
            ProgressoEntidade(
                casoId = sessao.casoId,
                cenaAtual = sessao.cenaAtual,
                escolhas = sessao.caminho,
                pistas = sessao.pistas.map(Pista::id),
                desfechoAlcancado = sessao.desfecho?.let { sessao.cenaAtual },
                atualizadoEm = instante,
                versaoEsquema = sessao.revisao.esquema,
                versaoConteudo = sessao.revisao.conteudo,
            ),
        )

        if (sessao.concluida) {
            conclusaoDao.registrar(
                ConclusaoEntidade(
                    casoId = sessao.casoId,
                    cenaFinal = sessao.cenaAtual,
                    tituloDesfecho = tituloDesfecho ?: sessao.desfecho?.titulo.orEmpty(),
                    pistas = sessao.pistas.map(Pista::id),
                    concluidoEm = instante,
                    versaoEsquema = sessao.revisao.esquema,
                    versaoConteudo = sessao.revisao.conteudo,
                ),
            )
        }
    }

    override suspend fun reiniciar(casoId: String): ResultadoArmazenamento<Unit> =
        protegido("reiniciar o caso") { progressoDao.apagar(casoId) }

    override fun historico(): Flow<List<ConclusaoRegistrada>> = conclusaoDao.historico()
        .map { lista -> lista.map(ConclusaoEntidade::paraDominio) }
        .catch { emit(emptyList()) }

    private inline fun protegido(
        acao: String,
        bloco: () -> Unit,
    ): ResultadoArmazenamento<Unit> = try {
        bloco()
        ResultadoArmazenamento.Sucesso(Unit)
    } catch (erro: Exception) {
        ResultadoArmazenamento.Falha("Não foi possível $acao: ${erro.message ?: "erro de armazenamento"}")
    }
}

private fun ProgressoEntidade.paraDominio() = ProgressoSalvo(
    casoId = casoId,
    cenaAtual = cenaAtual,
    escolhas = escolhas,
    pistasDescobertas = pistas,
    desfechoAlcancado = desfechoAlcancado,
    atualizadoEm = atualizadoEm,
    revisao = RevisaoCaso(versaoEsquema, versaoConteudo),
)

private fun ConclusaoEntidade.paraDominio() = ConclusaoRegistrada(
    casoId = casoId,
    cenaFinal = cenaFinal,
    tituloDesfecho = tituloDesfecho,
    pistas = pistas,
    concluidoEm = concluidoEm,
    revisao = RevisaoCaso(versaoEsquema, versaoConteudo),
)

package br.com.w3ti.indicio.domain.armazenamento

import br.com.w3ti.indicio.domain.model.sessao.ConclusaoRegistrada
import br.com.w3ti.indicio.domain.model.sessao.ProgressoSalvo
import br.com.w3ti.indicio.domain.model.sessao.SessaoInvestigacao
import kotlinx.coroutines.flow.Flow

/**
 * Guarda o andamento das investigações.
 *
 * Declarado como interface para que os testes de apresentação usem dublês em
 * memória, sem Room nem Android.
 */
interface RepositorioProgresso {

    /** Todos os progressos atuais, usados por visões resumidas como o catálogo. */
    fun progressos(): Flow<List<ProgressoSalvo>>

    /** Progresso mexido mais recentemente, base do "Continuar". */
    fun maisRecente(): Flow<ProgressoSalvo?>

    suspend fun progresso(casoId: String): ProgressoSalvo?

    /**
     * Grava o estado atual da sessão. Quando a sessão está concluída, também
     * registra a conclusão no histórico.
     */
    suspend fun salvar(
        sessao: SessaoInvestigacao,
        tituloDesfecho: String? = null,
    ): ResultadoArmazenamento<Unit>

    /** Apaga o progresso do caso preservando o histórico de conclusões. */
    suspend fun reiniciar(casoId: String): ResultadoArmazenamento<Unit>

    fun historico(): Flow<List<ConclusaoRegistrada>>
}

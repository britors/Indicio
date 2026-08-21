package br.com.w3ti.indicio.application.caso

import br.com.w3ti.indicio.domain.armazenamento.RepositorioProgresso
import br.com.w3ti.indicio.domain.caso.RepositorioCasos
import br.com.w3ti.indicio.domain.model.caso.ResumoCaso
import br.com.w3ti.indicio.domain.model.sessao.ProgressoSalvo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** O caso que o botão "Continuar" deve abrir, com o progresso a retomar. */
data class CasoParaContinuar(
    val resumo: ResumoCaso,
    val progresso: ProgressoSalvo,
)

/**
 * Decide se há uma sessão válida para retomar.
 *
 * Não basta existir progresso gravado: o caso precisa continuar no catálogo e
 * disponível, e a sessão não pode já ter chegado ao final. Progresso órfão —
 * de um caso removido ou de um formato antigo — simplesmente não é oferecido,
 * sem mensagem de erro para o jogador.
 */
class ObterCasoParaContinuar(
    private val repositorioCasos: RepositorioCasos,
    private val repositorioProgresso: RepositorioProgresso,
) {

    operator fun invoke(): Flow<CasoParaContinuar?> =
        repositorioProgresso.maisRecente().map { progresso ->
            if (progresso == null || progresso.concluido) return@map null

            val catalogo = repositorioCasos.catalogo().valorOuNulo() ?: return@map null
            val resumo = catalogo.resumo(progresso.casoId) ?: return@map null

            if (!resumo.disponivel) null else CasoParaContinuar(resumo, progresso)
        }
}

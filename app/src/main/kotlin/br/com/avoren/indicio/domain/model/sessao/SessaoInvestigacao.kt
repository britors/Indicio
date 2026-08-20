package br.com.avoren.indicio.domain.model.sessao

import br.com.avoren.indicio.domain.model.caso.AnotacaoCaderno
import br.com.avoren.indicio.domain.model.caso.Caso
import br.com.avoren.indicio.domain.model.caso.Conversa
import br.com.avoren.indicio.domain.model.caso.Desfecho
import br.com.avoren.indicio.domain.model.caso.Etapa
import br.com.avoren.indicio.domain.model.caso.Lembranca
import br.com.avoren.indicio.domain.model.caso.LocalCaso
import br.com.avoren.indicio.domain.model.caso.Objetivo
import br.com.avoren.indicio.domain.model.caso.PessoaCaso
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.model.caso.Revelacoes
import br.com.avoren.indicio.domain.model.caso.RevisaoCaso

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
    val revisao: RevisaoCaso = RevisaoCaso.V1,
    val anotacoesPessoas: List<String> = emptyList(),
    val anotacoesLocais: List<String> = emptyList(),
    val conversas: List<String> = emptyList(),
    val lembrancas: List<Lembranca> = emptyList(),
) {
    /** Uma sessão concluída chegou a um final positivo e não aceita escolhas. */
    val concluida: Boolean get() = desfecho != null

    fun progresso(): ProgressoCaso = ProgressoCaso(
        casoId = casoId,
        escolhas = caminho,
        revisao = revisao,
    )

    fun etapaAtual(caso: Caso): Etapa? = caso
        .cena(cenaAtual)
        ?.etapaId
        ?.let(caso::etapa)

    fun objetivoAtual(caso: Caso): Objetivo? = caso
        .cena(cenaAtual)
        ?.objetivoId
        ?.let(caso::objetivo)

    /** Projeção que nunca inclui conteúdo ainda oculto pelo percurso. */
    fun cadernoRevelado(caso: Caso): CadernoRevelado = CadernoRevelado(
        pistas = pistas,
        pessoas = caso.caderno.pessoas.mapNotNull { pessoa ->
            val reveladas = pessoa.anotacoes.filter { it.id in anotacoesPessoas }
            val conversasDaPessoa = caso.caderno.conversas.filter {
                it.pessoaId == pessoa.id && it.id in conversas
            }
            if (reveladas.isEmpty() && conversasDaPessoa.isEmpty()) null
            else PessoaRevelada(pessoa, reveladas, conversasDaPessoa)
        },
        locais = caso.caderno.locais.mapNotNull { local ->
            val reveladas = local.anotacoes.filter { it.id in anotacoesLocais }
            if (reveladas.isEmpty()) null else LocalRevelado(local, reveladas)
        },
        conversas = conversas.mapNotNull(caso.caderno::conversa),
    )

    /** Até três lembretes, priorizando os essenciais sem mudar a ordem descoberta. */
    fun lembrancasParaRetomada(limite: Int = 3): List<Lembranca> {
        if (limite <= 0) return emptyList()

        val escolhidas = buildSet {
            lembrancas.indices.reversed()
                .filter { lembrancas[it].essencial }
                .take(limite)
                .forEach(::add)
            lembrancas.indices.reversed()
                .filterNot { it in this }
                .take(limite - size)
                .forEach(::add)
        }
        return lembrancas.filterIndexed { indice, _ -> indice in escolhidas }
    }

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

    internal fun comRevelacoes(caso: Caso, revelacoes: Revelacoes): SessaoInvestigacao {
        if (revelacoes.vazias) return this

        return copy(
            pistas = pistas.adicionarSemRepetir(revelacoes.pistas.mapNotNull(caso.caderno::pista)) { it.id },
            anotacoesPessoas = anotacoesPessoas.adicionarSemRepetir(
                revelacoes.anotacoesPessoas.filter { caso.caderno.anotacaoPessoa(it) != null },
            ) { it },
            anotacoesLocais = anotacoesLocais.adicionarSemRepetir(
                revelacoes.anotacoesLocais.filter { caso.caderno.anotacaoLocal(it) != null },
            ) { it },
            conversas = conversas.adicionarSemRepetir(
                revelacoes.conversas.filter { caso.caderno.conversa(it) != null },
            ) { it },
            lembrancas = lembrancas.adicionarSemRepetir(
                revelacoes.lembrancas.mapNotNull(caso::lembranca),
            ) { it.id },
        )
    }
}

data class PessoaRevelada(
    val pessoa: PessoaCaso,
    val anotacoes: List<AnotacaoCaderno>,
    val conversas: List<Conversa>,
)

data class LocalRevelado(
    val local: LocalCaso,
    val anotacoes: List<AnotacaoCaderno>,
)

data class CadernoRevelado(
    val pistas: List<Pista>,
    val pessoas: List<PessoaRevelada>,
    val locais: List<LocalRevelado>,
    val conversas: List<Conversa>,
)

private inline fun <T, K> List<T>.adicionarSemRepetir(
    novos: List<T>,
    chave: (T) -> K,
): List<T> {
    val existentes = mapTo(mutableSetOf(), chave)
    return this + novos.filter { existentes.add(chave(it)) }
}

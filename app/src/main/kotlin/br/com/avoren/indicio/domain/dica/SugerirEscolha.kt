package br.com.avoren.indicio.domain.dica

import br.com.avoren.indicio.domain.model.caso.Caso
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Escolha
import br.com.avoren.indicio.domain.model.caso.Revelacoes
import br.com.avoren.indicio.domain.model.sessao.SessaoInvestigacao

/** Recomenda o caminho mais curto até algum conteúdo ainda não descoberto. */
class SugerirEscolha {

    fun executar(caso: Caso, sessao: SessaoInvestigacao): Escolha? {
        val cenaAtual = caso.cena(sessao.cenaAtual) ?: return null
        return cenaAtual.escolhas.minByOrNull { escolha ->
            distanciaAteDescoberta(caso, sessao, escolha)
        }
    }

    private fun distanciaAteDescoberta(
        caso: Caso,
        sessao: SessaoInvestigacao,
        escolhaInicial: Escolha,
    ): Int {
        if (escolhaInicial.temDescobertaNova(sessao)) return 0

        val visitadas = mutableSetOf<String>()
        val fila = ArrayDeque<Pair<String, Int>>()
        fila.add(escolhaInicial.proximaCena to 1)

        while (fila.isNotEmpty()) {
            val (cenaId, distancia) = fila.removeFirst()
            if (!visitadas.add(cenaId)) continue
            val cena = caso.cena(cenaId) ?: continue
            if (cena.temDescobertaNova(sessao)) return distancia
            cena.escolhas.forEach { fila.add(it.proximaCena to distancia + 1) }
        }

        return SEM_DESCOBERTA
    }

    private fun Cena.temDescobertaNova(sessao: SessaoInvestigacao): Boolean =
        pista?.let { nova -> sessao.pistas.none { it.id == nova.id } } == true ||
            revelacoes.temDescobertaNova(sessao)

    private fun Escolha.temDescobertaNova(sessao: SessaoInvestigacao): Boolean =
        pista?.let { nova -> sessao.pistas.none { it.id == nova.id } } == true ||
            revelacoes.temDescobertaNova(sessao)

    private fun Revelacoes.temDescobertaNova(sessao: SessaoInvestigacao): Boolean =
        pistas.any { id -> sessao.pistas.none { it.id == id } } ||
            anotacoesPessoas.any { it !in sessao.anotacoesPessoas } ||
            anotacoesLocais.any { it !in sessao.anotacoesLocais } ||
            conversas.any { it !in sessao.conversas } ||
            lembrancas.any { id -> sessao.lembrancas.none { it.id == id } }

    private companion object {
        const val SEM_DESCOBERTA = Int.MAX_VALUE
    }
}

package br.com.avoren.indicio.domain.model.caso

/** Etapa narrativa ordenada de uma investigação longa. */
data class Etapa(
    val id: String,
    val titulo: String,
    val descricao: String,
    val resumoConclusao: String,
    val resumoRetomada: String,
    val objetivos: List<Objetivo>,
)

/** Pergunta concreta que orienta o jogador dentro de uma etapa. */
data class Objetivo(
    val id: String,
    val texto: String,
    val perguntaEmAberto: String,
)

data class AnotacaoCaderno(
    val id: String,
    val texto: String,
)

data class PessoaCaso(
    val id: String,
    val nome: String,
    val papel: String,
    val imagem: Imagem? = null,
    val anotacoes: List<AnotacaoCaderno>,
)

data class LocalCaso(
    val id: String,
    val nome: String,
    val imagem: Imagem? = null,
    val anotacoes: List<AnotacaoCaderno>,
)

data class Conversa(
    val id: String,
    val pessoaId: String,
    val titulo: String,
    val texto: String,
    val narracao: String? = null,
) {
    val textoNarrado: String get() = narracao?.takeIf(String::isNotBlank) ?: texto
}

/** Conteúdo autoral do caderno; a sessão decide quais ids já foram revelados. */
data class CadernoCaso(
    val pistas: List<Pista> = emptyList(),
    val pessoas: List<PessoaCaso> = emptyList(),
    val locais: List<LocalCaso> = emptyList(),
    val conversas: List<Conversa> = emptyList(),
) {
    fun pista(id: String): Pista? = pistas.firstOrNull { it.id == id }

    fun anotacaoPessoa(id: String): AnotacaoCaderno? = pessoas
        .flatMap(PessoaCaso::anotacoes)
        .firstOrNull { it.id == id }

    fun pessoaDaAnotacao(id: String): PessoaCaso? = pessoas
        .firstOrNull { pessoa -> pessoa.anotacoes.any { it.id == id } }

    fun anotacaoLocal(id: String): AnotacaoCaderno? = locais
        .flatMap(LocalCaso::anotacoes)
        .firstOrNull { it.id == id }

    fun localDaAnotacao(id: String): LocalCaso? = locais
        .firstOrNull { local -> local.anotacoes.any { it.id == id } }

    fun conversa(id: String): Conversa? = conversas.firstOrNull { it.id == id }
}

data class Lembranca(
    val id: String,
    val texto: String,
    val essencial: Boolean = false,
)

/** Referências reveladas ao aplicar uma escolha ou entrar numa cena. */
data class Revelacoes(
    val pistas: List<String> = emptyList(),
    val anotacoesPessoas: List<String> = emptyList(),
    val anotacoesLocais: List<String> = emptyList(),
    val conversas: List<String> = emptyList(),
    val lembrancas: List<String> = emptyList(),
) {
    val vazias: Boolean
        get() = pistas.isEmpty() && anotacoesPessoas.isEmpty() &&
            anotacoesLocais.isEmpty() && conversas.isEmpty() && lembrancas.isEmpty()
}

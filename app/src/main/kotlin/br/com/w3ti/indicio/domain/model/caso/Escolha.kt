package br.com.w3ti.indicio.domain.model.caso

/**
 * Uma das duas opções oferecidas em uma cena comum.
 *
 * Escolhas menos adequadas não interrompem a história: elas apontam para outra
 * cena e, com frequência, revelam uma [pista].
 */
data class Escolha(
    val id: String,
    val texto: String,
    val proximaCena: String,
    val pista: Pista? = null,
    val revelacoes: Revelacoes = Revelacoes(),
    val dica: String? = null,
)

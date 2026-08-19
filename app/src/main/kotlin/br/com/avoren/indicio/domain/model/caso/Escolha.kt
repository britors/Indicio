package br.com.avoren.indicio.domain.model.caso

import kotlinx.serialization.Serializable

/**
 * Uma das duas opções oferecidas em uma cena comum.
 *
 * Escolhas menos adequadas não interrompem a história: elas apontam para outra
 * cena e, com frequência, revelam uma [pista].
 */
@Serializable
data class Escolha(
    val id: String,
    val texto: String,
    val proximaCena: String,
    val pista: Pista? = null,
)

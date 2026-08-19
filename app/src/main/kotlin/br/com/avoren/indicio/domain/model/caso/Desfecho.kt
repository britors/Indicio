package br.com.avoren.indicio.domain.model.caso

import kotlinx.serialization.Serializable

/**
 * Metadados de conclusão de uma cena final.
 *
 * Todo desfecho do produto é positivo: [mensagem] encerra a história e
 * [explicacaoPistas] retoma gentilmente o raciocínio que levou até ali.
 */
@Serializable
data class Desfecho(
    val titulo: String,
    val mensagem: String,
    val explicacaoPistas: String,
)

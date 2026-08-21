package br.com.avoren.indicio.domain.dica

import br.com.avoren.indicio.domain.armazenamento.ResultadoArmazenamento

/** Registro persistente de uma recomendação já entregue pelo Anônimo. */
data class DicaRegistrada(
    val casoId: String,
    val cenaId: String,
    val escolhaId: String,
    val usadaEm: Long,
)

/** Porta local que guarda o consumo semanal e as dicas já reveladas. */
interface RepositorioDicas {
    suspend fun porCena(casoId: String, cenaId: String): ResultadoArmazenamento<DicaRegistrada?>

    suspend fun quantidadeDesde(inicio: Long): ResultadoArmazenamento<Int>

    suspend fun registrarSeDisponivel(
        dica: DicaRegistrada,
        inicioDaSemana: Long,
        limite: Int,
    ): ResultadoArmazenamento<Boolean>
}

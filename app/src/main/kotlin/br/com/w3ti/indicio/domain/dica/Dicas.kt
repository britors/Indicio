package br.com.w3ti.indicio.domain.dica

import br.com.w3ti.indicio.domain.armazenamento.ResultadoArmazenamento

/** Registro persistente de uma recomendação já entregue pelo Anônimo. */
data class DicaRegistrada(
    val casoId: String,
    val cenaId: String,
    val escolhaId: String,
    val usadaEm: Long,
)

/** Porta local que guarda o consumo semanal por caso e as dicas já reveladas. */
interface RepositorioDicas {
    suspend fun porCena(casoId: String, cenaId: String): ResultadoArmazenamento<DicaRegistrada?>

    suspend fun quantidadeDoCasoDesde(casoId: String, inicio: Long): ResultadoArmazenamento<Int>

    suspend fun registrarSeDisponivel(
        dica: DicaRegistrada,
        inicioDaSemana: Long,
        limite: Int,
    ): ResultadoArmazenamento<Boolean>
}

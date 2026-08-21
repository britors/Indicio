package br.com.avoren.indicio.application.dica

import br.com.avoren.indicio.domain.armazenamento.ResultadoArmazenamento
import br.com.avoren.indicio.domain.dica.DicaRegistrada
import br.com.avoren.indicio.domain.dica.RepositorioDicas
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

data class SituacaoDica(
    val restantesDoCasoNestaSemana: Int,
    val escolhaIdRevelada: String? = null,
)

sealed interface ResultadoRevelacaoDica {
    data class Revelada(val situacao: SituacaoDica) : ResultadoRevelacaoDica
    data object LimiteSemanalAtingido : ResultadoRevelacaoDica
    data class Falha(val causa: String) : ResultadoRevelacaoDica
}

/** Aplica a cota semanal por caso sem misturar calendário, persistência e interface. */
class GerenciarDicas(
    private val repositorio: RepositorioDicas,
    private val agora: () -> Long = System::currentTimeMillis,
    private val zona: ZoneId = ZoneId.systemDefault(),
) {
    suspend fun consultar(
        casoId: String,
        cenaId: String,
    ): ResultadoArmazenamento<SituacaoDica> {
        val existente = when (val resultado = repositorio.porCena(casoId, cenaId)) {
            is ResultadoArmazenamento.Falha -> return resultado
            is ResultadoArmazenamento.Sucesso -> resultado.valor
        }
        val quantidade = when (
            val resultado = repositorio.quantidadeDoCasoDesde(casoId, inicioDaSemana())
        ) {
            is ResultadoArmazenamento.Falha -> return resultado
            is ResultadoArmazenamento.Sucesso -> resultado.valor
        }
        return ResultadoArmazenamento.Sucesso(
            SituacaoDica(
                restantesDoCasoNestaSemana = (LIMITE_SEMANAL_POR_CASO - quantidade).coerceAtLeast(0),
                escolhaIdRevelada = existente?.escolhaId,
            ),
        )
    }

    suspend fun revelar(
        casoId: String,
        cenaId: String,
        escolhaId: String,
    ): ResultadoRevelacaoDica {
        when (val consulta = consultar(casoId, cenaId)) {
            is ResultadoArmazenamento.Falha -> return ResultadoRevelacaoDica.Falha(consulta.causa)
            is ResultadoArmazenamento.Sucesso -> {
                val situacao = consulta.valor
                if (situacao.escolhaIdRevelada != null) {
                    return ResultadoRevelacaoDica.Revelada(situacao)
                }
                if (situacao.restantesDoCasoNestaSemana == 0) {
                    return ResultadoRevelacaoDica.LimiteSemanalAtingido
                }

                val dica = DicaRegistrada(casoId, cenaId, escolhaId, agora())
                return when (
                    val registro = repositorio.registrarSeDisponivel(
                        dica = dica,
                        inicioDaSemana = inicioDaSemana(),
                        limite = LIMITE_SEMANAL_POR_CASO,
                    )
                ) {
                    is ResultadoArmazenamento.Falha -> ResultadoRevelacaoDica.Falha(registro.causa)
                    is ResultadoArmazenamento.Sucesso -> if (!registro.valor) {
                        ResultadoRevelacaoDica.LimiteSemanalAtingido
                    } else {
                        when (val atualizada = consultar(casoId, cenaId)) {
                            is ResultadoArmazenamento.Falha -> {
                                ResultadoRevelacaoDica.Falha(atualizada.causa)
                            }
                            is ResultadoArmazenamento.Sucesso -> {
                                ResultadoRevelacaoDica.Revelada(atualizada.valor)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun inicioDaSemana(): Long {
        val data = Instant.ofEpochMilli(agora())
            .atZone(zona)
            .toLocalDate()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return data.atStartOfDay(zona).toInstant().toEpochMilli()
    }

    companion object {
        const val LIMITE_SEMANAL_POR_CASO = 3
    }
}

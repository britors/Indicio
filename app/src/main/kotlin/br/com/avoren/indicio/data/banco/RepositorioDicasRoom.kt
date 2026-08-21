package br.com.avoren.indicio.data.banco

import br.com.avoren.indicio.domain.armazenamento.ResultadoArmazenamento
import br.com.avoren.indicio.domain.dica.DicaRegistrada
import br.com.avoren.indicio.domain.dica.RepositorioDicas
import kotlinx.coroutines.CancellationException

class RepositorioDicasRoom(
    private val dao: DicaDao,
) : RepositorioDicas {
    override suspend fun porCena(
        casoId: String,
        cenaId: String,
    ): ResultadoArmazenamento<DicaRegistrada?> = protegido("consultar a dica") {
        dao.porCena(casoId, cenaId)?.paraDominio()
    }

    override suspend fun quantidadeDoCasoDesde(
        casoId: String,
        inicio: Long,
    ): ResultadoArmazenamento<Int> =
        protegido("consultar a cota de dicas do caso") {
            dao.quantidadeDoCasoDesde(casoId, inicio)
        }

    override suspend fun registrarSeDisponivel(
        dica: DicaRegistrada,
        inicioDaSemana: Long,
        limite: Int,
    ): ResultadoArmazenamento<Boolean> =
        protegido("registrar a dica") {
            dao.registrarSeDisponivel(dica.paraEntidade(), inicioDaSemana, limite)
        }

    private suspend fun <T> protegido(
        operacao: String,
        bloco: suspend () -> T,
    ): ResultadoArmazenamento<T> = try {
        ResultadoArmazenamento.Sucesso(bloco())
    } catch (erro: CancellationException) {
        throw erro
    } catch (erro: Exception) {
        ResultadoArmazenamento.Falha("Não foi possível $operacao: ${erro.message.orEmpty()}")
    }

    private fun DicaEntidade.paraDominio() = DicaRegistrada(casoId, cenaId, escolhaId, usadaEm)

    private fun DicaRegistrada.paraEntidade() = DicaEntidade(casoId, cenaId, escolhaId, usadaEm)
}

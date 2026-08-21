package br.com.w3ti.indicio.application.dica

import br.com.w3ti.indicio.domain.armazenamento.ResultadoArmazenamento
import br.com.w3ti.indicio.domain.dica.DicaRegistrada
import br.com.w3ti.indicio.domain.dica.RepositorioDicas
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GerenciarDicasTest {

    @Test
    fun bloqueiaAQuartaDicaDoMesmoCasoNaMesmaSemana() = executar {
        val relogio = Relogio("2026-08-19T12:00:00Z")
        val gerenciador = GerenciarDicas(RepositorioEmMemoria(), relogio::agora, UTC)

        assertTrue(gerenciador.revelar("caso", "cena-1", "a") is ResultadoRevelacaoDica.Revelada)
        assertTrue(gerenciador.revelar("caso", "cena-2", "b") is ResultadoRevelacaoDica.Revelada)
        assertTrue(gerenciador.revelar("caso", "cena-3", "a") is ResultadoRevelacaoDica.Revelada)

        assertEquals(
            ResultadoRevelacaoDica.LimiteSemanalAtingido,
            gerenciador.revelar("caso", "cena-4", "b"),
        )
    }

    @Test
    fun esgotarUmCasoNaoConsomeACotaDeOutro() = executar {
        val gerenciador = GerenciarDicas(
            RepositorioEmMemoria(),
            Relogio("2026-08-19T12:00:00Z")::agora,
            UTC,
        )
        repeat(3) { indice ->
            assertTrue(
                gerenciador.revelar("primeiro", "cena-$indice", "a") is
                    ResultadoRevelacaoDica.Revelada,
            )
        }

        val outroCaso = gerenciador.revelar("segundo", "cena-1", "a")

        assertTrue(outroCaso is ResultadoRevelacaoDica.Revelada)
        assertEquals(
            2,
            (outroCaso as ResultadoRevelacaoDica.Revelada)
                .situacao
                .restantesDoCasoNestaSemana,
        )
    }

    @Test
    fun dicaJaReveladaNaoConsomeNovamente() = executar {
        val repositorio = RepositorioEmMemoria()
        val gerenciador = GerenciarDicas(
            repositorio,
            Relogio("2026-08-19T12:00:00Z")::agora,
            UTC,
        )

        gerenciador.revelar("caso", "cena-1", "a")
        val repetida = gerenciador.revelar("caso", "cena-1", "a")

        val situacao = (repetida as ResultadoRevelacaoDica.Revelada).situacao
        assertEquals(2, situacao.restantesDoCasoNestaSemana)
        assertEquals(1, repositorio.registros.size)
    }

    @Test
    fun cotaRenovaNaSegundaFeira() = executar {
        val relogio = Relogio("2026-08-23T20:00:00Z")
        val repositorio = RepositorioEmMemoria()
        val gerenciador = GerenciarDicas(repositorio, relogio::agora, UTC)
        gerenciador.revelar("caso", "domingo-1", "a")
        gerenciador.revelar("caso", "domingo-2", "b")
        gerenciador.revelar("caso", "domingo-3", "a")

        relogio.valor = Instant.parse("2026-08-24T00:01:00Z").toEpochMilli()

        val novaSemana = gerenciador.revelar("caso", "segunda", "a")
        assertTrue(novaSemana is ResultadoRevelacaoDica.Revelada)
        assertEquals(
            2,
            (novaSemana as ResultadoRevelacaoDica.Revelada)
                .situacao
                .restantesDoCasoNestaSemana,
        )
    }

    private class Relogio(valorInicial: String) {
        var valor = Instant.parse(valorInicial).toEpochMilli()
        fun agora() = valor
    }

    private class RepositorioEmMemoria : RepositorioDicas {
        val registros = mutableListOf<DicaRegistrada>()

        override suspend fun porCena(
            casoId: String,
            cenaId: String,
        ) = ResultadoArmazenamento.Sucesso(
            registros.firstOrNull { it.casoId == casoId && it.cenaId == cenaId },
        )

        override suspend fun quantidadeDoCasoDesde(casoId: String, inicio: Long) =
            ResultadoArmazenamento.Sucesso(
                registros.count { it.casoId == casoId && it.usadaEm >= inicio },
            )

        override suspend fun registrarSeDisponivel(
            dica: DicaRegistrada,
            inicioDaSemana: Long,
            limite: Int,
        ): ResultadoArmazenamento<Boolean> {
            if (registros.any { it.casoId == dica.casoId && it.cenaId == dica.cenaId }) {
                return ResultadoArmazenamento.Sucesso(true)
            }
            if (registros.count {
                    it.casoId == dica.casoId && it.usadaEm >= inicioDaSemana
                } >= limite
            ) {
                return ResultadoArmazenamento.Sucesso(false)
            }
            registros.removeAll { it.casoId == dica.casoId && it.cenaId == dica.cenaId }
            registros += dica
            return ResultadoArmazenamento.Sucesso(true)
        }
    }

    private fun executar(bloco: suspend () -> Unit) = kotlinx.coroutines.test.runTest { bloco() }

    private companion object {
        val UTC: ZoneId = ZoneId.of("UTC")
    }
}

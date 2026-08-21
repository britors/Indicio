package br.com.w3ti.indicio.fake

import br.com.w3ti.indicio.domain.caso.ErroCarga
import br.com.w3ti.indicio.domain.caso.RepositorioCasos
import br.com.w3ti.indicio.domain.caso.ResultadoCarga
import br.com.w3ti.indicio.domain.model.caso.Caso
import br.com.w3ti.indicio.domain.model.caso.Catalogo

/**
 * Repositório em memória para os testes de apresentação.
 */
class RepositorioCasosFalso(
    private val casos: Map<String, Caso> = mapOf(CasoFixtures.ID to CasoFixtures.casoValido()),
    private val erro: ErroCarga? = null,
    private val catalogo: Catalogo = Catalogo(emptyList()),
) : RepositorioCasos {

    override suspend fun catalogo(): ResultadoCarga<Catalogo> =
        erro?.let { ResultadoCarga.Falha(it) } ?: ResultadoCarga.Sucesso(catalogo)

    override suspend fun caso(id: String): ResultadoCarga<Caso> = when {
        erro != null -> ResultadoCarga.Falha(erro)
        else -> casos[id]?.let { ResultadoCarga.Sucesso(it) }
            ?: ResultadoCarga.Falha(ErroCarga.CasoDesconhecido(id))
    }
}

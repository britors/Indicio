package br.com.w3ti.indicio.domain.caso

import br.com.w3ti.indicio.domain.model.caso.Caso
import br.com.w3ti.indicio.domain.model.caso.Catalogo

/**
 * Acesso ao conteúdo narrativo.
 *
 * Declarado como interface para que os testes forneçam casos em memória sem
 * depender de assets nem do ambiente Android.
 */
interface RepositorioCasos {

    suspend fun catalogo(): ResultadoCarga<Catalogo>

    suspend fun caso(id: String): ResultadoCarga<Caso>
}

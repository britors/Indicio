package br.com.w3ti.indicio.domain.repositorio

import br.com.w3ti.indicio.domain.model.IdentidadeApp

/**
 * Fonte da identidade do aplicativo.
 *
 * Declarado como interface para que os testes forneçam dublês sem depender do
 * ambiente Android.
 */
interface RepositorioIdentidade {
    fun identidade(): IdentidadeApp
}

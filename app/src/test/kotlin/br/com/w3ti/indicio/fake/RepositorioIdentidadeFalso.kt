package br.com.w3ti.indicio.fake

import br.com.w3ti.indicio.domain.model.IdentidadeApp
import br.com.w3ti.indicio.domain.repositorio.RepositorioIdentidade

/**
 * Dublê usado nos testes de unidade, sem dependência do ambiente Android.
 */
class RepositorioIdentidadeFalso(
    private val identidade: IdentidadeApp = IdentidadeApp(
        nome = "Indício",
        slogan = "Toda escolha revela uma pista.",
        versao = "0.1.0",
    ),
) : RepositorioIdentidade {

    var vezesConsultado: Int = 0
        private set

    override fun identidade(): IdentidadeApp {
        vezesConsultado++
        return identidade
    }
}

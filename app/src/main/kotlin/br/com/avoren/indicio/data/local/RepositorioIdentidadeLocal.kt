package br.com.avoren.indicio.data.local

import android.content.Context
import br.com.avoren.indicio.BuildConfig
import br.com.avoren.indicio.R
import br.com.avoren.indicio.domain.model.IdentidadeApp
import br.com.avoren.indicio.domain.repositorio.RepositorioIdentidade

/**
 * Lê a identidade dos recursos locais do aplicativo. Não há acesso a rede.
 */
class RepositorioIdentidadeLocal(
    private val context: Context,
) : RepositorioIdentidade {

    override fun identidade(): IdentidadeApp = IdentidadeApp(
        nome = context.getString(R.string.app_nome),
        slogan = context.getString(R.string.app_slogan),
        versao = BuildConfig.VERSION_NAME,
    )
}

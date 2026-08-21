package br.com.w3ti.indicio.domain.model

/**
 * Identidade pública do aplicativo, exibida na tela inicial.
 *
 * Fica no domínio para que a camada de interface não dependa de recursos
 * Android nem de [android.content.pm.PackageManager] para se compor.
 */
data class IdentidadeApp(
    val nome: String,
    val slogan: String,
    val versao: String,
)

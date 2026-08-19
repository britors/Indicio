package br.com.avoren.indicio.data.caso

import android.content.res.AssetManager
import java.io.IOException

/**
 * Leitura bruta dos arquivos de conteúdo.
 *
 * Isola o acesso a assets para que o repositório possa ser testado na JVM.
 */
interface FonteCasos {

    /**
     * @throws IOException quando o arquivo não existe ou não pode ser lido.
     */
    fun ler(caminho: String): String
}

/** Lê o conteúdo empacotado no APK. Nada é buscado em rede. */
class FonteCasosAssets(
    private val assets: AssetManager,
) : FonteCasos {

    override fun ler(caminho: String): String =
        assets.open(caminho).bufferedReader().use { it.readText() }
}

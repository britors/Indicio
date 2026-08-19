package br.com.avoren.indicio.fake

import br.com.avoren.indicio.data.caso.FonteCasos
import java.io.FileNotFoundException

/**
 * Fonte de conteúdo em memória, para exercitar o repositório sem assets.
 */
class FonteCasosEmMemoria(
    private val arquivos: Map<String, String>,
) : FonteCasos {

    override fun ler(caminho: String): String =
        arquivos[caminho] ?: throw FileNotFoundException(caminho)
}

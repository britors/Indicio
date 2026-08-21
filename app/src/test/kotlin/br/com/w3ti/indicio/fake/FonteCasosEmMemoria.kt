package br.com.w3ti.indicio.fake

import br.com.w3ti.indicio.data.caso.FonteCasos
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

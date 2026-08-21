package br.com.w3ti.indicio.fake

import br.com.w3ti.indicio.data.caso.FonteCasos
import java.io.File
import java.io.FileNotFoundException

/**
 * Lê os assets reais do módulo direto do sistema de arquivos.
 *
 * Permite que o conteúdo publicado seja validado por `./gradlew test`, sem
 * emulador — é a rede de proteção contra regressões de conteúdo.
 */
class FonteCasosDeArquivo(
    private val raiz: File = File("src/main/assets"),
) : FonteCasos {

    init {
        require(raiz.isDirectory) {
            "Diretório de assets não encontrado em ${raiz.absolutePath}. " +
                "Os testes de unidade devem rodar com o diretório do módulo como raiz."
        }
    }

    override fun ler(caminho: String): String {
        val arquivo = File(raiz, caminho)
        if (!arquivo.isFile) throw FileNotFoundException(caminho)
        return arquivo.readText()
    }
}

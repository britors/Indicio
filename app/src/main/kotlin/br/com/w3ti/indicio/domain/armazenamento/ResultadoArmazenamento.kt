package br.com.w3ti.indicio.domain.armazenamento

/**
 * Resultado de uma operação de armazenamento local.
 *
 * O armazenamento nunca lança para as camadas acima: uma falha vira [Falha] e
 * a sessão em memória continua válida, de modo que o jogador possa seguir
 * jogando mesmo se o disco recusar a escrita.
 */
sealed interface ResultadoArmazenamento<out T> {

    data class Sucesso<out T>(val valor: T) : ResultadoArmazenamento<T>

    data class Falha(val causa: String) : ResultadoArmazenamento<Nothing>

    val bemSucedido: Boolean get() = this is Sucesso
}

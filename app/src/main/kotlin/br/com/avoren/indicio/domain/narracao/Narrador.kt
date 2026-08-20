package br.com.avoren.indicio.domain.narracao

import kotlinx.coroutines.flow.StateFlow

/**
 * Situação da narração em voz alta.
 *
 * [INDISPONIVEL] não é erro: muitos aparelhos não têm voz em português
 * instalada, e o aplicativo precisa continuar plenamente utilizável assim.
 */
enum class EstadoNarracao {
    /** Ainda preparando o mecanismo de voz. */
    PREPARANDO,

    /** Há voz utilizável e nada está sendo falado no momento. */
    PRONTO,

    FALANDO,

    /** Não há mecanismo, voz ou idioma utilizável neste aparelho. */
    INDISPONIVEL,
}

/**
 * Leitura em voz alta dos trechos da história.
 *
 * Declarado como interface para que os testes usem um dublê e para que a
 * ausência de voz seja apenas mais um estado, não um caminho especial.
 */
interface Narrador {

    val estado: StateFlow<EstadoNarracao>

    /** Lê o texto, interrompendo o que estiver sendo falado. */
    fun falar(texto: String)

    fun parar()

    /** Libera o mecanismo de voz. Depois disso o narrador não fala mais. */
    fun encerrar()
}

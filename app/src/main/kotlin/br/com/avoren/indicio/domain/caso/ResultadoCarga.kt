package br.com.avoren.indicio.domain.caso

import br.com.avoren.indicio.domain.validacao.ProblemaValidacao

/**
 * Resultado de uma tentativa de carregar conteúdo local.
 *
 * O carregamento nunca lança exceção para a camada de interface: qualquer
 * problema vira um [Falha] descritivo, de modo que a tela possa mostrar uma
 * mensagem respeitosa e oferecer um caminho de recuperação.
 */
sealed interface ResultadoCarga<out T> {

    data class Sucesso<out T>(val valor: T) : ResultadoCarga<T>

    data class Falha(val erro: ErroCarga) : ResultadoCarga<Nothing>

    fun valorOuNulo(): T? = (this as? Sucesso)?.valor
}

/** Causas possíveis de falha ao carregar catálogo ou caso. */
sealed interface ErroCarga {

    /** Descrição técnica, destinada a logs e a quem escreve os casos. */
    val detalhe: String

    data class ArquivoNaoEncontrado(val caminho: String) : ErroCarga {
        override val detalhe: String = "Arquivo não encontrado: $caminho"
    }

    data class JsonInvalido(val caminho: String, val causa: String) : ErroCarga {
        override val detalhe: String = "JSON inválido em $caminho: $causa"
    }

    data class VersaoIncompativel(
        val caminho: String,
        val encontrada: Int,
        val suportada: Int,
    ) : ErroCarga {
        override val detalhe: String =
            "Versão de esquema $encontrada em $caminho; esta versão do aplicativo entende $suportada"
    }

    data class CasoIndisponivel(val casoId: String) : ErroCarga {
        override val detalhe: String = "O caso \"$casoId\" ainda não está disponível"
    }

    data class CasoDesconhecido(val casoId: String) : ErroCarga {
        override val detalhe: String = "O caso \"$casoId\" não consta no catálogo"
    }

    data class GrafoInvalido(
        val casoId: String,
        val problemas: List<ProblemaValidacao>,
    ) : ErroCarga {
        override val detalhe: String =
            "O caso \"$casoId\" tem ${problemas.size} problema(s):\n" +
                problemas.joinToString("\n") { "  - ${it.mensagemLegivel()}" }
    }
}

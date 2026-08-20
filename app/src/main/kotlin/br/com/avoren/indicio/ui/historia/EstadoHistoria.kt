package br.com.avoren.indicio.ui.historia

import br.com.avoren.indicio.domain.caso.ErroCarga
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Desfecho
import br.com.avoren.indicio.domain.model.caso.Pista

/**
 * Estado observável da tela narrativa.
 *
 * A interface apenas renderiza o que está aqui; nenhuma decisão narrativa
 * acontece na camada de apresentação.
 */
sealed interface EstadoHistoria {

    data object Carregando : EstadoHistoria

    /** O conteúdo não pôde ser carregado. A tela oferece nova tentativa. */
    data class Falha(val erro: ErroCarga) : EstadoHistoria

    /** O caso mudou e o progresso só pode ser substituído com consentimento. */
    data class AtualizacaoNecessaria(val tituloCaso: String) : EstadoHistoria

    data class EmCurso(
        val tituloCaso: String,
        val cena: Cena,
        val pistas: List<Pista>,
        val temInvestigacaoLonga: Boolean = false,
        val escolhasHabilitadas: Boolean = true,
    ) : EstadoHistoria

    data class Concluida(
        val tituloCaso: String,
        val cena: Cena,
        val desfecho: Desfecho,
        val pistas: List<Pista>,
    ) : EstadoHistoria
}

/**
 * Acontecimentos pontuais, consumidos uma única vez.
 *
 * Separados do estado porque não descrevem a tela, e sim algo que acabou de
 * ocorrer — anunciar duas vezes uma pista para o TalkBack seria ruído.
 */
sealed interface EventoHistoria {

    data class PistasReveladas(val pistas: List<Pista>) : EventoHistoria

    /** Uma escolha foi ignorada, tipicamente por toque duplo. */
    data object EscolhaIgnorada : EventoHistoria

    /**
     * O progresso não pôde ser gravado.
     *
     * A sessão em memória continua íntegra: o jogador segue jogando e apenas
     * é avisado de que o aplicativo pode não retomar deste ponto.
     */
    data class FalhaAoSalvar(val mensagem: String) : EventoHistoria
}

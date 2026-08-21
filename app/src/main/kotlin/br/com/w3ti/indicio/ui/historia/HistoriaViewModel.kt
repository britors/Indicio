package br.com.w3ti.indicio.ui.historia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.w3ti.indicio.domain.armazenamento.RepositorioProgresso
import br.com.w3ti.indicio.domain.armazenamento.ResultadoArmazenamento
import br.com.w3ti.indicio.domain.caso.RepositorioCasos
import br.com.w3ti.indicio.domain.caso.ResultadoCarga
import br.com.w3ti.indicio.domain.dica.SugerirEscolha
import br.com.w3ti.indicio.domain.model.caso.Caso
import br.com.w3ti.indicio.domain.model.sessao.ProgressoCaso
import br.com.w3ti.indicio.domain.model.sessao.SessaoInvestigacao
import br.com.w3ti.indicio.domain.narracao.EstadoNarracao
import br.com.w3ti.indicio.domain.narracao.Narrador
import br.com.w3ti.indicio.domain.narrativa.MecanismoNarrativo
import br.com.w3ti.indicio.domain.narrativa.ResultadoEscolha
import br.com.w3ti.indicio.domain.narrativa.ResultadoReconstrucao
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Conduz a tela narrativa em fluxo unidirecional.
 *
 * A interface envia apenas intenções ([abrir], [escolher], [reiniciar]) e
 * observa [estado]. O ViewModel não conhece Compose, e a lógica da história
 * inteira vive no [MecanismoNarrativo].
 */
class HistoriaViewModel(
    private val repositorioCasos: RepositorioCasos,
    private val repositorioProgresso: RepositorioProgresso,
    private val narrador: Narrador? = null,
    private val mecanismo: MecanismoNarrativo = MecanismoNarrativo(),
    private val sugerirEscolha: SugerirEscolha = SugerirEscolha(),
) : ViewModel() {

    /**
     * Situação da narração.
     *
     * Quando não há narrador, o estado é [EstadoNarracao.INDISPONIVEL] — a
     * interface esconde o controle de voz e nada mais muda.
     */
    val estadoNarracao: StateFlow<EstadoNarracao> = narrador?.estado
        ?: MutableStateFlow(EstadoNarracao.INDISPONIVEL).asStateFlow()

    private val _estado = MutableStateFlow<EstadoHistoria>(EstadoHistoria.Carregando)
    val estado: StateFlow<EstadoHistoria> = _estado.asStateFlow()

    private val _eventos = MutableSharedFlow<EventoHistoria>(
        extraBufferCapacity = EVENTOS_EM_BUFFER,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val eventos: SharedFlow<EventoHistoria> = _eventos.asSharedFlow()

    private var caso: Caso? = null
    private var sessao: SessaoInvestigacao? = null

    /**
     * Barra novas escolhas enquanto a transição anterior não terminou de ser
     * gravada. O mecanismo já recusaria a escolha repetida, porque o
     * identificador tocado pertence à cena anterior; esta trava garante que o
     * salvamento de uma escolha nunca se cruze com o da seguinte.
     */
    private var transicaoEmAndamento = false

    /**
     * Carrega um caso.
     *
     * Com [retomar] verdadeiro, reabre na cena em que o jogador parou.
     */
    fun abrir(casoId: String, retomar: Boolean = true) {
        viewModelScope.launch {
            _estado.value = EstadoHistoria.Carregando

            when (val resultado = repositorioCasos.caso(casoId)) {
                is ResultadoCarga.Falha -> _estado.value = EstadoHistoria.Falha(resultado.erro)

                is ResultadoCarga.Sucesso -> {
                    // Uma sessão já concluída não tem o que retomar: abrir o
                    // caso de novo começa uma investigação nova, em vez de
                    // devolver o jogador ao final que ele já leu.
                    val salvo = if (retomar) {
                        repositorioProgresso.progresso(casoId)
                            ?.takeUnless { it.concluido }
                            ?.paraReconstrucao()
                    } else {
                        null
                    }
                    if (instalar(resultado.valor, salvo)) {
                        // Abrir ou retomar também é um acesso relevante. Além
                        // de atualizar o cartão do catálogo, isto permite
                        // retomar um caso mesmo antes da primeira escolha.
                        sessao?.let { sessaoInstalada -> salvar(sessaoInstalada) }
                    }
                }
            }
        }
    }

    fun escolher(escolhaId: String) {
        val casoAtual = caso ?: return
        val sessaoAtual = sessao ?: return

        if (transicaoEmAndamento) {
            emitir(EventoHistoria.EscolhaIgnorada)
            return
        }
        transicaoEmAndamento = true
        atualizarEstado(habilitado = false)

        viewModelScope.launch {
            try {
                when (val resultado = mecanismo.escolher(casoAtual, sessaoAtual, escolhaId)) {
                    is ResultadoEscolha.Recusada -> emitir(EventoHistoria.EscolhaIgnorada)

                    is ResultadoEscolha.Aplicada -> {
                        // A fala da cena anterior não pode continuar sobre o
                        // texto novo.
                        narrador?.parar()
                        sessao = resultado.sessao
                        if (resultado.pistasReveladas.isNotEmpty()) {
                            emitir(EventoHistoria.PistasReveladas(resultado.pistasReveladas))
                        }
                        salvar(resultado.sessao)
                    }
                }
            } finally {
                transicaoEmAndamento = false
                atualizarEstado(habilitado = true)
            }
        }
    }

    /** Recomeça o caso atual do zero. O histórico de conclusões não é afetado. */
    fun reiniciar() {
        val casoAtual = caso ?: return

        viewModelScope.launch {
            val nova = mecanismo.reiniciar(casoAtual) ?: return@launch
            sessao = nova

            val resultado = repositorioProgresso.reiniciar(casoAtual.id)
            if (resultado is ResultadoArmazenamento.Falha) {
                emitir(EventoHistoria.FalhaAoSalvar(resultado.causa))
            }
            atualizarEstado(habilitado = true)
        }
    }

    /** Lê em voz alta o trecho da cena atual, ou interrompe a leitura em curso. */
    fun alternarNarracao() {
        val narrador = narrador ?: return

        if (narrador.estado.value == EstadoNarracao.FALANDO) {
            narrador.parar()
            return
        }

        val cena = caso?.cena(sessao?.cenaAtual.orEmpty()) ?: return
        narrador.falar(cena.textoNarrado)
    }

    /** Interrompe a fala sem encerrar o mecanismo, ao pausar ou sair da tela. */
    fun silenciar() {
        narrador?.parar()
    }

    override fun onCleared() {
        narrador?.encerrar()
    }

    /**
     * Grava o progresso. Uma falha é anunciada, mas não desfaz a escolha: o
     * estado em memória continua válido e jogável.
     */
    private suspend fun salvar(sessao: SessaoInvestigacao) {
        val resultado = repositorioProgresso.salvar(
            sessao = sessao,
            tituloDesfecho = sessao.desfecho?.titulo,
        )
        if (resultado is ResultadoArmazenamento.Falha) {
            emitir(EventoHistoria.FalhaAoSalvar(resultado.causa))
        }
    }

    private fun instalar(caso: Caso, progresso: ProgressoCaso?): Boolean {
        this.caso = caso

        sessao = when {
            progresso == null -> mecanismo.iniciar(caso)

            else -> when (val reconstrucao = mecanismo.reconstruir(caso, progresso)) {
                is ResultadoReconstrucao.Sucesso -> reconstrucao.sessao
                is ResultadoReconstrucao.ProgressoIncompativel -> {
                    sessao = null
                    _estado.value = EstadoHistoria.AtualizacaoNecessaria(caso.titulo)
                    return false
                }
            }
        }

        atualizarEstado(habilitado = true)
        return sessao != null
    }

    private fun atualizarEstado(habilitado: Boolean) {
        val casoAtual = caso ?: return
        val sessaoAtual = sessao ?: return
        val cena = casoAtual.cena(sessaoAtual.cenaAtual) ?: return
        val desfecho = sessaoAtual.desfecho

        _estado.value = if (desfecho != null) {
            EstadoHistoria.Concluida(
                tituloCaso = casoAtual.titulo,
                cena = cena,
                desfecho = desfecho,
                pistas = sessaoAtual.pistas,
            )
        } else {
            EstadoHistoria.EmCurso(
                tituloCaso = casoAtual.titulo,
                cena = cena,
                pistas = sessaoAtual.pistas,
                temInvestigacaoLonga = casoAtual.etapas.isNotEmpty(),
                escolhasHabilitadas = habilitado,
                escolhaSugerida = sugerirEscolha.executar(casoAtual, sessaoAtual),
            )
        }
    }

    private fun emitir(evento: EventoHistoria) {
        _eventos.tryEmit(evento)
    }

    companion object {
        private const val EVENTOS_EM_BUFFER = 4

        fun fabrica(
            repositorioCasos: RepositorioCasos,
            repositorioProgresso: RepositorioProgresso,
            criarNarrador: () -> Narrador,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    HistoriaViewModel(
                        repositorioCasos = repositorioCasos,
                        repositorioProgresso = repositorioProgresso,
                        narrador = criarNarrador(),
                    )
                }
            }
    }
}

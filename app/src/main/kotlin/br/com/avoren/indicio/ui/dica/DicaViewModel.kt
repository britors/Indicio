package br.com.avoren.indicio.ui.dica

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.avoren.indicio.application.dica.GerenciarDicas
import br.com.avoren.indicio.application.dica.ResultadoRevelacaoDica
import br.com.avoren.indicio.application.dica.SituacaoDica
import br.com.avoren.indicio.domain.armazenamento.ResultadoArmazenamento
import br.com.avoren.indicio.domain.dica.RepositorioDicas
import br.com.avoren.indicio.domain.model.caso.Escolha
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EstadoDica {
    data object Oculta : EstadoDica
    data class Carregando(val cenaId: String) : EstadoDica
    data class Disponivel(val cenaId: String, val restantes: Int) : EstadoDica
    data class Revelando(val cenaId: String, val restantes: Int) : EstadoDica
    data class Revelada(val cenaId: String, val mensagem: String?, val restantes: Int) : EstadoDica
    data class Esgotada(val cenaId: String) : EstadoDica
    data class Falha(val cenaId: String) : EstadoDica
}

/** Estado de apresentação da mensagem do Anônimo, independente da história. */
class DicaViewModel(
    private val gerenciarDicas: GerenciarDicas,
) : ViewModel() {
    private val _estado = MutableStateFlow<EstadoDica>(EstadoDica.Oculta)
    val estado: StateFlow<EstadoDica> = _estado.asStateFlow()

    private var solicitacao: Solicitacao? = null

    fun carregar(
        casoId: String,
        cenaId: String,
        escolhas: List<Escolha>,
        escolhaSugerida: Escolha?,
    ) {
        if (escolhaSugerida == null) {
            solicitacao = null
            _estado.value = EstadoDica.Oculta
            return
        }

        val nova = Solicitacao(casoId, cenaId, escolhas, escolhaSugerida.id)
        solicitacao = nova
        consultar(nova)
    }

    fun recarregar() {
        solicitacao?.let(::consultar)
    }

    fun revelar() {
        val atual = solicitacao ?: return
        val disponivel = _estado.value as? EstadoDica.Disponivel ?: return
        _estado.value = EstadoDica.Revelando(atual.cenaId, disponivel.restantes)

        viewModelScope.launch {
            val resultado = gerenciarDicas.revelar(
                casoId = atual.casoId,
                cenaId = atual.cenaId,
                escolhaId = atual.escolhaSugeridaId,
            )
            if (solicitacao != atual) return@launch
            _estado.value = when (resultado) {
                is ResultadoRevelacaoDica.Falha -> EstadoDica.Falha(atual.cenaId)
                ResultadoRevelacaoDica.LimiteSemanalAtingido -> EstadoDica.Esgotada(atual.cenaId)
                is ResultadoRevelacaoDica.Revelada -> resultado.situacao.paraEstado(atual)
            }
        }
    }

    private fun consultar(atual: Solicitacao) {
        _estado.value = EstadoDica.Carregando(atual.cenaId)
        viewModelScope.launch {
            val resultado = gerenciarDicas.consultar(atual.casoId, atual.cenaId)
            if (solicitacao != atual) return@launch
            _estado.value = when (resultado) {
                is ResultadoArmazenamento.Falha -> EstadoDica.Falha(atual.cenaId)
                is ResultadoArmazenamento.Sucesso -> resultado.valor.paraEstado(atual)
            }
        }
    }

    private fun SituacaoDica.paraEstado(atual: Solicitacao): EstadoDica {
        val revelada = escolhaIdRevelada?.let { id -> atual.escolhas.firstOrNull { it.id == id } }
        return when {
            revelada != null -> EstadoDica.Revelada(
                cenaId = atual.cenaId,
                mensagem = revelada.dica,
                restantes = restantesDoCasoNestaSemana,
            )
            escolhaIdRevelada != null -> EstadoDica.Oculta
            restantesDoCasoNestaSemana > 0 -> {
                EstadoDica.Disponivel(atual.cenaId, restantesDoCasoNestaSemana)
            }
            else -> EstadoDica.Esgotada(atual.cenaId)
        }
    }

    private data class Solicitacao(
        val casoId: String,
        val cenaId: String,
        val escolhas: List<Escolha>,
        val escolhaSugeridaId: String,
    )

    companion object {
        fun fabrica(repositorio: RepositorioDicas): ViewModelProvider.Factory = viewModelFactory {
            initializer { DicaViewModel(GerenciarDicas(repositorio)) }
        }
    }
}

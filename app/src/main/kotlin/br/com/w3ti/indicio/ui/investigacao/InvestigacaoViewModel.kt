package br.com.w3ti.indicio.ui.investigacao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.w3ti.indicio.application.investigacao.CarregarInvestigacao
import br.com.w3ti.indicio.application.investigacao.ResultadoCarregamentoInvestigacao
import br.com.w3ti.indicio.domain.armazenamento.RepositorioProgresso
import br.com.w3ti.indicio.domain.caso.RepositorioCasos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Coordena apenas o carregamento das projeções auxiliares; não altera o grafo. */
class InvestigacaoViewModel(
    private val carregar: CarregarInvestigacao,
    private val projetar: ProjetorInvestigacao = ProjetorInvestigacao(),
    private val agora: () -> Long = System::currentTimeMillis,
) : ViewModel() {
    private val _estado = MutableStateFlow<EstadoInvestigacao>(EstadoInvestigacao.Carregando)
    val estado: StateFlow<EstadoInvestigacao> = _estado.asStateFlow()

    fun abrir(casoId: String) {
        viewModelScope.launch {
            _estado.value = EstadoInvestigacao.Carregando
            _estado.value = when (val resultado = carregar(casoId)) {
                is ResultadoCarregamentoInvestigacao.Falha -> EstadoInvestigacao.Falha(resultado.erro)
                is ResultadoCarregamentoInvestigacao.ProgressoIncompativel -> {
                    EstadoInvestigacao.ProgressoIncompativel
                }
                is ResultadoCarregamentoInvestigacao.Sucesso -> projetar(
                    carregada = resultado.investigacao,
                    agora = agora(),
                )
            }
        }
    }

    companion object {
        fun fabrica(
            repositorioCasos: RepositorioCasos,
            repositorioProgresso: RepositorioProgresso,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                InvestigacaoViewModel(
                    carregar = CarregarInvestigacao(repositorioCasos, repositorioProgresso),
                )
            }
        }
    }
}

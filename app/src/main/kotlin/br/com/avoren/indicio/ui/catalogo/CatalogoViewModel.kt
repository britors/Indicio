package br.com.avoren.indicio.ui.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.avoren.indicio.application.catalogo.CasoDoCatalogo
import br.com.avoren.indicio.application.catalogo.ProjetarCasosDoCatalogo
import br.com.avoren.indicio.domain.armazenamento.RepositorioProgresso
import br.com.avoren.indicio.domain.armazenamento.ResultadoArmazenamento
import br.com.avoren.indicio.domain.caso.ErroCarga
import br.com.avoren.indicio.domain.caso.RepositorioCasos
import br.com.avoren.indicio.domain.caso.ResultadoCarga
import br.com.avoren.indicio.domain.model.caso.Categoria
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/** Uma categoria e os casos que pertencem a ela. */
data class GrupoDeCategoria(
    val categoria: Categoria,
    val casos: List<CasoDoCatalogo>,
)

sealed interface EventoCatalogo {
    data class CasoReiniciado(val casoId: String) : EventoCatalogo
    data class FalhaAoReiniciar(val mensagem: String) : EventoCatalogo
}

sealed interface EstadoCatalogo {

    data object Carregando : EstadoCatalogo

    data class Falha(val erro: ErroCarga) : EstadoCatalogo

    data class Conteudo(val grupos: List<GrupoDeCategoria>) : EstadoCatalogo
}

/**
 * Apresenta o catálogo agrupado pelas cinco categorias.
 *
 * Categorias sem nenhum caso continuam visíveis, com o rótulo de que ainda
 * estão sendo preparadas — o catálogo mostra o plano sem prometer datas.
 */
class CatalogoViewModel(
    private val repositorioCasos: RepositorioCasos,
    private val repositorioProgresso: RepositorioProgresso,
    private val projetar: ProjetarCasosDoCatalogo = ProjetarCasosDoCatalogo(),
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoCatalogo>(EstadoCatalogo.Carregando)
    val estado: StateFlow<EstadoCatalogo> = _estado.asStateFlow()
    private val _eventos = MutableSharedFlow<EventoCatalogo>(extraBufferCapacity = 1)
    val eventos: SharedFlow<EventoCatalogo> = _eventos.asSharedFlow()
    private var carregamento: Job? = null

    init {
        carregar()
    }

    fun carregar() {
        carregamento?.cancel()
        carregamento = viewModelScope.launch {
            _estado.value = EstadoCatalogo.Carregando

            when (val resultado = repositorioCasos.catalogo()) {
                is ResultadoCarga.Falha -> _estado.value = EstadoCatalogo.Falha(resultado.erro)
                is ResultadoCarga.Sucesso -> combine(
                    repositorioProgresso.progressos(),
                    repositorioProgresso.historico(),
                ) { progressos, conclusoes ->
                    val casosProjetados = projetar(resultado.valor, progressos, conclusoes)
                        .associateBy { caso -> caso.resumo.id }
                    EstadoCatalogo.Conteudo(
                        resultado.valor.porCategoria().map { (categoria, casos) ->
                            GrupoDeCategoria(
                                categoria = categoria,
                                casos = casos.mapNotNull { resumo -> casosProjetados[resumo.id] },
                            )
                        },
                    )
                }.collect { conteudo -> _estado.value = conteudo }
            }
        }
    }

    fun reiniciar(casoId: String) {
        viewModelScope.launch {
            when (val resultado = repositorioProgresso.reiniciar(casoId)) {
                is ResultadoArmazenamento.Sucesso -> {
                    _eventos.emit(EventoCatalogo.CasoReiniciado(casoId))
                }
                is ResultadoArmazenamento.Falha -> {
                    _eventos.emit(EventoCatalogo.FalhaAoReiniciar(resultado.causa))
                }
            }
        }
    }

    companion object {
        fun fabrica(
            repositorioCasos: RepositorioCasos,
            repositorioProgresso: RepositorioProgresso,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { CatalogoViewModel(repositorioCasos, repositorioProgresso) }
            }
    }
}

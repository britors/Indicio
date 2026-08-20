package br.com.avoren.indicio.ui.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.avoren.indicio.domain.caso.ErroCarga
import br.com.avoren.indicio.domain.caso.RepositorioCasos
import br.com.avoren.indicio.domain.caso.ResultadoCarga
import br.com.avoren.indicio.domain.model.caso.Categoria
import br.com.avoren.indicio.domain.model.caso.ResumoCaso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Uma categoria e os casos que pertencem a ela. */
data class GrupoDeCategoria(
    val categoria: Categoria,
    val casos: List<ResumoCaso>,
)

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
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoCatalogo>(EstadoCatalogo.Carregando)
    val estado: StateFlow<EstadoCatalogo> = _estado.asStateFlow()

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            _estado.value = EstadoCatalogo.Carregando

            _estado.value = when (val resultado = repositorioCasos.catalogo()) {
                is ResultadoCarga.Falha -> EstadoCatalogo.Falha(resultado.erro)
                is ResultadoCarga.Sucesso -> EstadoCatalogo.Conteudo(
                    resultado.valor.porCategoria().map { (categoria, casos) ->
                        GrupoDeCategoria(categoria, casos)
                    },
                )
            }
        }
    }

    companion object {
        fun fabrica(repositorioCasos: RepositorioCasos): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { CatalogoViewModel(repositorioCasos) }
            }
    }
}

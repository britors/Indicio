package br.com.w3ti.indicio.navegacao

import kotlinx.serialization.Serializable

/**
 * Destinos de navegação do aplicativo.
 *
 * A conclusão de um caso não é um destino próprio: ela é um estado da sessão e
 * é desenhada pela própria tela da história, para que o progresso não precise
 * ser carregado duas vezes nem duplicado entre dois ViewModels.
 */
sealed interface Rota {

    @Serializable
    data object Apresentacao : Rota

    @Serializable
    data object Inicio : Rota

    @Serializable
    data object Catalogo : Rota

    @Serializable
    data class Historia(val casoId: String, val retomar: Boolean = true) : Rota

    @Serializable
    data class Pausa(val casoId: String, val temEtapas: Boolean = false) : Rota

    @Serializable
    data class Retomada(val casoId: String) : Rota

    @Serializable
    data class Etapas(val casoId: String) : Rota

    @Serializable
    data class Caderno(val casoId: String) : Rota

    @Serializable
    data object Configuracoes : Rota

    @Serializable
    data object Sobre : Rota
}

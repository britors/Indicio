package br.com.avoren.indicio.domain.armazenamento

import br.com.avoren.indicio.domain.model.preferencias.Preferencias
import br.com.avoren.indicio.domain.model.preferencias.TamanhoTexto
import kotlinx.coroutines.flow.Flow

/** Preferências persistidas do aplicativo. */
interface RepositorioPreferencias {

    val preferencias: Flow<Preferencias>

    suspend fun definirTamanhoTexto(tamanho: TamanhoTexto): ResultadoArmazenamento<Unit>

    suspend fun definirReducaoDeMovimentos(reduzir: Boolean): ResultadoArmazenamento<Unit>
}

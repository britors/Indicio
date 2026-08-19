package br.com.avoren.indicio.data.preferencias

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.avoren.indicio.domain.armazenamento.RepositorioPreferencias
import br.com.avoren.indicio.domain.armazenamento.ResultadoArmazenamento
import br.com.avoren.indicio.domain.model.preferencias.TamanhoTexto
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import br.com.avoren.indicio.domain.model.preferencias.Preferencias as PreferenciasDoUsuario

/**
 * Preferências de leitura e conforto guardadas com DataStore.
 *
 * Leitura com falha cai para os valores padrão, que são os mais confortáveis;
 * o aplicativo nunca fica sem preferências utilizáveis.
 */
class RepositorioPreferenciasDataStore(
    private val dataStore: DataStore<Preferences>,
) : RepositorioPreferencias {

    override val preferencias: Flow<PreferenciasDoUsuario> = dataStore.data
        .catch { erro ->
            if (erro is IOException) emit(emptyPreferences()) else throw erro
        }
        .map { armazenadas ->
            PreferenciasDoUsuario(
                tamanhoTexto = TamanhoTexto.porChave(armazenadas[CHAVE_TAMANHO_TEXTO]),
                reduzirMovimentos = armazenadas[CHAVE_REDUZIR_MOVIMENTOS] ?: false,
            )
        }

    override suspend fun definirTamanhoTexto(tamanho: TamanhoTexto): ResultadoArmazenamento<Unit> =
        gravar { it[CHAVE_TAMANHO_TEXTO] = tamanho.chave }

    override suspend fun definirReducaoDeMovimentos(reduzir: Boolean): ResultadoArmazenamento<Unit> =
        gravar { it[CHAVE_REDUZIR_MOVIMENTOS] = reduzir }

    private suspend fun gravar(
        alteracao: (androidx.datastore.preferences.core.MutablePreferences) -> Unit,
    ): ResultadoArmazenamento<Unit> = try {
        dataStore.edit(alteracao)
        ResultadoArmazenamento.Sucesso(Unit)
    } catch (erro: IOException) {
        ResultadoArmazenamento.Falha(
            "Não foi possível salvar a preferência: ${erro.message ?: "erro de armazenamento"}",
        )
    }

    private companion object {
        val CHAVE_TAMANHO_TEXTO = stringPreferencesKey("tamanho_texto")
        val CHAVE_REDUZIR_MOVIMENTOS = booleanPreferencesKey("reduzir_movimentos")
    }
}

package br.com.avoren.indicio.data.banco

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

/**
 * Guarda listas de identificadores como JSON.
 *
 * São sempre listas curtas de ids; uma tabela de relacionamento traria
 * complexidade sem benefício prático aqui.
 */
class ConversoresLista {

    @TypeConverter
    fun listaParaTexto(valor: List<String>): String = Json.encodeToString(valor)

    @TypeConverter
    fun textoParaLista(valor: String): List<String> = Json.decodeFromString(valor)
}

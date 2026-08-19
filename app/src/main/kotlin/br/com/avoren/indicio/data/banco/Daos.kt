package br.com.avoren.indicio.data.banco

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressoDao {

    @Query("SELECT * FROM progresso WHERE casoId = :casoId")
    suspend fun porCaso(casoId: String): ProgressoEntidade?

    /** Base do "Continuar": a sessão mexida mais recentemente. */
    @Query("SELECT * FROM progresso ORDER BY atualizadoEm DESC LIMIT 1")
    fun maisRecente(): Flow<ProgressoEntidade?>

    @Upsert
    suspend fun salvar(progresso: ProgressoEntidade)

    @Query("DELETE FROM progresso WHERE casoId = :casoId")
    suspend fun apagar(casoId: String)
}

@Dao
interface ConclusaoDao {

    @Insert
    suspend fun registrar(conclusao: ConclusaoEntidade)

    @Query("SELECT * FROM conclusoes ORDER BY concluidoEm DESC")
    fun historico(): Flow<List<ConclusaoEntidade>>

    @Query("SELECT COUNT(*) FROM conclusoes WHERE casoId = :casoId")
    suspend fun quantidadePorCaso(casoId: String): Int
}

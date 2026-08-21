package br.com.w3ti.indicio.data.banco

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressoDao {

    @Query("SELECT * FROM progresso ORDER BY atualizadoEm DESC")
    fun observarTodos(): Flow<List<ProgressoEntidade>>

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

@Dao
interface DicaDao {
    @Query("SELECT * FROM dicas WHERE casoId = :casoId AND cenaId = :cenaId")
    suspend fun porCena(casoId: String, cenaId: String): DicaEntidade?

    @Query("SELECT COUNT(*) FROM dicas WHERE casoId = :casoId AND usadaEm >= :inicio")
    suspend fun quantidadeDoCasoDesde(casoId: String, inicio: Long): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserir(dica: DicaEntidade): Long

    @Transaction
    suspend fun registrarSeDisponivel(
        dica: DicaEntidade,
        inicioDaSemana: Long,
        limite: Int,
    ): Boolean {
        if (porCena(dica.casoId, dica.cenaId) != null) return true
        if (quantidadeDoCasoDesde(dica.casoId, inicioDaSemana) >= limite) return false
        return inserir(dica) != -1L
    }
}

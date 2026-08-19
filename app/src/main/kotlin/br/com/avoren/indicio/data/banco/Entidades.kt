package br.com.avoren.indicio.data.banco

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Progresso de um caso, um registro por caso.
 *
 * Além das escolhas — que bastariam para reconstruir tudo — guarda cena atual,
 * pistas e desfecho já resolvidos. Essa redundância deliberada permite que a
 * tela inicial decida sobre "Continuar" sem abrir e validar o JSON do caso.
 */
@Entity(tableName = "progresso")
data class ProgressoEntidade(
    @PrimaryKey val casoId: String,
    val cenaAtual: String,
    val escolhas: List<String>,
    val pistas: List<String>,
    val desfechoAlcancado: String?,
    val atualizadoEm: Long,
)

/**
 * Histórico de conclusões.
 *
 * Independente do progresso: reiniciar um caso apaga o progresso e preserva
 * tudo o que já foi concluído antes.
 */
@Entity(tableName = "conclusoes")
data class ConclusaoEntidade(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val casoId: String,
    val cenaFinal: String,
    val tituloDesfecho: String,
    val pistas: List<String>,
    val concluidoEm: Long,
)

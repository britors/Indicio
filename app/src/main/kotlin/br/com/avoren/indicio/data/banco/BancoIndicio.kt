package br.com.avoren.indicio.data.banco

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Banco local do aplicativo. Nada sai do aparelho.
 */
@Database(
    entities = [ProgressoEntidade::class, ConclusaoEntidade::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(ConversoresLista::class)
abstract class BancoIndicio : RoomDatabase() {

    abstract fun progressoDao(): ProgressoDao

    abstract fun conclusaoDao(): ConclusaoDao

    companion object {
        private const val NOME = "indicio.db"

        fun criar(context: Context): BancoIndicio =
            Room.databaseBuilder(context, BancoIndicio::class.java, NOME).build()
    }
}

package br.com.avoren.indicio.data.banco

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Banco local do aplicativo. Nada sai do aparelho.
 */
@Database(
    entities = [ProgressoEntidade::class, ConclusaoEntidade::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(ConversoresLista::class)
abstract class BancoIndicio : RoomDatabase() {

    abstract fun progressoDao(): ProgressoDao

    abstract fun conclusaoDao(): ConclusaoDao

    companion object {
        private const val NOME = "indicio.db"

        val MIGRACAO_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE progresso ADD COLUMN versaoEsquema INTEGER NOT NULL DEFAULT 1",
                )
                db.execSQL(
                    "ALTER TABLE progresso ADD COLUMN versaoConteudo INTEGER NOT NULL DEFAULT 1",
                )
                db.execSQL(
                    "ALTER TABLE conclusoes ADD COLUMN versaoEsquema INTEGER NOT NULL DEFAULT 1",
                )
                db.execSQL(
                    "ALTER TABLE conclusoes ADD COLUMN versaoConteudo INTEGER NOT NULL DEFAULT 1",
                )
            }
        }

        fun criar(context: Context): BancoIndicio =
            Room.databaseBuilder(context, BancoIndicio::class.java, NOME)
                .addMigrations(MIGRACAO_1_2)
                .build()
    }
}

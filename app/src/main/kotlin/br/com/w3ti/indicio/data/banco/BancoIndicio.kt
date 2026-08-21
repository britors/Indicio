package br.com.w3ti.indicio.data.banco

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
    entities = [ProgressoEntidade::class, ConclusaoEntidade::class, DicaEntidade::class],
    version = 3,
    exportSchema = true,
)
@TypeConverters(ConversoresLista::class)
abstract class BancoIndicio : RoomDatabase() {

    abstract fun progressoDao(): ProgressoDao

    abstract fun conclusaoDao(): ConclusaoDao

    abstract fun dicaDao(): DicaDao

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

        val MIGRACAO_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS dicas (" +
                        "casoId TEXT NOT NULL, cenaId TEXT NOT NULL, " +
                        "escolhaId TEXT NOT NULL, usadaEm INTEGER NOT NULL, " +
                        "PRIMARY KEY(casoId, cenaId))",
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_dicas_usadaEm ON dicas(usadaEm)")
            }
        }

        fun criar(context: Context): BancoIndicio =
            Room.databaseBuilder(context, BancoIndicio::class.java, NOME)
                .addMigrations(MIGRACAO_1_2, MIGRACAO_2_3)
                .build()
    }
}

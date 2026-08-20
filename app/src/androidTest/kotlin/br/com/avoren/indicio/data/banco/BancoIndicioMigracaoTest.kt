package br.com.avoren.indicio.data.banco

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BancoIndicioMigracaoTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        BancoIndicio::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun migracao1Para2PreservaProgressoEHistoricoComoVersao1() {
        helper.createDatabase(NOME_BANCO, 1).apply {
            execSQL(
                "INSERT INTO progresso " +
                    "(casoId, cenaAtual, escolhas, pistas, desfechoAlcancado, atualizadoEm) " +
                    "VALUES ('caso-legado', 'cena-2', '[\"escolha-1\"]', '[]', NULL, 100)",
            )
            execSQL(
                "INSERT INTO conclusoes " +
                    "(casoId, cenaFinal, tituloDesfecho, pistas, concluidoEm) " +
                    "VALUES ('caso-legado', 'final', 'Final tranquilo', '[]', 90)",
            )
            close()
        }

        val banco = helper.runMigrationsAndValidate(
            NOME_BANCO,
            2,
            true,
            BancoIndicio.MIGRACAO_1_2,
        )

        banco.query(
            "SELECT versaoEsquema, versaoConteudo FROM progresso WHERE casoId = 'caso-legado'",
        ).use { cursor ->
            cursor.moveToFirst()
            assertEquals(1, cursor.getInt(0))
            assertEquals(1, cursor.getInt(1))
        }
        banco.query(
            "SELECT versaoEsquema, versaoConteudo FROM conclusoes WHERE casoId = 'caso-legado'",
        ).use { cursor ->
            cursor.moveToFirst()
            assertEquals(1, cursor.getInt(0))
            assertEquals(1, cursor.getInt(1))
        }
        banco.close()
    }

    private companion object {
        const val NOME_BANCO = "migracao-indicio-test"
    }
}

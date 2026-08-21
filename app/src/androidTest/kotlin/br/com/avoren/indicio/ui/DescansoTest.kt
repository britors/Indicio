package br.com.avoren.indicio.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import br.com.avoren.indicio.domain.model.preferencias.TamanhoTexto
import br.com.avoren.indicio.ui.descanso.TelaDescanso
import br.com.avoren.indicio.ui.tema.TemaIndicio
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import org.junit.Rule
import org.junit.Test

class DescansoTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun mostraMensagemEContagemRegressiva() {
        composeRule.setContent {
            TemaIndicio {
                TelaDescanso(
                    tempoRestante = 4.minutes + 7.seconds,
                    duracaoTotal = 5.minutes,
                )
            }
        }

        composeRule.onNodeWithText("Descanse um pouco").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Tempo restante: 4 minutos e 7 segundos")
            .assertIsDisplayed()
    }

    @Test
    fun textoMuitoGrandeMantemOrientacaoDoRetornoAutomaticoAcessivel() {
        composeRule.setContent {
            TemaIndicio(TamanhoTexto.MUITO_GRANDE) {
                TelaDescanso(
                    tempoRestante = 5.minutes,
                    duracaoTotal = 5.minutes,
                )
            }
        }

        composeRule.onNodeWithText(
            "A investigação continua automaticamente ao fim da pausa.",
        ).performScrollTo().assertIsDisplayed()
    }
}

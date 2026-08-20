package br.com.avoren.indicio.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Escolha
import br.com.avoren.indicio.domain.model.caso.Imagem
import br.com.avoren.indicio.domain.narracao.EstadoNarracao
import br.com.avoren.indicio.ui.historia.ConteudoHistoria
import br.com.avoren.indicio.ui.historia.EstadoHistoria
import br.com.avoren.indicio.ui.tema.TemaIndicio
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Cobre a tela jogável: ilustração, controle de narração e as duas escolhas.
 */
class ExperienciaJogavelTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun mostrar(
        estadoNarracao: EstadoNarracao,
        onAlternar: () -> Unit = {},
    ) {
        composeRule.setContent {
            TemaIndicio {
                ConteudoHistoria(
                    estado = EstadoHistoria.EmCurso(
                        tituloCaso = "Caso",
                        cena = Cena(
                            id = "cena",
                            texto = "Texto da cena.",
                            imagem = Imagem(
                                recurso = "recurso_que_nao_existe",
                                descricaoAcessivel = DESCRICAO_DA_IMAGEM,
                            ),
                            escolhas = listOf(
                                Escolha("a", "Primeira opção", "outra"),
                                Escolha("b", "Segunda opção", "outra"),
                            ),
                        ),
                        pistas = emptyList(),
                    ),
                    estadoNarracao = estadoNarracao,
                    onEscolher = {},
                    onAlternarNarracao = onAlternar,
                    onPausar = {},
                )
            }
        }
    }

    @Test
    fun comVozOControleOfereceOuvirOTrecho() {
        var alternou = 0
        mostrar(EstadoNarracao.PRONTO) { alternou++ }

        composeRule.onNodeWithText("Ouvir o trecho").assertHasClickAction().performClick()

        assertEquals(1, alternou)
    }

    @Test
    fun enquantoFalaOControleOfereceParar() {
        mostrar(EstadoNarracao.FALANDO)

        composeRule.onNodeWithText("Parar a narração").assertHasClickAction()
    }

    @Test
    fun semVozAparecerAvisoNaoBloqueanteEAsEscolhasSeguem() {
        mostrar(EstadoNarracao.INDISPONIVEL)

        composeRule.onNodeWithText(
            "Este aparelho não tem voz em português instalada. " +
                "A história continua disponível para leitura.",
        ).assertIsDisplayed()

        composeRule.onNodeWithText("Primeira opção").assertHasClickAction()
        composeRule.onNodeWithText("Segunda opção").assertHasClickAction()
    }

    @Test
    fun enquantoPreparaNaoMostraControleNemAviso() {
        mostrar(EstadoNarracao.PREPARANDO)

        composeRule.onNodeWithText("Ouvir o trecho").assertDoesNotExist()
        composeRule.onNodeWithText("Parar a narração").assertDoesNotExist()
    }

    @Test
    fun semIlustracaoADescricaoAcessivelEApresentadaComoTexto() {
        mostrar(EstadoNarracao.PRONTO)

        composeRule.onNodeWithText(DESCRICAO_DA_IMAGEM).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(DESCRICAO_DA_IMAGEM).assertExists()
    }

    private companion object {
        const val DESCRICAO_DA_IMAGEM = "Vitrine de vidro sobre um pedestal deslocado."
    }
}

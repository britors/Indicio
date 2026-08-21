package br.com.w3ti.indicio.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.com.w3ti.indicio.domain.model.caso.Cena
import br.com.w3ti.indicio.domain.model.caso.Escolha
import br.com.w3ti.indicio.domain.model.caso.Imagem
import br.com.w3ti.indicio.domain.narracao.EstadoNarracao
import br.com.w3ti.indicio.ui.historia.ConteudoHistoria
import br.com.w3ti.indicio.ui.historia.EstadoHistoria
import br.com.w3ti.indicio.ui.tema.TemaIndicio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        textoDaCena: String = "Texto da cena.",
        onAlternar: () -> Unit = {},
    ) {
        composeRule.setContent {
            TemaIndicio {
                ConteudoHistoria(
                    estado = EstadoHistoria.EmCurso(
                        tituloCaso = "Caso",
                        cena = Cena(
                            id = "cena",
                            texto = textoDaCena,
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
                    onConfiguracoes = {},
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

    @Test
    fun textoLongoPodeSerExpandidoERecolhido() {
        val textoLongo = List(8) { "Uma observação importante permanece na cena." }.joinToString(" ")
        mostrar(EstadoNarracao.PRONTO, textoDaCena = textoLongo)

        val alturaRecolhida = composeRule.onNodeWithText(textoLongo)
            .fetchSemanticsNode().boundsInRoot.height

        composeRule.onNodeWithText("Mostrar texto completo")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        val alturaExpandida = composeRule.onNodeWithText(textoLongo)
            .fetchSemanticsNode().boundsInRoot.height
        assertTrue(alturaExpandida > alturaRecolhida)

        composeRule.onNodeWithText("Recolher texto")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        val alturaRecolhidaNovamente = composeRule.onNodeWithText(textoLongo)
            .fetchSemanticsNode().boundsInRoot.height
        assertEquals(alturaRecolhida, alturaRecolhidaNovamente)
    }

    private companion object {
        const val DESCRICAO_DA_IMAGEM = "Vitrine de vidro sobre um pedestal deslocado."
    }
}

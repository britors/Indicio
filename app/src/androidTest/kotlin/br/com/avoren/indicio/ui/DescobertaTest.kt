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
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.model.preferencias.TamanhoTexto
import br.com.avoren.indicio.domain.narracao.EstadoNarracao
import br.com.avoren.indicio.ui.historia.ConteudoHistoria
import br.com.avoren.indicio.ui.historia.EstadoHistoria
import br.com.avoren.indicio.ui.historia.MomentoDeDescoberta
import br.com.avoren.indicio.ui.tema.TemaIndicio
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DescobertaTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun descobertaMostraTituloEExplicaPorQueAPistaImporta() {
        var dispensou = false
        composeRule.setContent {
            TemaIndicio {
                MomentoDeDescoberta(
                    pista = PISTA,
                    onDispensar = { dispensou = true },
                )
            }
        }

        composeRule.onNodeWithText("NOVA PISTA NO CADERNO").assertIsDisplayed()
        composeRule.onNodeWithText(PISTA.titulo).assertIsDisplayed()
        composeRule.onNodeWithText("Por que importa").assertIsDisplayed()
        composeRule.onNodeWithText(PISTA.relevancia!!).assertIsDisplayed()
        composeRule.onNodeWithText("Continuar investigando")
            .assertHasClickAction()
            .performClick()

        assertTrue(dispensou)
    }

    @Test
    fun menuIndicaPistasNovasAteOCadernoSerAberto() {
        var abriuCaderno = false
        composeRule.setContent {
            TemaIndicio {
                ConteudoHistoria(
                    estado = EstadoHistoria.EmCurso(
                        tituloCaso = "Caso",
                        cena = CENA,
                        pistas = listOf(PISTA),
                    ),
                    estadoNarracao = EstadoNarracao.INDISPONIVEL,
                    onEscolher = {},
                    onAlternarNarracao = {},
                    onConfiguracoes = {},
                    onAbrirCaderno = { abriuCaderno = true },
                    pistasNaoLidas = 2,
                )
            }
        }

        composeRule.onNodeWithContentDescription("Abrir menu da investigação").performClick()
        composeRule.onNodeWithText("2 novas pistas").assertIsDisplayed().performClick()

        assertTrue(abriuCaderno)
    }

    @Test
    fun descobertaContinuaAcessivelComTextoMuitoGrande() {
        composeRule.setContent {
            TemaIndicio(TamanhoTexto.MUITO_GRANDE) {
                MomentoDeDescoberta(pista = PISTA, onDispensar = {})
            }
        }

        composeRule.onNodeWithText("NOVA PISTA NO CADERNO").assertIsDisplayed()
        composeRule.onNodeWithText("Continuar investigando").assertHasClickAction()
    }

    private companion object {
        val PISTA = Pista(
            id = "vitrine",
            titulo = "A vitrine sem sinais de força",
            descricao = "Vidro e fechadura permanecem intactos.",
            relevancia = "Isso desloca a atenção para a rotina da sala.",
        )
        val CENA = Cena(
            id = "cena",
            texto = "A sala ainda guarda detalhes importantes.",
            imagem = Imagem("arte_inexistente", "Sala preservada para a investigação."),
            escolhas = listOf(
                Escolha("a", "Examinar a vitrine", "destino-a"),
                Escolha("b", "Ouvir a curadora", "destino-b"),
            ),
        )
    }
}

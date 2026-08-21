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
import br.com.w3ti.indicio.domain.model.preferencias.TamanhoTexto
import br.com.w3ti.indicio.domain.narracao.EstadoNarracao
import br.com.w3ti.indicio.ui.dica.EstadoDica
import br.com.w3ti.indicio.ui.historia.ConteudoHistoria
import br.com.w3ti.indicio.ui.historia.EstadoHistoria
import br.com.w3ti.indicio.ui.tema.TemaIndicio
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DicaTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun confirmaAntesDeConsumirEDicaFicaAntesDeConfiguracoes() {
        var pediu = false
        montar(EstadoDica.Disponivel(CENA_ID, restantes = 3), onRevelar = { pediu = true })

        composeRule.onNodeWithText("Dica do Anônimo").assertDoesNotExist()
        abrirMenu()

        val dica = composeRule.onNodeWithText("Dica do Anônimo")
            .assertHasClickAction()
        val configuracoes = composeRule.onNodeWithText("Configurações")
        assertTrue(
            dica.fetchSemanticsNode().boundsInRoot.top <
                configuracoes.fetchSemanticsNode().boundsInRoot.top,
        )
        dica.performClick()

        assertFalse(pediu)
        composeRule.onNodeWithText("Bilhete do Anônimo").assertIsDisplayed()
        composeRule.onNodeWithText("Para este caso, você tem 3 dicas nesta semana.").assertIsDisplayed()
        composeRule.onNodeWithText("Usar uma dica").performClick()
        assertTrue(pediu)
    }

    @Test
    fun fecharBilheteNaoConsomeDica() {
        var pediu = false
        montar(EstadoDica.Disponivel(CENA_ID, restantes = 3), onRevelar = { pediu = true })

        abrirMenu()
        composeRule.onNodeWithText("Dica do Anônimo").performClick()
        composeRule.onNodeWithText("Agora não").performClick()

        assertFalse(pediu)
        composeRule.onNodeWithText("Bilhete do Anônimo").assertDoesNotExist()
    }

    @Test
    fun mensagemReveladaInsinuaOCaminhoSemNomearAEscolha() {
        montar(EstadoDica.Revelada(CENA_ID, MENSAGEM_INDIRETA, restantes = 1))

        abrirMenu()
        composeRule.onNodeWithText("Dica do Anônimo").performClick()

        composeRule.onNodeWithText("Mensagem do Anônimo").assertIsDisplayed()
        composeRule.onNodeWithText(MENSAGEM_INDIRETA).assertIsDisplayed()
        composeRule
            .onNodeWithText("1 dica restante para este caso nesta semana")
            .assertIsDisplayed()
    }

    @Test
    fun painelContinuaAcessivelComTextoMuitoGrande() {
        montar(
            EstadoDica.Disponivel(CENA_ID, restantes = 3),
            tamanhoTexto = TamanhoTexto.MUITO_GRANDE,
        )

        abrirMenu()
        composeRule.onNodeWithText("Dica do Anônimo").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Bilhete do Anônimo").assertIsDisplayed()
    }

    private fun abrirMenu() {
        composeRule.onNodeWithContentDescription("Abrir menu da investigação").performClick()
    }

    private fun montar(
        estadoDica: EstadoDica,
        onRevelar: () -> Unit = {},
        tamanhoTexto: TamanhoTexto = TamanhoTexto.GRANDE,
    ) {
        composeRule.setContent {
            TemaIndicio(tamanhoTexto) {
                ConteudoHistoria(
                    estado = EstadoHistoria.EmCurso(
                        tituloCaso = "Caso",
                        cena = Cena(
                            id = CENA_ID,
                            texto = "Dois detalhes ainda precisam ser comparados.",
                            imagem = Imagem("arte_inexistente", "Sala da investigação"),
                            escolhas = listOf(
                                Escolha("a", PRIMEIRA_ESCOLHA, "destino-a"),
                                Escolha("b", "Examinar o corredor", "destino-b"),
                            ),
                        ),
                        pistas = emptyList(),
                    ),
                    estadoNarracao = EstadoNarracao.INDISPONIVEL,
                    onEscolher = {},
                    onAlternarNarracao = {},
                    onConfiguracoes = {},
                    estadoDica = estadoDica,
                    onRevelarDica = onRevelar,
                )
            }
        }
    }

    private companion object {
        const val CENA_ID = "cena"
        const val PRIMEIRA_ESCOLHA = "Conversar com a curadora"
        const val MENSAGEM_INDIRETA =
            "Quem conhece cada canto da sala talvez tenha reparado no detalhe que escapou aos demais."
    }
}

package br.com.avoren.indicio.ui

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Escolha
import br.com.avoren.indicio.domain.model.caso.Imagem
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.model.preferencias.TamanhoTexto
import br.com.avoren.indicio.domain.narracao.EstadoNarracao
import br.com.avoren.indicio.ui.historia.ConteudoHistoria
import br.com.avoren.indicio.ui.historia.EstadoHistoria
import br.com.avoren.indicio.ui.tema.AlturaMinimaBotao
import br.com.avoren.indicio.ui.tema.TemaIndicio
import org.junit.Rule
import org.junit.Test

/**
 * Requisitos de acessibilidade da tela onde o jogo acontece.
 *
 * O produto é feito para pessoas idosas e para quem tem dificuldade cognitiva
 * leve: alvo de toque, papel do controle, texto equivalente da imagem e
 * comportamento com o texto ampliado são requisitos, e não acabamento.
 */
class AcessibilidadeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun montar(
        tamanho: TamanhoTexto = TamanhoTexto.GRANDE,
        recursoDaArte: String = ARTE_EXISTENTE,
    ) {
        composeRule.setContent {
            TemaIndicio(tamanho) {
                ConteudoHistoria(
                    estado = EstadoHistoria.EmCurso(
                        tituloCaso = "O Mistério da Taça Desaparecida",
                        cena = Cena(
                            id = "vitrine",
                            texto = "A vitrine está intacta. O pedestal, porém, não está centralizado.",
                            imagem = Imagem(recursoDaArte, DESCRICAO),
                            escolhas = listOf(
                                Escolha("a", PRIMEIRA_ESCOLHA, "po"),
                                Escolha("b", SEGUNDA_ESCOLHA, "forro"),
                            ),
                        ),
                        pistas = listOf(Pista("p", TITULO_DA_PISTA, DESCRICAO_DA_PISTA)),
                    ),
                    estadoNarracao = EstadoNarracao.PRONTO,
                    onEscolher = {},
                    onAlternarNarracao = {},
                    onPausar = {},
                )
            }
        }
    }

    @Test
    fun cadaEscolhaEUmBotaoComAlvoConfortavel() {
        montar()

        listOf(PRIMEIRA_ESCOLHA, SEGUNDA_ESCOLHA).forEach { texto ->
            composeRule.onNodeWithText(texto)
                .performScrollTo()
                .assertHasClickAction()
                .assertHeightIsAtLeast(AlturaMinimaBotao)
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        }
    }

    @Test
    fun a_carta_e_aparencia_e_o_controle_continua_sendo_botao() {
        // A escolha virou carta na apresentação. Se algum dia deixar de expor
        // papel de botão, deixa de ser operável só por botões — que é regra do
        // produto, não preferência visual.
        montar()

        composeRule.onNodeWithText(PRIMEIRA_ESCOLHA)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
    }

    @Test
    fun a_arte_da_cena_expoe_o_texto_equivalente() {
        montar()

        composeRule.onNodeWithContentDescription(DESCRICAO).assertExists()
    }

    @Test
    fun sem_arte_a_descricao_acessivel_continua_disponivel() {
        // Caso novo cuja arte ainda não foi desenhada: a cena não pode ficar
        // muda para o leitor de tela.
        montar(recursoDaArte = "cena_que_nao_existe")

        composeRule.onNodeWithContentDescription(DESCRICAO).assertExists()
    }

    @Test
    fun os_titulos_de_secao_sao_cabecalhos() {
        montar()

        composeRule.onNodeWithText("Qual caminho seguir?").assert(isHeading())
        composeRule.onNodeWithText("Caderno de pistas").performScrollTo().assert(isHeading())
    }

    @Test
    fun o_controle_de_narracao_anuncia_o_proprio_estado() {
        montar()

        composeRule.onNodeWithText("Ouvir o trecho")
            .assertHasClickAction()
            .assertHeightIsAtLeast(AlturaMinimaBotao)
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.StateDescription))
    }

    @Test
    fun a_pista_e_sempre_textual() {
        // Nenhuma pista pode depender de ícone ou de cor.
        montar()

        composeRule.onNodeWithText(TITULO_DA_PISTA).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(DESCRICAO_DA_PISTA).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun com_texto_muito_grande_as_escolhas_continuam_alcancaveis() {
        // O critério que mais custa: o texto ampliado não pode empurrar as
        // escolhas para fora nem cobri-las.
        montar(tamanho = TamanhoTexto.MUITO_GRANDE)

        listOf(PRIMEIRA_ESCOLHA, SEGUNDA_ESCOLHA).forEach { texto ->
            composeRule.onNodeWithText(texto)
                .performScrollTo()
                .assertIsDisplayed()
                .assertHasClickAction()
                .assertHeightIsAtLeast(AlturaMinimaBotao)
        }
    }

    @Test
    fun com_texto_muito_grande_o_trecho_da_cena_nao_e_truncado() {
        montar(tamanho = TamanhoTexto.MUITO_GRANDE)

        composeRule.onNodeWithText(
            "A vitrine está intacta. O pedestal, porém, não está centralizado.",
        ).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun a_pausa_continua_ao_alcance_com_texto_muito_grande() {
        montar(tamanho = TamanhoTexto.MUITO_GRANDE)

        composeRule.onNodeWithContentDescription("Pausar")
            .assertIsDisplayed()
            .assertHasClickAction()
            .assertHeightIsAtLeast(AlturaMinimaBotao)
    }

    private companion object {
        /** Precisa ser uma arte que existe, para exercitar o caminho da imagem. */
        const val ARTE_EXISTENTE = "cena_vitrine"
        const val DESCRICAO = "Vitrine de vidro fechada sobre um pedestal deslocado."
        const val PRIMEIRA_ESCOLHA = "Examinar o chão em volta do pedestal"
        const val SEGUNDA_ESCOLHA = "Olhar para o forro, acima da vitrine"
        const val TITULO_DA_PISTA = "O pedestal fora do lugar"
        const val DESCRICAO_DA_PISTA = "Está à esquerda da marca antiga no piso."
    }
}

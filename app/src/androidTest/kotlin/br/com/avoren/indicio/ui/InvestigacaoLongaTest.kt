package br.com.avoren.indicio.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import br.com.avoren.indicio.domain.model.preferencias.TamanhoTexto
import br.com.avoren.indicio.ui.investigacao.CadernoUi
import br.com.avoren.indicio.ui.investigacao.ConteudoCaderno
import br.com.avoren.indicio.ui.investigacao.ConteudoEtapas
import br.com.avoren.indicio.ui.investigacao.ConteudoRetomada
import br.com.avoren.indicio.ui.investigacao.ConversaUi
import br.com.avoren.indicio.ui.investigacao.EstadoInvestigacao
import br.com.avoren.indicio.ui.investigacao.EtapaUi
import br.com.avoren.indicio.ui.investigacao.LocalUi
import br.com.avoren.indicio.ui.investigacao.ObjetivoUi
import br.com.avoren.indicio.ui.investigacao.PessoaUi
import br.com.avoren.indicio.ui.investigacao.PistaUi
import br.com.avoren.indicio.ui.investigacao.RetomadaUi
import br.com.avoren.indicio.ui.investigacao.SituacaoEtapa
import br.com.avoren.indicio.ui.investigacao.TAG_LISTA_CADERNO
import br.com.avoren.indicio.ui.tema.AlturaMinimaBotao
import br.com.avoren.indicio.ui.tema.TemaIndicio
import org.junit.Rule
import org.junit.Test

class InvestigacaoLongaTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun cadernoVazioExplicaQueAindaNaoHaPistas() {
        composeRule.setContent {
            TemaIndicio { ConteudoCaderno(estado = estado(), onVoltar = {}) }
        }

        composeRule.onNodeWithText("Nenhuma pista foi descoberta ainda.").assertIsDisplayed()
        composeRule.onNodeWithText("Pessoas").assertHasClickAction().performClick()
        composeRule.onNodeWithText("Nenhuma pessoa foi registrada ainda.").assertIsDisplayed()
    }

    @Test
    fun cadernoMantemTodasAsAbasVisiveisComTextoMuitoGrande() {
        composeRule.setContent {
            TemaIndicio(TamanhoTexto.MUITO_GRANDE) {
                ConteudoCaderno(estado = estado(), onVoltar = {})
            }
        }

        listOf("Pistas", "Pessoas", "Locais", "Conversas").forEach { rotulo ->
            composeRule.onNodeWithText(rotulo)
                .assertIsDisplayed()
                .assertHasClickAction()
                .assertHeightIsAtLeast(AlturaMinimaBotao)
        }

        composeRule.onNodeWithText("Conversas").performClick()
        composeRule.onNodeWithText("Nenhuma conversa foi registrada ainda.").assertIsDisplayed()
    }

    @Test
    fun retomadaParcialMostraContextoLembrancaEObjetivo() {
        composeRule.setContent {
            TemaIndicio {
                ConteudoRetomada(
                    estado = estado(
                        retomada = RetomadaUi(
                            etapa = "Comparar registros",
                            resumo = "Você começou a conferir horários e etiquetas.",
                            lembrancas = listOf("As duas listas foram feitas pela manhã."),
                        ),
                    ),
                    onContinuar = {},
                    onAbrirCaderno = {},
                    onVoltar = {},
                )
            }
        }

        composeRule.onNodeWithText("Comparar registros").assertIsDisplayed()
        composeRule.onNodeWithText("As duas listas foram feitas pela manhã.", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("Continuar investigação").assertHasClickAction()
    }

    @Test
    fun cadernoLongoRolaEConversaAbreSemEscolhaNarrativa() {
        val pistas = (1..20).map { PistaUi("p$it", "Pista $it", "Descrição $it") }
        val conversa = ConversaUi("c1", "O registro da manhã", "A conversa registrada.", "A arquivista")
        composeRule.setContent {
            TemaIndicio {
                ConteudoCaderno(
                    estado = estado(
                        caderno = CadernoUi(
                            pistas = pistas,
                            pessoas = listOf(PessoaUi("pessoa", "A arquivista", "Acervo", listOf("Anotação"), listOf(conversa))),
                            locais = listOf(LocalUi("local", "Sala de consulta", listOf("Armário compartilhado"))),
                            conversas = listOf(conversa),
                        ),
                    ),
                    onVoltar = {},
                )
            }
        }

        composeRule.onNodeWithTag(TAG_LISTA_CADERNO).performScrollToIndex(20)
        composeRule.onNodeWithText("Pista 20").assertIsDisplayed()
        composeRule.onNodeWithText("Conversas").performClick()
        composeRule.onNodeWithText("O registro da manhã").performClick()
        composeRule.onNodeWithText("A conversa registrada.").assertIsDisplayed()
    }

    @Test
    fun etapasConcluidasNaoOferecemRetomarNemRevelamFutura() {
        composeRule.setContent {
            TemaIndicio {
                ConteudoEtapas(
                    estado = estado(
                        etapas = listOf(
                            EtapaUi("e1", 1, SituacaoEtapa.CONCLUIDA, "Observar", "Resumo seguro"),
                            EtapaUi("e2", 2, SituacaoEtapa.CONCLUIDA, "Comparar", "Resumo seguro"),
                        ),
                        concluida = true,
                    ),
                    onContinuar = {},
                    onVoltar = {},
                )
            }
        }

        composeRule.onNodeWithText("Observar").assertIsDisplayed()
        composeRule.onNodeWithText("Comparar").assertIsDisplayed()
        composeRule.onNodeWithText("Retomar etapa atual").assertDoesNotExist()
    }

    private fun estado(
        retomada: RetomadaUi? = null,
        etapas: List<EtapaUi> = emptyList(),
        caderno: CadernoUi = CadernoUi(emptyList(), emptyList(), emptyList(), emptyList()),
        concluida: Boolean = false,
    ) = EstadoInvestigacao.Conteudo(
        casoId = "caso-generico",
        tituloCaso = "Caso genérico",
        retomada = retomada,
        etapas = etapas,
        objetivoAtual = ObjetivoUi("Comparar os registros", "O que mudou entre as listas?"),
        caderno = caderno,
        concluida = concluida,
        exibirRetomada = retomada != null,
    )
}

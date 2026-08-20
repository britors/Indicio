package br.com.avoren.indicio.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import br.com.avoren.indicio.domain.model.caso.Categoria
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Desfecho
import br.com.avoren.indicio.domain.model.caso.Imagem
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.model.caso.ResumoCaso
import br.com.avoren.indicio.domain.model.caso.TipoCena
import br.com.avoren.indicio.domain.model.preferencias.Preferencias
import br.com.avoren.indicio.domain.narracao.EstadoNarracao
import br.com.avoren.indicio.domain.model.preferencias.TamanhoTexto
import br.com.avoren.indicio.ui.catalogo.ConteudoCatalogo
import br.com.avoren.indicio.ui.catalogo.EstadoCatalogo
import br.com.avoren.indicio.ui.catalogo.GrupoDeCategoria
import br.com.avoren.indicio.ui.catalogo.TAG_LISTA_CATALOGO
import br.com.avoren.indicio.ui.configuracoes.ConteudoConfiguracoes
import br.com.avoren.indicio.ui.historia.ConteudoConclusao
import br.com.avoren.indicio.ui.historia.ConteudoHistoria
import br.com.avoren.indicio.ui.historia.EstadoHistoria
import br.com.avoren.indicio.ui.inicio.ConteudoInicio
import br.com.avoren.indicio.ui.inicio.EstadoInicio
import br.com.avoren.indicio.ui.pausa.TelaPausa
import br.com.avoren.indicio.ui.sobre.TelaSobre
import br.com.avoren.indicio.ui.tema.TemaIndicio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Verifica que cada tela estrutural aparece e que seus botões acionam a ação
 * correspondente.
 */
class TelasEstruturaisTest {

    @get:Rule
    val composeRule = createComposeRule()

    // ---------- Início ----------

    @Test
    fun inicioMostraMarcaEAcoes() {
        composeRule.setContent {
            TemaIndicio {
                ConteudoInicio(
                    estado = EstadoInicio(nome = "Indício", slogan = "Toda escolha revela uma pista.", versao = "0.1.0"),
                    onContinuar = {},
                    onEscolherCaso = {},
                    onConfiguracoes = {},
                    onSobre = {},
                )
            }
        }

        composeRule.onNodeWithText("Indício").assertIsDisplayed()
        composeRule.onNodeWithText("Toda escolha revela uma pista.").assertIsDisplayed()
        composeRule.onNodeWithText("Escolher caso").assertHasClickAction()
        composeRule.onNodeWithText("Configurações").assertHasClickAction()
        composeRule.onNodeWithText("Sobre").assertHasClickAction()
    }

    @Test
    fun continuarNaoApareceSemProgresso() {
        composeRule.setContent {
            TemaIndicio {
                ConteudoInicio(
                    estado = EstadoInicio(nome = "Indício", slogan = "s", versao = "0.1.0"),
                    onContinuar = {},
                    onEscolherCaso = {},
                    onConfiguracoes = {},
                    onSobre = {},
                )
            }
        }

        composeRule.onNodeWithText("Continuar").assertDoesNotExist()
    }

    @Test
    fun continuarApareceEAbreOCasoSalvo() {
        var abertoCom: String? = null
        composeRule.setContent {
            TemaIndicio {
                ConteudoInicio(
                    estado = EstadoInicio(
                        nome = "Indício",
                        slogan = "s",
                        versao = "0.1.0",
                        casoParaContinuar = "taca-desaparecida",
                        tituloParaContinuar = "O Mistério da Taça Desaparecida",
                    ),
                    onContinuar = { abertoCom = it },
                    onEscolherCaso = {},
                    onConfiguracoes = {},
                    onSobre = {},
                )
            }
        }

        composeRule
            .onNodeWithContentDescription("Continuar: O Mistério da Taça Desaparecida")
            .performClick()

        assertEquals("taca-desaparecida", abertoCom)
    }

    // ---------- Catálogo ----------

    @Test
    fun catalogoSeparaDisponivelDeFuturo() {
        composeRule.setContent {
            TemaIndicio {
                ConteudoCatalogo(
                    estado = EstadoCatalogo.Conteudo(
                        listOf(
                            GrupoDeCategoria(
                                Categoria.FUTEBOL,
                                listOf(
                                    ResumoCaso(
                                        id = "disponivel",
                                        titulo = "Caso disponível",
                                        sinopse = "Pode jogar.",
                                        categoria = Categoria.FUTEBOL,
                                        disponivel = true,
                                    ),
                                    ResumoCaso(
                                        id = "futuro",
                                        titulo = "Caso futuro",
                                        sinopse = "Ainda não.",
                                        categoria = Categoria.FUTEBOL,
                                        disponivel = false,
                                    ),
                                ),
                            ),
                            GrupoDeCategoria(Categoria.FAROESTE, emptyList()),
                        ),
                    ),
                    onAbrirCaso = {},
                    onTentarNovamente = {},
                )
            }
        }

        composeRule.onNodeWithText("Disponível agora").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Abrir o caso Caso disponível").assertHasClickAction()
        composeRule.onNodeWithTag(TAG_LISTA_CATALOGO).performScrollToIndex(4)
        composeRule.onNodeWithText("EM PREPARAÇÃO · SEM PREVISÃO").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Abrir o caso Caso futuro").assertDoesNotExist()
    }

    // ---------- História ----------

    @Test
    fun historiaMostraDuasEscolhasEAcionaAEscolhida() {
        var escolhida: String? = null
        composeRule.setContent {
            TemaIndicio {
                ConteudoHistoria(
                    estado = EstadoHistoria.EmCurso(
                        tituloCaso = "Caso",
                        cena = cenaComDuasEscolhas(),
                        pistas = emptyList(),
                    ),
                    estadoNarracao = EstadoNarracao.INDISPONIVEL,
                    onEscolher = { escolhida = it },
                    onAlternarNarracao = {},
                    onPausar = {},
                )
            }
        }

        composeRule.onNodeWithText("Primeira opção").assertHasClickAction()
        composeRule.onNodeWithText("Segunda opção").assertHasClickAction()
        composeRule.onNodeWithText("Segunda opção").performClick()

        assertEquals("b", escolhida)
    }

    @Test
    fun escolhasDesabilitadasDuranteATransicao() {
        var cliques = 0
        composeRule.setContent {
            TemaIndicio {
                ConteudoHistoria(
                    estado = EstadoHistoria.EmCurso(
                        tituloCaso = "Caso",
                        cena = cenaComDuasEscolhas(),
                        pistas = emptyList(),
                        escolhasHabilitadas = false,
                    ),
                    estadoNarracao = EstadoNarracao.INDISPONIVEL,
                    onEscolher = { cliques++ },
                    onAlternarNarracao = {},
                    onPausar = {},
                )
            }
        }

        composeRule.onNodeWithText("Primeira opção").performClick()

        assertEquals(0, cliques)
    }

    @Test
    fun pistasAparecemComoTexto() {
        composeRule.setContent {
            TemaIndicio {
                ConteudoHistoria(
                    estado = EstadoHistoria.EmCurso(
                        tituloCaso = "Caso",
                        cena = cenaComDuasEscolhas(),
                        pistas = listOf(Pista("p1", "Pista encontrada", "Descrição da pista.")),
                    ),
                    estadoNarracao = EstadoNarracao.INDISPONIVEL,
                    onEscolher = {},
                    onAlternarNarracao = {},
                    onPausar = {},
                )
            }
        }

        composeRule.onNodeWithText("Pista encontrada").assertIsDisplayed()
        composeRule.onNodeWithText("Descrição da pista.").assertIsDisplayed()
    }

    // ---------- Conclusão ----------

    @Test
    fun conclusaoMostraDesfechoExplicacaoEOpcoes() {
        var jogouNovamente = false
        var voltouAoCatalogo = false
        composeRule.setContent {
            TemaIndicio {
                ConteudoConclusao(
                    estado = EstadoHistoria.Concluida(
                        tituloCaso = "Caso",
                        cena = Cena(
                            id = "fim",
                            tipo = TipoCena.FINAL,
                            texto = "Texto do encerramento.",
                            imagem = Imagem("r", "Descrição acessível do encerramento."),
                        ),
                        desfecho = Desfecho(
                            titulo = "Caso resolvido",
                            mensagem = "Mensagem de encerramento.",
                            explicacaoPistas = "As pistas apontavam para o mesmo lugar.",
                        ),
                        pistas = listOf(Pista("p1", "Pista encontrada", "Descrição da pista.")),
                    ),
                    onJogarNovamente = { jogouNovamente = true },
                    onVoltarAoCatalogo = { voltouAoCatalogo = true },
                )
            }
        }

        composeRule.onNodeWithText("Caso resolvido").assertIsDisplayed()
        composeRule.onNodeWithText("As pistas apontavam para o mesmo lugar.").assertIsDisplayed()
        composeRule.onNodeWithText("Pista encontrada").assertIsDisplayed()

        composeRule.onNodeWithText("Jogar novamente").performClick()
        composeRule.onNodeWithText("Voltar ao catálogo").performClick()

        assertTrue(jogouNovamente)
        assertTrue(voltouAoCatalogo)
    }

    // ---------- Pausa ----------

    @Test
    fun pausaOfereceAsQuatroAcoes() {
        composeRule.setContent {
            TemaIndicio {
                TelaPausa(
                    onContinuar = {},
                    onConfiguracoes = {},
                    onReiniciar = {},
                    onVoltarAoInicio = {},
                )
            }
        }

        composeRule.onNodeWithText("Continuar a história").assertHasClickAction()
        composeRule.onNodeWithText("Configurações").assertHasClickAction()
        composeRule.onNodeWithText("Reiniciar o caso").assertHasClickAction()
        composeRule.onNodeWithText("Voltar ao início").assertHasClickAction()
    }

    @Test
    fun reiniciarPedeConfirmacaoAntesDeDescartarProgresso() {
        var reiniciou = false
        composeRule.setContent {
            TemaIndicio {
                TelaPausa(
                    onContinuar = {},
                    onConfiguracoes = {},
                    onReiniciar = { reiniciou = true },
                    onVoltarAoInicio = {},
                )
            }
        }

        composeRule.onNodeWithText("Reiniciar o caso").performClick()
        composeRule.onNodeWithText("Reiniciar o caso?").assertIsDisplayed()
        assertTrue("Reiniciou sem confirmação", !reiniciou)

        composeRule.onNodeWithText("Cancelar").performClick()
        assertTrue("Cancelar não deveria reiniciar", !reiniciou)

        composeRule.onNodeWithText("Reiniciar o caso").performClick()
        composeRule.onNodeWithText("Reiniciar").performClick()
        assertTrue(reiniciou)
    }

    // ---------- Configurações ----------

    @Test
    fun configuracoesAcionamAsPreferencias() {
        var tamanho: TamanhoTexto? = null
        var reduzir: Boolean? = null
        composeRule.setContent {
            TemaIndicio {
                ConteudoConfiguracoes(
                    preferencias = Preferencias(),
                    onTamanhoTexto = { tamanho = it },
                    onReduzirMovimentos = { reduzir = it },
                )
            }
        }

        composeRule.onNodeWithText("Muito grande").performClick()
        assertEquals(TamanhoTexto.MUITO_GRANDE, tamanho)

        composeRule.onNodeWithText("Reduzir animações").performClick()
        assertEquals(true, reduzir)
    }

    // ---------- Sobre ----------

    @Test
    fun sobreExibeOAvisoMedico() {
        composeRule.setContent { TemaIndicio { TelaSobre(versao = "0.1.0") } }

        composeRule.onNodeWithText(AVISO_MEDICO).assertIsDisplayed()
    }

    @Test
    fun sobreExibeCreditosEAbreORepositorio() {
        var abriuRepositorio = false
        composeRule.setContent {
            TemaIndicio {
                TelaSobre(
                    versao = "0.1.0",
                    onAbrirGithub = { abriuRepositorio = true },
                )
            }
        }

        composeRule.onNodeWithText("Criado por Rodrigo Brito")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithText("GitHub: britors/Indicio")
            .performScrollTo()
            .performClick()

        assertTrue(abriuRepositorio)
    }

    @Test
    fun nenhumaOutraTelaExibeOAvisoMedico() {
        composeRule.setContent {
            TemaIndicio {
                ConteudoInicio(
                    estado = EstadoInicio(nome = "Indício", slogan = "s", versao = "0.1.0"),
                    onContinuar = {},
                    onEscolherCaso = {},
                    onConfiguracoes = {},
                    onSobre = {},
                )
            }
        }

        composeRule.onNodeWithText(AVISO_MEDICO).assertDoesNotExist()
    }

    private fun cenaComDuasEscolhas() = Cena(
        id = "cena",
        texto = "Texto da cena.",
        imagem = Imagem("r", "Descrição acessível da cena."),
        escolhas = listOf(
            br.com.avoren.indicio.domain.model.caso.Escolha("a", "Primeira opção", "outra"),
            br.com.avoren.indicio.domain.model.caso.Escolha("b", "Segunda opção", "outra"),
        ),
    )

    private companion object {
        const val AVISO_MEDICO =
            "Indício é uma experiência de entretenimento e estímulo cognitivo. " +
                "Não substitui avaliação, tratamento ou acompanhamento médico."
    }
}

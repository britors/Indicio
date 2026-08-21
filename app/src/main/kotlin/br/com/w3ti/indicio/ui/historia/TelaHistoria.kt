package br.com.w3ti.indicio.ui.historia

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.w3ti.indicio.R
import br.com.w3ti.indicio.domain.model.caso.Cena
import br.com.w3ti.indicio.domain.model.caso.Escolha
import br.com.w3ti.indicio.domain.model.caso.Imagem
import br.com.w3ti.indicio.domain.model.caso.Pista
import br.com.w3ti.indicio.domain.narracao.EstadoNarracao
import br.com.w3ti.indicio.ui.carta.Carta
import br.com.w3ti.indicio.ui.carta.CartaDeEscolha
import br.com.w3ti.indicio.ui.carta.CartaDistribuida
import br.com.w3ti.indicio.ui.comum.BarraDoTopo
import br.com.w3ti.indicio.ui.comum.BotaoIcone
import br.com.w3ti.indicio.ui.comum.BotaoSecundario
import br.com.w3ti.indicio.ui.comum.ControleDeNarracao
import br.com.w3ti.indicio.ui.comum.IconesIndicio
import br.com.w3ti.indicio.ui.comum.PainelDeTextoRecolhivel
import br.com.w3ti.indicio.ui.comum.RotuloEditorial
import br.com.w3ti.indicio.ui.dica.EstadoDica
import br.com.w3ti.indicio.ui.tema.ElevacaoIndicio
import br.com.w3ti.indicio.ui.tema.AlturaMinimaBotao
import br.com.w3ti.indicio.ui.tema.EspacamentoIndicio
import br.com.w3ti.indicio.ui.tema.FormasIndicio
import br.com.w3ti.indicio.ui.tema.TemaIndicio

/** Tela narrativa em que a cena e as escolhas formam um jogo de cartas. */
@Composable
internal fun ConteudoHistoria(
    estado: EstadoHistoria.EmCurso,
    estadoNarracao: EstadoNarracao,
    onEscolher: (String) -> Unit,
    onAlternarNarracao: () -> Unit,
    onConfiguracoes: () -> Unit,
    modifier: Modifier = Modifier,
    onAbrirEtapas: () -> Unit = {},
    onAbrirCaderno: () -> Unit = {},
    pistasNaoLidas: Int = 0,
    bloquearMenu: Boolean = false,
    estadoDica: EstadoDica = EstadoDica.Oculta,
    onRevelarDica: () -> Unit = {},
    onRecarregarDica: () -> Unit = {},
) {
    val rolagem = rememberScrollState()

    LaunchedEffect(estado.cena.id) { rolagem.scrollTo(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraDoTopo(
                titulo = estado.tituloCaso,
                acao = {
                    MenuDaInvestigacao(
                        cenaId = estado.cena.id,
                        temEtapas = estado.temInvestigacaoLonga,
                        onAbrirEtapas = onAbrirEtapas,
                        onAbrirCaderno = onAbrirCaderno,
                        pistasNaoLidas = pistasNaoLidas,
                        onConfiguracoes = onConfiguracoes,
                        estadoDica = estadoDica,
                        onRevelarDica = onRevelarDica,
                        onRecarregarDica = onRecarregarDica,
                        bloqueado = bloquearMenu,
                        modifier = Modifier.padding(end = EspacamentoIndicio.minimo),
                    )
                },
            )
        },
    ) { espacamento ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
                .verticalScroll(rolagem)
                .padding(
                    horizontal = EspacamentoIndicio.margemDaTela,
                    vertical = EspacamentoIndicio.padrao,
                ),
        ) {
            CartaDistribuida(chave = estado.cena.id, modifier = Modifier.fillMaxWidth()) {
                CartaDaCena(
                    cena = estado.cena,
                    estadoNarracao = estadoNarracao,
                    onAlternarNarracao = onAlternarNarracao,
                )
            }

            Spacer(modifier = Modifier.height(EspacamentoIndicio.grande))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = stringResource(R.string.historia_escolhas),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() },
                )
                Text(
                    text = stringResource(R.string.historia_escolhas_apoio),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = EspacamentoIndicio.medio),
                )
            }

            Spacer(modifier = Modifier.height(EspacamentoIndicio.medio))

            estado.cena.escolhas.forEachIndexed { indice, escolha ->
                CartaDeEscolha(
                    numero = indice + 1,
                    texto = escolha.texto,
                    onClick = { onEscolher(escolha.id) },
                    habilitado = estado.escolhasHabilitadas,
                )
                Spacer(modifier = Modifier.height(EspacamentoIndicio.pequeno))
            }

            Spacer(modifier = Modifier.height(EspacamentoIndicio.pequeno))
            if (estado.temInvestigacaoLonga) {
                BotaoSecundario(
                    texto = stringResource(R.string.historia_ver_etapas),
                    icone = IconesIndicio.lista,
                    onClick = onAbrirEtapas,
                )
                Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))
            }
            PainelDePistas(
                pistas = estado.pistas,
                onAbrirCaderno = onAbrirCaderno,
            )
            Spacer(modifier = Modifier.height(EspacamentoIndicio.extraGrande))
        }
    }
}

@Composable
private fun MenuDaInvestigacao(
    cenaId: String,
    temEtapas: Boolean,
    onAbrirEtapas: () -> Unit,
    onAbrirCaderno: () -> Unit,
    pistasNaoLidas: Int,
    onConfiguracoes: () -> Unit,
    estadoDica: EstadoDica,
    onRevelarDica: () -> Unit,
    onRecarregarDica: () -> Unit,
    bloqueado: Boolean,
    modifier: Modifier = Modifier,
) {
    var aberto by rememberSaveable { mutableStateOf(false) }
    var bilheteAberto by rememberSaveable(cenaId) { mutableStateOf(false) }

    LaunchedEffect(bloqueado, cenaId) {
        if (bloqueado) {
            aberto = false
            bilheteAberto = false
        }
    }

    Box(modifier = modifier) {
        BotaoIcone(
            textoAcessivel = stringResource(R.string.historia_abrir_menu),
            onClick = { aberto = true },
        ) {
            Icon(
                imageVector = IconesIndicio.menu,
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = aberto,
            onDismissRequest = { aberto = false },
            modifier = Modifier.widthIn(min = LARGURA_MINIMA_MENU),
        ) {
            if (temEtapas) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.historia_ver_etapas)) },
                    onClick = {
                        aberto = false
                        onAbrirEtapas()
                    },
                    leadingIcon = {
                        Icon(imageVector = IconesIndicio.lista, contentDescription = null)
                    },
                    modifier = Modifier.heightIn(min = AlturaMinimaBotao),
                )
            }
            DropdownMenuItem(
                text = {
                    Column {
                        Text(stringResource(R.string.historia_caderno))
                        if (pistasNaoLidas > 0) {
                            Text(
                                text = pluralStringResource(
                                    R.plurals.historia_pistas_novas,
                                    pistasNaoLidas,
                                    pistasNaoLidas,
                                ),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                },
                onClick = {
                    aberto = false
                    onAbrirCaderno()
                },
                leadingIcon = {
                    BadgedBox(
                        badge = {
                            if (pistasNaoLidas > 0) {
                                Badge { Text(pistasNaoLidas.toString()) }
                            }
                        },
                    ) {
                        Icon(imageVector = IconesIndicio.pesquisar, contentDescription = null)
                    }
                },
                modifier = Modifier.heightIn(min = AlturaMinimaBotao),
            )
            ItemDicaNoMenu(
                estado = estadoDica,
                onAbrirBilhete = {
                    aberto = false
                    bilheteAberto = true
                },
                onTentarNovamente = {
                    aberto = false
                    onRecarregarDica()
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.configuracoes_titulo)) },
                onClick = {
                    aberto = false
                    onConfiguracoes()
                },
                leadingIcon = {
                    Icon(imageVector = IconesIndicio.configuracoes, contentDescription = null)
                },
                modifier = Modifier.heightIn(min = AlturaMinimaBotao),
            )
        }
    }

    if (bilheteAberto) {
        BilheteDoAnonimo(
            estado = estadoDica,
            onUsarDica = onRevelarDica,
            onTentarNovamente = onRecarregarDica,
            onFechar = { bilheteAberto = false },
        )
    }
}

/** Celebra uma descoberta sem interromper a interação com a cena. */
@Composable
internal fun MomentoDeDescoberta(
    pista: Pista,
    onDispensar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotulo = stringResource(R.string.historia_descoberta_rotulo)

    Surface(
        modifier = modifier
            .navigationBarsPadding()
            .padding(EspacamentoIndicio.padrao)
            .widthIn(max = LARGURA_MAXIMA_DESCOBERTA)
            .semantics {
                paneTitle = rotulo
                liveRegion = LiveRegionMode.Assertive
            },
        shape = FormasIndicio.cartao,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
        shadowElevation = ElevacaoIndicio.cartao,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(EspacamentoIndicio.grande),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
        ) {
            RotuloEditorial(texto = rotulo)
            Text(text = pista.titulo, style = MaterialTheme.typography.titleLarge)
            Column(verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.minimo)) {
                Text(
                    text = stringResource(R.string.historia_descoberta_relevancia),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = pista.relevancia
                        ?: stringResource(R.string.historia_descoberta_relevancia_generica),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            TextButton(
                onClick = onDispensar,
                modifier = Modifier
                    .align(Alignment.End)
                    .heightIn(min = AlturaMinimaBotao),
            ) {
                Text(stringResource(R.string.historia_descoberta_continuar))
            }
        }
    }
}

@Composable
private fun ItemDicaNoMenu(
    estado: EstadoDica,
    onAbrirBilhete: () -> Unit,
    onTentarNovamente: () -> Unit,
) {
    when (estado) {
        EstadoDica.Oculta -> Unit

        is EstadoDica.Carregando,
        is EstadoDica.Revelando,
        -> ItemDicaNoMenu(
            apoio = stringResource(R.string.historia_menu_dica_consultando),
            habilitado = false,
            onClick = {},
        )

        is EstadoDica.Disponivel -> ItemDicaNoMenu(
            apoio = pluralStringResource(
                R.plurals.historia_menu_dica_saldo,
                estado.restantes,
                estado.restantes,
            ),
            onClick = onAbrirBilhete,
        )

        is EstadoDica.Revelada -> ItemDicaNoMenu(
            apoio = stringResource(R.string.historia_menu_dica_reabrir),
            onClick = onAbrirBilhete,
        )

        is EstadoDica.Esgotada -> ItemDicaNoMenu(
            apoio = stringResource(R.string.historia_menu_dicas_esgotadas),
            habilitado = false,
            onClick = {},
        )

        is EstadoDica.Falha -> ItemDicaNoMenu(
            apoio = stringResource(R.string.historia_menu_dica_tentar_novamente),
            onClick = onTentarNovamente,
        )
    }
}

@Composable
private fun ItemDicaNoMenu(
    apoio: String,
    habilitado: Boolean = true,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Column {
                Text(stringResource(R.string.historia_menu_dica_titulo))
                Text(text = apoio, style = MaterialTheme.typography.bodySmall)
            }
        },
        onClick = onClick,
        enabled = habilitado,
        leadingIcon = {
            Icon(imageVector = IconesIndicio.conversa, contentDescription = null)
        },
        modifier = Modifier.heightIn(min = AlturaMinimaBotao),
    )
}

@Composable
private fun BilheteDoAnonimo(
    estado: EstadoDica,
    onUsarDica: () -> Unit,
    onTentarNovamente: () -> Unit,
    onFechar: () -> Unit,
) {
    when (estado) {
        EstadoDica.Oculta,
        is EstadoDica.Esgotada,
        -> Unit

        is EstadoDica.Disponivel -> AlertDialog(
            onDismissRequest = onFechar,
            icon = { Icon(IconesIndicio.conversa, contentDescription = null) },
            title = { Text(stringResource(R.string.historia_dialogo_dica_titulo)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.pequeno)) {
                    Text(
                        pluralStringResource(
                            R.plurals.historia_dialogo_dica_saldo,
                            estado.restantes,
                            estado.restantes,
                        ),
                    )
                    Text(stringResource(R.string.historia_dialogo_dica_pergunta))
                }
            },
            confirmButton = {
                AcaoDoBilhete(
                    texto = stringResource(R.string.historia_dialogo_dica_usar),
                    onClick = onUsarDica,
                )
            },
            dismissButton = {
                AcaoDoBilhete(
                    texto = stringResource(R.string.historia_dialogo_dica_agora_nao),
                    onClick = onFechar,
                )
            },
        )

        is EstadoDica.Carregando,
        is EstadoDica.Revelando,
        -> AlertDialog(
            onDismissRequest = onFechar,
            icon = { Icon(IconesIndicio.conversa, contentDescription = null) },
            title = { Text(stringResource(R.string.historia_dica_consultando)) },
            text = { Text(stringResource(R.string.historia_dialogo_dica_aguarde)) },
            confirmButton = {
                AcaoDoBilhete(
                    texto = stringResource(R.string.historia_dialogo_dica_fechar),
                    onClick = onFechar,
                )
            },
        )

        is EstadoDica.Revelada -> AlertDialog(
            onDismissRequest = onFechar,
            icon = { Icon(IconesIndicio.conversa, contentDescription = null) },
            title = { Text(stringResource(R.string.historia_dica_remetente)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.padrao)) {
                    Text(
                        text = estado.mensagem
                            ?: stringResource(R.string.historia_dica_mensagem_generica),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = pluralStringResource(
                            R.plurals.historia_dicas_restantes,
                            estado.restantes,
                            estado.restantes,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            },
            confirmButton = {
                AcaoDoBilhete(
                    texto = stringResource(R.string.historia_dialogo_dica_entendi),
                    onClick = onFechar,
                )
            },
        )

        is EstadoDica.Falha -> AlertDialog(
            onDismissRequest = onFechar,
            icon = { Icon(IconesIndicio.conversa, contentDescription = null) },
            title = { Text(stringResource(R.string.historia_dialogo_dica_falha_titulo)) },
            text = { Text(stringResource(R.string.historia_dialogo_dica_falha_mensagem)) },
            confirmButton = {
                AcaoDoBilhete(
                    texto = stringResource(R.string.historia_dialogo_dica_tentar_novamente),
                    onClick = onTentarNovamente,
                )
            },
            dismissButton = {
                AcaoDoBilhete(
                    texto = stringResource(R.string.historia_dialogo_dica_fechar),
                    onClick = onFechar,
                )
            },
        )
    }
}

@Composable
private fun AcaoDoBilhete(
    texto: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.heightIn(min = AlturaMinimaBotao),
    ) {
        Text(texto)
    }
}

@Composable
private fun CartaDaCena(
    cena: Cena,
    estadoNarracao: EstadoNarracao,
    onAlternarNarracao: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Carta(modifier = modifier) {
        Box {
            val alturaDaJanela = with(LocalDensity.current) {
                LocalWindowInfo.current.containerSize.height.toDp()
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                val larguraPelaAltura = alturaDaJanela * FRACAO_DA_ALTURA_DA_ARTE * PROPORCAO_ARTE_CENA
                IlustracaoDaCena(
                    imagem = cena.imagem,
                    proporcao = PROPORCAO_ARTE_CENA,
                    modifier = Modifier.width(minOf(maxWidth, larguraPelaAltura)),
                )
            }

            if (cena.pista != null) {
                Surface(
                    modifier = Modifier.padding(EspacamentoIndicio.medio),
                    shape = FormasIndicio.pequena,
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    RotuloEditorial(
                        texto = stringResource(R.string.historia_nova_pista),
                        modifier = Modifier.padding(
                            horizontal = EspacamentoIndicio.medio,
                            vertical = EspacamentoIndicio.pequeno,
                        ),
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(EspacamentoIndicio.grande),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
        ) {
            ControleDeNarracao(
                estado = estadoNarracao,
                onAlternar = onAlternarNarracao,
            )

            PainelDeTextoRecolhivel(
                chave = cena.id,
                texto = cena.texto,
            )
        }
    }
}

/** Caderno resumido, seguido do conteúdo textual que já foi descoberto. */
@Composable
internal fun PainelDePistas(
    pistas: List<Pista>,
    modifier: Modifier = Modifier,
    onAbrirCaderno: () -> Unit = {},
) {
    Surface(
        onClick = onAbrirCaderno,
        modifier = modifier.fillMaxWidth(),
        shape = FormasIndicio.controle,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)),
        shadowElevation = ElevacaoIndicio.controle,
    ) {
        Column(
            modifier = Modifier.padding(EspacamentoIndicio.padrao),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.historia_caderno),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() },
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.pequeno),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = IconesIndicio.pesquisar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(R.string.historia_abrir_caderno),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            if (pistas.isEmpty()) {
                Text(
                    text = stringResource(R.string.historia_pistas_nenhuma),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                pistas.forEach { pista ->
                    Column(verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.minimo)) {
                        Text(text = pista.titulo, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = pista.descricao,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

private const val PROPORCAO_ARTE_CENA = 16f / 9f
private const val FRACAO_DA_ALTURA_DA_ARTE = 0.46f
private val LARGURA_MINIMA_MENU = 280.dp
private val LARGURA_MAXIMA_DESCOBERTA = 560.dp

@Preview(showBackground = true)
@Composable
private fun PreviaHistoria() {
    TemaIndicio {
        ConteudoHistoria(
            estado = EstadoHistoria.EmCurso(
                tituloCaso = "O Mistério da Taça Desaparecida",
                cena = Cena(
                    id = "vitrine",
                    texto = "A vitrine está intacta. O pedestal, porém, não está centralizado.",
                    imagem = Imagem("cena_vitrine", "Vitrine de vidro sobre um pedestal deslocado."),
                    pista = Pista("pedestal", "O pedestal fora do lugar", "Está à esquerda da marca."),
                    escolhas = listOf(
                        Escolha("a", "Examinar o chão em volta do pedestal", "po"),
                        Escolha("b", "Olhar para o forro, acima da vitrine", "forro"),
                        Escolha("c", "Registrar a disposição completa da sala", "inventario"),
                    ),
                ),
                pistas = listOf(
                    Pista("pedestal", "O pedestal fora do lugar", "Está à esquerda da marca no piso."),
                ),
            ),
            estadoNarracao = EstadoNarracao.PRONTO,
            onEscolher = {},
            onAlternarNarracao = {},
            onConfiguracoes = {},
            onAbrirEtapas = {},
            onAbrirCaderno = {},
        )
    }
}

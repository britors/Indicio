package br.com.w3ti.indicio.ui.investigacao

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.w3ti.indicio.R
import br.com.w3ti.indicio.ui.comum.AbaDeInvestigacao
import br.com.w3ti.indicio.ui.comum.AbasDeInvestigacao
import br.com.w3ti.indicio.ui.comum.BarraDoTopo
import br.com.w3ti.indicio.ui.comum.BotaoSecundario
import br.com.w3ti.indicio.ui.comum.CartaoDeRegistro
import br.com.w3ti.indicio.ui.comum.EstadoDoRegistro
import br.com.w3ti.indicio.ui.comum.IconesIndicio
import br.com.w3ti.indicio.ui.comum.PainelDeObjetivo
import br.com.w3ti.indicio.ui.comum.RotuloEditorial
import br.com.w3ti.indicio.ui.tema.ElevacaoIndicio
import br.com.w3ti.indicio.ui.tema.EspacamentoIndicio
import br.com.w3ti.indicio.ui.tema.FormasIndicio

private enum class AbaCaderno(val id: String) {
    PISTAS("pistas"),
    PESSOAS("pessoas"),
    LOCAIS("locais"),
    CONVERSAS("conversas"),
}

@Composable
internal fun ConteudoCaderno(
    estado: EstadoInvestigacao.Conteudo,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var abaId by rememberSaveable { mutableStateOf(AbaCaderno.PISTAS.id) }
    var conversaAbertaId by rememberSaveable { mutableStateOf<String?>(null) }
    val conversaAberta = estado.caderno.conversas.firstOrNull { it.id == conversaAbertaId }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BarraDoTopo(
                titulo = stringResource(R.string.caderno_titulo),
                onVoltar = onVoltar,
            )
        },
    ) { espacamento ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
                .padding(top = EspacamentoIndicio.padrao),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
        ) {
            AbasDeInvestigacao(
                abas = listOf(
                    AbaDeInvestigacao(
                        AbaCaderno.PISTAS.id,
                        stringResource(R.string.caderno_aba_pistas),
                        IconesIndicio.pesquisar,
                    ),
                    AbaDeInvestigacao(
                        AbaCaderno.PESSOAS.id,
                        stringResource(R.string.caderno_aba_pessoas),
                        IconesIndicio.pessoa,
                    ),
                    AbaDeInvestigacao(
                        AbaCaderno.LOCAIS.id,
                        stringResource(R.string.caderno_aba_locais),
                        IconesIndicio.local,
                    ),
                    AbaDeInvestigacao(
                        AbaCaderno.CONVERSAS.id,
                        stringResource(R.string.caderno_aba_conversas),
                        IconesIndicio.conversa,
                    ),
                ),
                selecionadaId = abaId,
                onSelecionar = { abaId = it },
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize().testTag(TAG_LISTA_CADERNO),
                contentPadding = PaddingValues(
                    start = EspacamentoIndicio.margemDaTela,
                    end = EspacamentoIndicio.margemDaTela,
                    bottom = EspacamentoIndicio.extraGrande,
                ),
                verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.padrao),
            ) {
                estado.objetivoAtual?.let { objetivo ->
                    item(key = "objetivo") {
                        PainelDeObjetivo(
                            rotulo = stringResource(R.string.caderno_pergunta_aberta),
                            texto = objetivo.perguntaEmAberto,
                        )
                    }
                }

                when (abaId) {
                    AbaCaderno.PISTAS.id -> {
                        if (estado.caderno.pistas.isEmpty()) {
                            item { EstadoVazio(stringResource(R.string.caderno_vazio_pistas)) }
                        } else {
                            itemsIndexed(estado.caderno.pistas, key = { _, pista -> pista.id }) { indice, pista ->
                                CartaoDeRegistro(
                                    marcador = (indice + 1).toString().padStart(2, '0'),
                                    titulo = pista.titulo,
                                    descricao = pista.descricao,
                                    estado = EstadoDoRegistro.CONCLUIDO,
                                )
                            }
                        }
                    }

                    AbaCaderno.PESSOAS.id -> {
                        if (estado.caderno.pessoas.isEmpty()) {
                            item { EstadoVazio(stringResource(R.string.caderno_vazio_pessoas)) }
                        } else {
                            itemsIndexed(estado.caderno.pessoas, key = { _, pessoa -> pessoa.id }) { indice, pessoa ->
                                CartaoPessoa(
                                    numero = indice + 1,
                                    pessoa = pessoa,
                                    onAbrirConversa = { conversaAbertaId = it },
                                )
                            }
                        }
                    }

                    AbaCaderno.LOCAIS.id -> {
                        if (estado.caderno.locais.isEmpty()) {
                            item { EstadoVazio(stringResource(R.string.caderno_vazio_locais)) }
                        } else {
                            itemsIndexed(estado.caderno.locais, key = { _, local -> local.id }) { indice, local ->
                                CartaoAnotado(
                                    marcador = stringResource(R.string.caderno_local_numero, indice + 1),
                                    titulo = local.nome,
                                    anotacoes = local.anotacoes,
                                )
                            }
                        }
                    }

                    else -> {
                        if (estado.caderno.conversas.isEmpty()) {
                            item { EstadoVazio(stringResource(R.string.caderno_vazio_conversas)) }
                        } else {
                            itemsIndexed(
                                estado.caderno.conversas,
                                key = { _, conversa -> conversa.id },
                            ) { indice, conversa ->
                                CartaoConversa(
                                    numero = indice + 1,
                                    conversa = conversa,
                                    onAbrir = { conversaAbertaId = conversa.id },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    conversaAberta?.let { conversa ->
        AlertDialog(
            onDismissRequest = { conversaAbertaId = null },
            title = { Text(conversa.titulo) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio)) {
                    if (conversa.pessoa.isNotBlank()) {
                        RotuloEditorial(texto = conversa.pessoa)
                    }
                    Text(text = conversa.texto, style = MaterialTheme.typography.bodyLarge)
                }
            },
            confirmButton = {
                TextButton(onClick = { conversaAbertaId = null }) {
                    Icon(imageVector = IconesIndicio.fechar, contentDescription = null)
                    Text(
                        text = stringResource(R.string.caderno_fechar_conversa),
                        modifier = Modifier.padding(start = EspacamentoIndicio.pequeno),
                    )
                }
            },
        )
    }
}

internal const val TAG_LISTA_CADERNO = "lista_caderno"

@Composable
private fun CartaoPessoa(
    numero: Int,
    pessoa: PessoaUi,
    onAbrirConversa: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = FormasIndicio.cartao,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
        shadowElevation = ElevacaoIndicio.controle,
    ) {
        Column(
            modifier = Modifier.padding(EspacamentoIndicio.padrao),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
        ) {
            RotuloEditorial(texto = stringResource(R.string.caderno_pessoa_numero, numero))
            Text(
                text = pessoa.nome,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = pessoa.papel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Anotacoes(pessoa.anotacoes)
            pessoa.conversas.forEach { conversa ->
                BotaoSecundario(
                    texto = stringResource(R.string.caderno_rever_conversa),
                    icone = IconesIndicio.conversa,
                    onClick = { onAbrirConversa(conversa.id) },
                )
            }
        }
    }
}

@Composable
private fun CartaoAnotado(marcador: String, titulo: String, anotacoes: List<String>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = FormasIndicio.cartao,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
        shadowElevation = ElevacaoIndicio.controle,
    ) {
        Column(
            modifier = Modifier.padding(EspacamentoIndicio.padrao),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
        ) {
            RotuloEditorial(texto = marcador)
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() },
            )
            Anotacoes(anotacoes)
        }
    }
}

@Composable
private fun Anotacoes(anotacoes: List<String>) {
    if (anotacoes.isEmpty()) return
    Text(
        text = stringResource(R.string.caderno_anotacoes),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
    )
    anotacoes.forEach { Text(text = "• $it", style = MaterialTheme.typography.bodyMedium) }
}

@Composable
private fun CartaoConversa(numero: Int, conversa: ConversaUi, onAbrir: () -> Unit) {
    Surface(
        onClick = onAbrir,
        modifier = Modifier.fillMaxWidth(),
        shape = FormasIndicio.cartao,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
        shadowElevation = ElevacaoIndicio.controle,
    ) {
        Column(
            modifier = Modifier.padding(EspacamentoIndicio.padrao),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.pequeno),
        ) {
            RotuloEditorial(texto = stringResource(R.string.caderno_conversa_numero, numero))
            Text(
                text = conversa.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            if (conversa.pessoa.isNotBlank()) {
                Text(
                    text = conversa.pessoa,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.pequeno),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = IconesIndicio.conversa,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.caderno_rever_conversa),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun EstadoVazio(texto: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = FormasIndicio.controle,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(EspacamentoIndicio.grande),
        )
    }
}

package br.com.avoren.indicio.ui.comum

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.ui.tema.ElevacaoIndicio
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio
import br.com.avoren.indicio.ui.tema.FormasIndicio

/** Item de navegação do caderno; texto e identidade vêm da tela consumidora. */
data class AbaDeInvestigacao(
    val id: String,
    val rotulo: String,
)

/** Navegação horizontal comum a pistas, pessoas, locais e conversas. */
@Composable
fun AbasDeInvestigacao(
    abas: List<AbaDeInvestigacao>,
    selecionadaId: String,
    onSelecionar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = EspacamentoIndicio.margemDaTela),
        horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.pequeno),
    ) {
        items(abas, key = AbaDeInvestigacao::id) { aba ->
            val selecionada = aba.id == selecionadaId
            Surface(
                onClick = { onSelecionar(aba.id) },
                shape = FormasIndicio.controle,
                color = if (selecionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                contentColor = if (selecionada) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                border = if (selecionada) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.semantics {
                    role = Role.Tab
                    selected = selecionada
                },
            ) {
                Text(
                    text = aba.rotulo,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(
                        horizontal = EspacamentoIndicio.padrao,
                        vertical = EspacamentoIndicio.medio,
                    ),
                )
            }
        }
    }
}

/** Painel curto para o objetivo atual ou para uma pergunta ainda em aberto. */
@Composable
fun PainelDeObjetivo(
    rotulo: String,
    texto: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = FormasIndicio.cartao,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(EspacamentoIndicio.padrao),
            verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.pequeno),
        ) {
            RotuloEditorial(texto = rotulo)
            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/** Estado visual de um registro do caderno ou de uma etapa narrativa. */
enum class EstadoDoRegistro {
    CONCLUIDO,
    ATUAL,
    FUTURO,
}

/** Cartão textual reutilizado nas listas de pistas e etapas. */
@Composable
fun CartaoDeRegistro(
    marcador: String,
    titulo: String,
    descricao: String,
    estado: EstadoDoRegistro,
    modifier: Modifier = Modifier,
    selo: String? = null,
) {
    val borda = if (estado == EstadoDoRegistro.ATUAL) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = FormasIndicio.controle,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(if (estado == EstadoDoRegistro.ATUAL) 2.dp else 1.dp, borda),
        shadowElevation = ElevacaoIndicio.controle,
    ) {
        Row(
            modifier = Modifier.padding(EspacamentoIndicio.medio),
            horizontalArrangement = Arrangement.spacedBy(EspacamentoIndicio.medio),
            verticalAlignment = Alignment.Top,
        ) {
            Surface(
                shape = FormasIndicio.pequena,
                color = if (estado == EstadoDoRegistro.CONCLUIDO) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
                contentColor = if (estado == EstadoDoRegistro.CONCLUIDO) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                },
                modifier = Modifier.widthIn(min = 46.dp),
            ) {
                Text(
                    text = marcador,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(
                        horizontal = EspacamentoIndicio.medio,
                        vertical = EspacamentoIndicio.padrao,
                    ),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(EspacamentoIndicio.minimo),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                    )
                    selo?.let {
                        Surface(
                            shape = FormasIndicio.pequena,
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(start = EspacamentoIndicio.pequeno),
                        ) {
                            Text(
                                text = it.uppercase(),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(
                                    horizontal = EspacamentoIndicio.pequeno,
                                    vertical = EspacamentoIndicio.minimo,
                                ),
                            )
                        }
                    }
                }
                Text(
                    text = descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

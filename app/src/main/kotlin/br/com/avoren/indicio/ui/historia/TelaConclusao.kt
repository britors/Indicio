package br.com.avoren.indicio.ui.historia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.R
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Desfecho
import br.com.avoren.indicio.domain.model.caso.Imagem
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.model.caso.TipoCena
import br.com.avoren.indicio.ui.comum.BotaoPrincipal
import br.com.avoren.indicio.ui.comum.BotaoSecundario
import br.com.avoren.indicio.ui.comum.IconesIndicio
import br.com.avoren.indicio.ui.comum.TituloDeTela
import br.com.avoren.indicio.ui.tema.EspacamentoIndicio
import br.com.avoren.indicio.ui.tema.TemaIndicio

/**
 * Conclusão do caso.
 *
 * Fecha a história explicando o raciocínio, sem cobrar do jogador o que ele
 * deixou de notar, e oferece dois caminhos adiante.
 */
@Composable
internal fun ConteudoConclusao(
    estado: EstadoHistoria.Concluida,
    onJogarNovamente: () -> Unit,
    onVoltarAoCatalogo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { espacamento ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacamento)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = EspacamentoIndicio.margemDaTela,
                    vertical = EspacamentoIndicio.grande,
                ),
        ) {
            TituloDeTela(texto = stringResource(R.string.conclusao_titulo))

            Spacer(modifier = Modifier.height(EspacamentoIndicio.pequeno))

            Text(
                text = estado.desfecho.titulo,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() },
            )

            Spacer(modifier = Modifier.height(EspacamentoIndicio.grande))

            Text(text = estado.cena.texto, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(EspacamentoIndicio.grande))

            Text(
                text = estado.desfecho.mensagem,
                style = MaterialTheme.typography.bodyLarge,
            )

            androidx.compose.material3.HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.14f),
            )

            Spacer(modifier = Modifier.height(EspacamentoIndicio.grande))

            Text(
                text = stringResource(R.string.conclusao_pistas),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() },
            )

            Spacer(modifier = Modifier.height(EspacamentoIndicio.medio))

            Text(
                text = estado.desfecho.explicacaoPistas,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(EspacamentoIndicio.extraGrande))

            PainelDePistas(pistas = estado.pistas)

            Spacer(modifier = Modifier.height(EspacamentoIndicio.destaque))

            BotaoPrincipal(
                texto = stringResource(R.string.conclusao_jogar_novamente),
                icone = IconesIndicio.reiniciar,
                onClick = onJogarNovamente,
            )

            Spacer(modifier = Modifier.height(EspacamentoIndicio.padrao))

            BotaoSecundario(
                texto = stringResource(R.string.conclusao_voltar_catalogo),
                icone = IconesIndicio.lista,
                onClick = onVoltarAoCatalogo,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviaConclusao() {
    TemaIndicio {
        ConteudoConclusao(
            estado = EstadoHistoria.Concluida(
                tituloCaso = "O Mistério da Taça Desaparecida",
                cena = Cena(
                    id = "final",
                    tipo = TipoCena.FINAL,
                    texto = "A taça volta para a vitrine limpa e seca.",
                    imagem = Imagem("cena_final", "Taça de prata de volta à vitrine."),
                ),
                desfecho = Desfecho(
                    titulo = "A taça em segurança",
                    mensagem = "A taça nunca saiu do museu.",
                    explicacaoPistas = "O pedestal deslocado mostrava que ninguém forçou nada.",
                ),
                pistas = listOf(
                    Pista("pedestal", "O pedestal fora do lugar", "Estava à esquerda da marca."),
                ),
            ),
            onJogarNovamente = {},
            onVoltarAoCatalogo = {},
        )
    }
}

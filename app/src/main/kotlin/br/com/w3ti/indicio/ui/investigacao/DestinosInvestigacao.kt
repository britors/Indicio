package br.com.w3ti.indicio.ui.investigacao

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.w3ti.indicio.domain.armazenamento.RepositorioProgresso
import br.com.w3ti.indicio.domain.caso.RepositorioCasos
import br.com.w3ti.indicio.ui.comum.ConteudoCarregando
import br.com.w3ti.indicio.ui.comum.ConteudoDeFalha

@Composable
fun DestinoRetomada(
    casoId: String,
    repositorioCasos: RepositorioCasos,
    repositorioProgresso: RepositorioProgresso,
    onContinuar: () -> Unit,
    onAbrirCaderno: () -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvestigacaoViewModel = viewModel(
        factory = InvestigacaoViewModel.fabrica(repositorioCasos, repositorioProgresso),
    ),
) {
    LaunchedEffect(casoId) { viewModel.abrir(casoId) }
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    when (val atual = estado) {
        EstadoInvestigacao.Carregando -> ConteudoCarregando(modifier)
        is EstadoInvestigacao.Falha -> ConteudoDeFalha(
            onTentarNovamente = { viewModel.abrir(casoId) },
            modifier = modifier,
        )
        EstadoInvestigacao.ProgressoIncompativel -> {
            LaunchedEffect(Unit) { onContinuar() }
            ConteudoCarregando(modifier)
        }
        is EstadoInvestigacao.Conteudo -> {
            if (atual.exibirRetomada && atual.retomada != null) {
                ConteudoRetomada(
                    estado = atual,
                    onContinuar = onContinuar,
                    onAbrirCaderno = onAbrirCaderno,
                    onVoltar = onVoltar,
                    modifier = modifier,
                )
            } else {
                LaunchedEffect(atual.casoId) { onContinuar() }
                ConteudoCarregando(modifier)
            }
        }
    }
}

@Composable
fun DestinoEtapas(
    casoId: String,
    repositorioCasos: RepositorioCasos,
    repositorioProgresso: RepositorioProgresso,
    onContinuar: () -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvestigacaoViewModel = viewModel(
        factory = InvestigacaoViewModel.fabrica(repositorioCasos, repositorioProgresso),
    ),
) {
    LaunchedEffect(casoId) { viewModel.abrir(casoId) }
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    when (val atual = estado) {
        EstadoInvestigacao.Carregando -> ConteudoCarregando(modifier)
        is EstadoInvestigacao.Falha -> ConteudoDeFalha(
            onTentarNovamente = { viewModel.abrir(casoId) },
            modifier = modifier,
        )
        EstadoInvestigacao.ProgressoIncompativel -> ConteudoDeFalha(
            onTentarNovamente = onVoltar,
            modifier = modifier,
        )
        is EstadoInvestigacao.Conteudo -> ConteudoEtapas(
            estado = atual,
            onContinuar = onContinuar,
            onVoltar = onVoltar,
            modifier = modifier,
        )
    }
}

@Composable
fun DestinoCaderno(
    casoId: String,
    repositorioCasos: RepositorioCasos,
    repositorioProgresso: RepositorioProgresso,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvestigacaoViewModel = viewModel(
        factory = InvestigacaoViewModel.fabrica(repositorioCasos, repositorioProgresso),
    ),
) {
    LaunchedEffect(casoId) { viewModel.abrir(casoId) }
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    when (val atual = estado) {
        EstadoInvestigacao.Carregando -> ConteudoCarregando(modifier)
        is EstadoInvestigacao.Falha -> ConteudoDeFalha(
            onTentarNovamente = { viewModel.abrir(casoId) },
            modifier = modifier,
        )
        EstadoInvestigacao.ProgressoIncompativel -> ConteudoDeFalha(
            onTentarNovamente = onVoltar,
            modifier = modifier,
        )
        is EstadoInvestigacao.Conteudo -> ConteudoCaderno(
            estado = atual,
            onVoltar = onVoltar,
            modifier = modifier,
        )
    }
}

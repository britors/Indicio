package br.com.avoren.indicio.ui.investigacao

import br.com.avoren.indicio.application.investigacao.DecidirExibicaoDaRetomada
import br.com.avoren.indicio.application.investigacao.InvestigacaoCarregada
import br.com.avoren.indicio.domain.caso.ErroCarga

sealed interface EstadoInvestigacao {
    data object Carregando : EstadoInvestigacao
    data class Falha(val erro: ErroCarga) : EstadoInvestigacao
    data object ProgressoIncompativel : EstadoInvestigacao
    data class Conteudo(
        val casoId: String,
        val tituloCaso: String,
        val retomada: RetomadaUi?,
        val etapas: List<EtapaUi>,
        val objetivoAtual: ObjetivoUi?,
        val caderno: CadernoUi,
        val concluida: Boolean,
        val exibirRetomada: Boolean,
    ) : EstadoInvestigacao {
        val temEtapas: Boolean get() = etapas.isNotEmpty()
    }
}

data class RetomadaUi(
    val etapa: String,
    val resumo: String,
    val lembrancas: List<String>,
)

data class ObjetivoUi(
    val texto: String,
    val perguntaEmAberto: String,
)

enum class SituacaoEtapa {
    CONCLUIDA,
    ATUAL,
    FUTURA,
}

/** Título e descrição são nulos para que etapas futuras nunca vazem conteúdo. */
data class EtapaUi(
    val id: String,
    val numero: Int,
    val situacao: SituacaoEtapa,
    val titulo: String?,
    val descricao: String?,
)

data class CadernoUi(
    val pistas: List<PistaUi>,
    val pessoas: List<PessoaUi>,
    val locais: List<LocalUi>,
    val conversas: List<ConversaUi>,
) {
    val totalRegistros: Int
        get() = pistas.size + pessoas.size + locais.size + conversas.size
}

data class PistaUi(val id: String, val titulo: String, val descricao: String)

data class PessoaUi(
    val id: String,
    val nome: String,
    val papel: String,
    val anotacoes: List<String>,
    val conversas: List<ConversaUi>,
)

data class LocalUi(val id: String, val nome: String, val anotacoes: List<String>)

data class ConversaUi(
    val id: String,
    val titulo: String,
    val texto: String,
    val pessoa: String,
)

/** Converte o domínio reconstruído num contrato pequeno e seguro para Compose. */
class ProjetorInvestigacao(
    private val decidirRetomada: DecidirExibicaoDaRetomada = DecidirExibicaoDaRetomada(),
) {
    operator fun invoke(
        carregada: InvestigacaoCarregada,
        agora: Long,
    ): EstadoInvestigacao.Conteudo {
        val caso = carregada.caso
        val sessao = carregada.sessao
        val etapaAtual = sessao.etapaAtual(caso)
        val indiceAtual = caso.etapas.indexOfFirst { it.id == etapaAtual?.id }
        val objetivo = sessao.objetivoAtual(caso)
        val caderno = sessao.cadernoRevelado(caso)
        val conversasPorId = caderno.conversas.associateBy { it.id }

        val etapas = caso.etapas.mapIndexed { indice, etapa ->
            val situacao = when {
                sessao.concluida -> SituacaoEtapa.CONCLUIDA
                indice < indiceAtual -> SituacaoEtapa.CONCLUIDA
                indice == indiceAtual -> SituacaoEtapa.ATUAL
                else -> SituacaoEtapa.FUTURA
            }
            EtapaUi(
                id = etapa.id,
                numero = indice + 1,
                situacao = situacao,
                titulo = etapa.titulo.takeUnless { situacao == SituacaoEtapa.FUTURA },
                descricao = when (situacao) {
                    SituacaoEtapa.CONCLUIDA -> etapa.resumoConclusao
                    SituacaoEtapa.ATUAL -> etapa.descricao
                    SituacaoEtapa.FUTURA -> null
                },
            )
        }

        val conversasUi = caderno.conversas.map { conversa ->
            ConversaUi(
                id = conversa.id,
                titulo = conversa.titulo,
                texto = conversa.texto,
                pessoa = caso.caderno.pessoas.firstOrNull { it.id == conversa.pessoaId }?.nome.orEmpty(),
            )
        }

        return EstadoInvestigacao.Conteudo(
            casoId = caso.id,
            tituloCaso = caso.titulo,
            retomada = etapaAtual?.let { etapa ->
                RetomadaUi(
                    etapa = etapa.titulo,
                    resumo = etapa.resumoRetomada,
                    lembrancas = sessao.lembrancasParaRetomada().map { it.texto },
                )
            },
            etapas = etapas,
            objetivoAtual = objetivo?.let { ObjetivoUi(it.texto, it.perguntaEmAberto) },
            caderno = CadernoUi(
                pistas = caderno.pistas.map { PistaUi(it.id, it.titulo, it.descricao) },
                pessoas = caderno.pessoas.map { revelada ->
                    PessoaUi(
                        id = revelada.pessoa.id,
                        nome = revelada.pessoa.nome,
                        papel = revelada.pessoa.papel,
                        anotacoes = revelada.anotacoes.map { it.texto },
                        conversas = revelada.conversas.mapNotNull { conversasPorId[it.id] }.map { conversa ->
                            ConversaUi(
                                id = conversa.id,
                                titulo = conversa.titulo,
                                texto = conversa.texto,
                                pessoa = revelada.pessoa.nome,
                            )
                        },
                    )
                },
                locais = caderno.locais.map { revelado ->
                    LocalUi(
                        id = revelado.local.id,
                        nome = revelado.local.nome,
                        anotacoes = revelado.anotacoes.map { it.texto },
                    )
                },
                conversas = conversasUi,
            ),
            concluida = sessao.concluida,
            exibirRetomada = etapaAtual != null && !sessao.concluida && decidirRetomada(
                atualizadoEm = carregada.progressoSalvo?.atualizadoEm,
                agora = agora,
            ),
        )
    }
}

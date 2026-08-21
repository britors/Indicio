package br.com.w3ti.indicio.domain.narrativa

import br.com.w3ti.indicio.domain.model.caso.Caso
import br.com.w3ti.indicio.domain.model.caso.Cena
import br.com.w3ti.indicio.domain.model.caso.Pista
import br.com.w3ti.indicio.domain.model.caso.TipoCena
import br.com.w3ti.indicio.domain.model.sessao.ProgressoCaso
import br.com.w3ti.indicio.domain.model.sessao.SessaoInvestigacao

/**
 * Conduz a história a partir dos dados do caso.
 *
 * O mecanismo não conhece nenhuma história em particular: ele apenas segue o
 * grafo declarado no JSON. Não existe derrota, vida, cronômetro nem pontuação —
 * toda escolha leva a uma cena válida ou a um final positivo, e escolhas menos
 * adequadas costumam revelar uma pista antes de reencontrar o caminho.
 *
 * É puro e independente de Android, Compose e persistência.
 */
class MecanismoNarrativo {

    /**
     * Abre um caso na cena inicial, já recolhendo a pista dessa cena.
     *
     * @return `null` se a cena inicial não existir. Casos entregues pelo
     * repositório são validados antes, portanto isso indica dados construídos
     * à mão em testes.
     */
    fun iniciar(caso: Caso): SessaoInvestigacao? {
        val inicial = caso.cena(caso.cenaInicial) ?: return null

        return SessaoInvestigacao(
            casoId = caso.id,
            cenaAtual = inicial.id,
            revisao = caso.revisao,
        )
            .comPista(inicial.pista)
            .comRevelacoes(caso, inicial.revelacoes)
            .comDesfechoDe(inicial)
    }

    /** Recomeça o caso do zero, descartando caminho e pistas da sessão atual. */
    fun reiniciar(caso: Caso): SessaoInvestigacao? = iniciar(caso)

    /**
     * Aplica uma das escolhas oferecidas pela cena atual.
     *
     * Escolhas que não pertencem à cena atual são recusadas. Isso cobre tanto
     * dados inconsistentes quanto o toque duplo: o segundo toque carrega o
     * identificador da cena anterior, que já não é válido.
     */
    fun escolher(
        caso: Caso,
        sessao: SessaoInvestigacao,
        escolhaId: String,
    ): ResultadoEscolha {
        if (sessao.concluida) {
            return ResultadoEscolha.Recusada(sessao, MotivoRecusa.SESSAO_CONCLUIDA)
        }

        val cena = caso.cena(sessao.cenaAtual)
            ?: return ResultadoEscolha.Recusada(sessao, MotivoRecusa.CENA_INEXISTENTE)

        val escolha = cena.escolhas.firstOrNull { it.id == escolhaId }
            ?: return ResultadoEscolha.Recusada(sessao, MotivoRecusa.ESCOLHA_INDISPONIVEL)

        val proxima = caso.cena(escolha.proximaCena)
            ?: return ResultadoEscolha.Recusada(sessao, MotivoRecusa.CENA_INEXISTENTE)

        val nova = sessao
            .copy(cenaAtual = proxima.id, caminho = sessao.caminho + escolha.id)
            .comPista(escolha.pista)
            .comRevelacoes(caso, escolha.revelacoes)
            .comPista(proxima.pista)
            .comRevelacoes(caso, proxima.revelacoes)
            .comDesfechoDe(proxima)

        val reveladas = nova.pistas - sessao.pistas.toSet()

        return ResultadoEscolha.Aplicada(sessao = nova, pistasReveladas = reveladas)
    }

    /**
     * Reconstrói a sessão reproduzindo o progresso salvo sobre o caso.
     *
     * O resultado é determinístico: o mesmo caso e o mesmo progresso produzem
     * sempre a mesma cena, o mesmo caminho e as mesmas pistas.
     */
    fun reconstruir(caso: Caso, progresso: ProgressoCaso): ResultadoReconstrucao {
        if (progresso.casoId != caso.id) {
            return ResultadoReconstrucao.ProgressoIncompativel(
                passo = 0,
                escolhaId = "",
                motivo = "o progresso é do caso \"${progresso.casoId}\"",
            )
        }

        if (progresso.revisao.esquema != caso.revisao.esquema) {
            return ResultadoReconstrucao.ProgressoIncompativel(
                passo = 0,
                escolhaId = "",
                motivo = "o progresso usa o esquema ${progresso.revisao.esquema}, " +
                    "mas o caso instalado usa ${caso.revisao.esquema}",
            )
        }

        if (progresso.revisao.conteudo > caso.revisao.conteudo) {
            return ResultadoReconstrucao.ProgressoIncompativel(
                passo = 0,
                escolhaId = "",
                motivo = "o progresso pertence a uma revisão mais nova do caso",
            )
        }

        var sessao = iniciar(caso)
            ?: return ResultadoReconstrucao.ProgressoIncompativel(
                passo = 0,
                escolhaId = "",
                motivo = "a cena inicial \"${caso.cenaInicial}\" não existe",
            )

        progresso.escolhas.forEachIndexed { indice, escolhaId ->
            when (val resultado = escolher(caso, sessao, escolhaId)) {
                is ResultadoEscolha.Aplicada -> sessao = resultado.sessao
                is ResultadoEscolha.Recusada -> return ResultadoReconstrucao.ProgressoIncompativel(
                    passo = indice,
                    escolhaId = escolhaId,
                    motivo = resultado.motivo.descricao,
                )
            }
        }

        return ResultadoReconstrucao.Sucesso(sessao)
    }

    private fun SessaoInvestigacao.comDesfechoDe(cena: Cena): SessaoInvestigacao =
        if (cena.tipo == TipoCena.FINAL) copy(desfecho = cena.desfecho) else this
}

/** Resultado de aplicar uma escolha. */
sealed interface ResultadoEscolha {

    /** A história avançou; [pistasReveladas] traz o que foi descoberto agora. */
    data class Aplicada(
        val sessao: SessaoInvestigacao,
        val pistasReveladas: List<Pista> = emptyList(),
    ) : ResultadoEscolha

    /** Nada mudou. A sessão devolvida é a mesma recebida. */
    data class Recusada(
        val sessao: SessaoInvestigacao,
        val motivo: MotivoRecusa,
    ) : ResultadoEscolha
}

/**
 * Por que uma escolha não foi aplicada.
 *
 * Nenhum destes motivos é uma punição ao jogador: são condições técnicas que a
 * interface trata silenciosamente ou com uma mensagem neutra.
 */
enum class MotivoRecusa(val descricao: String) {
    SESSAO_CONCLUIDA("o caso já foi concluído"),
    ESCOLHA_INDISPONIVEL("a escolha não pertence à cena atual"),
    CENA_INEXISTENTE("a cena referida não existe no caso"),
}

/** Resultado de reproduzir um progresso salvo. */
sealed interface ResultadoReconstrucao {

    data class Sucesso(val sessao: SessaoInvestigacao) : ResultadoReconstrucao

    /** O progresso não combina mais com o caso — provavelmente o caso mudou. */
    data class ProgressoIncompativel(
        val passo: Int,
        val escolhaId: String,
        val motivo: String,
    ) : ResultadoReconstrucao
}

package br.com.w3ti.indicio.domain.validacao

import br.com.w3ti.indicio.domain.model.caso.Caso
import br.com.w3ti.indicio.domain.model.caso.Cena
import br.com.w3ti.indicio.domain.model.caso.Escolha
import br.com.w3ti.indicio.domain.model.caso.Revelacoes
import br.com.w3ti.indicio.domain.model.caso.TipoCena

/** Regras exclusivas do contrato narrativo v2. */
internal class ValidadorCasoLongo {

    fun validar(caso: Caso): List<ProblemaValidacao> = buildList {
        if (caso.revisao.conteudo < 1) {
            add(problema(caso, "versaoConteudo", "a versão de conteúdo precisa ser positiva"))
        }
        if (caso.etapas.isEmpty()) {
            add(problema(caso, "etapas", "um caso do esquema 2 precisa ter etapas"))
            return@buildList
        }

        validarTextosEIds(caso, this)
        validarCenasETransicoes(caso, this)
        validarRevelacoes(caso, this)
        validarGrafoAciclico(caso, this)
        validarConversasVisiveis(caso, this)
    }

    private fun validarTextosEIds(caso: Caso, problemas: MutableList<ProblemaValidacao>) {
        val ids = buildList {
            add("id" to caso.id)
            caso.etapas.forEachIndexed { etapaIndice, etapa ->
                add("etapas[$etapaIndice].id" to etapa.id)
                exigirTexto(caso, "etapas[$etapaIndice].titulo", etapa.titulo, problemas)
                exigirTexto(caso, "etapas[$etapaIndice].descricao", etapa.descricao, problemas)
                exigirTexto(caso, "etapas[$etapaIndice].resumoConclusao", etapa.resumoConclusao, problemas)
                exigirTexto(caso, "etapas[$etapaIndice].resumoRetomada", etapa.resumoRetomada, problemas)
                if (etapa.objetivos.isEmpty()) {
                    problemas += problema(caso, "etapas[$etapaIndice].objetivos", "a etapa precisa de um objetivo")
                }
                etapa.objetivos.forEachIndexed { objetivoIndice, objetivo ->
                    add("etapas[$etapaIndice].objetivos[$objetivoIndice].id" to objetivo.id)
                    exigirTexto(caso, "objetivos[$objetivoIndice].texto", objetivo.texto, problemas)
                    exigirTexto(caso, "objetivos[$objetivoIndice].perguntaEmAberto", objetivo.perguntaEmAberto, problemas)
                }
            }
            caso.cenas.forEachIndexed { indice, cena ->
                add("cenas[$indice].id" to cena.id)
                cena.escolhas.forEachIndexed { escolhaIndice, escolha ->
                    add("cenas[$indice].escolhas[$escolhaIndice].id" to escolha.id)
                }
            }
            caso.caderno.pistas.forEachIndexed { indice, pista ->
                add("caderno.pistas[$indice].id" to pista.id)
                exigirTexto(caso, "caderno.pistas[$indice].titulo", pista.titulo, problemas)
                exigirTexto(caso, "caderno.pistas[$indice].descricao", pista.descricao, problemas)
            }
            caso.caderno.pessoas.forEachIndexed { indice, pessoa ->
                add("caderno.pessoas[$indice].id" to pessoa.id)
                exigirTexto(caso, "caderno.pessoas[$indice].nome", pessoa.nome, problemas)
                exigirTexto(caso, "caderno.pessoas[$indice].papel", pessoa.papel, problemas)
                if (pessoa.anotacoes.isEmpty()) {
                    problemas += problema(caso, "caderno.pessoas[$indice].anotacoes", "a pessoa precisa de uma anotação")
                }
                validarImagemOpcional(caso, "caderno.pessoas[$indice].imagem", pessoa.imagem, problemas)
                pessoa.anotacoes.forEachIndexed { notaIndice, nota ->
                    add("caderno.pessoas[$indice].anotacoes[$notaIndice].id" to nota.id)
                    exigirTexto(caso, "anotacoes[$notaIndice].texto", nota.texto, problemas)
                }
            }
            caso.caderno.locais.forEachIndexed { indice, local ->
                add("caderno.locais[$indice].id" to local.id)
                exigirTexto(caso, "caderno.locais[$indice].nome", local.nome, problemas)
                if (local.anotacoes.isEmpty()) {
                    problemas += problema(caso, "caderno.locais[$indice].anotacoes", "o local precisa de uma anotação")
                }
                validarImagemOpcional(caso, "caderno.locais[$indice].imagem", local.imagem, problemas)
                local.anotacoes.forEachIndexed { notaIndice, nota ->
                    add("caderno.locais[$indice].anotacoes[$notaIndice].id" to nota.id)
                    exigirTexto(caso, "anotacoes[$notaIndice].texto", nota.texto, problemas)
                }
            }
            caso.caderno.conversas.forEachIndexed { indice, conversa ->
                add("caderno.conversas[$indice].id" to conversa.id)
                exigirTexto(caso, "caderno.conversas[$indice].titulo", conversa.titulo, problemas)
                exigirTexto(caso, "caderno.conversas[$indice].texto", conversa.texto, problemas)
            }
            caso.lembrancas.forEachIndexed { indice, lembranca ->
                add("lembrancas[$indice].id" to lembranca.id)
                exigirTexto(caso, "lembrancas[$indice].texto", lembranca.texto, problemas)
            }
        }

        ids.filterNot { (_, id) -> PADRAO_ID.matches(id) }
            .forEach { (campo, _) ->
                problemas += problema(caso, campo, "o identificador deve usar minúsculas, números e hífens")
            }
        validarDuplicados(caso, "etapas[].id", caso.etapas.map { it.id }, problemas)
        validarDuplicados(caso, "objetivos[].id", caso.etapas.flatMap { it.objetivos }.map { it.id }, problemas)
        validarDuplicados(caso, "escolhas[].id", caso.cenas.flatMap { it.escolhas }.map { it.id }, problemas)
        validarDuplicados(caso, "caderno.pistas[].id", caso.caderno.pistas.map { it.id }, problemas)
        validarDuplicados(
            caso,
            "caderno.pessoas[].anotacoes[].id",
            caso.caderno.pessoas.flatMap { it.anotacoes }.map { it.id },
            problemas,
        )
        validarDuplicados(
            caso,
            "caderno.locais[].anotacoes[].id",
            caso.caderno.locais.flatMap { it.anotacoes }.map { it.id },
            problemas,
        )
        validarDuplicados(caso, "caderno.conversas[].id", caso.caderno.conversas.map { it.id }, problemas)
        validarDuplicados(caso, "lembrancas[].id", caso.lembrancas.map { it.id }, problemas)
    }

    private fun validarCenasETransicoes(caso: Caso, problemas: MutableList<ProblemaValidacao>) {
        val posicoes = caso.etapas.mapIndexed { indice, etapa -> etapa.id to indice }.toMap()
        val primeiraEtapa = caso.etapas.first().id
        val ultimaEtapa = caso.etapas.last().id
        if (caso.cena(caso.cenaInicial)?.etapaId != primeiraEtapa) {
            problemas += problema(caso, "cenaInicial", "a cena inicial precisa pertencer à primeira etapa")
        }

        val objetivos = caso.etapas.flatMap { etapa -> etapa.objetivos.map { it.id to etapa.id } }.toMap()
        val objetivosUsados = mutableSetOf<String>()
        caso.cenas.forEach { cena ->
            val etapa = cena.etapaId
            if (etapa == null || etapa !in posicoes) {
                problemas += problema(caso, "etapaId", "a cena \"${cena.id}\" aponta para uma etapa inexistente", cena.id)
                return@forEach
            }
            if (cena.tipo == TipoCena.FINAL) {
                if (etapa != ultimaEtapa) {
                    problemas += problema(caso, "etapaId", "cenas finais pertencem à última etapa", cena.id)
                }
                if (cena.objetivoId != null) {
                    problemas += problema(caso, "objetivoId", "cena final não possui objetivo atual", cena.id)
                }
            } else {
                val objetivo = cena.objetivoId
                if (objetivo == null || objetivos[objetivo] != etapa) {
                    problemas += problema(caso, "objetivoId", "o objetivo precisa pertencer à etapa da cena", cena.id)
                } else {
                    objetivosUsados += objetivo
                }
                if (cena.escolhas.map { it.texto }.distinct().size != cena.escolhas.size ||
                    cena.escolhas.map { it.proximaCena }.distinct().size != cena.escolhas.size
                ) {
                    problemas += problema(caso, "escolhas", "as duas escolhas precisam ter textos e destinos distintos", cena.id)
                }
            }

            cena.escolhas.forEachIndexed { indice, escolha ->
                val destino = caso.cena(escolha.proximaCena) ?: return@forEachIndexed
                val posicaoDestino = destino.etapaId?.let(posicoes::get) ?: return@forEachIndexed
                val posicaoOrigem = posicoes[etapa] ?: return@forEachIndexed
                val salto = posicaoDestino - posicaoOrigem
                if (salto !in 0..1) {
                    problemas += problema(
                        caso,
                        "escolhas[$indice].proximaCena",
                        "a transição não pode voltar nem saltar uma etapa",
                        cena.id,
                    )
                }
            }
        }

        (objetivos.keys - objetivosUsados).forEach { id ->
            problemas += problema(caso, "objetivos", "o objetivo \"$id\" não é usado por nenhuma cena")
        }
    }

    private fun validarRevelacoes(caso: Caso, problemas: MutableList<ProblemaValidacao>) {
        val validos = mapOf(
            "pistas" to caso.caderno.pistas.map { it.id }.toSet(),
            "anotacoesPessoas" to caso.caderno.pessoas.flatMap { it.anotacoes }.map { it.id }.toSet(),
            "anotacoesLocais" to caso.caderno.locais.flatMap { it.anotacoes }.map { it.id }.toSet(),
            "conversas" to caso.caderno.conversas.map { it.id }.toSet(),
            "lembrancas" to caso.lembrancas.map { it.id }.toSet(),
        )
        val usados = validos.mapValues { mutableSetOf<String>() }

        caso.cenas.forEach { cena ->
            validarRevelacoes(caso, cena.id, "revelacoes", cena.revelacoes, validos, usados, problemas)
            cena.escolhas.forEachIndexed { indice, escolha ->
                validarRevelacoes(
                    caso,
                    cena.id,
                    "escolhas[$indice].revelacoes",
                    escolha.revelacoes,
                    validos,
                    usados,
                    problemas,
                )
            }
        }

        validos.forEach { (tipo, ids) ->
            (ids - usados.getValue(tipo)).forEach { id ->
                problemas += problema(caso, "caderno.$tipo", "o conteúdo \"$id\" nunca é revelado")
            }
        }

        val pessoas = caso.caderno.pessoas.map { it.id }.toSet()
        caso.caderno.conversas.forEachIndexed { indice, conversa ->
            if (conversa.pessoaId !in pessoas) {
                problemas += problema(
                    caso,
                    "caderno.conversas[$indice].pessoaId",
                    "a pessoa \"${conversa.pessoaId}\" não existe",
                )
            }
        }
    }

    private fun validarRevelacoes(
        caso: Caso,
        cenaId: String,
        campoBase: String,
        revelacoes: Revelacoes,
        validos: Map<String, Set<String>>,
        usados: Map<String, MutableSet<String>>,
        problemas: MutableList<ProblemaValidacao>,
    ) {
        val listas = mapOf(
            "pistas" to revelacoes.pistas,
            "anotacoesPessoas" to revelacoes.anotacoesPessoas,
            "anotacoesLocais" to revelacoes.anotacoesLocais,
            "conversas" to revelacoes.conversas,
            "lembrancas" to revelacoes.lembrancas,
        )
        listas.forEach { (tipo, ids) ->
            ids.forEachIndexed { indice, id ->
                if (id !in validos.getValue(tipo)) {
                    problemas += problema(
                        caso,
                        "$campoBase.$tipo[$indice]",
                        "o conteúdo \"$id\" não existe",
                        cenaId,
                    )
                } else {
                    usados.getValue(tipo) += id
                }
            }
        }
    }

    private fun validarGrafoAciclico(caso: Caso, problemas: MutableList<ProblemaValidacao>) {
        val visitados = mutableSetOf<String>()
        val ativos = mutableSetOf<String>()
        var cicloEncontrado = false

        fun visitar(id: String) {
            if (!ativos.add(id)) {
                cicloEncontrado = true
                return
            }
            if (!visitados.add(id)) {
                ativos -= id
                return
            }
            caso.cena(id)?.escolhas?.forEach { visitar(it.proximaCena) }
            ativos -= id
        }
        visitar(caso.cenaInicial)
        if (cicloEncontrado) {
            problemas += problema(caso, "cenas", "o grafo do esquema 2 não pode conter ciclos")
        }
    }

    /** Garante em todo caminho que uma conversa não anteceda a pessoa. */
    private fun validarConversasVisiveis(caso: Caso, problemas: MutableList<ProblemaValidacao>) {
        val pessoaPorNota = caso.caderno.pessoas.flatMap { pessoa ->
            pessoa.anotacoes.map { it.id to pessoa.id }
        }.toMap()
        val pessoaPorConversa = caso.caderno.conversas.associate { it.id to it.pessoaId }
        val estadosVisitados = mutableSetOf<Pair<String, Set<String>>>()

        fun aplicar(
            cenaId: String,
            campo: String,
            revelacoes: Revelacoes,
            visiveis: Set<String>,
        ): Set<String> {
            val agora = visiveis + revelacoes.anotacoesPessoas.mapNotNull(pessoaPorNota::get)
            revelacoes.conversas.forEach { conversaId ->
                val pessoa = pessoaPorConversa[conversaId] ?: return@forEach
                if (pessoa !in agora) {
                    problemas += problema(
                        caso,
                        campo,
                        "a conversa \"$conversaId\" aparece antes de sua pessoa",
                        cenaId,
                    )
                }
            }
            return agora
        }

        fun percorrer(cena: Cena, visiveisAntes: Set<String>, ativos: Set<String>) {
            if (cena.id in ativos || !estadosVisitados.add(cena.id to visiveisAntes)) return
            val naCena = aplicar(cena.id, "revelacoes.conversas", cena.revelacoes, visiveisAntes)
            cena.escolhas.forEachIndexed { indice, escolha ->
                val naEscolha = aplicar(
                    cena.id,
                    "escolhas[$indice].revelacoes.conversas",
                    escolha.revelacoes,
                    naCena,
                )
                caso.cena(escolha.proximaCena)?.let { destino ->
                    percorrer(destino, naEscolha, ativos + cena.id)
                }
            }
        }

        caso.cena(caso.cenaInicial)?.let { percorrer(it, emptySet(), emptySet()) }
    }

    private fun validarImagemOpcional(
        caso: Caso,
        campo: String,
        imagem: br.com.w3ti.indicio.domain.model.caso.Imagem?,
        problemas: MutableList<ProblemaValidacao>,
    ) {
        if (imagem == null) return
        exigirTexto(caso, "$campo.recurso", imagem.recurso, problemas)
        exigirTexto(caso, "$campo.descricaoAcessivel", imagem.descricaoAcessivel, problemas)
    }

    private fun exigirTexto(
        caso: Caso,
        campo: String,
        texto: String,
        problemas: MutableList<ProblemaValidacao>,
    ) {
        if (texto.isBlank()) problemas += problema(caso, campo, "o texto não pode estar vazio")
    }

    private fun validarDuplicados(
        caso: Caso,
        campo: String,
        ids: List<String>,
        problemas: MutableList<ProblemaValidacao>,
    ) {
        ids.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.forEach { id ->
            problemas += problema(caso, campo, "o identificador \"$id\" está repetido")
        }
    }

    private fun problema(
        caso: Caso,
        campo: String,
        mensagem: String,
        cenaId: String? = null,
    ) = ProblemaValidacao(caso.id, cenaId, campo, mensagem)

    private companion object {
        val PADRAO_ID = Regex("^[a-z0-9]+(?:-[a-z0-9]+)*$")
    }
}

package br.com.avoren.indicio.domain.validacao

import br.com.avoren.indicio.domain.model.caso.Caso
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Escolha
import br.com.avoren.indicio.domain.model.caso.Pista
import br.com.avoren.indicio.domain.model.caso.TipoCena

/**
 * Verifica se um caso desserializado forma uma história jogável.
 *
 * O validador é puro e independente de Android; roda tanto no carregamento em
 * produção quanto nos testes que protegem o conteúdo contra regressões. Todos
 * os problemas são coletados antes de retornar, para que um arquivo com vários
 * erros seja corrigido de uma vez.
 */
class ValidadorCaso {

    fun validar(caso: Caso): List<ProblemaValidacao> {
        val problemas = mutableListOf<ProblemaValidacao>()

        validarMetadados(caso, problemas)
        val idsDeCena = validarIdentificadoresDeCena(caso, problemas)
        validarCenaInicial(caso, idsDeCena, problemas)
        caso.cenas.forEach { cena -> validarCena(caso, cena, idsDeCena, problemas) }
        validarPistas(caso, problemas)
        validarAlcancabilidade(caso, problemas)
        if (caso.revisao.esquema == 2) {
            problemas += ValidadorCasoLongo().validar(caso)
        }

        return problemas
    }

    private fun validarMetadados(caso: Caso, problemas: MutableList<ProblemaValidacao>) {
        if (caso.id.isBlank()) {
            problemas += problema(caso, campo = "id", mensagem = "o caso precisa de um identificador")
        }
        if (caso.titulo.isBlank()) {
            problemas += problema(caso, campo = "titulo", mensagem = "o caso precisa de um título")
        }
        if (caso.sinopse.isBlank()) {
            problemas += problema(caso, campo = "sinopse", mensagem = "o caso precisa de uma sinopse")
        }
        if (caso.cenas.isEmpty()) {
            problemas += problema(caso, campo = "cenas", mensagem = "o caso não tem nenhuma cena")
        }
    }

    /** Retorna os identificadores existentes e registra os repetidos. */
    private fun validarIdentificadoresDeCena(
        caso: Caso,
        problemas: MutableList<ProblemaValidacao>,
    ): Set<String> {
        caso.cenas.filter { it.id.isBlank() }.forEach {
            problemas += problema(caso, campo = "cenas[].id", mensagem = "há cena sem identificador")
        }

        caso.cenas
            .groupingBy(Cena::id)
            .eachCount()
            .filterValues { it > 1 }
            .keys
            .sorted()
            .forEach { repetido ->
                problemas += problema(
                    caso,
                    cenaId = repetido,
                    campo = "id",
                    mensagem = "identificador de cena repetido",
                )
            }

        return caso.cenas.map(Cena::id).toSet()
    }

    private fun validarCenaInicial(
        caso: Caso,
        idsDeCena: Set<String>,
        problemas: MutableList<ProblemaValidacao>,
    ) {
        when {
            caso.cenaInicial.isBlank() ->
                problemas += problema(caso, campo = "cenaInicial", mensagem = "a cena inicial não foi informada")

            caso.cenaInicial !in idsDeCena ->
                problemas += problema(
                    caso,
                    campo = "cenaInicial",
                    mensagem = "a cena inicial \"${caso.cenaInicial}\" não existe",
                )
        }
    }

    private fun validarCena(
        caso: Caso,
        cena: Cena,
        idsDeCena: Set<String>,
        problemas: MutableList<ProblemaValidacao>,
    ) {
        if (cena.texto.isBlank()) {
            problemas += problema(caso, cena.id, "texto", "a cena precisa de texto narrativo")
        }
        if (cena.imagem.recurso.isBlank()) {
            problemas += problema(caso, cena.id, "imagem.recurso", "a cena precisa de uma imagem")
        }
        if (cena.imagem.descricaoAcessivel.isBlank()) {
            problemas += problema(
                caso,
                cena.id,
                "imagem.descricaoAcessivel",
                "a imagem precisa de descrição acessível",
            )
        }
        cena.pista?.let { validarPista(caso, cena.id, "pista", it, problemas) }

        when (cena.tipo) {
            TipoCena.COMUM -> validarCenaComum(caso, cena, idsDeCena, problemas)
            TipoCena.FINAL -> validarCenaFinal(caso, cena, problemas)
        }
    }

    private fun validarCenaComum(
        caso: Caso,
        cena: Cena,
        idsDeCena: Set<String>,
        problemas: MutableList<ProblemaValidacao>,
    ) {
        if (cena.escolhas.isEmpty()) {
            problemas += problema(
                caso,
                cena.id,
                "escolhas",
                "cena comum sem saída: são necessárias exatamente duas escolhas",
            )
        } else if (cena.escolhas.size != ESCOLHAS_POR_CENA) {
            problemas += problema(
                caso,
                cena.id,
                "escolhas",
                "a cena tem ${cena.escolhas.size} escolha(s); são necessárias exatamente $ESCOLHAS_POR_CENA",
            )
        }

        if (cena.desfecho != null) {
            problemas += problema(
                caso,
                cena.id,
                "desfecho",
                "apenas cenas do tipo \"final\" podem ter metadados de conclusão",
            )
        }

        cena.escolhas
            .groupingBy(Escolha::id)
            .eachCount()
            .filterValues { it > 1 }
            .keys
            .sorted()
            .forEach { repetido ->
                problemas += problema(caso, cena.id, "escolhas[].id", "identificador de escolha repetido: \"$repetido\"")
            }

        cena.escolhas.forEachIndexed { indice, escolha ->
            val campoBase = "escolhas[$indice]"
            if (escolha.id.isBlank()) {
                problemas += problema(caso, cena.id, "$campoBase.id", "a escolha precisa de um identificador")
            }
            if (escolha.texto.isBlank()) {
                problemas += problema(caso, cena.id, "$campoBase.texto", "a escolha precisa de texto")
            }
            when {
                escolha.proximaCena.isBlank() ->
                    problemas += problema(caso, cena.id, "$campoBase.proximaCena", "a escolha não aponta para nenhuma cena")

                escolha.proximaCena !in idsDeCena ->
                    problemas += problema(
                        caso,
                        cena.id,
                        "$campoBase.proximaCena",
                        "a cena \"${escolha.proximaCena}\" não existe",
                    )
            }
            escolha.pista?.let { validarPista(caso, cena.id, "$campoBase.pista", it, problemas) }
        }
    }

    private fun validarCenaFinal(
        caso: Caso,
        cena: Cena,
        problemas: MutableList<ProblemaValidacao>,
    ) {
        if (cena.escolhas.isNotEmpty()) {
            problemas += problema(caso, cena.id, "escolhas", "cena final não pode oferecer escolhas")
        }

        val desfecho = cena.desfecho
        if (desfecho == null) {
            problemas += problema(caso, cena.id, "desfecho", "cena final sem metadados de conclusão")
            return
        }
        if (desfecho.titulo.isBlank()) {
            problemas += problema(caso, cena.id, "desfecho.titulo", "o desfecho precisa de um título")
        }
        if (desfecho.mensagem.isBlank()) {
            problemas += problema(caso, cena.id, "desfecho.mensagem", "o desfecho precisa de uma mensagem de encerramento")
        }
        if (desfecho.explicacaoPistas.isBlank()) {
            problemas += problema(
                caso,
                cena.id,
                "desfecho.explicacaoPistas",
                "o desfecho precisa explicar as pistas principais",
            )
        }
    }

    private fun validarPista(
        caso: Caso,
        cenaId: String,
        campo: String,
        pista: Pista,
        problemas: MutableList<ProblemaValidacao>,
    ) {
        if (pista.id.isBlank()) {
            problemas += problema(caso, cenaId, "$campo.id", "a pista precisa de um identificador")
        }
        if (pista.titulo.isBlank()) {
            problemas += problema(caso, cenaId, "$campo.titulo", "a pista precisa de um título")
        }
        if (pista.descricao.isBlank()) {
            problemas += problema(caso, cenaId, "$campo.descricao", "a pista precisa de uma descrição")
        }
    }

    /**
     * A mesma pista pode ser alcançada por caminhos diferentes, mas o conteúdo
     * precisa ser idêntico; caso contrário o jogador veria textos distintos
     * para a mesma descoberta.
     */
    private fun validarPistas(caso: Caso, problemas: MutableList<ProblemaValidacao>) {
        val todas = caso.cenas.flatMap { cena ->
            listOfNotNull(cena.pista) + cena.escolhas.mapNotNull(Escolha::pista)
        }

        todas.groupBy(Pista::id)
            .filterValues { ocorrencias -> ocorrencias.distinct().size > 1 }
            .keys
            .sorted()
            .forEach { id ->
                problemas += problema(
                    caso,
                    campo = "pistas",
                    mensagem = "a pista \"$id\" aparece com conteúdos diferentes",
                )
            }
    }

    private fun validarAlcancabilidade(caso: Caso, problemas: MutableList<ProblemaValidacao>) {
        if (caso.cenaInicial !in caso.cenas.map(Cena::id).toSet()) return

        val alcancadas = mutableSetOf(caso.cenaInicial)
        val fila = ArrayDeque(listOf(caso.cenaInicial))

        while (fila.isNotEmpty()) {
            val atual = caso.cena(fila.removeFirst()) ?: continue
            atual.escolhas.forEach { escolha ->
                if (alcancadas.add(escolha.proximaCena)) {
                    fila += escolha.proximaCena
                }
            }
        }

        caso.cenas
            .map(Cena::id)
            .distinct()
            .filterNot { it in alcancadas }
            .forEach { inalcancavel ->
                problemas += problema(
                    caso,
                    cenaId = inalcancavel,
                    campo = "cenas",
                    mensagem = "a cena não é alcançável a partir da cena inicial",
                )
            }
    }

    private fun problema(
        caso: Caso,
        cenaId: String? = null,
        campo: String,
        mensagem: String,
    ) = ProblemaValidacao(casoId = caso.id, cenaId = cenaId, campo = campo, mensagem = mensagem)

    private companion object {
        const val ESCOLHAS_POR_CENA = 2
    }
}

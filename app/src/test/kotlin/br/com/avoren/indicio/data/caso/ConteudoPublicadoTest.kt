package br.com.avoren.indicio.data.caso

import br.com.avoren.indicio.domain.caso.ResultadoCarga
import br.com.avoren.indicio.domain.model.caso.Caso
import br.com.avoren.indicio.domain.model.caso.Cena
import br.com.avoren.indicio.domain.model.caso.Escolha
import br.com.avoren.indicio.domain.model.caso.TipoCena
import br.com.avoren.indicio.fake.FonteCasosDeArquivo
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Protege o conteúdo que realmente vai para o aplicativo.
 *
 * Falha se um caso publicado deixar de carregar, de validar ou de respeitar as
 * regras editoriais verificáveis automaticamente.
 */
class ConteudoPublicadoTest {

    private val repositorio = RepositorioCasosJson(
        fonte = FonteCasosDeArquivo(),
        dispatcher = UnconfinedTestDispatcher(),
    )

    private suspend fun casosPublicados(): List<Caso> {
        val catalogo = (repositorio.catalogo() as ResultadoCarga.Sucesso).valor
        return catalogo.disponiveis().map { resumo ->
            val resultado = repositorio.caso(resumo.id)
            assertTrue(
                "O caso \"${resumo.id}\" não passou na validação: $resultado",
                resultado is ResultadoCarga.Sucesso,
            )
            (resultado as ResultadoCarga.Sucesso).valor
        }
    }

    @Test
    fun `o catalogo publicado carrega e valida`() = runTest {
        val resultado = repositorio.catalogo()

        assertTrue("Catálogo inválido: $resultado", resultado is ResultadoCarga.Sucesso)
    }

    @Test
    fun `todo caso publicado declara uma capa acessivel`() = runTest {
        val catalogo = (repositorio.catalogo() as ResultadoCarga.Sucesso).valor

        catalogo.disponiveis().forEach { resumo ->
            val imagem = resumo.imagem
            assertTrue("O caso \"${resumo.id}\" não declara uma capa no catálogo.", imagem != null)
            assertTrue("A capa de \"${resumo.id}\" não declara um recurso.", !imagem?.recurso.isNullOrBlank())
            assertTrue(
                "A capa de \"${resumo.id}\" não possui descrição acessível.",
                !imagem?.descricaoAcessivel.isNullOrBlank(),
            )
        }
    }

    @Test
    fun `todo caso disponivel carrega e valida`() = runTest {
        assertTrue(casosPublicados().isNotEmpty())
    }

    @Test
    fun `cada caso tem ao menos doze cenas`() = runTest {
        casosPublicados().forEach { caso ->
            assertTrue(
                "O caso \"${caso.id}\" tem ${caso.cenas.size} cenas; o mínimo é 12.",
                caso.cenas.size >= 12,
            )
        }
    }

    @Test
    fun `cada caso tem entre dois e tres finais positivos`() = runTest {
        casosPublicados().forEach { caso ->
            val finais = caso.cenas.count { it.tipo == TipoCena.FINAL }
            assertTrue("O caso \"${caso.id}\" tem $finais finais; o esperado é 2 ou 3.", finais in 2..3)
        }
    }

    @Test
    fun `todo final e alcancavel a partir da cena inicial`() = runTest {
        casosPublicados().forEach { caso ->
            val alcancadas = alcancaveis(caso)
            caso.cenas.filter { it.tipo == TipoCena.FINAL }.forEach { final ->
                assertTrue(
                    "O final \"${final.id}\" do caso \"${caso.id}\" não é alcançável.",
                    final.id in alcancadas,
                )
            }
        }
    }

    @Test
    fun `nenhuma escolha leva de volta a propria cena`() = runTest {
        casosPublicados().forEach { caso ->
            caso.cenas.forEach { cena ->
                cena.escolhas.forEach { escolha ->
                    assertEquals(
                        "A escolha \"${escolha.id}\" volta para a própria cena \"${cena.id}\".",
                        false,
                        escolha.proximaCena == cena.id,
                    )
                }
            }
        }
    }

    @Test
    fun `todo caso tem ao menos tres pistas distintas`() = runTest {
        casosPublicados().forEach { caso ->
            assertTrue(
                "O caso \"${caso.id}\" tem ${caso.pistas().size} pista(s).",
                caso.pistas().size >= 3,
            )
        }
    }

    @Test
    fun `a taca longa mantem duracao editorial equilibrada em todo percurso`() = runTest {
        val caso = casosPublicados().single { it.id == "taca-desaparecida" }
        val comprimentos = comprimentosAteFinal(caso, caso.cenaInicial)
        val palavras = palavrasAteFinal(caso, caso.cenaInicial)

        assertEquals(2, caso.revisao.esquema)
        assertEquals(6, caso.revisao.conteudo)
        assertEquals(6, caso.etapas.size)
        assertEquals(83, caso.cenas.count { it.tipo == TipoCena.COMUM })
        assertEquals(2, caso.cenas.count { it.tipo == TipoCena.FINAL })
        assertTrue(caso.cenas.filter { it.tipo == TipoCena.COMUM }.all { it.escolhas.size == 2 })
        assertEquals(42..42, comprimentos)
        assertTrue(
            "O menor percurso tem apenas ${palavras.first} palavras.",
            palavras.first >= 5_500,
        )
        assertTrue(
            "A diferença entre percursos é de ${palavras.last - palavras.first} palavras.",
            palavras.last - palavras.first <= 300,
        )
    }

    @Test
    fun `nenhum termo de violencia, punicao ou marca real aparece no conteudo`() = runTest {
        casosPublicados().forEach { caso ->
            val texto = textoCompleto(caso).lowercase()
            TERMOS_PROIBIDOS.forEach { termo ->
                // Palavra inteira: "retirou" não pode acusar o termo "tiro".
                val ocorrencia = Regex("\\b${Regex.escape(termo)}\\b")
                assertTrue(
                    "O caso \"${caso.id}\" contém o termo proibido \"$termo\".",
                    !ocorrencia.containsMatchIn(texto),
                )
            }
        }
    }

    @Test
    fun `toda descricao acessivel e informativa`() = runTest {
        casosPublicados().forEach { caso ->
            caso.cenas.forEach { cena ->
                assertTrue(
                    "A descrição acessível da cena \"${cena.id}\" é curta demais.",
                    cena.imagem.descricaoAcessivel.length >= 30,
                )
            }
        }
    }

    @Test
    fun `dicas dos casos longos insinuam sem entregar o texto da escolha`() = runTest {
        casosPublicados().filter { it.revisao.esquema == 2 }.forEach { caso ->
            caso.cenas.flatMap(Cena::escolhas).forEach { escolha ->
                val dica = escolha.dica.orEmpty()
                assertTrue("A escolha \"${escolha.id}\" não possui dica narrativa.", dica.isNotBlank())
                assertTrue(
                    "A dica da escolha \"${escolha.id}\" entrega diretamente o caminho.",
                    !dica.contains(escolha.texto, ignoreCase = true) &&
                        !dica.contains("siga por", ignoreCase = true),
                )
            }
        }
    }

    @Test
    fun `pistas dos casos longos explicam por que cada descoberta importa`() = runTest {
        casosPublicados().filter { it.revisao.esquema == 2 }.forEach { caso ->
            caso.caderno.pistas.forEach { pista ->
                assertTrue(
                    "A pista \"${pista.id}\" não explica sua relevância.",
                    pista.relevancia.orEmpty().length >= 40,
                )
            }
        }
    }

    @Test
    fun `galeria nove mantem uma investigacao equilibrada em todo percurso`() = runTest {
        val caso = casosPublicados().single { it.id == "silencio-galeria-nove" }
        val comprimentos = comprimentosAteFinal(caso, caso.cenaInicial)
        val palavras = palavrasAteFinal(caso, caso.cenaInicial)

        assertEquals(2, caso.revisao.esquema)
        assertEquals(1, caso.revisao.conteudo)
        assertEquals(5, caso.etapas.size)
        assertEquals(21, caso.cenas.count { it.tipo == TipoCena.COMUM })
        assertEquals(2, caso.cenas.count { it.tipo == TipoCena.FINAL })
        assertTrue(caso.cenas.filter { it.tipo == TipoCena.COMUM }.all { it.escolhas.size == 2 })
        assertEquals(11..11, comprimentos)
        assertTrue(
            "O menor percurso da Galeria Nove tem apenas ${palavras.first} palavras.",
            palavras.first >= 1_200,
        )
        assertTrue(
            "A diferença entre percursos da Galeria Nove é de ${palavras.last - palavras.first} palavras.",
            palavras.last - palavras.first <= 450,
        )
    }

    @Test
    fun `sumico da mumia mantem uma investigacao documental em todo percurso`() = runTest {
        val caso = casosPublicados().single { it.id == "sumico-da-mumia" }
        val comprimentos = comprimentosAteFinal(caso, caso.cenaInicial)
        val palavras = palavrasAteFinal(caso, caso.cenaInicial)
        val texto = textoCompleto(caso).lowercase()

        assertEquals(2, caso.revisao.esquema)
        assertEquals(2, caso.revisao.conteudo)
        assertEquals(5, caso.etapas.size)
        assertEquals(21, caso.cenas.count { it.tipo == TipoCena.COMUM })
        assertEquals(2, caso.cenas.count { it.tipo == TipoCena.FINAL })
        assertTrue(caso.cenas.filter { it.tipo == TipoCena.COMUM }.all { it.escolhas.size == 2 })
        assertEquals(11..11, comprimentos)
        assertTrue(
            "O menor percurso do Sumiço da Múmia tem apenas ${palavras.first} palavras.",
            palavras.first >= 1_200,
        )
        assertTrue(
            "A diferença entre percursos do Sumiço da Múmia é de ${palavras.last - palavras.first} palavras.",
            palavras.last - palavras.first <= 450,
        )
        listOf("maldição", "múmia viva", "ganha vida", "uma noite no museu").forEach { termo ->
            assertTrue(
                "O caso documental contém a expressão fantástica ou distintiva \"$termo\".",
                termo !in texto,
            )
        }
        listOf(
            "cairo", "gizé", "saqqara", "quéops", "quéfren", "miquerinos", "djoser",
            "egito", "egípc", "1926",
        ).forEach { termo ->
            assertTrue(
                "O caso inteiramente fictício contém a referência real ou data substituída \"$termo\".",
                termo !in texto,
            )
        }
    }

    @Test
    fun `novos casos compactos mantem cinco etapas e percursos equilibrados`() = runTest {
        val novosIds = setOf(
            "ultimo-quadro-estrela-papel",
            "cidade-sem-meio-dia",
            "cartas-casa-magnolias",
            "farol-duas-mares",
            "ultima-transmissao-radio-aurora",
            "jardim-fora-de-epoca",
            "enigma-vagao-boreal",
            "roubo-rosa-boreal",
        )
        val novos = casosPublicados().filter { it.id in novosIds }

        assertEquals(novosIds, novos.map(Caso::id).toSet())
        novos.forEach { caso ->
            val comprimentos = comprimentosAteFinal(caso, caso.cenaInicial)
            val palavras = palavrasAteFinal(caso, caso.cenaInicial)

            assertEquals(2, caso.revisao.esquema)
            assertEquals(1, caso.revisao.conteudo)
            assertEquals(5, caso.etapas.size)
            assertEquals(11, caso.cenas.count { it.tipo == TipoCena.COMUM })
            assertEquals(2, caso.cenas.count { it.tipo == TipoCena.FINAL })
            assertEquals(6..6, comprimentos)
            assertTrue(
                "O menor percurso de ${caso.titulo} tem apenas ${palavras.first} palavras.",
                palavras.first >= 450,
            )
            assertTrue(
                "Os percursos de ${caso.titulo} diferem em ${palavras.last - palavras.first} palavras.",
                palavras.last - palavras.first <= 100,
            )
        }
    }

    @Test
    fun `casos ferroviarios mantem enredos e identidades inteiramente originais`() = runTest {
        val casos = casosPublicados().filter { it.id in setOf("enigma-vagao-boreal", "roubo-rosa-boreal") }

        assertEquals(2, casos.size)
        casos.forEach { caso ->
            val texto = textoCompleto(caso).lowercase()
            assertTrue("A travessia precisa permanecer situada no Canadá.", "canad" in texto)
            listOf(
                "expresso do oriente",
                "orient express",
                "poirot",
                "agatha christie",
                "murder on the orient express",
                "assassinato no expresso do oriente",
            ).forEach { referencia ->
                assertTrue(
                    "O caso ferroviário contém a referência distintiva \"$referencia\".",
                    referencia !in texto,
                )
            }
        }

        val roubo = casos.single { it.id == "roubo-rosa-boreal" }
        assertTrue("O novo caso precisa tratar de um furto real.", "furto" in textoCompleto(roubo).lowercase())
    }

    private fun alcancaveis(caso: Caso): Set<String> {
        val vistas = mutableSetOf(caso.cenaInicial)
        val fila = ArrayDeque(listOf(caso.cenaInicial))
        while (fila.isNotEmpty()) {
            val cena = caso.cena(fila.removeFirst()) ?: continue
            cena.escolhas.forEach { escolha ->
                if (vistas.add(escolha.proximaCena)) fila += escolha.proximaCena
            }
        }
        return vistas
    }

    private fun comprimentosAteFinal(
        caso: Caso,
        cenaInicial: String,
    ): IntRange {
        val memo = mutableMapOf<String, IntRange>()

        fun calcular(cenaId: String): IntRange = memo.getOrPut(cenaId) {
            val cena = requireNotNull(caso.cena(cenaId))
            if (cena.tipo == TipoCena.FINAL) {
                0..0
            } else {
                val destinos = cena.escolhas.map { escolha -> calcular(escolha.proximaCena) }
                (1 + destinos.minOf(IntRange::first))..(1 + destinos.maxOf(IntRange::last))
            }
        }

        return calcular(cenaInicial)
    }

    private fun palavrasAteFinal(
        caso: Caso,
        cenaInicial: String,
    ): IntRange {
        val memo = mutableMapOf<String, IntRange>()

        fun calcular(cenaId: String): IntRange = memo.getOrPut(cenaId) {
            val cena = requireNotNull(caso.cena(cenaId))
            val palavrasDaCena = cena.texto.trim().split(Regex("\\s+")).size
            if (cena.tipo == TipoCena.FINAL) {
                palavrasDaCena..palavrasDaCena
            } else {
                val destinos = cena.escolhas.map { escolha -> calcular(escolha.proximaCena) }
                (palavrasDaCena + destinos.minOf(IntRange::first))..
                    (palavrasDaCena + destinos.maxOf(IntRange::last))
            }
        }

        return calcular(cenaInicial)
    }

    private fun textoCompleto(caso: Caso): String = buildString {
        append(caso.titulo).append(' ').append(caso.sinopse).append(' ')
        caso.cenas.forEach { cena: Cena ->
            append(cena.texto).append(' ')
            append(cena.narracao.orEmpty()).append(' ')
            append(cena.imagem.descricaoAcessivel).append(' ')
            cena.pista?.let { append(it.titulo).append(' ').append(it.descricao).append(' ') }
            cena.escolhas.forEach { escolha: Escolha ->
                append(escolha.texto).append(' ').append(escolha.dica.orEmpty()).append(' ')
            }
            cena.desfecho?.let {
                append(it.titulo).append(' ').append(it.mensagem).append(' ').append(it.explicacaoPistas)
            }
        }
        caso.caderno.pistas.forEach { pista -> append(pista.relevancia.orEmpty()).append(' ') }
        caso.etapas.forEach { etapa ->
            append(etapa.titulo).append(' ').append(etapa.descricao).append(' ')
            append(etapa.resumoConclusao).append(' ').append(etapa.resumoRetomada).append(' ')
            etapa.objetivos.forEach { objetivo ->
                append(objetivo.texto).append(' ').append(objetivo.perguntaEmAberto).append(' ')
            }
        }
        caso.caderno.pessoas.forEach { pessoa ->
            append(pessoa.nome).append(' ').append(pessoa.papel).append(' ')
            pessoa.anotacoes.forEach { anotacao -> append(anotacao.texto).append(' ') }
        }
        caso.caderno.locais.forEach { local ->
            append(local.nome).append(' ')
            local.anotacoes.forEach { anotacao -> append(anotacao.texto).append(' ') }
        }
        caso.caderno.conversas.forEach { conversa ->
            append(conversa.titulo).append(' ').append(conversa.texto).append(' ')
            append(conversa.narracao.orEmpty()).append(' ')
        }
        caso.lembrancas.forEach { lembranca -> append(lembranca.texto).append(' ') }
    }

    private companion object {
        /**
         * Termos que contrariam as regras editoriais: violência, punição,
         * linguagem de fracasso, alegações médicas e marcas reais.
         */
        val TERMOS_PROIBIDOS = listOf(
            "fifa", "cbf", "conmebol", "uefa",
            "copa do mundo", "copa do brasil", "copa américa",
            "libertadores", "champions", "brasileirão", "mundial de clubes",
            "morte", "morreu", "sangue", "arma", "faca", "tiro",
            "game over", "você perdeu", "derrota", "fracasso", "punição",
            "demência", "alzheimer", "tratamento", "terapia", "cura",
        )
    }
}

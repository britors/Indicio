package br.com.w3ti.indicio.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import br.com.w3ti.indicio.data.banco.BancoIndicio
import br.com.w3ti.indicio.data.banco.RepositorioProgressoRoom
import br.com.w3ti.indicio.data.banco.RepositorioDicasRoom
import br.com.w3ti.indicio.data.caso.FonteCasos
import br.com.w3ti.indicio.data.caso.FonteCasosAssets
import br.com.w3ti.indicio.data.caso.RepositorioCasosJson
import br.com.w3ti.indicio.data.local.RepositorioIdentidadeLocal
import br.com.w3ti.indicio.data.narracao.NarradorTextToSpeech
import br.com.w3ti.indicio.data.preferencias.RepositorioPreferenciasDataStore
import br.com.w3ti.indicio.domain.armazenamento.RepositorioPreferencias
import br.com.w3ti.indicio.domain.armazenamento.RepositorioProgresso
import br.com.w3ti.indicio.domain.dica.RepositorioDicas
import br.com.w3ti.indicio.application.caso.ObterCasoParaContinuar
import br.com.w3ti.indicio.domain.caso.RepositorioCasos
import br.com.w3ti.indicio.domain.narracao.Narrador
import br.com.w3ti.indicio.domain.repositorio.RepositorioIdentidade
import br.com.w3ti.indicio.domain.validacao.ValidadorCaso

/**
 * Contrato de dependências do aplicativo.
 *
 * Substitui um framework de DI: cada nova dependência entra aqui como
 * propriedade, e os testes implementam a interface com dublês.
 */
interface ContainerAplicacao {
    val repositorioIdentidade: RepositorioIdentidade
    val repositorioCasos: RepositorioCasos
    val repositorioProgresso: RepositorioProgresso
    val repositorioPreferencias: RepositorioPreferencias
    val repositorioDicas: RepositorioDicas
    val obterCasoParaContinuar: ObterCasoParaContinuar

    /**
     * Cria um narrador novo.
     *
     * Não é propriedade: o mecanismo de voz tem ciclo de vida próprio e precisa
     * ser encerrado junto com quem o usa, e não junto com o aplicativo.
     */
    fun criarNarrador(): Narrador
}

/**
 * Implementação de produção. As dependências são criadas sob demanda para que
 * a inicialização do aplicativo permaneça barata — em especial o banco, que só
 * é aberto quando alguém realmente precisa dele.
 */
class ContainerAplicacaoPadrao(
    private val context: Context,
) : ContainerAplicacao {

    override val repositorioIdentidade: RepositorioIdentidade by lazy {
        RepositorioIdentidadeLocal(context)
    }

    private val fonteCasos: FonteCasos by lazy {
        FonteCasosAssets(context.assets)
    }

    override val repositorioCasos: RepositorioCasos by lazy {
        RepositorioCasosJson(fonte = fonteCasos, validador = ValidadorCaso())
    }

    private val banco: BancoIndicio by lazy { BancoIndicio.criar(context) }

    override val repositorioProgresso: RepositorioProgresso by lazy {
        RepositorioProgressoRoom(banco.progressoDao(), banco.conclusaoDao())
    }

    override val repositorioDicas: RepositorioDicas by lazy {
        RepositorioDicasRoom(banco.dicaDao())
    }

    private val dataStore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(ARQUIVO_PREFERENCIAS)
        }
    }

    override val repositorioPreferencias: RepositorioPreferencias by lazy {
        RepositorioPreferenciasDataStore(dataStore)
    }

    override val obterCasoParaContinuar: ObterCasoParaContinuar by lazy {
        ObterCasoParaContinuar(repositorioCasos, repositorioProgresso)
    }

    override fun criarNarrador(): Narrador = NarradorTextToSpeech(context)

    private companion object {
        const val ARQUIVO_PREFERENCIAS = "preferencias"
    }
}

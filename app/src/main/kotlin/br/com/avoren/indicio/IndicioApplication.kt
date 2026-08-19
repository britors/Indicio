package br.com.avoren.indicio

import android.app.Application
import br.com.avoren.indicio.di.ContainerAplicacao
import br.com.avoren.indicio.di.ContainerAplicacaoPadrao

/**
 * Ponto único de composição do aplicativo.
 *
 * A injeção de dependências é manual e propositalmente simples: o container é
 * uma propriedade substituível, de modo que testes instrumentados possam trocar
 * as implementações reais por dublês antes da primeira Activity subir.
 */
class IndicioApplication : Application() {

    lateinit var container: ContainerAplicacao
        private set

    override fun onCreate() {
        super.onCreate()
        container = ContainerAplicacaoPadrao(applicationContext)
    }

    /** Permite que testes instalem um container alternativo. */
    fun substituirContainer(novo: ContainerAplicacao) {
        container = novo
    }
}

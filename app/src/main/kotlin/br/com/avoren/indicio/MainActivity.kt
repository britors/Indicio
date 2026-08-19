package br.com.avoren.indicio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.avoren.indicio.navegacao.AppIndicio

/**
 * Activity única do aplicativo. Toda a navegação acontece em Compose.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as IndicioApplication).container

        setContent {
            AppIndicio(container = container)
        }
    }
}

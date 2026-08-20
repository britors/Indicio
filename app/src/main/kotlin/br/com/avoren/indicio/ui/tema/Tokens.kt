package br.com.avoren.indicio.ui.tema

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/** Medidas compartilhadas pela linguagem visual do aplicativo. */
object EspacamentoIndicio {
    val minimo = 4.dp
    val pequeno = 8.dp
    val medio = 12.dp
    val padrao = 16.dp
    val grande = 20.dp
    val extraGrande = 24.dp
    val destaque = 32.dp
    val margemDaTela = 20.dp
}

/** Formas com função consistente entre telas e componentes. */
object FormasIndicio {
    val pequena = RoundedCornerShape(10.dp)
    val controle = RoundedCornerShape(16.dp)
    val cartao = RoundedCornerShape(28.dp)
    val pilula = RoundedCornerShape(50)
}

/** Elevações discretas; hierarquia vem principalmente de cor e espaço. */
object ElevacaoIndicio {
    val cartao = 1.dp
    val controle = 0.dp
}

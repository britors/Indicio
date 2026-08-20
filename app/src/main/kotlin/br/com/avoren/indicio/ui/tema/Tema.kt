package br.com.avoren.indicio.ui.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import br.com.avoren.indicio.domain.model.preferencias.Preferencias
import br.com.avoren.indicio.domain.model.preferencias.TamanhoTexto

/** Altura mínima de botões de ação e de escolha exigida pelo produto. */
val AlturaMinimaBotao = 64.dp

/**
 * Indica se a interface deve evitar animações.
 *
 * Exposto como CompositionLocal porque a preferência atravessa todas as telas
 * e não faz parte do estado de nenhuma delas.
 */
val LocalReducaoDeMovimentos = staticCompositionLocalOf { false }

internal val EsquemaClaro = lightColorScheme(
    primary = AzulMarinho,
    onPrimary = Branco,
    primaryContainer = AzulMarinhoClaro,
    onPrimaryContainer = Branco,
    secondary = DouradoEnvelhecido,
    onSecondary = Branco,
    secondaryContainer = DouradoClaro,
    onSecondaryContainer = TintaSepia,
    background = PapelCreme,
    onBackground = TintaSepia,
    surface = PapelClaro,
    onSurface = TintaSepia,
    surfaceVariant = PapelSepia,
    onSurfaceVariant = TintaSuave,
    // Os dois contornos usam o tom acessível: `OutlinedButton` desta versão do
    // Material tira a borda de `outlineVariant`, e não de `outline`. Deixar
    // `outlineVariant` sem definir fazia o padrão do Material (#CAC4D0) vazar
    // para a borda dos botões, a 1,58:1 — contorno de controle praticamente
    // invisível. Filetes decorativos usam `BordaSuave` explicitamente.
    outline = BordaSepia,
    outlineVariant = BordaSepia,
    error = VermelhoDiscreto,
    onError = Branco,
)

/**
 * Tema do aplicativo.
 *
 * O produto define uma única aparência clara em papel/sépia; o modo escuro do
 * sistema não altera as cores. Cores dinâmicas do Material You não são usadas,
 * para preservar contraste e identidade visual previsíveis.
 */
@Composable
fun TemaIndicio(
    preferencias: Preferencias = Preferencias(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalReducaoDeMovimentos provides preferencias.reduzirMovimentos) {
        MaterialTheme(
            colorScheme = EsquemaClaro,
            shapes = Shapes(
                small = FormasIndicio.pequena,
                medium = FormasIndicio.controle,
                large = FormasIndicio.cartao,
                extraLarge = FormasIndicio.pilula,
            ),
            typography = tipografiaIndicio(preferencias.tamanhoTexto),
            content = content,
        )
    }
}

/** Atalho para prévias e testes que não dependem de preferências. */
@Composable
fun TemaIndicio(
    tamanhoTexto: TamanhoTexto,
    content: @Composable () -> Unit,
) = TemaIndicio(Preferencias(tamanhoTexto = tamanhoTexto), content)

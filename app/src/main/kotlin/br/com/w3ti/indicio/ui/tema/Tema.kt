package br.com.w3ti.indicio.ui.tema

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import br.com.w3ti.indicio.domain.model.preferencias.Preferencias
import br.com.w3ti.indicio.domain.model.preferencias.TamanhoTexto

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
    primary = AzulGaleria,
    onPrimary = Branco,
    primaryContainer = AzulGaleriaClaro,
    onPrimaryContainer = Branco,
    secondary = VerdeConservacao,
    onSecondary = Branco,
    secondaryContainer = VerdeConservacaoClaro,
    onSecondaryContainer = TintaPrincipal,
    tertiary = VerdeConservacaoClaro,
    onTertiary = TintaPrincipal,
    tertiaryContainer = SuperficieSuave,
    onTertiaryContainer = TintaPrincipal,
    background = FundoGaleria,
    onBackground = TintaPrincipal,
    surface = SuperficieClara,
    onSurface = TintaPrincipal,
    surfaceVariant = SuperficieSuave,
    onSurfaceVariant = TintaSuave,
    surfaceDim = SuperficieClaraEscurecida,
    surfaceBright = SuperficieClaraIluminada,
    surfaceContainerLowest = SuperficieClara,
    surfaceContainerLow = SuperficieClaraBaixa,
    surfaceContainer = SuperficieClaraMedia,
    surfaceContainerHigh = SuperficieClaraAlta,
    surfaceContainerHighest = SuperficieClaraMaxima,
    inverseSurface = SuperficieInversaClara,
    inverseOnSurface = TintaInversaClara,
    inversePrimary = AzulBruma,
    surfaceTint = AzulGaleria,
    // Os dois contornos usam o tom acessível. Filetes decorativos derivam de
    // `outline` com transparência, enquanto controles secundários usam relevo tonal.
    outline = BordaControleClara,
    outlineVariant = BordaControleClara,
    error = VermelhoDiscreto,
    onError = Branco,
)

internal val EsquemaEscuro = darkColorScheme(
    primary = AzulBruma,
    onPrimary = TintaAzul,
    primaryContainer = AzulPainelNoturno,
    onPrimaryContainer = TintaNoturna,
    secondary = VerdeNoturno,
    onSecondary = TintaSecundariaNoturna,
    secondaryContainer = AzulSecundarioPainelNoturno,
    onSecondaryContainer = AzulClaroNoturno,
    tertiary = VerdeNoturno,
    onTertiary = TintaSecundariaNoturna,
    tertiaryContainer = AzulSecundarioPainelNoturno,
    onTertiaryContainer = AzulClaroNoturno,
    background = FundoNoturno,
    onBackground = TintaNoturna,
    surface = SuperficieNoturna,
    onSurface = TintaNoturna,
    surfaceVariant = SuperficieNoturnaSuave,
    onSurfaceVariant = TintaNoturnaSuave,
    surfaceDim = SuperficieNoturnaEscurecida,
    surfaceBright = SuperficieNoturnaIluminada,
    surfaceContainerLowest = SuperficieNoturnaMinima,
    surfaceContainerLow = SuperficieNoturnaBaixa,
    surfaceContainer = SuperficieNoturnaMedia,
    surfaceContainerHigh = SuperficieNoturnaAlta,
    surfaceContainerHighest = SuperficieNoturnaMaxima,
    inverseSurface = SuperficieInversaNoturna,
    inverseOnSurface = TintaInversaNoturna,
    inversePrimary = AzulGaleria,
    surfaceTint = AzulBruma,
    outline = BordaControleEscura,
    outlineVariant = BordaControleEscura,
    error = VermelhoNoturno,
    onError = TintaErroNoturna,
)

/**
 * Tema do aplicativo.
 *
 * As aparências clara e escura preservam a identidade de galeria contemporânea
 * e seguem o modo escolhido pelo usuário no Android. Cores dinâmicas do Material
 * You não são usadas, para manter contraste e identidade visual previsíveis.
 */
@Composable
fun TemaIndicio(
    preferencias: Preferencias = Preferencias(),
    content: @Composable () -> Unit,
) {
    val escuro = isSystemInDarkTheme()
    val esquema = if (escuro) EsquemaEscuro else EsquemaClaro
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !escuro
                isAppearanceLightNavigationBars = !escuro
            }
        }
    }

    CompositionLocalProvider(LocalReducaoDeMovimentos provides preferencias.reduzirMovimentos) {
        MaterialTheme(
            colorScheme = esquema,
            shapes = Shapes(
                small = FormasIndicio.pequena,
                medium = FormasIndicio.controle,
                large = FormasIndicio.cartao,
                extraLarge = FormasIndicio.cartao,
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

package com.example.myapplication.presentation.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val SportsDarkColorScheme = darkColorScheme(
    // Primary: Neon Green
    primary = NeonGreen,
    onPrimary = DarkOnPrimary,
    primaryContainer = NeonGreenContainer,
    onPrimaryContainer = OnNeonGreenContainer,

    // Secondary: Cyan
    secondary = Cyan,
    onSecondary = DarkOnPrimary,
    secondaryContainer = CyanContainer,
    onSecondaryContainer = OnCyanContainer,

    // Tertiary: Electric Blue
    tertiary = ElectricBlue,
    onTertiary = DarkOnPrimary,
    tertiaryContainer = ElectricBlueContainer,
    onTertiaryContainer = OnElectricBlueContainer,

    // Background & Surface
    background = DarkBackground,
    onBackground = LightText,
    surface = DarkSurface,
    onSurface = LightText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,

    // Error
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,

    // Outline
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,

    // Inverse
    inverseSurface = LightText,
    inverseOnSurface = DarkBackground,
    inversePrimary = NeonGreenDark,

    // Surface tint
    surfaceTint = NeonGreen
)

// ===== Shape System =====
val SportShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// ===== Gradient Presets =====
val NeonGradient = Brush.horizontalGradient(
    colors = listOf(NeonGreen, Cyan)
)

val NeonGradientVertical = Brush.verticalGradient(
    colors = listOf(NeonGreen, Cyan)
)

val CoolGradient = Brush.horizontalGradient(
    colors = listOf(Cyan, ElectricBlue)
)

val CoolGradientVertical = Brush.verticalGradient(
    colors = listOf(Cyan, ElectricBlue)
)

val FireGradient = Brush.horizontalGradient(
    colors = listOf(SportAmber, ErrorRed)
)

val SurfaceGradient = Brush.verticalGradient(
    colors = listOf(DarkSurface, DarkSurfaceVariant)
)

val TopBarGradient = Brush.horizontalGradient(
    colors = listOf(DarkSurface, DarkSurfaceVariant, DarkSurface)
)

val CardGlowBorder = Brush.horizontalGradient(
    colors = listOf(
        NeonGreen.copy(alpha = 0.5f),
        Cyan.copy(alpha = 0.3f),
        ElectricBlue.copy(alpha = 0.5f)
    )
)

val CardGlowBorderSubtle = Brush.horizontalGradient(
    colors = listOf(
        NeonGreen.copy(alpha = 0.2f),
        Cyan.copy(alpha = 0.15f),
        ElectricBlue.copy(alpha = 0.2f)
    )
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = SportsDarkColorScheme

    // Dark status bar & navigation bar
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkBackground.toArgb()
            window.navigationBarColor = DarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = SportShapes,
        content = content
    )
}
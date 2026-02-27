package com.example.login.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Paleta de colores BeatTreat - Material Design 3 ──
object BeatTreatColors {
    // Primarios
    val Purple80        = Color(0xFFD0BCFF)
    val Purple60        = Color(0xFF9B30D9)
    val Purple40        = Color(0xFF7C3AED)
    val PurpleDark      = Color(0xFF5B21B6)

    // Secundarios
    val PinkLight       = Color(0xFFFFB3C6)
    val Pink            = Color(0xFFEC4899)

    // Fondos
    val Background      = Color(0xFF0D0D0D)
    val Surface         = Color(0xFF1A1A2E)
    val SurfaceVariant  = Color(0xFF2D2640)

    // Campos
    val FieldBackground = Color(0xFFEEEEEE)

    // Texto
    val OnPrimary       = Color(0xFFFFFFFF)
    val OnBackground    = Color(0xFFFFFFFF)
    val OnSurface       = Color(0xFFFFFFFF)
    val TextGray        = Color(0xFF888888)
    val TextDark        = Color(0xFF1A1A1A)

    // Estados
    val Error           = Color(0xFFCF6679)
    val Success         = Color(0xFF4CAF50)

    // Barra inferior
    val BottomBar       = Color(0xFF2A2A2A)
}

// ── Esquema de colores oscuro MD3 ──
private val DarkColorScheme = darkColorScheme(
    primary          = BeatTreatColors.Purple60,
    onPrimary        = BeatTreatColors.OnPrimary,
    primaryContainer = BeatTreatColors.PurpleDark,
    secondary        = BeatTreatColors.Pink,
    onSecondary      = BeatTreatColors.OnPrimary,
    background       = BeatTreatColors.Background,
    onBackground     = BeatTreatColors.OnBackground,
    surface          = BeatTreatColors.Surface,
    onSurface        = BeatTreatColors.OnSurface,
    surfaceVariant   = BeatTreatColors.SurfaceVariant,
    error            = BeatTreatColors.Error,
)

@Composable
fun BeatTreatTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}









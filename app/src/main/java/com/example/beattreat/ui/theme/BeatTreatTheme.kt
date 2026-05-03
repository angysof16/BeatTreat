package com.example.beattreat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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
        content     = content
    )
}
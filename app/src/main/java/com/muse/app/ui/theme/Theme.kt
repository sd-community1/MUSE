package com.muse.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// MUSE Noir palette
val MuseBlack       = Color(0xFF08090B)
val MuseBlackSurf   = Color(0xFF16181C)
val MuseBlackCard   = Color(0xFF1E2025)
val MuseGold        = Color(0xFFC9A961)
val MuseGoldDim     = Color(0xFF8A6A2E)
val MuseIvory       = Color(0xFFF5F1E8)
val MuseIvoryDim    = Color(0xFFA09888)
val MuseBorder      = Color(0xFF2A2620)
val MuseWhite       = Color(0xFFFFFFFF)
val MuseError       = Color(0xFFCF6679)

private val MuseDarkColorScheme = darkColorScheme(
    primary          = MuseGold,
    onPrimary        = MuseBlack,
    primaryContainer = MuseGoldDim,
    secondary        = MuseIvory,
    onSecondary      = MuseBlack,
    background       = MuseBlack,
    onBackground     = MuseIvory,
    surface          = MuseBlackSurf,
    onSurface        = MuseIvory,
    surfaceVariant   = MuseBlackCard,
    onSurfaceVariant = MuseIvoryDim,
    outline          = MuseBorder,
    error            = MuseError,
    onError          = MuseWhite
)

@Composable
fun MuseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MuseDarkColorScheme,
        content = content
    )
}

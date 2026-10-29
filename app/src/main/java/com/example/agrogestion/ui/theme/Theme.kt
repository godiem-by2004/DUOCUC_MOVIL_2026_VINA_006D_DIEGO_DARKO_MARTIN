package com.example.agrogestion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AgroPrimary,
    onPrimary = Color.White,
    primaryContainer = AgroMintContainer,
    onPrimaryContainer = AgroPrimary,
    secondary = AgroSecondary,
    onSecondary = Color.White,
    secondaryContainer = AgroSurfaceVariant,
    onSecondaryContainer = AgroTextPrimary,
    tertiary = AgroTertiary,
    onTertiary = Color.White,
    background = AgroNeutralBg,
    onBackground = AgroTextPrimary,
    surface = AgroSurfaceCard,
    onSurface = AgroTextPrimary,
    surfaceVariant = AgroSurfaceVariant,
    onSurfaceVariant = AgroTextSecondary,
    error = AgroWarningRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = AgroSecondary,
    onPrimary = AgroTextPrimary,
    primaryContainer = AgroPrimary,
    onPrimaryContainer = Color.White,
    secondary = AgroMintLight,
    tertiary = AgroTertiary,
    background = Color(0xFF121A14),
    onBackground = Color(0xFFE8ECE2),
    surface = Color(0xFF1B261E),
    onSurface = Color(0xFFE8ECE2),
    surfaceVariant = Color(0xFF26352A),
    onSurfaceVariant = Color(0xFFB7C4B9),
    error = Color(0xFFEF5350)
)

@Composable
fun AgroGestionTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

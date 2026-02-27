package com.example.calitech.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * CaliTech brand colors — vibrant teal/cyan palette for a premium, modern feel.
 */
private val PrimaryColor = Color(0xFF00BFA6)
private val PrimaryVariant = Color(0xFF00897B)
private val SecondaryColor = Color(0xFF7C4DFF)
private val BackgroundDark = Color(0xFF121212)
private val SurfaceDark = Color(0xFF1E1E2E)
private val OnPrimaryColor = Color.White
private val OnBackgroundDark = Color(0xFFE0E0E0)
private val ErrorColor = Color(0xFFCF6679)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = OnPrimaryColor,
    primaryContainer = PrimaryVariant,
    secondary = SecondaryColor,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = OnBackgroundDark,
    onSurface = OnBackgroundDark,
    error = ErrorColor
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = OnPrimaryColor,
    primaryContainer = Color(0xFFB2DFDB),
    secondary = SecondaryColor,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
    error = Color(0xFFB00020)
)

/**
 * CaliTech application theme.
 *
 * Uses a dark color scheme by default, consistent with modern fitness/ML apps.
 */
@Composable
fun CaliTechTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

package com.app_muslim.surah_yasin.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Islamic Color Palette
val IslamicGreen = Color(0xFF1B4332)
val IslamicGold = Color(0xFFD4AF37)
val IslamicCream = Color(0xFFF5F5DC)
val IslamicDarkGreen = Color(0xFF0F2419)
val IslamicLightGreen = Color(0xFF2D5D47)

private val LightColorScheme = lightColorScheme(
    primary = IslamicGreen,
    onPrimary = Color.White,
    primaryContainer = IslamicLightGreen,
    onPrimaryContainer = Color.White,
    secondary = IslamicGold,
    onSecondary = Color.Black,
    tertiary = IslamicCream,
    surface = Color.White,
    onSurface = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surfaceVariant = IslamicCream,
    onSurfaceVariant = Color.Black,
    outline = IslamicGreen.copy(alpha = 0.3f)
)

private val DarkColorScheme = darkColorScheme(
    primary = IslamicGreen,
    onPrimary = Color.White,
    primaryContainer = IslamicDarkGreen,
    onPrimaryContainer = Color.White,
    secondary = IslamicGold,
    onSecondary = Color.Black,
    tertiary = IslamicCream,
    surface = Color(0xFF1C1C1C),
    onSurface = Color.White,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color.White,
    outline = IslamicGreen.copy(alpha = 0.5f)
)

@Composable
fun TahlilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = TahlilTypography,
        content = content
    )
}
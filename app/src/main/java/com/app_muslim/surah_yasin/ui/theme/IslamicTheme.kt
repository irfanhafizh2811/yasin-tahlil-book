package com.app_muslim.surah_yasin.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun IslamicTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val islamicColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF2D7237), // Dark Islamic Green
            onPrimary = Color.White,
            secondary = Color(0xFFE6C547), // Softer Islamic Gold
            onSecondary = Color.Black,
            tertiary = Color(0xFF1A1A1A), // Dark Islamic Background
            surface = Color(0xFF1E1E1E),
            onSurface = Color.White,
            background = Color(0xFF121212),
            onBackground = Color.White
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF1B4332), // Islamic Green
            onPrimary = Color.White,
            secondary = Color(0xFFD4AF37), // Islamic Gold
            onSecondary = Color.Black,
            tertiary = Color(0xFFF5F5DC), // Islamic Cream
            surface = Color.White,
            onSurface = Color.Black,
            background = Color.White,
            onBackground = Color.Black
        )
    }
    
    MaterialTheme(
        colorScheme = islamicColorScheme,
        content = content
    )
}
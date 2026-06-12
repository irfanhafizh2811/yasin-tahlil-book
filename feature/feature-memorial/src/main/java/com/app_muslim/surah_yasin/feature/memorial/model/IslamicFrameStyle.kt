package com.app_muslim.surah_yasin.feature.memorial.model

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize

enum class IslamicFrameStyle(
    val id: String,
    val displayName: String,
    val displayNameArabic: String,
    val description: String,
    val colorScheme: FrameColorScheme,
    val complexity: FrameComplexity
) {
    NONE(
        id = "none",
        displayName = "No Frame",
        displayNameArabic = "بدون إطار",
        description = "Clean, simple presentation",
        colorScheme = FrameColorScheme.TRANSPARENT,
        complexity = FrameComplexity.MINIMAL
    ),
    
    CLASSIC_GOLD(
        id = "classic_gold",
        displayName = "Classic Gold",
        displayNameArabic = "ذهبي كلاسيكي",
        description = "Traditional gold ornamental frame",
        colorScheme = FrameColorScheme.GOLD,
        complexity = FrameComplexity.MODERATE
    ),
    
    GEOMETRIC_SILVER(
        id = "geometric_silver",
        displayName = "Geometric Silver",
        displayNameArabic = "فضي هندسي",
        description = "Islamic geometric patterns in silver",
        colorScheme = FrameColorScheme.SILVER,
        complexity = FrameComplexity.COMPLEX
    ),
    
    CALLIGRAPHY_BORDER(
        id = "calligraphy_border",
        displayName = "Calligraphy Border",
        displayNameArabic = "إطار خطي",
        description = "Beautiful Arabic calligraphy border",
        colorScheme = FrameColorScheme.DARK,
        complexity = FrameComplexity.COMPLEX
    ),
    
    MOSQUE_ARCH(
        id = "mosque_arch",
        displayName = "Mosque Arch",
        displayNameArabic = "قوس المسجد",
        description = "Elegant mosque architecture frame",
        colorScheme = FrameColorScheme.WARM,
        complexity = FrameComplexity.MODERATE
    ),
    
    FLORAL_PATTERN(
        id = "floral_pattern",
        displayName = "Floral Pattern",
        displayNameArabic = "نقوش نباتية",
        description = "Traditional Islamic floral designs",
        colorScheme = FrameColorScheme.NATURAL,
        complexity = FrameComplexity.MODERATE
    ),
    
    MINIMALIST_MODERN(
        id = "minimalist_modern",
        displayName = "Modern Minimal",
        displayNameArabic = "حديث بسيط",
        description = "Contemporary minimalist design",
        colorScheme = FrameColorScheme.NEUTRAL,
        complexity = FrameComplexity.MINIMAL
    ),
    
    ROYAL_ORNATE(
        id = "royal_ornate",
        displayName = "Royal Ornate",
        displayNameArabic = "ملكي مزخرف",
        description = "Elaborate royal palace inspired frame",
        colorScheme = FrameColorScheme.ROYAL,
        complexity = FrameComplexity.ELABORATE
    )
}

@Parcelize
data class FrameColorScheme(
    val primary: Long,
    val secondary: Long,
    val accent: Long,
    val background: Long = 0x00000000 // Transparent
) : Parcelable {
    
    companion object {
        val TRANSPARENT = FrameColorScheme(
            primary = 0x00000000,
            secondary = 0x00000000,
            accent = 0x00000000
        )
        
        val GOLD = FrameColorScheme(
            primary = 0xFFFFD700,
            secondary = 0xFFFFA500,
            accent = 0xFFB8860B
        )
        
        val SILVER = FrameColorScheme(
            primary = 0xFFC0C0C0,
            secondary = 0xFFDDDDDD,
            accent = 0xFF696969
        )
        
        val DARK = FrameColorScheme(
            primary = 0xFF2C2C2C,
            secondary = 0xFF1C1C1C,
            accent = 0xFF4A4A4A
        )
        
        val WARM = FrameColorScheme(
            primary = 0xFFD2691E,
            secondary = 0xFFCD853F,
            accent = 0xFFA0522D
        )
        
        val NATURAL = FrameColorScheme(
            primary = 0xFF8FBC8F,
            secondary = 0xFF98FB98,
            accent = 0xFF006400
        )
        
        val NEUTRAL = FrameColorScheme(
            primary = 0xFF808080,
            secondary = 0xFFA9A9A9,
            accent = 0xFF2F4F4F
        )
        
        val ROYAL = FrameColorScheme(
            primary = 0xFF4B0082,
            secondary = 0xFF8A2BE2,
            accent = 0xFFFFD700
        )
    }
}

enum class FrameComplexity(val displayName: String, val strokeCount: Int) {
    MINIMAL("Simple", 1),
    MODERATE("Detailed", 3),
    COMPLEX("Intricate", 5),
    ELABORATE("Ornate", 8)
}
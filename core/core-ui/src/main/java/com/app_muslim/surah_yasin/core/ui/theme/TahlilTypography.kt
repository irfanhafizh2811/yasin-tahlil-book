package com.app_muslim.surah_yasin.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.app_muslim.surah_yasin.core.ui.R

// Font Families - enhanced device compatibility with fallback strategy
val LpmqIsepMisbahFontFamily = try {
    FontFamily(
        Font(
            resId = R.font.font_lpmq_isep_misbah,
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    )
} catch (e: Exception) {
    // Fallback to system serif font for Arabic text readability
    FontFamily.Serif
}

val InterFontFamily = try {
    FontFamily(
        Font(
            resId = R.font.font_inter_reg, 
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        ),
        Font(
            resId = R.font.font_inter_bold, 
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )
} catch (e: Exception) {
    // Fallback to system sans-serif for UI text
    FontFamily.SansSerif
}

// Islamic Typography with system font fallback
val TahlilTypography = Typography(
    // Arabic text styles (for Quranic verses and prayers)
    headlineLarge = TextStyle(
        fontFamily = LpmqIsepMisbahFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 48.sp,
        letterSpacing = 0.8.sp,
        textAlign = TextAlign.End
    ),
    headlineMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 42.sp,
        letterSpacing = 0.6.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Latin text styles (for UI and translations)
    titleLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body text
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Labels
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// Islamic-specific text styles with enhanced fallback support
object IslamicTextStyles {
    // Primary Arabic prayer text with device-safe font loading
    val ArabicPrayerLarge = TextStyle(
        fontFamily = LpmqIsepMisbahFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.8.sp,
        textAlign = TextAlign.End
    )
    
    val ArabicPrayerMedium = TextStyle(
        fontFamily = LpmqIsepMisbahFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.6.sp,
        textAlign = TextAlign.End
    )
    
    val ArabicPrayerSmall = TextStyle(
        fontFamily = LpmqIsepMisbahFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.4.sp,
        textAlign = TextAlign.End
    )
    
    // Transliteration styles with device-safe fonts
    val Transliteration = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.3.sp,
        textAlign = TextAlign.Center
    )
    
    // Translation styles with system font fallback
    val Translation = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.2.sp,
        textAlign = TextAlign.Center
    )
    
    // Decorative Arabic headers with enhanced fallback
    val ArabicHeader = TextStyle(
        fontFamily = LpmqIsepMisbahFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 42.sp,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center
    )
}
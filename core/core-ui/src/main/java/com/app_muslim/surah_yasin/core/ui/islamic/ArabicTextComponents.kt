package com.app_muslim.surah_yasin.core.ui.islamic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app_muslim.surah_yasin.core.ui.theme.IslamicTextStyles

/**
 * Arabic prayer text component with proper RTL support and Islamic typography
 */
@Composable
fun ArabicPrayerText(
    arabicText: String,
    modifier: Modifier = Modifier,
    style: TextStyle = IslamicTextStyles.ArabicPrayerMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = Int.MAX_VALUE,
    accessibilityLabel: String? = null,
    enableSelection: Boolean = true
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val content = @Composable {
            Text(
                text = arabicText,
                modifier = modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = accessibilityLabel ?: "Arabic prayer text: $arabicText"
                    },
                style = style.copy(
                    color = color,
                    textDirection = TextDirection.Rtl,
                    textAlign = TextAlign.End
                ),
                maxLines = maxLines,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        
        if (enableSelection) {
            SelectionContainer {
                content()
            }
        } else {
            content()
        }
    }
}

/**
 * Transliteration text component for Arabic pronunciation guide
 */
@Composable
fun TransliterationText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = IslamicTextStyles.Transliteration,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    maxLines: Int = Int.MAX_VALUE,
    accessibilityLabel: String? = null
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = accessibilityLabel ?: "Pronunciation guide: $text"
            },
        style = style.copy(color = color),
        maxLines = maxLines,
        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
    )
}

/**
 * Translation text component for Arabic text translations
 */
@Composable
fun TranslationText(
    text: String,
    language: String = "en",
    modifier: Modifier = Modifier,
    style: TextStyle = IslamicTextStyles.Translation,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = Int.MAX_VALUE,
    accessibilityLabel: String? = null
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = accessibilityLabel ?: "Translation in $language: $text"
            },
        style = style.copy(color = color),
        maxLines = maxLines,
        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
    )
}

/**
 * Complete prayer display showing Arabic, transliteration, and translation
 */
@Composable
fun CompletePrayerDisplay(
    arabicText: String,
    transliteration: String? = null,
    translation: String? = null,
    language: String = "en",
    modifier: Modifier = Modifier,
    arabicTextStyle: TextStyle = IslamicTextStyles.ArabicPrayerMedium,
    showTransliteration: Boolean = true,
    showTranslation: Boolean = true,
    spacing: Dp = 12.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            // Arabic Text
            ArabicPrayerText(
                arabicText = arabicText,
                style = arabicTextStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Transliteration
            if (showTransliteration && transliteration != null) {
                TransliterationText(
                    text = transliteration,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Translation
            if (showTranslation && translation != null) {
                TranslationText(
                    text = translation,
                    language = language,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Scalable Arabic text with accessibility support
 */
@Composable
fun ScalableArabicText(
    arabicText: String,
    modifier: Modifier = Modifier,
    baseStyle: TextStyle = IslamicTextStyles.ArabicPrayerMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    scaleFactor: Float = 1f,
    maxLines: Int = Int.MAX_VALUE,
    accessibilityLabel: String? = null
) {
    val scaledStyle = baseStyle.copy(
        fontSize = baseStyle.fontSize * scaleFactor,
        lineHeight = baseStyle.lineHeight * scaleFactor
    )
    
    ArabicPrayerText(
        arabicText = arabicText,
        modifier = modifier,
        style = scaledStyle,
        color = color,
        maxLines = maxLines,
        accessibilityLabel = accessibilityLabel
    )
}

/**
 * Arabic header text for Islamic sections
 */
@Composable
fun ArabicHeaderText(
    arabicText: String,
    modifier: Modifier = Modifier,
    style: TextStyle = IslamicTextStyles.ArabicHeader,
    color: Color = MaterialTheme.colorScheme.primary,
    accessibilityLabel: String? = null
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Text(
            text = arabicText,
            modifier = modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = accessibilityLabel ?: "Arabic header: $arabicText"
                },
            style = style.copy(
                color = color,
                textDirection = TextDirection.Rtl,
                textAlign = TextAlign.Center
            )
        )
    }
}

/**
 * Responsive text size based on screen size
 */
@Composable
fun ResponsiveArabicText(
    arabicText: String,
    modifier: Modifier = Modifier,
    baseSize: Int = 20, // Base font size in sp
    color: Color = MaterialTheme.colorScheme.onSurface,
    accessibilityLabel: String? = null
) {
    // Simple responsive scaling - can be enhanced with WindowSizeClass
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    
    val scaleFactor = when {
        screenWidth < 400 -> 0.9f
        screenWidth > 600 -> 1.2f
        else -> 1f
    }
    
    val responsiveStyle = IslamicTextStyles.ArabicPrayerMedium.copy(
        fontSize = (baseSize * scaleFactor).sp,
        lineHeight = (baseSize * scaleFactor * 1.8f).sp
    )
    
    ArabicPrayerText(
        arabicText = arabicText,
        modifier = modifier,
        style = responsiveStyle,
        color = color,
        accessibilityLabel = accessibilityLabel
    )
}

/**
 * Prayer text with accessibility enhancements
 */
@Composable
fun AccessiblePrayerText(
    arabicText: String,
    transliteration: String? = null,
    translation: String? = null,
    modifier: Modifier = Modifier,
    enableTalkBack: Boolean = true,
    enableHapticFeedback: Boolean = true
) {
    val accessibilityDescription = buildString {
        append("Prayer text. ")
        append("Arabic: $arabicText. ")
        if (transliteration != null) {
            append("Pronunciation: $transliteration. ")
        }
        if (translation != null) {
            append("Meaning: $translation. ")
        }
    }
    
    CompletePrayerDisplay(
        arabicText = arabicText,
        transliteration = transliteration,
        translation = translation,
        modifier = modifier.semantics {
            if (enableTalkBack) {
                contentDescription = accessibilityDescription
            }
        }
    )
}
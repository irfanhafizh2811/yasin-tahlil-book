package com.app_muslim.surah_yasin.core.ui.islamic

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app_muslim.surah_yasin.core.ui.theme.IslamicTextStyles

/**
 * Accessibility-enhanced text scaling component for Islamic content
 */
@Composable
fun ScalableIslamicText(
    arabicText: String,
    transliteration: String? = null,
    translation: String? = null,
    modifier: Modifier = Modifier,
    baseArabicSize: Int = 20,
    baseTransliterationSize: Int = 16,
    baseTranslationSize: Int = 15,
    userScaleFactor: Float = 1f,
    systemScaleFactor: Float = 1f,
    enableDynamicType: Boolean = true,
    maxScaleFactor: Float = 3f,
    minScaleFactor: Float = 0.5f
) {
    // Combine user preference and system accessibility scaling
    val totalScaleFactor = (userScaleFactor * if (enableDynamicType) systemScaleFactor else 1f)
        .coerceIn(minScaleFactor, maxScaleFactor)
    
    val arabicStyle = IslamicTextStyles.ArabicPrayerMedium.copy(
        fontSize = (baseArabicSize * totalScaleFactor).sp,
        lineHeight = (baseArabicSize * totalScaleFactor * 1.8f).sp
    )
    
    val transliterationStyle = IslamicTextStyles.Transliteration.copy(
        fontSize = (baseTransliterationSize * totalScaleFactor).sp,
        lineHeight = (baseTransliterationSize * totalScaleFactor * 1.6f).sp
    )
    
    val translationStyle = IslamicTextStyles.Translation.copy(
        fontSize = (baseTranslationSize * totalScaleFactor).sp,
        lineHeight = (baseTranslationSize * totalScaleFactor * 1.5f).sp
    )
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = buildAccessibilityDescription(
                    arabicText = arabicText,
                    transliteration = transliteration,
                    translation = translation
                )
            },
        verticalArrangement = Arrangement.spacedBy((12 * totalScaleFactor).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Arabic Text
        ArabicPrayerText(
            arabicText = arabicText,
            style = arabicStyle,
            accessibilityLabel = "Arabic prayer text: $arabicText"
        )
        
        // Transliteration
        if (transliteration != null) {
            TransliterationText(
                text = transliteration,
                style = transliterationStyle,
                accessibilityLabel = "Pronunciation guide: $transliteration"
            )
        }
        
        // Translation
        if (translation != null) {
            TranslationText(
                text = translation,
                style = translationStyle,
                accessibilityLabel = "English translation: $translation"
            )
        }
    }
}

/**
 * Dynamic font size component that responds to user preferences
 */
@Composable
fun DynamicSizeText(
    text: String,
    isArabic: Boolean = false,
    modifier: Modifier = Modifier,
    baseStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    userPreference: FontSizePreference = FontSizePreference.Medium,
    respectSystemSettings: Boolean = true,
    maxLines: Int = Int.MAX_VALUE
) {
    val density = LocalDensity.current
    
    // Get system font scale (accessibility settings)
    val systemFontScale = if (respectSystemSettings) {
        density.fontScale
    } else {
        1f
    }
    
    // User preference multiplier
    val userMultiplier = when (userPreference) {
        FontSizePreference.ExtraSmall -> 0.75f
        FontSizePreference.Small -> 0.9f
        FontSizePreference.Medium -> 1f
        FontSizePreference.Large -> 1.2f
        FontSizePreference.ExtraLarge -> 1.5f
    }
    
    val finalMultiplier = userMultiplier * systemFontScale
    
    val adjustedStyle = if (isArabic) {
        IslamicTextStyles.ArabicPrayerMedium.copy(
            fontSize = baseStyle.fontSize * finalMultiplier,
            lineHeight = baseStyle.lineHeight * finalMultiplier,
            color = color
        )
    } else {
        baseStyle.copy(
            fontSize = baseStyle.fontSize * finalMultiplier,
            lineHeight = baseStyle.lineHeight * finalMultiplier,
            color = color
        )
    }
    
    if (isArabic) {
        ArabicPrayerText(
            arabicText = text,
            modifier = modifier,
            style = adjustedStyle,
            maxLines = maxLines
        )
    } else {
        BiDiText(
            text = text,
            modifier = modifier,
            style = adjustedStyle,
            maxLines = maxLines,
            isArabic = false
        )
    }
}

/**
 * High contrast text component for better visibility
 */
@Composable
fun HighContrastText(
    text: String,
    isArabic: Boolean = false,
    modifier: Modifier = Modifier,
    baseStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    useHighContrast: Boolean = false,
    backgroundColor: Color = MaterialTheme.colorScheme.surface
) {
    val textColor = if (useHighContrast) {
        if (isSystemInDarkTheme()) {
            Color.White
        } else {
            Color.Black
        }
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    val bgColor = if (useHighContrast) {
        if (isSystemInDarkTheme()) {
            Color.Black
        } else {
            Color.White
        }
    } else {
        backgroundColor
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        DynamicSizeText(
            text = text,
            isArabic = isArabic,
            modifier = Modifier.padding(16.dp),
            baseStyle = baseStyle,
            color = textColor
        )
    }
}

/**
 * Screen reader optimized component with detailed descriptions
 */
@Composable
fun ScreenReaderOptimizedPrayer(
    arabicText: String,
    transliteration: String? = null,
    translation: String? = null,
    source: String? = null,
    category: String? = null,
    modifier: Modifier = Modifier,
    enableDetailedDescriptions: Boolean = true
) {
    val detailedDescription = if (enableDetailedDescriptions) {
        buildDetailedAccessibilityDescription(
            arabicText = arabicText,
            transliteration = transliteration,
            translation = translation,
            source = source,
            category = category
        )
    } else {
        buildAccessibilityDescription(arabicText, transliteration, translation)
    }
    
    CompletePrayerDisplay(
        arabicText = arabicText,
        transliteration = transliteration,
        translation = translation,
        modifier = modifier.semantics {
            contentDescription = detailedDescription
            
            // Add custom actions for screen readers
            customActions = listOf(
                CustomAccessibilityAction("Read Arabic only") {
                    // This would trigger TTS for Arabic text only
                    true
                },
                CustomAccessibilityAction("Read translation only") {
                    // This would trigger TTS for translation only
                    true
                },
                CustomAccessibilityAction("Read pronunciation") {
                    // This would trigger TTS for transliteration
                    true
                }
            )
        }
    )
}

/**
 * Font size preference enumeration
 */
enum class FontSizePreference {
    ExtraSmall, Small, Medium, Large, ExtraLarge
}

/**
 * Text zoom component with controls
 */
@Composable
fun ZoomableTextDisplay(
    arabicText: String,
    transliteration: String? = null,
    translation: String? = null,
    modifier: Modifier = Modifier,
    initialZoom: Float = 1f,
    minZoom: Float = 0.5f,
    maxZoom: Float = 3f,
    showZoomControls: Boolean = true
) {
    var zoomLevel by remember { mutableStateOf(initialZoom) }
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showZoomControls) {
            ZoomControls(
                currentZoom = zoomLevel,
                onZoomChanged = { newZoom ->
                    zoomLevel = newZoom.coerceIn(minZoom, maxZoom)
                },
                minZoom = minZoom,
                maxZoom = maxZoom
            )
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy((12 * zoomLevel).dp)
            ) {
                ScalableIslamicText(
                    arabicText = arabicText,
                    transliteration = transliteration,
                    translation = translation,
                    userScaleFactor = zoomLevel
                )
            }
        }
    }
}

/**
 * Zoom controls component
 */
@Composable
fun ZoomControls(
    currentZoom: Float,
    onZoomChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    minZoom: Float = 0.5f,
    maxZoom: Float = 3f
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Text Size",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "${(currentZoom * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onZoomChanged((currentZoom - 0.1f).coerceAtLeast(minZoom)) },
                    enabled = currentZoom > minZoom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("A-")
                }
                
                Slider(
                    value = currentZoom,
                    onValueChange = onZoomChanged,
                    valueRange = minZoom..maxZoom,
                    modifier = Modifier.weight(3f)
                )
                
                OutlinedButton(
                    onClick = { onZoomChanged((currentZoom + 0.1f).coerceAtMost(maxZoom)) },
                    enabled = currentZoom < maxZoom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("A+")
                }
            }
            
            TextButton(
                onClick = { onZoomChanged(1f) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset to Default")
            }
        }
    }
}

/**
 * Utility function to build accessibility description
 */
private fun buildAccessibilityDescription(
    arabicText: String,
    transliteration: String?,
    translation: String?
): String {
    return buildString {
        append("Islamic prayer content. ")
        append("Arabic text: $arabicText. ")
        if (transliteration != null) {
            append("Pronunciation: $transliteration. ")
        }
        if (translation != null) {
            append("Translation: $translation. ")
        }
    }
}

/**
 * Utility function to build detailed accessibility description
 */
private fun buildDetailedAccessibilityDescription(
    arabicText: String,
    transliteration: String?,
    translation: String?,
    source: String?,
    category: String?
): String {
    return buildString {
        append("Islamic prayer content")
        if (category != null) {
            append(" from category: $category")
        }
        append(". ")
        
        append("This prayer contains Arabic text: $arabicText. ")
        
        if (transliteration != null) {
            append("Pronunciation guide available: $transliteration. ")
        }
        
        if (translation != null) {
            append("English translation: $translation. ")
        }
        
        if (source != null) {
            append("Source: $source. ")
        }
        
        append("You can interact with this content to read individual sections.")
    }
}
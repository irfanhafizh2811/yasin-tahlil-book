package com.app_muslim.surah_yasin.core.ui.islamic

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app_muslim.surah_yasin.core.ui.theme.IslamicTextStyles

/**
 * Data class for prayer content with multiple languages
 */
data class PrayerContent(
    val arabic: String,
    val transliteration: String? = null,
    val translations: Map<String, String> = emptyMap(),
    val source: String? = null,
    val category: String? = null
)

/**
 * Multi-language translation component with user preference support
 */
@Composable
fun MultiLanguageTranslation(
    translations: Map<String, String>,
    selectedLanguage: String = "en",
    modifier: Modifier = Modifier,
    style: TextStyle = IslamicTextStyles.Translation,
    showLanguageLabel: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) {
    val translationText = translations[selectedLanguage] 
        ?: translations["en"] 
        ?: translations.values.firstOrNull()
        ?: ""
    
    if (translationText.isNotEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (showLanguageLabel) {
                Text(
                    text = getLanguageName(selectedLanguage),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Text(
                text = translationText,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Translation in ${getLanguageName(selectedLanguage)}: $translationText"
                    },
                style = style.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = maxLines,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Enhanced transliteration component with phonetic styling
 */
@Composable
fun PhoneticTransliteration(
    transliteration: String,
    modifier: Modifier = Modifier,
    style: TextStyle = IslamicTextStyles.Transliteration,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    showLabel: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showLabel) {
            Text(
                text = "Pronunciation",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Text(
            text = transliteration,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Pronunciation guide: $transliteration"
                },
            style = style.copy(
                color = color,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            ),
            maxLines = maxLines,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

/**
 * Complete prayer card with all language variants
 */
@Composable
fun CompletePrayerCard(
    content: PrayerContent,
    selectedLanguage: String = "en",
    modifier: Modifier = Modifier,
    showTransliteration: Boolean = true,
    showTranslation: Boolean = true,
    showSource: Boolean = false,
    arabicTextSize: TextSizeVariant = TextSizeVariant.Medium,
    cardElevation: Dp = 2.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    val arabicStyle = when (arabicTextSize) {
        TextSizeVariant.Small -> IslamicTextStyles.ArabicPrayerSmall
        TextSizeVariant.Medium -> IslamicTextStyles.ArabicPrayerMedium
        TextSizeVariant.Large -> IslamicTextStyles.ArabicPrayerLarge
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Arabic Text
            ArabicPrayerText(
                arabicText = content.arabic,
                style = arabicStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Transliteration
            if (showTransliteration && content.transliteration != null) {
                PhoneticTransliteration(
                    transliteration = content.transliteration,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Translation
            if (showTranslation && content.translations.isNotEmpty()) {
                MultiLanguageTranslation(
                    translations = content.translations,
                    selectedLanguage = selectedLanguage
                )
            }
            
            // Source attribution
            if (showSource && content.source != null) {
                Text(
                    text = "— ${content.source}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.outline,
                        fontStyle = FontStyle.Italic
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

/**
 * Text size variants for Arabic text
 */
enum class TextSizeVariant {
    Small, Medium, Large
}

/**
 * Language selector component for translations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
    availableLanguages: List<String>,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = getLanguageName(selectedLanguage),
            onValueChange = { },
            readOnly = true,
            label = { Text("Translation Language") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableLanguages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(getLanguageName(language)) },
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Interactive prayer display with user controls
 */
@Composable
fun InteractivePrayerDisplay(
    content: PrayerContent,
    modifier: Modifier = Modifier,
    initialLanguage: String = "en",
    allowLanguageChange: Boolean = true,
    allowTextSizeChange: Boolean = true,
    onLanguageChanged: ((String) -> Unit)? = null,
    onTextSizeChanged: ((TextSizeVariant) -> Unit)? = null
) {
    var selectedLanguage by remember { mutableStateOf(initialLanguage) }
    var textSizeVariant by remember { mutableStateOf(TextSizeVariant.Medium) }
    var showControls by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Controls
        if (showControls && (allowLanguageChange || allowTextSizeChange)) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (allowLanguageChange && content.translations.isNotEmpty()) {
                        LanguageSelector(
                            availableLanguages = content.translations.keys.toList(),
                            selectedLanguage = selectedLanguage,
                            onLanguageSelected = { language ->
                                selectedLanguage = language
                                onLanguageChanged?.invoke(language)
                            }
                        )
                    }
                    
                    if (allowTextSizeChange) {
                        TextSizeSelector(
                            selectedSize = textSizeVariant,
                            onSizeSelected = { size ->
                                textSizeVariant = size
                                onTextSizeChanged?.invoke(size)
                            }
                        )
                    }
                }
            }
        }
        
        // Prayer content
        CompletePrayerCard(
            content = content,
            selectedLanguage = selectedLanguage,
            arabicTextSize = textSizeVariant,
            showSource = true
        )
        
        // Toggle controls button
        TextButton(
            onClick = { showControls = !showControls },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(if (showControls) "Hide Options" else "Show Options")
        }
    }
}

/**
 * Text size selector component
 */
@Composable
fun TextSizeSelector(
    selectedSize: TextSizeVariant,
    onSizeSelected: (TextSizeVariant) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Text Size:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        TextSizeVariant.values().forEach { variant ->
            FilterChip(
                selected = selectedSize == variant,
                onClick = { onSizeSelected(variant) },
                label = {
                    Text(
                        text = variant.name,
                        fontSize = when (variant) {
                            TextSizeVariant.Small -> 12.sp
                            TextSizeVariant.Medium -> 14.sp
                            TextSizeVariant.Large -> 16.sp
                        }
                    )
                }
            )
        }
    }
}

/**
 * Utility function to get language display name
 */
private fun getLanguageName(languageCode: String): String {
    return when (languageCode.lowercase()) {
        "en" -> "English"
        "id" -> "Bahasa Indonesia"
        "ar" -> "العربية"
        "ms" -> "Bahasa Melayu"
        "tr" -> "Türkçe"
        "ur" -> "اردو"
        "fa" -> "فارسی"
        "bn" -> "বাংলা"
        "hi" -> "हिन्दी"
        "fr" -> "Français"
        "de" -> "Deutsch"
        "es" -> "Español"
        else -> languageCode.uppercase()
    }
}
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app_muslim.surah_yasin.core.ui.theme.IslamicTextStyles
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

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

// Preview Functions
@Preview(showBackground = true, name = "Multi Language Translation")
@Composable
private fun PreviewMultiLanguageTranslation() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            MultiLanguageTranslation(
                translations = mapOf(
                    "en" to "In the name of Allah, the Most Gracious, the Most Merciful",
                    "id" to "Dengan nama Allah Yang Maha Pengasih lagi Maha Penyayang",
                    "ms" to "Dengan nama Allah Yang Maha Pemurah lagi Maha Mengasihani"
                ),
                selectedLanguage = "en",
                showLanguageLabel = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "Phonetic Transliteration")
@Composable
private fun PreviewPhoneticTransliteration() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            PhoneticTransliteration(
                transliteration = "Bismillahir Rahmanir Rahim",
                showLabel = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "Complete Prayer Card")
@Composable
private fun PreviewCompletePrayerCard() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            CompletePrayerCard(
                content = PrayerContent(
                    arabic = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                    transliteration = "Bismillahir Rahmanir Rahim",
                    translations = mapOf(
                        "en" to "In the name of Allah, the Most Gracious, the Most Merciful",
                        "id" to "Dengan nama Allah Yang Maha Pengasih lagi Maha Penyayang"
                    ),
                    source = "Al-Fatihah 1:1",
                    category = "Opening"
                ),
                selectedLanguage = "en",
                showTransliteration = true,
                showTranslation = true,
                showSource = true,
                arabicTextSize = TextSizeVariant.Medium
            )
        }
    }
}

@Preview(showBackground = true, name = "Complete Prayer Card - Large Text")
@Composable
private fun PreviewCompletePrayerCardLarge() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            CompletePrayerCard(
                content = PrayerContent(
                    arabic = "اللَّهُمَّ اغْفِرْ لَهُ وَارْحَمْهُ",
                    transliteration = "Allahummaghfir lahu warhamhu",
                    translations = mapOf(
                        "en" to "O Allah, forgive him and have mercy on him",
                        "id" to "Ya Allah, ampunilah dia dan rahmatilah dia"
                    ),
                    source = "Dua for the deceased"
                ),
                selectedLanguage = "en",
                arabicTextSize = TextSizeVariant.Large,
                showSource = true
            )
        }
    }
}

@Preview(showBackground = true, name = "Language Selector")
@Composable
private fun PreviewLanguageSelector() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            LanguageSelector(
                availableLanguages = listOf("en", "id", "ms", "ar", "tr"),
                selectedLanguage = "en",
                onLanguageSelected = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "Text Size Selector")
@Composable
private fun PreviewTextSizeSelector() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            TextSizeSelector(
                selectedSize = TextSizeVariant.Medium,
                onSizeSelected = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "Interactive Prayer Display - Collapsed")
@Composable
private fun PreviewInteractivePrayerDisplayCollapsed() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            InteractivePrayerDisplay(
                content = PrayerContent(
                    arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                    transliteration = "Rabbana atina fi'd-dunya hasanatan wa fi'l-akhirati hasanatan wa qina 'adhab an-nar",
                    translations = mapOf(
                        "en" to "Our Lord, give us good in this world and good in the hereafter, and save us from the punishment of the Fire",
                        "id" to "Ya Tuhan kami, berilah kami kebaikan di dunia dan kebaikan di akhirat, dan peliharalah kami dari azab neraka",
                        "ms" to "Ya Tuhan kami, kurniakanlah kepada kami kebaikan di dunia dan kebaikan di akhirat, dan peliharakanlah kami dari azab neraka"
                    ),
                    source = "Al-Baqarah 2:201"
                ),
                initialLanguage = "en",
                allowLanguageChange = true,
                allowTextSizeChange = true
            )
        }
    }
}

@Preview(showBackground = true, name = "Multi Language - Indonesian")
@Composable
private fun PreviewMultiLanguageIndonesian() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            MultiLanguageTranslation(
                translations = mapOf(
                    "en" to "Glory be to Allah and praise be to Him",
                    "id" to "Maha Suci Allah dan segala puji bagi-Nya",
                    "ar" to "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ"
                ),
                selectedLanguage = "id",
                showLanguageLabel = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "Prayer Card - Arabic Only")
@Composable
private fun PreviewPrayerCardArabicOnly() {
    TahlilTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            CompletePrayerCard(
                content = PrayerContent(
                    arabic = "الحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                    source = "Al-Fatihah 1:2"
                ),
                selectedLanguage = "en",
                showTransliteration = false,
                showTranslation = false,
                showSource = true,
                arabicTextSize = TextSizeVariant.Large
            )
        }
    }
}
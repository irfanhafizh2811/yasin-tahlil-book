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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app_muslim.surah_yasin.core.ui.theme.IslamicTextStyles
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

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
                    textDirection = TextDirection.Ltr,
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

// ============================================================================
// PREVIEW DATA PROVIDERS
// ============================================================================

class ArabicPrayerDataProvider : PreviewParameterProvider<Triple<String, String, String>> {
    override val values: Sequence<Triple<String, String, String>> = sequenceOf(
        // Arabic, Transliteration, Translation
        Triple(
            "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
            "Bismillahi Rahmani Raheem",
            "In the name of Allah, the Most Gracious, the Most Merciful"
        ),
        Triple(
            "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ",
            "Allahumma shalli 'ala Muhammadin wa 'ala aali Muhammad",
            "O Allah, send prayers upon Muhammad and the family of Muhammad"
        ),
        Triple(
            "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            "Subhan Allahi wa bihamdihi",
            "Glory be to Allah and praise Him"
        ),
        Triple(
            "لَا إِلَهَ إِلَّا اللَّهُ",
            "La ilaha illa Allah",
            "There is no god but Allah"
        ),
        Triple(
            "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            "Rabbana aatina fi'd-dunya hasanatan wa fi'l-aakhirati hasanatan wa qina 'adhaab an-naar",
            "Our Lord, give us good in this world and good in the hereafter and save us from the punishment of the Fire"
        )
    )
}

class ArabicTextScaleProvider : PreviewParameterProvider<Float> {
    override val values: Sequence<Float> = sequenceOf(0.8f, 1f, 1.2f, 1.5f, 2f)
}

// ============================================================================
// ARABIC PRAYER TEXT PREVIEWS
// ============================================================================

@Preview(name = "Arabic Prayer Text - Bismillah")
@Composable
fun PreviewArabicPrayerTextBismillah() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ArabicPrayerText(
                    arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم"
                )
            }
        }
    }
}

@Preview(name = "Arabic Prayer Text - Salawat")
@Composable
fun PreviewArabicPrayerTextSalawat() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ArabicPrayerText(
                    arabicText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ"
                )
            }
        }
    }
}

@Preview(name = "Arabic Prayer Text - Long Text")
@Composable
fun PreviewArabicPrayerTextLong() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ArabicPrayerText(
                    arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ"
                )
            }
        }
    }
}

@Preview(name = "Arabic Prayer Text - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewArabicPrayerTextDark() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ArabicPrayerText(
                    arabicText = "لَا إِلَهَ إِلَّا اللَّهُ"
                )
            }
        }
    }
}

// ============================================================================
// TRANSLITERATION TEXT PREVIEWS
// ============================================================================

@Preview(name = "Transliteration Text - Simple")
@Composable
fun PreviewTransliterationText() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                TransliterationText(
                    text = "Bismillahi Rahmani Raheem"
                )
            }
        }
    }
}

@Preview(name = "Transliteration Text - Long")
@Composable
fun PreviewTransliterationTextLong() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                TransliterationText(
                    text = "Allahumma shalli 'ala Muhammadin wa 'ala aali Muhammad kama shallayta 'ala Ibraheema wa 'ala aali Ibraheema innaka hameedum majeed"
                )
            }
        }
    }
}

// ============================================================================
// TRANSLATION TEXT PREVIEWS
// ============================================================================

@Preview(name = "Translation Text - English")
@Composable
fun PreviewTranslationTextEnglish() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                TranslationText(
                    text = "In the name of Allah, the Most Gracious, the Most Merciful",
                    language = "en"
                )
            }
        }
    }
}

@Preview(name = "Translation Text - Long")
@Composable
fun PreviewTranslationTextLong() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                TranslationText(
                    text = "Our Lord, give us good in this world and good in the hereafter and save us from the punishment of the Fire",
                    language = "en"
                )
            }
        }
    }
}

// ============================================================================
// COMPLETE PRAYER DISPLAY PREVIEWS
// ============================================================================

@Preview(name = "Complete Prayer Display - Bismillah")
@Composable
fun PreviewCompletePrayerDisplayBismillah() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                CompletePrayerDisplay(
                    arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                    transliteration = "Bismillahi Rahmani Raheem",
                    translation = "In the name of Allah, the Most Gracious, the Most Merciful"
                )
            }
        }
    }
}

@Preview(name = "Complete Prayer Display - Salawat")
@Composable
fun PreviewCompletePrayerDisplaySalawat() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                CompletePrayerDisplay(
                    arabicText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ",
                    transliteration = "Allahumma shalli 'ala Muhammadin wa 'ala aali Muhammad",
                    translation = "O Allah, send prayers upon Muhammad and the family of Muhammad"
                )
            }
        }
    }
}

@Preview(name = "Complete Prayer Display - No Transliteration")
@Composable
fun PreviewCompletePrayerDisplayNoTransliteration() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                CompletePrayerDisplay(
                    arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                    transliteration = "Subhan Allahi wa bihamdihi",
                    translation = "Glory be to Allah and praise Him",
                    showTransliteration = false
                )
            }
        }
    }
}

@Preview(name = "Complete Prayer Display - Arabic Only")
@Composable
fun PreviewCompletePrayerDisplayArabicOnly() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                CompletePrayerDisplay(
                    arabicText = "لَا إِلَهَ إِلَّا اللَّهُ",
                    transliteration = "La ilaha illa Allah",
                    translation = "There is no god but Allah",
                    showTransliteration = false,
                    showTranslation = false
                )
            }
        }
    }
}

@Preview(name = "Complete Prayer Display - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewCompletePrayerDisplayDark() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                CompletePrayerDisplay(
                    arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                    transliteration = "Bismillahi Rahmani Raheem",
                    translation = "In the name of Allah, the Most Gracious, the Most Merciful"
                )
            }
        }
    }
}

// ============================================================================
// SCALABLE ARABIC TEXT PREVIEWS
// ============================================================================

@Preview(name = "Scalable Arabic Text - Small")
@Composable
fun PreviewScalableArabicTextSmall() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ScalableArabicText(
                    arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                    scaleFactor = 0.8f
                )
            }
        }
    }
}

@Preview(name = "Scalable Arabic Text - Large")
@Composable
fun PreviewScalableArabicTextLarge() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ScalableArabicText(
                    arabicText = "اللَّهُ أَكْبَرُ",
                    scaleFactor = 1.5f
                )
            }
        }
    }
}

@Preview(name = "Scalable Arabic Text - Extra Large")
@Composable
fun PreviewScalableArabicTextExtraLarge() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ScalableArabicText(
                    arabicText = "الحمد لله",
                    scaleFactor = 2f
                )
            }
        }
    }
}

// ============================================================================
// ARABIC HEADER TEXT PREVIEWS
// ============================================================================

@Preview(name = "Arabic Header Text - Simple")
@Composable
fun PreviewArabicHeaderText() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ArabicHeaderText(
                    arabicText = "باب الدعاء"
                )
            }
        }
    }
}

@Preview(name = "Arabic Header Text - Long")
@Composable
fun PreviewArabicHeaderTextLong() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ArabicHeaderText(
                    arabicText = "سورة الفاتحة المباركة"
                )
            }
        }
    }
}

// ============================================================================
// RESPONSIVE ARABIC TEXT PREVIEWS
// ============================================================================

@Preview(name = "Responsive Arabic Text - Default")
@Composable
fun PreviewResponsiveArabicText() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ResponsiveArabicText(
                    arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم"
                )
            }
        }
    }
}

@Preview(name = "Responsive Arabic Text - Small Size", device = "spec:width=360dp,height=640dp,dpi=240")
@Composable
fun PreviewResponsiveArabicTextSmall() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ResponsiveArabicText(
                    arabicText = "لَا إِلَهَ إِلَّا اللَّهُ",
                    baseSize = 18
                )
            }
        }
    }
}

@Preview(name = "Responsive Arabic Text - Large Size", device = "spec:width=840dp,height=600dp,dpi=240")
@Composable
fun PreviewResponsiveArabicTextLarge() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ResponsiveArabicText(
                    arabicText = "اللَّهُمَّ اغْفِرْ لَنَا ذُنُوبَنَا",
                    baseSize = 24
                )
            }
        }
    }
}

// ============================================================================
// ACCESSIBLE PRAYER TEXT PREVIEWS
// ============================================================================

@Preview(name = "Accessible Prayer Text - Full")
@Composable
fun PreviewAccessiblePrayerTextFull() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                AccessiblePrayerText(
                    arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                    transliteration = "Bismillahi Rahmani Raheem",
                    translation = "In the name of Allah, the Most Gracious, the Most Merciful"
                )
            }
        }
    }
}

@Preview(name = "Accessible Prayer Text - Arabic Only")
@Composable
fun PreviewAccessiblePrayerTextArabicOnly() {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                AccessiblePrayerText(
                    arabicText = "اللَّهُ أَكْبَرُ"
                )
            }
        }
    }
}

// ============================================================================
// DEVICE-SPECIFIC PREVIEWS
// ============================================================================

@Preview(
    name = "Arabic Components - Landscape",
    widthDp = 840,
    heightDp = 360
)
@Composable
fun PreviewArabicComponentsLandscape() {
    TahlilTheme {
        Surface {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    ArabicPrayerText(
                        arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم"
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    CompletePrayerDisplay(
                        arabicText = "الحمد لله رب العالمين",
                        transliteration = "Alhamdulillahi rabbil alameen",
                        translation = "All praise is due to Allah, Lord of all the worlds"
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Arabic Components - Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun PreviewArabicComponentsTablet() {
    TahlilTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ArabicHeaderText(
                    arabicText = "سورة الفاتحة"
                )
                CompletePrayerDisplay(
                    arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                    transliteration = "Bismillahi Rahmani Raheem",
                    translation = "In the name of Allah, the Most Gracious, the Most Merciful"
                )
                ScalableArabicText(
                    arabicText = "الحمد لله رب العالمين",
                    scaleFactor = 1.2f
                )
            }
        }
    }
}

// ============================================================================
// DYNAMIC PREVIEWS WITH PARAMETERS
// ============================================================================

@Preview(name = "Arabic Prayer Text - Various Prayers")
@Composable
fun PreviewArabicPrayerTextDynamic(
    @PreviewParameter(ArabicPrayerDataProvider::class) prayerData: Triple<String, String, String>
) {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ArabicPrayerText(
                    arabicText = prayerData.first
                )
            }
        }
    }
}

@Preview(name = "Complete Prayer Display - Various Prayers")
@Composable
fun PreviewCompletePrayerDisplayDynamic(
    @PreviewParameter(ArabicPrayerDataProvider::class) prayerData: Triple<String, String, String>
) {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                CompletePrayerDisplay(
                    arabicText = prayerData.first,
                    transliteration = prayerData.second,
                    translation = prayerData.third
                )
            }
        }
    }
}

@Preview(name = "Scalable Arabic Text - Various Scales")
@Composable
fun PreviewScalableArabicTextDynamic(
    @PreviewParameter(ArabicTextScaleProvider::class) scaleFactor: Float
) {
    TahlilTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ScalableArabicText(
                    arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                    scaleFactor = scaleFactor
                )
            }
        }
    }
}
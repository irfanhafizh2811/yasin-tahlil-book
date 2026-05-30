package com.app_muslim.surah_yasin.core.ui.islamic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.core.ui.theme.IslamicGreen
import com.app_muslim.surah_yasin.core.ui.theme.IslamicGold
import com.app_muslim.surah_yasin.core.ui.theme.IslamicLightGreen
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

@Composable
fun IslamicCard(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            IslamicGreen,
                            IslamicLightGreen,
                            IslamicGreen.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                subtitle?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
                
                content()
            }
        }
    }
}

@Composable
fun IslamicPrayerCard(
    arabicText: String,
    transliteration: String,
    translation: String,
    prayerCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            IslamicGreen,
                            IslamicLightGreen,
                            IslamicGreen.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Arabic Text with proper RTL support
                ArabicPrayerText(
                    arabicText = arabicText,
                    style = com.app_muslim.surah_yasin.core.ui.theme.IslamicTextStyles.ArabicPrayerMedium,
                    color = Color.White,
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Transliteration
                TransliterationText(
                    text = transliteration,
                    style = com.app_muslim.surah_yasin.core.ui.theme.IslamicTextStyles.Transliteration,
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Translation
                TranslationText(
                    text = translation,
                    style = com.app_muslim.surah_yasin.core.ui.theme.IslamicTextStyles.Translation,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = IslamicGold.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "$prayerCount prayers offered",
                        style = MaterialTheme.typography.labelMedium,
                        color = IslamicGold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// PREVIEW DATA PROVIDERS
// ============================================================================

class IslamicCardDataProvider : PreviewParameterProvider<Pair<String, String?>> {
    override val values: Sequence<Pair<String, String?>> = sequenceOf(
        Pair("Memorial Prayer", "Tahlil for the Deceased"),
        Pair("Community Prayer", "Join the Global Community"),
        Pair("Prayer Count: 147", null),
        Pair("بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم", "In the name of Allah, the Most Gracious"),
        Pair("Yasin Chapter", "Complete recitation available")
    )
}

class IslamicPrayerCardDataProvider : PreviewParameterProvider<Pair<String, Int>> {
    override val values: Sequence<Pair<String, Int>> = sequenceOf(
        Pair("بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم", 45),
        Pair("اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ", 123),
        Pair("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", 67),
        Pair("لَا إِلَهَ إِلَّا اللَّهُ", 234),
        Pair("اللَّهُمَّ اغْفِرْ لَهُ", 89)
    )
}

// ============================================================================
// ISLAMIC CARD PREVIEWS
// ============================================================================

@Preview(name = "Islamic Card - Memorial Prayer")
@Composable
fun PreviewIslamicCardMemorial() {
    TahlilTheme {
        Surface {
            IslamicCard(
                title = "Memorial Prayer",
                subtitle = "Tahlil for the Deceased",
                onClick = {}
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "45 prayers offered",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Preview(name = "Islamic Card - Simple")
@Composable
fun PreviewIslamicCardSimple() {
    TahlilTheme {
        Surface {
            IslamicCard(
                title = "Prayer Count: 147",
                onClick = {}
            )
        }
    }
}

@Preview(name = "Islamic Card - Arabic Text")
@Composable
fun PreviewIslamicCardArabic() {
    TahlilTheme {
        Surface {
            IslamicCard(
                title = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                subtitle = "In the name of Allah, the Most Gracious, the Most Merciful",
                onClick = {}
            )
        }
    }
}

@Preview(name = "Islamic Card - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewIslamicCardDark() {
    TahlilTheme {
        Surface {
            IslamicCard(
                title = "Community Prayer",
                subtitle = "Join the Global Community",
                onClick = {}
            )
        }
    }
}

// ============================================================================
// ISLAMIC PRAYER CARD PREVIEWS
// ============================================================================

@Preview(name = "Islamic Prayer Card - Bismillah")
@Composable
fun PreviewIslamicPrayerCardBismillah() {
    TahlilTheme {
        Surface {
            IslamicPrayerCard(
                arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                transliteration = "Bismillahi Rahmani Raheem",
                translation = "In the name of Allah, the Most Gracious, the Most Merciful",
                prayerCount = 45,
                onClick = {}
            )
        }
    }
}

@Preview(name = "Islamic Prayer Card - Salawat")
@Composable
fun PreviewIslamicPrayerCardSalawat() {
    TahlilTheme {
        Surface {
            IslamicPrayerCard(
                arabicText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ",
                transliteration = "Allahumma shalli 'ala Muhammadin wa 'ala aali Muhammad",
                translation = "O Allah, send prayers upon Muhammad and the family of Muhammad",
                prayerCount = 123,
                onClick = {}
            )
        }
    }
}

@Preview(name = "Islamic Prayer Card - High Count")
@Composable
fun PreviewIslamicPrayerCardHighCount() {
    TahlilTheme {
        Surface {
            IslamicPrayerCard(
                arabicText = "لَا إِلَهَ إِلَّا اللَّهُ",
                transliteration = "La ilaha illa Allah",
                translation = "There is no god but Allah",
                prayerCount = 1247,
                onClick = {}
            )
        }
    }
}

@Preview(name = "Islamic Prayer Card - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewIslamicPrayerCardDark() {
    TahlilTheme {
        Surface {
            IslamicPrayerCard(
                arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                transliteration = "Subhan Allahi wa bihamdihi",
                translation = "Glory be to Allah and praise Him",
                prayerCount = 67,
                onClick = {}
            )
        }
    }
}

// ============================================================================
// DEVICE-SPECIFIC PREVIEWS
// ============================================================================

@Preview(
    name = "Islamic Card - Landscape",
    widthDp = 840,
    heightDp = 360
)
@Composable
fun PreviewIslamicCardLandscape() {
    TahlilTheme {
        Surface {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IslamicCard(
                    title = "Memorial Prayer",
                    subtitle = "Tahlil for the Deceased",
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                IslamicCard(
                    title = "Community Prayer", 
                    subtitle = "Join the Global Community",
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(
    name = "Islamic Card - Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun PreviewIslamicCardTablet() {
    TahlilTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IslamicCard(
                    title = "Memorial Prayer",
                    subtitle = "Tahlil for the Deceased",
                    onClick = {}
                )
                IslamicPrayerCard(
                    arabicText = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيم",
                    transliteration = "Bismillahi Rahmani Raheem",
                    translation = "In the name of Allah, the Most Gracious, the Most Merciful",
                    prayerCount = 45,
                    onClick = {}
                )
            }
        }
    }
}

@Preview(
    name = "Islamic Card - Small Phone",
    device = "spec:width=360dp,height=640dp,dpi=240"
)
@Composable
fun PreviewIslamicCardSmallPhone() {
    TahlilTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IslamicCard(
                    title = "Prayer Count: 147",
                    onClick = {}
                )
            }
        }
    }
}

// ============================================================================
// DYNAMIC PREVIEWS WITH PARAMETERS
// ============================================================================

@Preview(name = "Islamic Card - Various Content")
@Composable
fun PreviewIslamicCardDynamic(
    @PreviewParameter(IslamicCardDataProvider::class) cardData: Pair<String, String?>
) {
    TahlilTheme {
        Surface {
            IslamicCard(
                title = cardData.first,
                subtitle = cardData.second,
                onClick = {}
            )
        }
    }
}

@Preview(name = "Islamic Prayer Card - Various Prayers")
@Composable
fun PreviewIslamicPrayerCardDynamic(
    @PreviewParameter(IslamicPrayerCardDataProvider::class) prayerData: Pair<String, Int>
) {
    TahlilTheme {
        Surface {
            IslamicPrayerCard(
                arabicText = prayerData.first,
                transliteration = "Sample transliteration for preview",
                translation = "Sample translation for preview purposes",
                prayerCount = prayerData.second,
                onClick = {}
            )
        }
    }
}


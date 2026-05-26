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
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.core.ui.theme.IslamicGreen
import com.app_muslim.surah_yasin.core.ui.theme.IslamicGold
import com.app_muslim.surah_yasin.core.ui.theme.IslamicLightGreen

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
                    color = Color.White
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


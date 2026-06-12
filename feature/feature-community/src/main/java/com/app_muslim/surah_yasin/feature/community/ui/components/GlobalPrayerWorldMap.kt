package com.app_muslim.surah_yasin.feature.community.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.feature.community.model.*

/**
 * Global Prayer Statistics List - Alternative to World Map
 * Simple list-based visualization showing prayer activity across countries
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GlobalPrayerWorldMap(
    countryStats: List<CountryPrayerStats>,
    selectedCountry: CountryPrayerStats? = null,
    onCountrySelected: (CountryPrayerStats) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            // Header
            GlobalStatsHeader(
                totalCountries = countryStats.size,
                topCountry = countryStats.firstOrNull(),
                selectedCountry = selectedCountry
            )
            
            // Country List with statistics
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(countryStats) { country ->
                    CountryStatsCard(
                        country = country,
                        isSelected = selectedCountry?.countryCode == country.countryCode,
                        onSelect = { onCountrySelected(country) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GlobalStatsHeader(
    totalCountries: Int,
    topCountry: CountryPrayerStats?,
    selectedCountry: CountryPrayerStats?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🌍 Global Prayer Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Active in $totalCountries countries",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            if (selectedCountry != null && selectedCountry.countryName.isNotEmpty()) {
                CountryFlag(
                    countryName = selectedCountry.countryName,
                    flag = selectedCountry.flag
                )
            } else if (topCountry != null) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "🏆",
                            fontSize = 12.sp
                        )
                        Text(
                            text = topCountry.countryName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Text(
                        text = "${topCountry.totalPrayers} prayers",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun CountryStatsCard(
    country: CountryPrayerStats,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 12.dp else 4.dp,
        animationSpec = tween(300), label = "elevation"
    )
    
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Country flag and rank
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CountryFlag(
                        countryName = country.countryName,
                        flag = country.flag
                    )
                    Text(
                        text = "#${country.rank}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Country info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = country.countryName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "${country.totalPrayers} prayers • ${country.activeParticipants} participants",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Activity level indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ActivityLevelIndicator(
                    heatLevel = country.heatLevel,
                    size = 32.dp
                )
                
                Text(
                    text = "${(country.heatLevel * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CountryFlag(
    countryName: String,
    flag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (flag.isNotEmpty()) {
                Text(
                    text = flag,
                    fontSize = 20.sp
                )
            } else {
                Text(
                    text = countryName.take(2).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ActivityLevelIndicator(
    heatLevel: Float,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFF4CAF50),
        Color(0xFFFF9800),
        Color(0xFFE91E63)
    )
    
    val color = when {
        heatLevel < 0.33f -> colors[0]
        heatLevel < 0.66f -> colors[1]
        else -> colors[2]
    }
    
    Box(
        modifier = modifier
            .size(width = size, height = size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        val innerSize = size * heatLevel.coerceAtLeast(0.3f)
        Box(
            modifier = Modifier
                .size(width = innerSize, height = innerSize)
                .clip(CircleShape)
                .background(color)
        )
    }
}

// Sample data generator for development/testing
object WorldMapSampleData {
    fun getSampleCountryStats(): List<CountryPrayerStats> {
        return listOf(
            CountryPrayerStats(
                countryCode = "ID",
                countryName = "Indonesia",
                totalPrayers = 150000L,
                activeParticipants = 25000L,
                flag = "🇮🇩",
                heatLevel = 0.95f,
                rank = 1,
                popularPrayerTypes = listOf(
                    PrayerTypeCount(CommunityPrayerType.TAHLIL, 50000L, 33.3f),
                    PrayerTypeCount(CommunityPrayerType.YASIN, 60000L, 40.0f),
                    PrayerTypeCount(CommunityPrayerType.FATIHAH, 40000L, 26.7f)
                )
            ),
            CountryPrayerStats(
                countryCode = "SA",
                countryName = "Saudi Arabia",
                totalPrayers = 120000L,
                activeParticipants = 18000L,
                flag = "🇸🇦",
                heatLevel = 0.85f,
                rank = 2
            ),
            CountryPrayerStats(
                countryCode = "PK",
                countryName = "Pakistan",
                totalPrayers = 95000L,
                activeParticipants = 15000L,
                flag = "🇵🇰",
                heatLevel = 0.75f,
                rank = 3
            ),
            CountryPrayerStats(
                countryCode = "MY",
                countryName = "Malaysia",
                totalPrayers = 75000L,
                activeParticipants = 12000L,
                flag = "🇲🇾",
                heatLevel = 0.65f,
                rank = 4
            ),
            CountryPrayerStats(
                countryCode = "TR",
                countryName = "Turkey",
                totalPrayers = 60000L,
                activeParticipants = 9000L,
                flag = "🇹🇷",
                heatLevel = 0.55f,
                rank = 5
            )
        )
    }
}

// Preview Parameter Providers
class CountryPrayerStatsProvider : PreviewParameterProvider<List<CountryPrayerStats>> {
    override val values = sequenceOf(
        // Top performing countries
        listOf(
            CountryPrayerStats(
                countryCode = "ID",
                countryName = "Indonesia",
                totalPrayers = 2_500_000L,
                activeParticipants = 450_000L,
                flag = "🇮🇩",
                heatLevel = 1.0f,
                rank = 1,
                popularPrayerTypes = listOf(
                    PrayerTypeCount(CommunityPrayerType.TAHLIL, 1_000_000L, 40.0f),
                    PrayerTypeCount(CommunityPrayerType.YASIN, 900_000L, 36.0f),
                    PrayerTypeCount(CommunityPrayerType.FATIHAH, 600_000L, 24.0f)
                )
            ),
            CountryPrayerStats(
                countryCode = "PK",
                countryName = "Pakistan",
                totalPrayers = 1_800_000L,
                activeParticipants = 320_000L,
                flag = "🇵🇰",
                heatLevel = 0.95f,
                rank = 2
            ),
            CountryPrayerStats(
                countryCode = "SA",
                countryName = "Saudi Arabia",
                totalPrayers = 950_000L,
                activeParticipants = 180_000L,
                flag = "🇸🇦",
                heatLevel = 0.82f,
                rank = 3
            )
        )
    )
}

// Preview Composables
@Preview(name = "Global Prayer World Map - Default")
@Composable
fun GlobalPrayerWorldMapDefaultPreview(
    @PreviewParameter(CountryPrayerStatsProvider::class) 
    countryStats: List<CountryPrayerStats>
) {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GlobalPrayerWorldMap(
                countryStats = countryStats,
                onCountrySelected = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "World Map - With Selection")
@Composable
fun GlobalPrayerWorldMapSelectedPreview() {
    val countryStats = listOf(
        CountryPrayerStats(
            countryCode = "ID",
            countryName = "Indonesia",
            totalPrayers = 3_200_000L,
            activeParticipants = 650_000L,
            flag = "🇮🇩",
            heatLevel = 1.0f,
            rank = 1
        ),
        CountryPrayerStats(
            countryCode = "PK",
            countryName = "Pakistan",
            totalPrayers = 2_100_000L,
            activeParticipants = 380_000L,
            flag = "🇵🇰",
            heatLevel = 0.92f,
            rank = 2
        ),
        CountryPrayerStats(
            countryCode = "SA",
            countryName = "Saudi Arabia",
            totalPrayers = 1_150_000L,
            activeParticipants = 225_000L,
            flag = "🇸🇦",
            heatLevel = 0.85f,
            rank = 3
        )
    )
    
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GlobalPrayerWorldMap(
                countryStats = countryStats,
                selectedCountry = countryStats[1], // Pakistan selected
                onCountrySelected = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Empty Country List")
@Composable
fun GlobalPrayerWorldMapEmptyPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GlobalPrayerWorldMap(
                countryStats = emptyList(),
                onCountrySelected = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Country Stats Card")
@Composable
fun CountryStatsCardPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CountryStatsCard(
                country = CountryPrayerStats(
                    countryCode = "EG",
                    countryName = "Egypt",
                    totalPrayers = 725_000L,
                    activeParticipants = 118_000L,
                    flag = "🇪🇬",
                    heatLevel = 0.72f,
                    rank = 4
                ),
                isSelected = false,
                onSelect = { },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(name = "Country Flag Component")
@Composable
fun CountryFlagPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                CountryFlag(
                    countryName = "Indonesia",
                    flag = "🇮🇩"
                )
                CountryFlag(
                    countryName = "Saudi Arabia",
                    flag = "🇸🇦"
                )
                CountryFlag(
                    countryName = "Unknown Country",
                    flag = "" // No flag, shows country code
                )
            }
        }
    }
}

@Preview(name = "Activity Level Indicators")
@Composable
fun ActivityLevelIndicatorPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ActivityLevelIndicator(heatLevel = 0.2f, size = 32.dp)
                    Text("Low", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ActivityLevelIndicator(heatLevel = 0.5f, size = 32.dp)
                    Text("Medium", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ActivityLevelIndicator(heatLevel = 0.8f, size = 32.dp)
                    Text("High", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ActivityLevelIndicator(heatLevel = 1.0f, size = 32.dp)
                    Text("Max", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Preview(name = "Global Stats Header")
@Composable
fun GlobalStatsHeaderPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GlobalStatsHeader(
                totalCountries = 157,
                topCountry = CountryPrayerStats(
                    countryCode = "ID",
                    countryName = "Indonesia",
                    totalPrayers = 3_500_000L,
                    activeParticipants = 750_000L,
                    flag = "🇮🇩",
                    heatLevel = 1.0f,
                    rank = 1
                ),
                selectedCountry = null,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Global Stats Header - With Selection")
@Composable
fun GlobalStatsHeaderWithSelectionPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GlobalStatsHeader(
                totalCountries = 157,
                topCountry = CountryPrayerStats(
                    countryCode = "ID",
                    countryName = "Indonesia",
                    totalPrayers = 3_500_000L,
                    activeParticipants = 750_000L,
                    flag = "🇮🇩",
                    heatLevel = 1.0f,
                    rank = 1
                ),
                selectedCountry = CountryPrayerStats(
                    countryCode = "TR",
                    countryName = "Turkey",
                    totalPrayers = 1_200_000L,
                    activeParticipants = 185_000L,
                    flag = "🇹🇷",
                    heatLevel = 0.82f,
                    rank = 5
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
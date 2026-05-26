package com.app_muslim.surah_yasin.feature.community.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.compose.*
import com.app_muslim.surah_yasin.feature.community.model.*

/**
 * Global Prayer World Map Component
 * Interactive world map showing prayer activity across countries with heat visualization
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GlobalPrayerWorldMap(
    countryStats: List<CountryPrayerStats>,
    selectedCountry: CountryPrayerStats? = null,
    onCountrySelected: (CountryPrayerStats) -> Unit,
    modifier: Modifier = Modifier
) {
    var mapLoaded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
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
            // Map header
            WorldMapHeader(
                totalCountries = countryStats.size,
                topCountry = countryStats.firstOrNull(),
                selectedCountry = selectedCountry
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Google Map
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = rememberCameraPositionState {
                        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                            LatLng(20.0, 0.0), // Center on global view
                            2f
                        )
                    },
                    onMapLoaded = {
                        mapLoaded = true
                    },
                    properties = MapProperties(
                        mapType = MapType.NORMAL,
                        isMyLocationEnabled = false
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        mapToolbarEnabled = false,
                        myLocationButtonEnabled = false
                    )
                ) {
                    // Add markers for countries with prayer activity
                    countryStats.forEach { country ->
                        if (country.latitude != 0.0 && country.longitude != 0.0) {
                            PrayerActivityMarker(
                                position = LatLng(country.latitude, country.longitude),
                                country = country,
                                onMarkerClick = { onCountrySelected(country) }
                            )
                        }
                    }
                }
                
                // Loading overlay
                if (!mapLoaded) {
                    MapLoadingOverlay()
                }
                
                // Country details overlay
                selectedCountry?.let { country ->
                    CountryDetailsOverlay(
                        country = country,
                        onDismiss = { onCountrySelected(CountryPrayerStats("", "")) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WorldMapHeader(
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
private fun PrayerActivityMarker(
    position: LatLng,
    country: CountryPrayerStats,
    onMarkerClick: () -> Unit
) {
    // Heat level determines marker size and color intensity
    val markerSize = (20f + (country.heatLevel * 30f)).coerceIn(20f, 50f)
    val markerColor = Color.lerp(
        Color(0xFF4CAF50).copy(alpha = 0.6f),
        Color(0xFFE91E63).copy(alpha = 0.9f),
        country.heatLevel
    )
    
    Marker(
        state = MarkerState(position = position),
        title = country.countryName,
        snippet = "${country.totalPrayers} prayers • ${country.activeParticipants} participants",
        onClick = {
            onMarkerClick()
            true
        }
    )
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
private fun MapLoadingOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Loading global prayer map...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CountryDetailsOverlay(
    country: CountryPrayerStats,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300, easing = EaseOutQuart)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300, easing = EaseInQuart)
            )
        ) {
            CountryDetailsCard(
                country = country,
                onDismiss = onDismiss
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountryDetailsCard(
    country: CountryPrayerStats,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with country info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CountryFlag(
                        countryName = country.countryName,
                        flag = country.flag
                    )
                    
                    Column {
                        Text(
                            text = country.countryName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Rank #${country.rank} globally",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Prayer statistics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CountryStatItem(
                    icon = "🤲",
                    label = "Total Prayers",
                    value = "${country.totalPrayers}",
                    modifier = Modifier.weight(1f)
                )
                
                CountryStatItem(
                    icon = "👥",
                    label = "Participants",
                    value = "${country.activeParticipants}",
                    modifier = Modifier.weight(1f)
                )
                
                CountryStatItem(
                    icon = "🔥",
                    label = "Activity Level",
                    value = "${(country.heatLevel * 100).toInt()}%",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Popular prayer types
            if (country.popularPrayerTypes.isNotEmpty()) {
                Text(
                    text = "Popular Prayer Types",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                country.popularPrayerTypes.take(3).forEach { prayerTypeCount ->
                    PrayerTypeProgressBar(
                        prayerType = prayerTypeCount.prayerType,
                        percentage = prayerTypeCount.percentage
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun CountryStatItem(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PrayerTypeProgressBar(
    prayerType: CommunityPrayerType,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = prayerType.displayName,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(60.dp)
        )
        
        LinearProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = when (prayerType) {
                CommunityPrayerType.TAHLIL -> Color(0xFF4CAF50)
                CommunityPrayerType.YASIN -> Color(0xFF2196F3)
                CommunityPrayerType.FATIHAH -> Color(0xFFFF9800)
                CommunityPrayerType.DHIKR -> Color(0xFF9C27B0)
                CommunityPrayerType.DUA -> Color(0xFFE91E63)
                CommunityPrayerType.QURAN -> Color(0xFF00BCD4)
                CommunityPrayerType.COMMUNITY_PRAYER -> Color(0xFF795548)
            }
        )
        
        Text(
            text = "${percentage.toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(32.dp)
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
                latitude = -2.5,
                longitude = 118.0,
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
                latitude = 23.8,
                longitude = 45.0,
                flag = "🇸🇦",
                heatLevel = 0.85f,
                rank = 2
            ),
            CountryPrayerStats(
                countryCode = "PK",
                countryName = "Pakistan",
                totalPrayers = 95000L,
                activeParticipants = 15000L,
                latitude = 30.3,
                longitude = 69.3,
                flag = "🇵🇰",
                heatLevel = 0.75f,
                rank = 3
            ),
            CountryPrayerStats(
                countryCode = "MY",
                countryName = "Malaysia",
                totalPrayers = 75000L,
                activeParticipants = 12000L,
                latitude = 4.2,
                longitude = 101.9,
                flag = "🇲🇾",
                heatLevel = 0.65f,
                rank = 4
            ),
            CountryPrayerStats(
                countryCode = "TR",
                countryName = "Turkey",
                totalPrayers = 60000L,
                activeParticipants = 9000L,
                latitude = 38.9,
                longitude = 35.2,
                flag = "🇹🇷",
                heatLevel = 0.55f,
                rank = 5
            )
        )
    }
}
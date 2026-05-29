package com.app_muslim.surah_yasin.feature.community.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app_muslim.surah_yasin.feature.community.model.*

@Composable
fun CommunityScreen(
    globalStats: GlobalPrayerStatistics,
    recentMemorials: List<CommunityMemorial>,
    leaderboard: List<PrayerLeaderboard>,
    isLoading: Boolean,
    isOffline: Boolean = false,
    onMemorialClick: (CommunityMemorial) -> Unit,
    onJoinPrayer: (CommunityMemorial) -> Unit,
    onRefresh: (() -> Unit)? = null
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = "Loading community data" },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "CommunityScrollContent" },
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Offline indicator
        if (isOffline) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Offline mode",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Global Statistics
        item {
            GlobalStatisticsCard(
                globalStats = globalStats,
                onRefresh = onRefresh
            )
        }

        // Recent Memorials Section
        item {
            Text(
                text = "Recent Memorials",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (recentMemorials.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No recent memorials",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            items(recentMemorials) { memorial ->
                CommunityMemorialCard(
                    memorial = memorial,
                    onMemorialClick = onMemorialClick,
                    onJoinPrayer = onJoinPrayer
                )
            }
        }

        // Leaderboard Section
        item {
            Text(
                text = "Prayer Leaderboard",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (leaderboard.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Leaderboard updating...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            items(leaderboard.take(10)) { entry ->
                LeaderboardEntryCard(entry = entry)
            }
        }
    }
}

@Composable
fun GlobalStatisticsCard(
    globalStats: GlobalPrayerStatistics,
    onRefresh: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Global Prayer Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                onRefresh?.let {
                    IconButton(
                        onClick = it,
                        modifier = Modifier.semantics { 
                            contentDescription = "Refresh community" 
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    value = formatNumber(globalStats.totalPrayers),
                    label = "Total Prayers"
                )
                StatisticItem(
                    value = globalStats.activePrayers.toString(),
                    label = "Active Now"
                )
                StatisticItem(
                    value = globalStats.recentMemorials.toString(),
                    label = "New Memorials"
                )
            }
        }
    }
}

@Composable
fun StatisticItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun CommunityMemorialCard(
    memorial: CommunityMemorial,
    onMemorialClick: (CommunityMemorial) -> Unit,
    onJoinPrayer: (CommunityMemorial) -> Unit
) {
    var showJoinDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onMemorialClick(memorial) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = memorial.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = memorial.deceasedName,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = memorial.familyName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${memorial.totalPrayers} prayers",
                    style = MaterialTheme.typography.bodySmall
                )

                Button(
                    onClick = { showJoinDialog = true },
                    modifier = Modifier.semantics {
                        contentDescription = "Join prayer for ${memorial.deceasedName}"
                    }
                ) {
                    Text("Join Prayer")
                }
            }
        }
    }

    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = { Text("Join Community Prayer") },
            text = { Text("You are about to join a prayer for ${memorial.deceasedName}") },
            confirmButton = {
                Button(
                    onClick = {
                        onJoinPrayer(memorial)
                        showJoinDialog = false
                    }
                ) {
                    Text("Join Prayer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showJoinDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LeaderboardEntryCard(
    entry: PrayerLeaderboard
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(20.dp),
                color = when (entry.rank) {
                    1 -> MaterialTheme.colorScheme.tertiary
                    2 -> MaterialTheme.colorScheme.secondary
                    3 -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${entry.rank}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = entry.displayName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = entry.country,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Prayer count
            Text(
                text = "${entry.totalPrayers} prayers",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            // Flag emoji (simplified)
            Text(
                text = getCountryFlag(entry.country),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun LeaderboardSection(
    leaderboard: List<PrayerLeaderboard>,
    currentFilter: LeaderboardFilter = LeaderboardFilter.GLOBAL
) {
    Column {
        // Filter tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = currentFilter == LeaderboardFilter.GLOBAL,
                onClick = { /* Handle filter change */ },
                label = { Text("Global") }
            )
            FilterChip(
                selected = currentFilter == LeaderboardFilter.LOCAL,
                onClick = { /* Handle filter change */ },
                label = { Text("Local") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Leaderboard entries
        leaderboard.forEach { entry ->
            LeaderboardEntryCard(entry = entry)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Helper functions
private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> "%.1fM".format(number / 1_000_000.0)
        number >= 1_000 -> "%.1fK".format(number / 1_000.0)
        else -> number.toString()
    }
}

private fun getCountryFlag(country: String): String {
    return when (country.lowercase()) {
        "saudi arabia" -> "🇸🇦"
        "egypt" -> "🇪🇬"
        "pakistan" -> "🇵🇰"
        "indonesia" -> "🇮🇩"
        "malaysia" -> "🇲🇾"
        "turkey" -> "🇹🇷"
        "bangladesh" -> "🇧🇩"
        "iran" -> "🇮🇷"
        "iraq" -> "🇮🇶"
        "jordan" -> "🇯🇴"
        "lebanon" -> "🇱🇧"
        "syria" -> "🇸🇾"
        "morocco" -> "🇲🇦"
        "algeria" -> "🇩🇿"
        "tunisia" -> "🇹🇳"
        "libya" -> "🇱🇾"
        "sudan" -> "🇸🇩"
        "afghanistan" -> "🇦🇫"
        "nigeria" -> "🇳🇬"
        else -> "🌍"
    }
}

// Data classes for the tests
data class GlobalPrayerStatistics(
    val totalPrayers: Long = 0,
    val activePrayers: Int = 0,
    val recentMemorials: Int = 0
)

data class CommunityMemorial(
    val id: String,
    val title: String,
    val deceasedName: String,
    val totalPrayers: Int,
    val recentPrayers: Int,
    val familyName: String
)

data class PrayerLeaderboard(
    val userId: String,
    val displayName: String,
    val totalPrayers: Int,
    val rank: Int,
    val country: String
)

enum class LeaderboardFilter {
    LOCAL, GLOBAL
}
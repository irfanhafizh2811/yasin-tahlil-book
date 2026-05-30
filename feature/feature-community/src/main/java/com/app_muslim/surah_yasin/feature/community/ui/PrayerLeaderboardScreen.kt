package com.app_muslim.surah_yasin.feature.community.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.app_muslim.surah_yasin.feature.community.model.*
import com.app_muslim.surah_yasin.feature.community.model.LeaderboardTimeFrame as ModelLeaderboardTimeFrame
import com.app_muslim.surah_yasin.feature.community.viewmodel.PrayerLeaderboardViewModel
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

/**
 * Prayer Leaderboard Screen with Regional Rankings
 * Displays top prayer participants with time-based filtering
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerLeaderboardScreen(
    onBackPressed: (() -> Unit)? = null,
    viewModel: PrayerLeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Prayer Leaderboard", 
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = onBackPressed?.let {
                    {
                        IconButton(onClick = it) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                } ?: {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Filters
            TimeFrameSelector(
                selectedTimeFrame = uiState.selectedTimeFrame,
                onTimeFrameChange = { timeFrame ->
                    // viewModel.loadLeaderboard(timeFrame, uiState.selectedRegion)
                }
            )
            
            RegionFilterRow(
                selectedRegion = uiState.selectedRegion,
                onRegionChange = { region ->
                    // viewModel.loadLeaderboard(uiState.selectedTimeFrame, region)
                }
            )
            
            if (uiState.isLoading) {
                LoadingLeaderboard()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // User's current rank
                    uiState.userRank?.let { userRank ->
                        item {
                            CurrentUserRankCard(
                                userRank = userRank,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // Global leaderboard
                    if (uiState.globalLeaderboard.isNotEmpty()) {
                        itemsIndexed(uiState.globalLeaderboard) { _, entry ->
                            LeaderboardEntryCard(
                                entry = entry,
                                isCurrentUser = entry.userId == uiState.userRank?.userId
                            )
                        }
                    } else {
                        item {
                            EmptyLeaderboard()
                        }
                    }
                    
                    // User rank (if not in top list)
                    if (uiState.userRank != null && 
                        !uiState.globalLeaderboard.any { it.userId == uiState.userRank!!.userId }) {
                        item {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            CurrentUserRankCard(
                                userRank = uiState.userRank!!,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeFrameSelector(
    selectedTimeFrame: ModelLeaderboardTimeFrame,
    onTimeFrameChange: (ModelLeaderboardTimeFrame) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        LazyRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ModelLeaderboardTimeFrame.values()) { timeFrame ->
                FilterChip(
                    onClick = { onTimeFrameChange(timeFrame) },
                    label = { 
                        Text(
                            text = when (timeFrame) {
                                ModelLeaderboardTimeFrame.TODAY -> "Today"
                                ModelLeaderboardTimeFrame.THIS_WEEK -> "This Week"
                                ModelLeaderboardTimeFrame.THIS_MONTH -> "This Month"
                                ModelLeaderboardTimeFrame.ALL_TIME -> "All Time"
                            },
                            fontSize = 12.sp
                        )
                    },
                    selected = selectedTimeFrame == timeFrame,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4CAF50),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun RegionFilterRow(
    selectedRegion: String,
    onRegionChange: (String) -> Unit
) {
    // Simple region filter implementation
    Text(
        text = "Showing: $selectedRegion",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
private fun LoadingLeaderboard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyLeaderboard() {
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
                text = "No leaderboard data available",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun LeaderboardEntryCard(
    entry: PrayerLeaderboardEntry,
    isCurrentUser: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isCurrentUser) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank display
            Text(
                text = "#${entry.rank}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // User name
            Text(
                text = entry.displayName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            
            // Prayer count
            Text(
                text = "${entry.totalPrayers} prayers",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CurrentUserRankCard(
    userRank: PrayerLeaderboardEntry,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Your Rank",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${userRank.rank}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${userRank.totalPrayers} prayers",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

// Simple Preview Functions
@Preview(showBackground = true, name = "Prayer Leaderboard - Loading")
@Composable
private fun PreviewPrayerLeaderboardLoading() {
    TahlilTheme {
        Column {
            TimeFrameSelector(
                selectedTimeFrame = ModelLeaderboardTimeFrame.TODAY,
                onTimeFrameChange = {}
            )
            Spacer(modifier = Modifier.height(16.dp))
            LoadingLeaderboard()
        }
    }
}

@Preview(showBackground = true, name = "Time Frame Selector")
@Composable 
private fun PreviewTimeFrameSelector() {
    TahlilTheme {
        TimeFrameSelector(
            selectedTimeFrame = ModelLeaderboardTimeFrame.THIS_WEEK,
            onTimeFrameChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty Leaderboard")
@Composable
private fun PreviewEmptyLeaderboard() {
    TahlilTheme {
        EmptyLeaderboard()
    }
}

@Preview(showBackground = true, name = "Loading Indicator")
@Composable
private fun PreviewLoadingLeaderboard() {
    TahlilTheme {
        LoadingLeaderboard()
    }
}

@Preview(showBackground = true, name = "Prayer Leaderboard Entry")
@Composable
private fun PreviewLeaderboardEntry() {
    TahlilTheme {
        // For preview purposes, we'll create a simple display component
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#1",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Abdullah Rahman",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "1250 prayers",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Current User Rank Card")
@Composable
private fun PreviewCurrentUserRankCard() {
    TahlilTheme {
        // Create a mock entry for preview
        val mockUserRank = PrayerLeaderboardEntry(
            userId = "user1",
            displayName = "Current User",
            regionCode = "ID",
            regionName = "Indonesia",
            totalPrayers = 725L,
            totalSessions = 45L,
            rank = 12,
            favoriteParticipationType = CommunityPrayerType.TAHLIL,
            currentStreak = 15,
            joinedCommunitySince = java.time.ZonedDateTime.now()
        )
        
        CurrentUserRankCard(
            userRank = mockUserRank
        )
    }
}

@Preview(showBackground = true, name = "Prayer Leaderboard - Current User Entry")
@Composable
private fun PreviewLeaderboardEntryCurrentUser() {
    TahlilTheme {
        // Create a mock entry for preview
        val mockEntry = PrayerLeaderboardEntry(
            userId = "user1",
            displayName = "You",
            regionCode = "MY",
            regionName = "Malaysia",
            totalPrayers = 890L,
            totalSessions = 67L,
            rank = 5,
            favoriteParticipationType = CommunityPrayerType.YASIN,
            currentStreak = 25,
            joinedCommunitySince = java.time.ZonedDateTime.now()
        )
        
        LeaderboardEntryCard(
            entry = mockEntry,
            isCurrentUser = true
        )
    }
}

@Preview(showBackground = true, name = "Region Filter Row")
@Composable
private fun PreviewRegionFilterRow() {
    TahlilTheme {
        RegionFilterRow(
            selectedRegion = "Global",
            onRegionChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Prayer Leaderboard Screen - Dark", 
         uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPrayerLeaderboardDark() {
    TahlilTheme {
        Column {
            TimeFrameSelector(
                selectedTimeFrame = ModelLeaderboardTimeFrame.ALL_TIME,
                onTimeFrameChange = {}
            )
            EmptyLeaderboard()
        }
    }
}

@Preview(showBackground = true, name = "Prayer Leaderboard - Tablet",
         device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PreviewPrayerLeaderboardTablet() {
    TahlilTheme {
        TimeFrameSelector(
            selectedTimeFrame = ModelLeaderboardTimeFrame.THIS_MONTH,
            onTimeFrameChange = {}
        )
    }
}
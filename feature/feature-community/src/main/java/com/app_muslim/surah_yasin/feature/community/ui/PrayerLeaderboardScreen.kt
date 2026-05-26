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
import com.app_muslim.surah_yasin.feature.community.model.*
import com.app_muslim.surah_yasin.feature.community.viewmodel.PrayerLeaderboardViewModel

/**
 * Prayer Leaderboard Screen with Regional Rankings
 * Displays top prayer participants with time-based filtering
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerLeaderboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: PrayerLeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "🏆 Prayer Leaderboard",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
        
        // Filters
        TimeFrameSelector(
            selectedTimeFrame = uiState.selectedTimeFrame,
            onTimeFrameChange = { timeFrame ->
                viewModel.handleEvent(CommunityEvent.LoadLeaderboard(timeFrame, uiState.selectedRegion))
            }
        )
        
        RegionFilterRow(
            selectedRegion = uiState.selectedRegion,
            onRegionChange = { region ->
                viewModel.handleEvent(CommunityEvent.LoadLeaderboard(uiState.selectedTimeFrame, region))
            }
        )
        
        if (uiState.isLoading) {
            LoadingLeaderboard()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // User's rank if available
                uiState.userRank?.let { userRank ->
                    item {
                        UserRankCard(
                            userRank = userRank,
                            onClick = { onNavigateToProfile(userRank.userId) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // Top 3 podium
                if (uiState.globalLeaderboard.isNotEmpty()) {
                    item {
                        PodiumSection(
                            topThree = uiState.globalLeaderboard.take(3),
                            onNavigateToProfile = onNavigateToProfile
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                // Full leaderboard
                itemsIndexed(
                    items = uiState.globalLeaderboard.drop(3),
                    key = { _, entry -> entry.userId }
                ) { index, entry ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(300, delayMillis = index * 50)
                        )
                    ) {
                        LeaderboardEntryCard(
                            entry = entry,
                            rank = index + 4, // +3 for podium, +1 for 1-based ranking
                            onClick = { onNavigateToProfile(entry.userId) }
                        )
                    }
                }
                
                // Bottom padding
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        
        // Error handling
        uiState.errorMessage?.let { error ->
            LaunchedEffect(error) {
                // Show error snackbar
            }
        }
    }
}

@Composable
private fun TimeFrameSelector(
    selectedTimeFrame: LeaderboardTimeFrame,
    onTimeFrameChange: (LeaderboardTimeFrame) -> Unit
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
            items(LeaderboardTimeFrame.values()) { timeFrame ->
                FilterChip(
                    onClick = { onTimeFrameChange(timeFrame) },
                    label = { 
                        Text(
                            text = when (timeFrame) {
                                LeaderboardTimeFrame.TODAY -> "Today"
                                LeaderboardTimeFrame.THIS_WEEK -> "This Week"
                                LeaderboardTimeFrame.THIS_MONTH -> "This Month"
                                LeaderboardTimeFrame.ALL_TIME -> "All Time"
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
    val regions = listOf(
        "global" to "🌍 Global",
        "middle_east" to "🕌 Middle East",
        "south_asia" to "🇮🇳 South Asia",
        "southeast_asia" to "🇮🇩 Southeast Asia",
        "north_america" to "🇺🇸 North America",
        "europe" to "🇪🇺 Europe",
        "africa" to "🌍 Africa"
    )
    
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(regions) { (code, name) ->
            FilterChip(
                onClick = { onRegionChange(code) },
                label = { 
                    Text(
                        text = name,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = selectedRegion == code,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                    selectedLabelColor = Color(0xFF2E7D32)
                )
            )
        }
    }
}

@Composable
private fun PodiumSection(
    topThree: List<PrayerLeaderboardEntry>,
    onNavigateToProfile: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆 Top Performers",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Podium layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd place (left)
                if (topThree.size > 1) {
                    PodiumPosition(
                        entry = topThree[1],
                        position = 2,
                        height = 60.dp,
                        onClick = { onNavigateToProfile(topThree[1].userId) }
                    )
                }
                
                // 1st place (center, tallest)
                if (topThree.isNotEmpty()) {
                    PodiumPosition(
                        entry = topThree[0],
                        position = 1,
                        height = 80.dp,
                        onClick = { onNavigateToProfile(topThree[0].userId) }
                    )
                }
                
                // 3rd place (right)
                if (topThree.size > 2) {
                    PodiumPosition(
                        entry = topThree[2],
                        position = 3,
                        height = 40.dp,
                        onClick = { onNavigateToProfile(topThree[2].userId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PodiumPosition(
    entry: PrayerLeaderboardEntry,
    position: Int,
    height: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val podiumColor = when (position) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MaterialTheme.colorScheme.surface
    }
    
    val crownEmoji = when (position) {
        1 -> "👑"
        2 -> "🥈"
        3 -> "🥉"
        else -> ""
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        // Crown/Medal
        Text(
            text = crownEmoji,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // Profile
        Card(
            onClick = onClick,
            shape = CircleShape,
            modifier = Modifier.size(60.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                podiumColor.copy(alpha = 0.3f),
                                podiumColor.copy(alpha = 0.1f)
                            )
                        )
                    )
            ) {
                Text(
                    text = entry.displayName.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = entry.displayName,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "${entry.totalPrayers} prayers",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Podium base
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(
                    color = podiumColor,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
        ) {
            Text(
                text = "#$position",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun UserRankCard(
    userRank: PrayerLeaderboardEntry,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFF4CAF50).copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Your rank indicator
            Surface(
                shape = CircleShape,
                color = Color(0xFF4CAF50),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "YOU",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Your Rank: #${userRank.rank}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                
                Text(
                    text = "${userRank.totalPrayers} prayers • ${userRank.totalSessions} sessions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            if (userRank.currentStreak > 0) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFF9800).copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${userRank.currentStreak}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardEntryCard(
    entry: PrayerLeaderboardEntry,
    rank: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rank number
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$rank",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Profile avatar placeholder
            Surface(
                shape = CircleShape,
                color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = entry.displayName.take(2).uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
            
            // User info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${entry.totalPrayers} prayers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    
                    Text(
                        text = entry.regionName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Badges and streak
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (entry.badges.isNotEmpty()) {
                    Text(
                        text = "🎖️ ${entry.badges.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF9800)
                    )
                }
                
                if (entry.currentStreak > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${entry.currentStreak}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingLeaderboard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(10) {
            LeaderboardEntryShimmer()
        }
    }
}

@Composable
private fun LeaderboardEntryShimmer() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
            )
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
                )
            }
        }
    }
}
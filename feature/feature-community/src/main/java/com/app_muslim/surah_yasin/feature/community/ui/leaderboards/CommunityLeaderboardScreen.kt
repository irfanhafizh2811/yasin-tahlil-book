package com.app_muslim.surah_yasin.feature.community.ui.leaderboards

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import com.app_muslim.surah_yasin.feature.community.model.*
import com.app_muslim.surah_yasin.feature.community.viewmodel.CommunityLeaderboardViewModel
import com.app_muslim.surah_yasin.feature.community.viewmodel.CommunityLeaderboardUiState
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

/**
 * Community Leaderboard Screen - Compose Implementation
 * Displays prayer participation leaderboards with Islamic cultural respect
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityLeaderboardScreen(
    onNavigateToProfile: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CommunityLeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTimeFrame by viewModel.selectedTimeFrame.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.refreshLeaderboards()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        LeaderboardTopBar(
            onNavigateBack = onNavigateBack
        )
        
        // Filters and Categories
        LeaderboardFilters(
            selectedTimeFrame = selectedTimeFrame,
            selectedCategory = selectedCategory,
            selectedRegion = selectedRegion,
            onTimeFrameSelected = viewModel::updateTimeFrame,
            onCategorySelected = viewModel::updateCategory,
            onRegionSelected = viewModel::updateRegion
        )
        
        // Leaderboard Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Islamic Guidance Card
                IslamicLeaderboardGuidance()
            }
            
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                // Top 3 Podium
                if (uiState.leaderboard?.entries?.isNotEmpty() == true) {
                    item {
                        TopThreePodium(
                            topEntries = uiState.leaderboard!!.entries.take(3),
                            category = selectedCategory,
                            onProfileClick = onNavigateToProfile
                        )
                    }
                }
                
                // Current User Position (if not in top 3)
                uiState.currentUserPosition?.let { userPosition ->
                    if (userPosition.rank > 3) {
                        item {
                            CurrentUserPositionCard(
                                userPosition = userPosition,
                                category = selectedCategory
                            )
                        }
                    }
                }
                
                // Full Leaderboard List
                uiState.leaderboard?.let { leaderboard ->
                    items(leaderboard.entries.drop(3)) { entry ->
                        LeaderboardEntryCard(
                            entry = entry,
                            category = selectedCategory,
                            onProfileClick = { onNavigateToProfile(entry.userId) }
                        )
                    }
                }
                
                // Empty State
                if (uiState.leaderboard?.entries?.isEmpty() == true && !uiState.isLoading) {
                    item {
                        LeaderboardEmptyState(
                            category = selectedCategory,
                            timeFrame = selectedTimeFrame
                        )
                    }
                }
            }
        }
    }
    
    // Error Handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar
        }
    }
}

@Composable
private fun CommunityLeaderboardScreenContent(
    uiState: CommunityLeaderboardUiState,
    selectedTimeFrame: LeaderboardTimeFrame,
    selectedCategory: LeaderboardCategory,
    selectedRegion: String?,
    onNavigateToProfile: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onTimeFrameSelected: (LeaderboardTimeFrame) -> Unit = {},
    onCategorySelected: (LeaderboardCategory) -> Unit = {},
    onRegionSelected: (String?) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        LeaderboardTopBar(
            onNavigateBack = onNavigateBack
        )
        
        // Filters and Categories
        LeaderboardFilters(
            selectedTimeFrame = selectedTimeFrame,
            selectedCategory = selectedCategory,
            selectedRegion = selectedRegion,
            onTimeFrameSelected = onTimeFrameSelected,
            onCategorySelected = onCategorySelected,
            onRegionSelected = onRegionSelected
        )
        
        // Leaderboard Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Islamic Guidance Card
                IslamicLeaderboardGuidance()
            }
            
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                // Top 3 Podium
                if (uiState.leaderboard?.entries?.isNotEmpty() == true) {
                    item {
                        TopThreePodium(
                            topEntries = uiState.leaderboard!!.entries.take(3),
                            category = selectedCategory,
                            onProfileClick = onNavigateToProfile
                        )
                    }
                }
                
                // Current User Position (if not in top 3)
                uiState.currentUserPosition?.let { userPosition ->
                    if (userPosition.rank > 3) {
                        item {
                            CurrentUserPositionCard(
                                userPosition = userPosition,
                                category = selectedCategory
                            )
                        }
                    }
                }
                
                // Full Leaderboard List
                uiState.leaderboard?.let { leaderboard ->
                    items(leaderboard.entries.drop(3)) { entry ->
                        LeaderboardEntryCard(
                            entry = entry,
                            category = selectedCategory,
                            onProfileClick = { onNavigateToProfile(entry.userId) }
                        )
                    }
                }
                
                // Empty State
                if (uiState.leaderboard?.entries?.isEmpty() == true && !uiState.isLoading) {
                    item {
                        LeaderboardEmptyState(
                            category = selectedCategory,
                            timeFrame = selectedTimeFrame
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaderboardTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Community Leaderboard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "May Allah reward our dedication",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun LeaderboardFilters(
    selectedTimeFrame: LeaderboardTimeFrame,
    selectedCategory: LeaderboardCategory,
    selectedRegion: String?,
    onTimeFrameSelected: (LeaderboardTimeFrame) -> Unit,
    onCategorySelected: (LeaderboardCategory) -> Unit,
    onRegionSelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Time Frame Filter
        Text(
            text = "Time Period",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(LeaderboardTimeFrame.values()) { timeFrame ->
                FilterChip(
                    onClick = { onTimeFrameSelected(timeFrame) },
                    label = {
                        Text(
                            text = getTimeFrameDisplayName(timeFrame),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selected = timeFrame == selectedTimeFrame
                )
            }
        }
        
        // Category Filter
        Text(
            text = "Category",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(getAvailableCategories()) { category ->
                FilterChip(
                    onClick = { onCategorySelected(category) },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = category.icon,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    },
                    selected = category == selectedCategory
                )
            }
        }
    }
}

@Composable
private fun IslamicLeaderboardGuidance() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Islamic Reminder",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "\"Your prayers are for Allah and the departed soul. Rankings are to motivate community participation, not competition.\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun TopThreePodium(
    topEntries: List<LeaderboardEntry>,
    category: LeaderboardCategory,
    onProfileClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆 Top Contributors",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Second Place (if exists)
                if (topEntries.size >= 2) {
                    PodiumPosition(
                        entry = topEntries[1],
                        position = 2,
                        category = category,
                        height = 80.dp,
                        onProfileClick = onProfileClick
                    )
                }
                
                // First Place
                if (topEntries.isNotEmpty()) {
                    PodiumPosition(
                        entry = topEntries[0],
                        position = 1,
                        category = category,
                        height = 100.dp,
                        onProfileClick = onProfileClick
                    )
                }
                
                // Third Place (if exists)
                if (topEntries.size >= 3) {
                    PodiumPosition(
                        entry = topEntries[2],
                        position = 3,
                        category = category,
                        height = 60.dp,
                        onProfileClick = onProfileClick
                    )
                }
            }
        }
    }
}

@Composable
private fun PodiumPosition(
    entry: LeaderboardEntry,
    position: Int,
    category: LeaderboardCategory,
    height: androidx.compose.ui.unit.Dp,
    onProfileClick: (String) -> Unit
) {
    val colors = when (position) {
        1 -> listOf(Color(0xFFFFD700), Color(0xFFFF8F00)) // Gold
        2 -> listOf(Color(0xFFC0C0C0), Color(0xFF757575)) // Silver
        3 -> listOf(Color(0xFFCD7F32), Color(0xFF8D6E63)) // Bronze
        else -> listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
    ) {
        // Crown/Medal
        Icon(
            imageVector = when (position) {
                1 -> Icons.Default.EmojiEvents
                else -> Icons.Default.WorkspacePremium
            },
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = colors[0]
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // User Avatar (simplified)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(colors)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.displayName.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Name
        Text(
            text = entry.displayName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // Score
        Text(
            text = formatScore(entry.score, category),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Podium Base
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(height)
                .background(
                    brush = Brush.verticalGradient(colors.map { it.copy(alpha = 0.3f) }),
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$position",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors[0]
            )
        }
    }
}

@Composable
private fun CurrentUserPositionCard(
    userPosition: LeaderboardEntry,
    category: LeaderboardCategory
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Rank
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.tertiary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${userPosition.rank}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Column {
                    Text(
                        text = "Your Position",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = formatScore(userPosition.score, category),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            // Change indicator
            if (userPosition.change != RankChange.NO_CHANGE) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (userPosition.change == RankChange.UP) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (userPosition.change == RankChange.UP) Color(0xFF4CAF50) else Color(0xFFFF5722)
                    )
                    
                    Text(
                        text = when (userPosition.change) {
                            RankChange.UP -> "↑"
                            RankChange.DOWN -> "↓"
                            else -> "-"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (userPosition.change == RankChange.UP) Color(0xFF4CAF50) else Color(0xFFFF5722)
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardEntryCard(
    entry: LeaderboardEntry,
    category: LeaderboardCategory,
    onProfileClick: () -> Unit
) {
    Card(
        onClick = onProfileClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Rank
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${entry.rank}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = entry.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = entry.regionName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // Show any additional statistics if available
                        Badge(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ) {
                            Text(
                                text = "Active member",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatScore(entry.score, category),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (entry.change != RankChange.NO_CHANGE) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = if (entry.change == RankChange.UP) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = if (entry.change == RankChange.UP) Color(0xFF4CAF50) else Color(0xFFFF5722)
                        )
                        
                        Text(
                            text = when (entry.change) {
                                RankChange.UP -> "↑"
                                RankChange.DOWN -> "↓"
                                else -> "-"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (entry.change == RankChange.UP) Color(0xFF4CAF50) else Color(0xFFFF5722)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardEmptyState(
    category: LeaderboardCategory,
    timeFrame: LeaderboardTimeFrame
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Contributions Yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Be the first to contribute to the ${category.name.lowercase()} leaderboard for this ${getTimeFrameDisplayName(timeFrame).lowercase()}.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// Helper Functions

private fun getTimeFrameDisplayName(timeFrame: LeaderboardTimeFrame): String {
    return when (timeFrame) {
        LeaderboardTimeFrame.TODAY -> "Today"
        LeaderboardTimeFrame.THIS_WEEK -> "This Week"
        LeaderboardTimeFrame.THIS_MONTH -> "This Month"
        LeaderboardTimeFrame.ALL_TIME -> "All Time"
    }
}

private fun getAvailableCategories(): List<LeaderboardCategory> {
    return listOf(
        LeaderboardCategory.TOTAL_PRAYERS,
        LeaderboardCategory.MEMORIAL_PARTICIPATION,
        LeaderboardCategory.COMMUNITY_ENGAGEMENT,
        LeaderboardCategory.HELPING_FAMILIES,
        LeaderboardCategory.CONSISTENCY
    )
}

private fun getCategoryIcon(category: LeaderboardCategory): String {
    return category.icon
}

private fun formatScore(score: Long, category: LeaderboardCategory): String {
    return when (category.categoryId) {
        "total_prayers" -> "$score prayers"
        "memorial_participation" -> "$score memorials"
        "community_engagement" -> "$score interactions"
        "helping_families" -> "$score families helped"
        "consistency" -> "$score consecutive days"
        else -> "$score points"
    }
}

// Preview Functions

private fun createMockLeaderboard(): CommunityLeaderboard {
    val mockEntries = listOf(
        LeaderboardEntry(
            userId = "user_1",
            displayName = "Ahmad Abdullah",
            profilePictureUrl = null,
            rank = 1,
            score = 1200L,
            previousRank = 1,
            change = RankChange.UP,
            regionCode = "US",
            regionName = "North America",
            badges = emptyList(),
            statistics = mapOf("total_prayers" to 1200L, "sessions" to 120L),
            lastActiveAt = java.time.ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, java.time.ZoneOffset.UTC)
        ),
        LeaderboardEntry(
            userId = "user_2",
            displayName = "Fatimah Hassan",
            profilePictureUrl = null,
            rank = 2,
            score = 1150L,
            previousRank = 2,
            change = RankChange.NO_CHANGE,
            regionCode = "US",
            regionName = "North America",
            badges = emptyList(),
            statistics = mapOf("total_prayers" to 1150L, "sessions" to 115L),
            lastActiveAt = java.time.ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, java.time.ZoneOffset.UTC)
        ),
        LeaderboardEntry(
            userId = "user_3",
            displayName = "Omar Khalil",
            profilePictureUrl = null,
            rank = 3,
            score = 1100L,
            previousRank = 4,
            change = RankChange.UP,
            regionCode = "US",
            regionName = "North America",
            badges = emptyList(),
            statistics = mapOf("total_prayers" to 1100L, "sessions" to 110L),
            lastActiveAt = java.time.ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, java.time.ZoneOffset.UTC)
        )
    )
    
    return CommunityLeaderboard(
        leaderboardId = "preview_leaderboard",
        type = LeaderboardType.COMMUNITY,
        category = LeaderboardCategory.TOTAL_PRAYERS,
        timeFrame = LeaderboardTimeFrame.THIS_WEEK,
        region = null,
        entries = mockEntries,
        totalEntries = mockEntries.size,
        lastUpdated = java.time.ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, java.time.ZoneOffset.UTC),
        isRealTime = false
    )
}

@Preview(showBackground = true, name = "Community Leaderboard - Loading")
@Composable
private fun PreviewCommunityLeaderboardLoading() {
    TahlilTheme {
        CommunityLeaderboardScreenContent(
            uiState = CommunityLeaderboardUiState(isLoading = true),
            selectedTimeFrame = LeaderboardTimeFrame.THIS_WEEK,
            selectedCategory = LeaderboardCategory.TOTAL_PRAYERS,
            selectedRegion = null,
            onNavigateToProfile = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Community Leaderboard - With Data")
@Composable
private fun PreviewCommunityLeaderboardWithData() {
    TahlilTheme {
        CommunityLeaderboardScreenContent(
            uiState = CommunityLeaderboardUiState(
                isLoading = false,
                leaderboard = createMockLeaderboard()
            ),
            selectedTimeFrame = LeaderboardTimeFrame.THIS_WEEK,
            selectedCategory = LeaderboardCategory.TOTAL_PRAYERS,
            selectedRegion = null,
            onNavigateToProfile = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Community Leaderboard - Dark", 
         uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewCommunityLeaderboardDark() {
    TahlilTheme {
        CommunityLeaderboardScreenContent(
            uiState = CommunityLeaderboardUiState(
                isLoading = false,
                leaderboard = createMockLeaderboard()
            ),
            selectedTimeFrame = LeaderboardTimeFrame.THIS_WEEK,
            selectedCategory = LeaderboardCategory.TOTAL_PRAYERS,
            selectedRegion = null,
            onNavigateToProfile = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Community Leaderboard - Tablet",
         device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PreviewCommunityLeaderboardTablet() {
    TahlilTheme {
        CommunityLeaderboardScreenContent(
            uiState = CommunityLeaderboardUiState(
                isLoading = false,
                leaderboard = createMockLeaderboard()
            ),
            selectedTimeFrame = LeaderboardTimeFrame.THIS_WEEK,
            selectedCategory = LeaderboardCategory.TOTAL_PRAYERS,
            selectedRegion = null,
            onNavigateToProfile = {},
            onNavigateBack = {}
        )
    }
}

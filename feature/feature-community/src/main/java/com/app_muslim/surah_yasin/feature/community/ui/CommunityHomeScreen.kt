package com.app_muslim.surah_yasin.feature.community.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.coroutines.delay
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app_muslim.surah_yasin.feature.community.model.*
import com.app_muslim.surah_yasin.feature.community.viewmodel.CommunityHomeViewModel
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme

/**
 * Community Home Screen with Real-time Prayer Features
 * Displays global prayer participation, active sessions, and community activity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityHomeScreen(
    onNavigateToSession: (String) -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToCreateSession: () -> Unit,
    viewModel: CommunityHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
    
    // Refresh data when screen appears
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Global Prayer Statistics Header
            item {
                GlobalPrayerStatsCard(
                    stats = uiState.globalStats,
                    selectedRegion = selectedRegion,
                    onRegionChange = { region ->
                        viewModel.handleEvent(CommunityEvent.ChangeRegion(region))
                    }
                )
            }
            
            // Real-time Prayer Counter
            item {
                RealTimePrayerCounter(
                    totalActivePrayers = uiState.globalStats.totalActivePrayers,
                    totalParticipants = uiState.globalStats.totalParticipants
                )
            }
            
            // Active Community Sessions
            item {
                ActiveSessionsSection(
                    sessions = uiState.activeSessions,
                    onJoinSession = { sessionId ->
                        viewModel.handleEvent(CommunityEvent.JoinSession(sessionId))
                    },
                    onNavigateToSession = onNavigateToSession
                )
            }
            
            // Regional Leaderboard Preview
            item {
                RegionalLeaderboardPreview(
                    regionalStats = uiState.regionalStats,
                    onNavigateToLeaderboard = onNavigateToLeaderboard
                )
            }
            
            // Prayer Milestone Celebrations
            if (uiState.recentActivity.any { it.activityType == ActivityType.MILESTONE_REACHED }) {
                item {
                    PrayerMilestoneCelebration(
                        milestones = uiState.recentActivity.filter { 
                            it.activityType == ActivityType.MILESTONE_REACHED 
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            // Recent Prayer Activity Feed
            item {
                PrayerActivityFeed(
                    activities = uiState.recentActivity,
                    selectedRegion = selectedRegion
                )
            }
            
            // Upcoming Community Events
            if (uiState.upcomingEvents.isNotEmpty()) {
                item {
                    UpcomingEventsSection(
                        events = uiState.upcomingEvents
                    )
                }
            }
            
            // Add bottom padding for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        
        // Loading overlay
        if (uiState.isLoading) {
            LoadingOverlay()
        }
        
        // Error snackbar
        uiState.errorMessage?.let { error ->
            LaunchedEffect(error) {
                // Show error message
                viewModel.clearError()
            }
        }
        
        // Floating Action Button for creating sessions
        FloatingActionButton(
            onClick = onNavigateToCreateSession,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF4CAF50)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Prayer Session",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun GlobalPrayerStatsCard(
    stats: GlobalPrayerStats,
    selectedRegion: String,
    onRegionChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🕌 Global Prayer Community",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Active Prayers",
                    value = "${stats.totalActivePrayers}",
                    icon = "🤲"
                )
                
                StatItem(
                    label = "Participants",
                    value = "${stats.totalParticipants}",
                    icon = "👥"
                )
                
                StatItem(
                    label = "Today",
                    value = "${stats.totalPrayersToday}",
                    icon = "📅"
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Most Popular: ${stats.topPrayerType}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            
            // Region selector
            Spacer(modifier = Modifier.height(16.dp))
            
            RegionSelector(
                selectedRegion = selectedRegion,
                onRegionChange = onRegionChange
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RealTimePrayerCounter(
    totalActivePrayers: Long,
    totalParticipants: Long
) {
    val animatedPrayerCount by animateIntAsState(
        targetValue = totalActivePrayers.toInt(),
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic)
    )
    
    val animatedParticipantCount by animateIntAsState(
        targetValue = totalParticipants.toInt(),
        animationSpec = tween(durationMillis = 1200, easing = EaseOutCubic)
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Live",
                    tint = Color.Red
                )
                Text(
                    text = "LIVE",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "$animatedPrayerCount",
                style = MaterialTheme.typography.displayLarge,
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "prayers happening now",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "by $animatedParticipantCount Muslims worldwide",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            // Pulsing animation for live indicator
            PulsingDot()
        }
    }
}

@Composable
private fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size((8 * scale).dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF4CAF50))
        )
        
        Text(
            text = "Live updates",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF4CAF50)
        )
    }
}

@Composable
private fun ActiveSessionsSection(
    sessions: List<CommunityPrayerSession>,
    onJoinSession: (String) -> Unit,
    onNavigateToSession: (String) -> Unit
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
                    text = "Active Prayer Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${sessions.size} active",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (sessions.isEmpty()) {
                Text(
                    text = "No active sessions. Be the first to start one!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 20.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                sessions.take(3).forEach { session ->
                    CommunitySessionCard(
                        session = session,
                        onJoin = { onJoinSession(session.sessionId) },
                        onClick = { onNavigateToSession(session.sessionId) }
                    )
                    
                    if (session != sessions.take(3).last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                if (sessions.size > 3) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { /* Navigate to full sessions list */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View all ${sessions.size} sessions")
                    }
                }
            }
        }
    }
}

@Composable
private fun CommunitySessionCard(
    session: CommunityPrayerSession,
    onJoin: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = session.prayerType.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "by ${session.hostDisplayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                SessionStatusBadge(status = session.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${session.participants.size}/${session.maxParticipants} participants",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    Text(
                        text = "Target: ${session.targetPrayerCount} prayers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                OutlinedButton(
                    onClick = onJoin,
                    modifier = Modifier.height(32.dp),
                    enabled = session.status in listOf(SessionStatus.WAITING, SessionStatus.STARTING)
                ) {
                    Text("Join", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun SessionStatusBadge(status: SessionStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        SessionStatus.WAITING -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "Waiting")
        SessionStatus.STARTING -> Triple(Color(0xFFF3E5F5), Color(0xFF7B1FA2), "Starting")
        SessionStatus.IN_PROGRESS -> Triple(Color(0xFFE8F5E8), Color(0xFF2E7D32), "Active")
        SessionStatus.PAUSED -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "Paused")
        SessionStatus.COMPLETED -> Triple(Color(0xFFE8F5E8), Color(0xFF2E7D32), "Completed")
        SessionStatus.CANCELLED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Cancelled")
        SessionStatus.EXPIRED -> Triple(Color(0xFFFAFAFA), Color(0xFF616161), "Expired")
    }
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RegionalLeaderboardPreview(
    regionalStats: List<RegionalPrayerStats>,
    onNavigateToLeaderboard: () -> Unit
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
                    text = "🏆 Regional Leaderboard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onNavigateToLeaderboard) {
                    Text("View All")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            regionalStats.take(3).forEachIndexed { index, stats ->
                RegionalStatsItem(
                    rank = stats.rank,
                    regionName = stats.regionName,
                    activePrayers = stats.activePrayers,
                    totalParticipants = stats.totalParticipants
                )
                
                if (index < 2) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun RegionalStatsItem(
    rank: Int,
    regionName: String,
    activePrayers: Long,
    totalParticipants: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Rank badge
        Surface(
            shape = RoundedCornerShape(50),
            color = when (rank) {
                1 -> Color(0xFFFFD700) // Gold
                2 -> Color(0xFFC0C0C0) // Silver  
                3 -> Color(0xFFCD7F32) // Bronze
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.size(32.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = regionName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "$activePrayers active • $totalParticipants participants",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun PrayerActivityFeed(
    activities: List<PrayerActivityItem>,
    selectedRegion: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📢 Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (activities.isEmpty()) {
                Text(
                    text = "No recent activity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                activities.take(5).forEach { activity ->
                    ActivityItem(activity = activity)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ActivityItem(activity: PrayerActivityItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Activity type icon
        Surface(
            shape = RoundedCornerShape(50),
            color = Color(0xFF4CAF50).copy(alpha = 0.1f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = when (activity.activityType) {
                        ActivityType.PRAYER_COMPLETED -> "🤲"
                        ActivityType.SESSION_JOINED -> "➕"
                        ActivityType.SESSION_HOSTED -> "🎯"
                        ActivityType.MILESTONE_REACHED -> "🏆"
                        ActivityType.BADGE_EARNED -> "🎖️"
                        ActivityType.MEMORIAL_CREATED -> "🕊️"
                        ActivityType.STREAK_ACHIEVEMENT -> "🔥"
                    },
                    fontSize = 14.sp
                )
            }
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.userDisplayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = activity.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Text(
            text = formatTimeAgo(activity.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun UpcomingEventsSection(
    events: List<CommunityPrayerEvent>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📅 Upcoming Events",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            events.take(2).forEach { event ->
                CommunityEventCard(event = event)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CommunityEventCard(event: CommunityPrayerEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDateTime(event.startTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50)
                )
                
                Text(
                    text = "${event.currentParticipants} participants",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun RegionSelector(
    selectedRegion: String,
    onRegionChange: (String) -> Unit
) {
    val regions = listOf(
        "global" to "Global",
        "middle_east" to "Middle East",
        "south_asia" to "South Asia", 
        "southeast_asia" to "Southeast Asia",
        "north_america" to "North America",
        "europe" to "Europe",
        "africa" to "Africa"
    )
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(regions) { (code, name) ->
            FilterChip(
                onClick = { onRegionChange(code) },
                label = { Text(name, fontSize = 12.sp) },
                selected = selectedRegion == code,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color.White,
                    selectedLabelColor = Color(0xFF4CAF50)
                )
            )
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF4CAF50)
        )
    }
}

// Helper functions
private fun formatTimeAgo(timestamp: java.time.ZonedDateTime): String {
    val now = java.time.ZonedDateTime.now()
    val duration = java.time.Duration.between(timestamp, now)
    
    return when {
        duration.toMinutes() < 1 -> "now"
        duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
        duration.toHours() < 24 -> "${duration.toHours()}h ago"
        else -> "${duration.toDays()}d ago"
    }
}

private fun formatDateTime(timestamp: java.time.ZonedDateTime): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm")
    return timestamp.format(formatter)
}

@Composable
private fun PrayerMilestoneCelebration(
    milestones: List<PrayerActivityItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated celebration header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CelebrationIcon()
                
                Text(
                    text = "🎉 Prayer Milestones Reached!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                CelebrationIcon()
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Milestone items with animations
            milestones.take(3).forEach { milestone ->
                AnimatedMilestoneItem(milestone = milestone)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // View all milestones button
            if (milestones.size > 3) {
                TextButton(
                    onClick = { /* Navigate to full milestones */ }
                ) {
                    Text("View all ${milestones.size} milestones")
                }
            }
        }
    }
}

@Composable
private fun CelebrationIcon() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier
            .size(24.dp)
            .graphicsLayer {
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
            }
    ) {
        Text(
            text = "✨",
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun AnimatedMilestoneItem(
    milestone: PrayerActivityItem,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300) // Stagger the animation
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(500, easing = EaseOutBack)
        ) + fadeIn(animationSpec = tween(500))
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Milestone icon with glow effect
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700),
                                    Color(0xFFFFA500)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 20.sp
                    )
                }
                
                // Milestone details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = milestone.message,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "by ${milestone.userDisplayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    milestone.prayerCount?.let { count ->
                        Text(
                            text = "$count prayers completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
                
                // Time indicator
                Text(
                    text = formatTimeAgo(milestone.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
// ============================================================================
// PREVIEW FUNCTIONS & DATA PROVIDERS
// ============================================================================

class CommunityHomeUiStateProvider : PreviewParameterProvider<CommunityHomeUiState> {
    override val values: Sequence<CommunityHomeUiState> = sequenceOf(
        CommunityHomeUiState(), // Default loading state
        CommunityHomeUiState(
            isLoading = false,
            globalStats = GlobalPrayerStats(
                totalActivePrayers = 1247,
                totalParticipants = 8934,
                totalPrayersToday = 45782,
                topPrayerType = "Yasin"
            ),
            activeSessions = listOf(
                CommunityPrayerSession(
                    sessionId = "session-1",
                    memorialId = "memorial-1",
                    hostUserId = "user-1",
                    hostDisplayName = "Ahmad Ibn Muhammad",
                    prayerType = CommunityPrayerType.TAHLIL,
                    regionCode = "ME",
                    startTime = java.time.ZonedDateTime.now(),
                    estimatedDuration = 15,
                    targetPrayerCount = 100
                ),
                CommunityPrayerSession(
                    sessionId = "session-2", 
                    memorialId = "memorial-2",
                    hostUserId = "user-2",
                    hostDisplayName = "Fatimah Al-Zahra",
                    prayerType = CommunityPrayerType.YASIN,
                    regionCode = "SEA",
                    startTime = java.time.ZonedDateTime.now(),
                    estimatedDuration = 20,
                    targetPrayerCount = 1
                )
            ),
            recentActivity = listOf(
                PrayerActivityItem(
                    activityId = "activity-1",
                    activityType = ActivityType.PRAYER_COMPLETED,
                    userId = "user-3",
                    userDisplayName = "Hassan Al-Basri",
                    prayerType = CommunityPrayerType.TAHLIL,
                    prayerCount = 100,
                    regionCode = "ME",
                    timestamp = java.time.ZonedDateTime.now().minusMinutes(5),
                    message = "completed 100 Tahlil recitations"
                )
            )
        ), // Full active state
        CommunityHomeUiState(
            isLoading = false,
            globalStats = GlobalPrayerStats(
                totalActivePrayers = 856,
                totalParticipants = 5421,
                totalPrayersToday = 23891,
                topPrayerType = "Al-Fatihah"
            ),
            activeSessions = emptyList(), // No active sessions
            recentActivity = emptyList()
        ) // Quiet period state
    )
}

@Composable
fun CommunityHomeScreenPreview(
    uiState: CommunityHomeUiState = CommunityHomeUiState()
) {
    CommunityHomeScreenContent(
        uiState = uiState,
        onNavigateToSession = { },
        onNavigateToLeaderboard = { },
        onNavigateToCreateSession = { },
        onRefresh = { }
    )
}

@Composable
private fun CommunityHomeScreenContent(
    uiState: CommunityHomeUiState,
    onNavigateToSession: (String) -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToCreateSession: () -> Unit,
    onRefresh: () -> Unit
) {
    // This is a preview wrapper - simplified implementation
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🌍 Global Community",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Global Statistics Card
            if (uiState.globalStats != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Global Prayer Statistics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("${uiState.globalStats.totalActivePrayers}")
                                Text(
                                    "Active Sessions",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column {
                                Text("${uiState.globalStats.totalParticipants}")
                                Text(
                                    "Users Online",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column {
                                Text("${uiState.globalStats.totalPrayersToday}")
                                Text(
                                    "Prayers Today",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Trending: ${uiState.globalStats.topPrayerType}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            
            // Active Sessions
            Text(
                "Active Prayer Sessions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (uiState.activeSessions.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No active sessions right now\nCreate one to start praying with the community",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                uiState.activeSessions.take(3).forEach { session ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                session.hostDisplayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${session.prayerType.displayName} Prayer • ${session.participants.size} participants",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Region: ${session.regionCode}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToCreateSession,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Create Session")
                }
                OutlinedButton(
                    onClick = onNavigateToLeaderboard,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Leaderboard")
                }
            }
        }
    }
}

// ============================================================================
// COMMUNITY HOME SCREEN PREVIEWS
// ============================================================================


@Preview(showBackground = true, name = "Community Home - Loading")
@Composable
private fun PreviewCommunityHomeLoading() {
    TahlilTheme {
        CommunityHomeScreenPreview(
            uiState = CommunityHomeUiState(isLoading = true)
        )
    }
}

@Preview(showBackground = true, name = "Community Home - Global Stats Card")
@Composable
private fun PreviewGlobalPrayerStatsCard() {
    TahlilTheme {
        GlobalPrayerStatsCard(
            stats = GlobalPrayerStats(
                totalActivePrayers = 1247,
                totalParticipants = 8934,
                totalPrayersToday = 45782,
                topPrayerType = "Yasin"
            ),
            selectedRegion = "Global",
            onRegionChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Real-time Prayer Counter")
@Composable  
private fun PreviewRealTimePrayerCounter() {
    TahlilTheme {
        RealTimePrayerCounter(
            totalActivePrayers = 1500,
            totalParticipants = 9500
        )
    }
}

@Preview(showBackground = true, name = "Community Home - Dark", 
         uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewCommunityHomeDark() {
    TahlilTheme {
        CommunityHomeScreenPreview(
            uiState = CommunityHomeUiState(
                isLoading = false,
                globalStats = GlobalPrayerStats(
                    totalActivePrayers = 1500,
                    totalParticipants = 9500,
                    totalPrayersToday = 45000,
                    topPrayerType = "Yasin"
                )
            )
        )
    }
}

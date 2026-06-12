package com.app_muslim.surah_yasin.feature.community.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.feature.community.model.*
import kotlinx.coroutines.delay
import java.time.ZonedDateTime

/**
 * Global Milestone Celebrations Component
 * Animated celebrations for community achievements with Islamic design elements
 */

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GlobalMilestoneCelebrations(
    milestones: List<GlobalMilestone>,
    celebrationEvents: List<MilestoneCelebration>,
    onMilestoneClick: (GlobalMilestone) -> Unit,
    onCelebrationDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(celebrationEvents) {
        // Auto-dismiss celebrations after 10 seconds
        celebrationEvents.forEach { celebration ->
            delay(10000)
            onCelebrationDismiss(celebration.id)
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        // Main milestone content
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active milestones
            if (milestones.isNotEmpty()) {
                ActiveMilestonesSection(
                    milestones = milestones,
                    onMilestoneClick = onMilestoneClick
                )
            }
            
            // Recent achievements
            val completedMilestones = milestones.filter { it.isCompleted }
            if (completedMilestones.isNotEmpty()) {
                RecentAchievementsSection(
                    achievements = completedMilestones.take(5)
                )
            }
        }
        
        // Celebration overlays
        celebrationEvents.forEach { celebration ->
            CelebrationOverlay(
                celebration = celebration,
                onDismiss = { onCelebrationDismiss(celebration.id) }
            )
        }
    }
}

@Composable
private fun ActiveMilestonesSection(
    milestones: List<GlobalMilestone>,
    onMilestoneClick: (GlobalMilestone) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    text = "🎯 Active Milestones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${milestones.filter { !it.isCompleted }.size} active",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = milestones.filter { !it.isCompleted }.take(5),
                    key = { it.milestoneId }
                ) { milestone ->
                    AnimatedMilestoneCard(
                        milestone = milestone,
                        onClick = { onMilestoneClick(milestone) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedMilestoneCard(
    milestone: GlobalMilestone,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (milestone.currentValue.toFloat() / milestone.targetValue.toFloat()).coerceAtMost(1f)
    
    // Animated progress
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = EaseOutCubic)
    )
    
    // Pulsing effect when close to completion (>90%)
    val pulseAnimation = rememberInfiniteTransition()
    val pulseAlpha by pulseAnimation.animateFloat(
        initialValue = 1f,
        targetValue = if (progress > 0.9f) 0.7f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = pulseAlpha },
        colors = CardDefaults.cardColors(
            containerColor = if (progress > 0.9f) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = milestone.icon,
                        fontSize = 20.sp
                    )
                    
                    Text(
                        text = milestone.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f, false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (progress > 0.9f) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress bar
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when {
                    progress >= 1f -> Color(0xFF4CAF50)
                    progress > 0.9f -> Color(0xFFFF9800)
                    progress > 0.5f -> Color(0xFF2196F3)
                    else -> MaterialTheme.colorScheme.outline
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress text and countries
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatNumber(milestone.currentValue)} / ${formatNumber(milestone.targetValue)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                if (milestone.participatingCountries.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Countries",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        
                        Text(
                            text = "${milestone.participatingCountries.size} countries",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentAchievementsSection(
    achievements: List<GlobalMilestone>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    text = "🏆 Recent Achievements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${achievements.size} completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(
                    items = achievements,
                    key = { it.milestoneId }
                ) { achievement ->
                    AchievementBadge(achievement = achievement)
                }
            }
        }
    }
}

@Composable
private fun AchievementBadge(
    achievement: GlobalMilestone,
    modifier: Modifier = Modifier
) {
    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Card(
        modifier = modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                // Rotating background glow
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .rotate(rotation)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700).copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                
                Text(
                    text = achievement.icon,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            achievement.achievedAt?.let { achievedAt ->
                Text(
                    text = formatRelativeTime(achievedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CelebrationOverlay(
    celebration: MilestoneCelebration,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(celebration) {
        isVisible = true
        delay(8000) // Auto dismiss after 8 seconds
        isVisible = false
        delay(500) // Wait for exit animation
        onDismiss()
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(800, easing = EaseOutBack)
        ) + fadeIn(animationSpec = tween(800)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(500, easing = EaseInBack)
        ) + fadeOut(animationSpec = tween(500)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            CelebrationCard(
                celebration = celebration,
                onDismiss = { 
                    isVisible = false
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun CelebrationCard(
    celebration: MilestoneCelebration,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Confetti background
            ConfettiAnimation(
                modifier = Modifier.fillMaxSize(),
                isActive = true
            )
            
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = celebration.icon,
                            fontSize = 24.sp
                        )
                        
                        Text(
                            text = "Milestone Achieved!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Celebration content
                Text(
                    text = celebration.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = celebration.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Achievement details
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🎯 Achievement Value",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            
                            Text(
                                text = formatNumber(celebration.achievedValue),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        if (celebration.participatingCountries.isNotEmpty()) {
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "🌍 Global Reach",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                
                                Text(
                                    text = "${celebration.participatingCountries.size} countries",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Celebration message
                Text(
                    text = celebration.celebrationMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ConfettiAnimation(
    modifier: Modifier = Modifier,
    isActive: Boolean
) {
    if (!isActive) return
    
    // Simple confetti effect using multiple animated elements
    val confettiColors = listOf(
        Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFFF9800),
        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFFFFD700)
    )
    
    Box(modifier = modifier) {
        repeat(20) { index ->
            ConfettiParticle(
                color = confettiColors[index % confettiColors.size],
                delay = index * 100
            )
        }
    }
}

@Composable
private fun ConfettiParticle(
    color: Color,
    delay: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000 + delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val offsetX by infiniteTransition.animateFloat(
        initialValue = (0..300).random().toFloat(),
        targetValue = (50..250).random().toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + delay, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500 + delay, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier
            .offset(offsetX.dp, offsetY.dp)
            .rotate(rotation)
            .size(8.dp)
            .background(color, CircleShape)
    )
}

// Helper functions
private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

private fun formatRelativeTime(dateTime: java.time.ZonedDateTime): String {
    val now = java.time.ZonedDateTime.now()
    val duration = java.time.Duration.between(dateTime, now)
    
    return when {
        duration.toMinutes() < 1 -> "Just now"
        duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
        duration.toHours() < 24 -> "${duration.toHours()}h ago"
        duration.toDays() < 7 -> "${duration.toDays()}d ago"
        else -> "${duration.toDays() / 7}w ago"
    }
}

// Data models for celebrations
data class MilestoneCelebration(
    val id: String,
    val title: String,
    val description: String,
    val celebrationMessage: String,
    val achievedValue: Long,
    val participatingCountries: List<String>,
    val icon: String = "🎉",
    val timestamp: java.time.ZonedDateTime = java.time.ZonedDateTime.now()
)

// Preview Parameter Providers
class GlobalMilestoneCelebrationProvider : PreviewParameterProvider<Pair<List<GlobalMilestone>, List<MilestoneCelebration>>> {
    override val values = sequenceOf(
        // Active milestones with no celebrations
        Pair(
            listOf(
                GlobalMilestone(
                    milestoneId = "1M_prayers",
                    type = MilestoneType.TOTAL_PRAYERS,
                    title = "1 Million Memorial Prayers",
                    description = "Unite in remembrance worldwide",
                    targetValue = 1_000_000,
                    currentValue = 850_000,
                    icon = "🤲",
                    participatingCountries = listOf("Indonesia", "Pakistan", "Turkey", "Egypt", "Bangladesh"),
                    isCompleted = false
                ),
                GlobalMilestone(
                    milestoneId = "500_countries",
                    type = MilestoneType.COUNTRIES_REACHED,
                    title = "500 Cities Participating",
                    description = "Global Islamic community reach",
                    targetValue = 500,
                    currentValue = 420,
                    icon = "🌍",
                    participatingCountries = listOf("Malaysia", "UAE", "Morocco", "Algeria"),
                    isCompleted = false
                ),
                GlobalMilestone(
                    milestoneId = "ramadan_unity",
                    type = MilestoneType.TOTAL_PRAYERS,
                    title = "Ramadan Unity Goal",
                    description = "Special Ramadan memorial prayers",
                    targetValue = 2_000_000,
                    currentValue = 1_850_000,
                    icon = "🌙",
                    participatingCountries = listOf("Saudi Arabia", "Iran", "Iraq", "Jordan"),
                    isCompleted = false
                )
            ),
            emptyList()
        ),
        
        // Completed milestones with active celebration
        Pair(
            listOf(
                GlobalMilestone(
                    milestoneId = "100k_completed",
                    type = MilestoneType.TOTAL_PRAYERS,
                    title = "100K Memorial Prayers",
                    description = "First major community milestone",
                    targetValue = 100_000,
                    currentValue = 100_000,
                    icon = "🎯",
                    participatingCountries = listOf("Indonesia", "Malaysia"),
                    isCompleted = true,
                    achievedAt = ZonedDateTime.now().minusHours(2)
                ),
                GlobalMilestone(
                    milestoneId = "50_countries",
                    type = MilestoneType.COUNTRIES_REACHED,
                    title = "50 Countries United",
                    description = "Global Islamic brotherhood",
                    targetValue = 50,
                    currentValue = 50,
                    icon = "🕊️",
                    participatingCountries = (1..50).map { "Country$it" },
                    isCompleted = true,
                    achievedAt = ZonedDateTime.now().minusMinutes(15)
                )
            ),
            listOf(
                MilestoneCelebration(
                    id = "celebration_100k",
                    title = "100K Memorial Prayers Achieved!",
                    description = "The Islamic community has come together to perform 100,000 memorial prayers.",
                    celebrationMessage = "Barakallahu fi kum! May Allah accept our collective prayers and grant peace to all souls.",
                    achievedValue = 100_000,
                    participatingCountries = listOf("Indonesia", "Malaysia", "Pakistan", "Turkey"),
                    icon = "🎉"
                )
            )
        ),
        
        // Mix of active and completed with multiple celebrations
        Pair(
            listOf(
                GlobalMilestone(
                    milestoneId = "hajj_prayers",
                    type = MilestoneType.TOTAL_PRAYERS,
                    title = "Hajj Season Memorial Prayers",
                    description = "Special prayers during Hajj season",
                    targetValue = 5_000_000,
                    currentValue = 4_750_000,
                    icon = "🕋",
                    participatingCountries = listOf("Saudi Arabia", "Indonesia", "Pakistan", "Bangladesh", "Egypt"),
                    isCompleted = false
                ),
                GlobalMilestone(
                    milestoneId = "youth_engagement",
                    type = MilestoneType.GLOBAL_PARTICIPANTS,
                    title = "Young Muslims United",
                    description = "Youth participation in memorial prayers",
                    targetValue = 250_000,
                    currentValue = 250_000,
                    icon = "👥",
                    participatingCountries = listOf("Turkey", "Morocco", "Algeria", "Tunisia"),
                    isCompleted = true,
                    achievedAt = ZonedDateTime.now().minusHours(6)
                )
            ),
            listOf(
                MilestoneCelebration(
                    id = "celebration_youth",
                    title = "Youth Milestone Achieved!",
                    description = "250,000 young Muslims have participated in memorial prayers.",
                    celebrationMessage = "Mashallah! The next generation continues our beautiful traditions.",
                    achievedValue = 250_000,
                    participatingCountries = listOf("Turkey", "Morocco", "Algeria", "Tunisia"),
                    icon = "✨"
                ),
                MilestoneCelebration(
                    id = "celebration_global",
                    title = "Global Unity Milestone!",
                    description = "All continents now participating in memorial prayers.",
                    celebrationMessage = "SubhanAllah! Our ummah spans the entire globe in remembrance.",
                    achievedValue = 7,
                    participatingCountries = (1..195).map { "Country$it" },
                    icon = "🌏"
                )
            )
        )
    )
}

class ActiveMilestonesProvider : PreviewParameterProvider<List<GlobalMilestone>> {
    override val values = sequenceOf(
        // Near completion milestones (>90%)
        listOf(
            GlobalMilestone(
                milestoneId = "million_dua",
                type = MilestoneType.TOTAL_PRAYERS,
                title = "1 Million Dua for Deceased",
                description = "Collective supplication milestone",
                targetValue = 1_000_000,
                currentValue = 950_000, // 95%
                icon = "🤲",
                participatingCountries = listOf("Indonesia", "Pakistan", "Bangladesh", "India", "Turkey"),
                isCompleted = false
            ),
            GlobalMilestone(
                milestoneId = "surah_yasin",
                type = MilestoneType.TOTAL_PRAYERS,
                title = "Surah Yasin Recitations",
                description = "Heart of the Quran for our loved ones",
                targetValue = 500_000,
                currentValue = 475_000, // 95%
                icon = "📖",
                participatingCountries = listOf("Saudi Arabia", "UAE", "Kuwait", "Qatar"),
                isCompleted = false
            )
        ),
        
        // Mid-progress milestones
        listOf(
            GlobalMilestone(
                milestoneId = "family_unity",
                type = MilestoneType.FAMILY_SHARING,
                title = "Family Memorial Sharing",
                description = "Families united in remembrance",
                targetValue = 100_000,
                currentValue = 60_000,
                icon = "👨‍👩‍👧‍👦",
                participatingCountries = listOf("Malaysia", "Singapore", "Brunei"),
                isCompleted = false
            ),
            GlobalMilestone(
                milestoneId = "friday_prayers",
                type = MilestoneType.COMMUNITY_SESSIONS,
                title = "Friday Memorial Gatherings",
                description = "Weekly community remembrance",
                targetValue = 10_000,
                currentValue = 3_500,
                icon = "🕌",
                participatingCountries = listOf("Morocco", "Algeria", "Tunisia", "Libya"),
                isCompleted = false
            )
        )
    )
}

class CompletedAchievementsProvider : PreviewParameterProvider<List<GlobalMilestone>> {
    override val values = sequenceOf(
        listOf(
            GlobalMilestone(
                milestoneId = "first_thousand",
                type = MilestoneType.TOTAL_PRAYERS,
                title = "First 1000 Prayers",
                description = "Community foundation milestone",
                targetValue = 1_000,
                currentValue = 1_000,
                icon = "🌟",
                participatingCountries = listOf("Indonesia", "Malaysia"),
                isCompleted = true,
                achievedAt = ZonedDateTime.now().minusDays(30)
            ),
            GlobalMilestone(
                milestoneId = "ten_thousand",
                type = MilestoneType.TOTAL_PRAYERS,
                title = "10K Memorial Prayers",
                description = "Growing community participation",
                targetValue = 10_000,
                currentValue = 10_000,
                icon = "⭐",
                participatingCountries = listOf("Pakistan", "Bangladesh", "India"),
                isCompleted = true,
                achievedAt = ZonedDateTime.now().minusDays(15)
            ),
            GlobalMilestone(
                milestoneId = "global_reach",
                type = MilestoneType.COUNTRIES_REACHED,
                title = "100 Countries Participating",
                description = "Worldwide Islamic unity",
                targetValue = 100,
                currentValue = 100,
                icon = "🌍",
                participatingCountries = (1..100).map { "Country$it" },
                isCompleted = true,
                achievedAt = ZonedDateTime.now().minusHours(8)
            ),
            GlobalMilestone(
                milestoneId = "ramadan_special",
                type = MilestoneType.TOTAL_PRAYERS,
                title = "Ramadan Special Prayers",
                description = "Holy month commemorative prayers",
                targetValue = 500_000,
                currentValue = 500_000,
                icon = "🌙",
                participatingCountries = listOf("Saudi Arabia", "Egypt", "Jordan", "Syria", "Lebanon"),
                isCompleted = true,
                achievedAt = ZonedDateTime.now().minusHours(3)
            ),
            GlobalMilestone(
                milestoneId = "scholar_blessed",
                type = MilestoneType.TOTAL_PRAYERS,
                title = "Scholar-Blessed Prayers",
                description = "Prayers blessed by Islamic scholars",
                targetValue = 25_000,
                currentValue = 25_000,
                icon = "📿",
                participatingCountries = listOf("Turkey", "Iran", "Afghanistan", "Uzbekistan"),
                isCompleted = true,
                achievedAt = ZonedDateTime.now().minusMinutes(45)
            )
        )
    )
}

// Preview Composables
@Preview(name = "Active Milestones - Normal")
@Composable
fun GlobalMilestoneCelebrationsActivePreview(
    @PreviewParameter(GlobalMilestoneCelebrationProvider::class) 
    data: Pair<List<GlobalMilestone>, List<MilestoneCelebration>>
) {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GlobalMilestoneCelebrations(
                milestones = data.first,
                celebrationEvents = data.second,
                onMilestoneClick = { },
                onCelebrationDismiss = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "With Active Celebration")
@Composable
fun GlobalMilestoneCelebrationWithCelebrationPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GlobalMilestoneCelebrations(
                milestones = listOf(
                    GlobalMilestone(
                        milestoneId = "achieved",
                        type = MilestoneType.TOTAL_PRAYERS,
                        title = "1 Million Memorial Prayers",
                        description = "Historic community achievement",
                        targetValue = 1_000_000,
                        currentValue = 1_000_000,
                        icon = "🎯",
                        participatingCountries = listOf("Indonesia", "Pakistan", "Turkey", "Egypt", "Bangladesh"),
                        isCompleted = true,
                        achievedAt = ZonedDateTime.now().minusMinutes(5)
                    )
                ),
                celebrationEvents = listOf(
                    MilestoneCelebration(
                        id = "million_celebration",
                        title = "1 Million Prayers Achieved!",
                        description = "The global Islamic community has united to perform 1 million memorial prayers.",
                        celebrationMessage = "Alhamdulillahi rabbil alameen! May Allah accept our collective remembrance and grant eternal peace to all souls we pray for.",
                        achievedValue = 1_000_000,
                        participatingCountries = listOf("Indonesia", "Pakistan", "Turkey", "Egypt", "Bangladesh", "Malaysia", "Saudi Arabia", "Iran"),
                        icon = "🎉"
                    )
                ),
                onMilestoneClick = { },
                onCelebrationDismiss = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Near Completion Milestones")
@Composable
fun GlobalMilestoneCelebrationNearCompletionPreview(
    @PreviewParameter(ActiveMilestonesProvider::class) 
    milestones: List<GlobalMilestone>
) {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GlobalMilestoneCelebrations(
                milestones = milestones,
                celebrationEvents = emptyList(),
                onMilestoneClick = { },
                onCelebrationDismiss = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Recent Achievements Only")
@Composable
fun GlobalMilestoneCelebrationAchievementsPreview(
    @PreviewParameter(CompletedAchievementsProvider::class) 
    achievements: List<GlobalMilestone>
) {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GlobalMilestoneCelebrations(
                milestones = achievements,
                celebrationEvents = emptyList(),
                onMilestoneClick = { },
                onCelebrationDismiss = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Empty State")
@Composable
fun GlobalMilestoneCelebrationEmptyPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GlobalMilestoneCelebrations(
                milestones = emptyList(),
                celebrationEvents = emptyList(),
                onMilestoneClick = { },
                onCelebrationDismiss = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Multiple Celebrations")
@Composable
fun GlobalMilestoneCelebrationMultiplePreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GlobalMilestoneCelebrations(
                milestones = listOf(
                    GlobalMilestone(
                        milestoneId = "hajj_complete",
                        type = MilestoneType.TOTAL_PRAYERS,
                        title = "Hajj Season Prayers Complete",
                        description = "Special Hajj memorial prayers",
                        targetValue = 2_000_000,
                        currentValue = 2_000_000,
                        icon = "🕋",
                        participatingCountries = listOf("Saudi Arabia", "Indonesia", "Pakistan", "Bangladesh", "Egypt"),
                        isCompleted = true,
                        achievedAt = ZonedDateTime.now().minusMinutes(10)
                    )
                ),
                celebrationEvents = listOf(
                    MilestoneCelebration(
                        id = "hajj_celebration",
                        title = "Hajj Milestone Achieved!",
                        description = "2 million memorial prayers completed during Hajj season.",
                        celebrationMessage = "Hajj Mabrur! Our prayers unite with the pilgrims in Mecca.",
                        achievedValue = 2_000_000,
                        participatingCountries = listOf("Saudi Arabia", "Indonesia", "Pakistan", "Bangladesh", "Egypt"),
                        icon = "🕋"
                    ),
                    MilestoneCelebration(
                        id = "unity_celebration",
                        title = "Global Unity Achievement!",
                        description = "All time zones participating simultaneously.",
                        celebrationMessage = "SubhanAllah! The sun never sets on our collective remembrance.",
                        achievedValue = 24,
                        participatingCountries = (1..195).map { "Country$it" },
                        icon = "🌍"
                    )
                ),
                onMilestoneClick = { },
                onCelebrationDismiss = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Milestone Card - Near Complete")
@Composable
fun AnimatedMilestoneCardNearCompletePreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AnimatedMilestoneCard(
                milestone = GlobalMilestone(
                    milestoneId = "near_complete",
                    type = MilestoneType.COMMUNITY_SESSIONS,
                    title = "Community Iftar Memorial Prayers",
                    description = "Special prayers during community Iftar gatherings",
                    targetValue = 50_000,
                    currentValue = 47_500, // 95%
                    icon = "🌙",
                    participatingCountries = listOf("UAE", "Qatar", "Kuwait", "Bahrain", "Oman"),
                    isCompleted = false
                ),
                onClick = { },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(name = "Achievement Badge")
@Composable
fun AchievementBadgePreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AchievementBadge(
                achievement = GlobalMilestone(
                    milestoneId = "ramadan_achievement",
                    type = MilestoneType.TOTAL_PRAYERS,
                    title = "Ramadan Memorial Prayers",
                    description = "Special Ramadan remembrance",
                    targetValue = 100_000,
                    currentValue = 100_000,
                    icon = "🌙",
                    participatingCountries = listOf("Indonesia", "Malaysia", "Brunei"),
                    isCompleted = true,
                    achievedAt = ZonedDateTime.now().minusHours(6)
                ),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(name = "Celebration Card")
@Composable
fun CelebrationCardPreview() {
    TahlilTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CelebrationCard(
                celebration = MilestoneCelebration(
                    id = "special_celebration",
                    title = "10 Million Prayers Milestone!",
                    description = "An unprecedented achievement in global Islamic community unity.",
                    celebrationMessage = "Alhamdulillah! 10 million hearts united in remembrance. May Allah accept our collective prayers and grant the highest levels of Jannah to all souls we remember.",
                    achievedValue = 10_000_000,
                    participatingCountries = listOf("Indonesia", "Pakistan", "Bangladesh", "India", "Turkey", "Egypt", "Iran", "Malaysia", "Saudi Arabia", "Morocco"),
                    icon = "🎊"
                ),
                onDismiss = { },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
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
import com.app_muslim.surah_yasin.feature.community.model.*
import kotlinx.coroutines.delay

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
                            ),
                            radius = 50f
                        ),
                    contentAlignment = Alignment.Center
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
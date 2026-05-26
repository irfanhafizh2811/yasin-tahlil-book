package com.app_muslim.surah_yasin.feature.community.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app_muslim.surah_yasin.feature.community.model.*

/**
 * Community Achievements Card Component
 * Displays prayer milestones, badges, and community celebrations
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CommunityAchievementsCard(
    userBadges: List<PrayerBadge>,
    recentMilestones: List<PrayerActivityItem>,
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    var selectedBadgeCategory by remember { mutableStateOf(BadgeCategory.PARTICIPATION) }
    
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
            // Header with streak indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 Community Achievements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                PrayerStreakIndicator(streak = currentStreak)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Badge categories
            BadgeCategoryTabs(
                categories = BadgeCategory.values().toList(),
                selectedCategory = selectedBadgeCategory,
                onCategorySelected = { selectedBadgeCategory = it }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Badges for selected category
            val categoryBadges = userBadges.filter { it.category == selectedBadgeCategory }
            if (categoryBadges.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(categoryBadges) { badge ->
                        BadgeItem(badge = badge)
                    }
                }
            } else {
                EmptyBadgeState(category = selectedBadgeCategory)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Recent milestones
            if (recentMilestones.isNotEmpty()) {
                Text(
                    text = "Recent Milestones",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                recentMilestones.take(3).forEach { milestone ->
                    MilestoneItem(milestone = milestone)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun PrayerStreakIndicator(
    streak: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val glowAnimation by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Row(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = "Prayer Streak",
            tint = MaterialTheme.colorScheme.primary.copy(alpha = glowAnimation),
            modifier = Modifier.size(16.dp)
        )
        
        Text(
            text = "$streak days",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BadgeCategoryTabs(
    categories: List<BadgeCategory>,
    selectedCategory: BadgeCategory,
    onCategorySelected: (BadgeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = getCategoryDisplayName(category),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = category == selectedCategory,
                leadingIcon = {
                    Icon(
                        imageVector = getCategoryIcon(category),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun BadgeItem(
    badge: PrayerBadge,
    modifier: Modifier = Modifier
) {
    var showDetails by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Badge icon with level indication
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    brush = getBadgeLevelGradient(badge.level),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getBadgeIcon(badge.category),
                contentDescription = badge.name,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = badge.name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            fontSize = 10.sp
        )
        
        Text(
            text = badge.level.name,
            style = MaterialTheme.typography.labelSmall,
            color = getBadgeLevelColor(badge.level),
            fontSize = 8.sp
        )
    }
}

@Composable
private fun EmptyBadgeState(
    category: BadgeCategory,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = getCategoryIcon(category),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "No ${getCategoryDisplayName(category).lowercase()} badges yet",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Keep participating in community prayers to earn badges!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MilestoneItem(
    milestone: PrayerActivityItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = getActivityIcon(milestone.activityType),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        
        Text(
            text = milestone.message,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = getRelativeTime(milestone.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

// Helper functions
private fun getCategoryDisplayName(category: BadgeCategory): String {
    return when (category) {
        BadgeCategory.PARTICIPATION -> "Participation"
        BadgeCategory.LEADERSHIP -> "Leadership"
        BadgeCategory.DEDICATION -> "Dedication"
        BadgeCategory.COMMUNITY -> "Community"
        BadgeCategory.MILESTONE -> "Milestones"
        BadgeCategory.SPECIAL -> "Special"
    }
}

private fun getCategoryIcon(category: BadgeCategory): ImageVector {
    return when (category) {
        BadgeCategory.PARTICIPATION -> Icons.Default.Group
        BadgeCategory.LEADERSHIP -> Icons.Default.EmojiEvents
        BadgeCategory.DEDICATION -> Icons.Default.LocalFireDepartment
        BadgeCategory.COMMUNITY -> Icons.Default.Favorite
        BadgeCategory.MILESTONE -> Icons.Default.Star
        BadgeCategory.SPECIAL -> Icons.Default.Celebration
    }
}

private fun getBadgeIcon(category: BadgeCategory): ImageVector {
    return when (category) {
        BadgeCategory.PARTICIPATION -> Icons.Default.Group
        BadgeCategory.LEADERSHIP -> Icons.Default.EmojiEvents
        BadgeCategory.DEDICATION -> Icons.Default.LocalFireDepartment
        BadgeCategory.COMMUNITY -> Icons.Default.Favorite
        BadgeCategory.MILESTONE -> Icons.Default.Star
        BadgeCategory.SPECIAL -> Icons.Default.Celebration
    }
}

@Composable
private fun getBadgeLevelGradient(level: BadgeLevel): Brush {
    return when (level) {
        BadgeLevel.BRONZE -> Brush.verticalGradient(
            colors = listOf(Color(0xFFCD7F32), Color(0xFF8B4513))
        )
        BadgeLevel.SILVER -> Brush.verticalGradient(
            colors = listOf(Color(0xFFC0C0C0), Color(0xFF808080))
        )
        BadgeLevel.GOLD -> Brush.verticalGradient(
            colors = listOf(Color(0xFFFFD700), Color(0xFFB8860B))
        )
        BadgeLevel.PLATINUM -> Brush.verticalGradient(
            colors = listOf(Color(0xFFE5E4E2), Color(0xFF999999))
        )
        BadgeLevel.DIAMOND -> Brush.verticalGradient(
            colors = listOf(Color(0xFFB9F2FF), Color(0xFF4169E1))
        )
    }
}

@Composable
private fun getBadgeLevelColor(level: BadgeLevel): Color {
    return when (level) {
        BadgeLevel.BRONZE -> Color(0xFFCD7F32)
        BadgeLevel.SILVER -> Color(0xFFC0C0C0)
        BadgeLevel.GOLD -> Color(0xFFFFD700)
        BadgeLevel.PLATINUM -> Color(0xFFE5E4E2)
        BadgeLevel.DIAMOND -> Color(0xFFB9F2FF)
    }
}

private fun getActivityIcon(activityType: ActivityType): ImageVector {
    return when (activityType) {
        ActivityType.PRAYER_COMPLETED -> Icons.Default.CheckCircle
        ActivityType.SESSION_JOINED -> Icons.Default.Group
        ActivityType.SESSION_HOSTED -> Icons.Default.EmojiEvents
        ActivityType.MILESTONE_REACHED -> Icons.Default.Star
        ActivityType.BADGE_EARNED -> Icons.Default.EmojiEvents
        ActivityType.MEMORIAL_CREATED -> Icons.Default.Add
        ActivityType.STREAK_ACHIEVEMENT -> Icons.Default.LocalFireDepartment
    }
}

private fun getRelativeTime(timestamp: java.time.ZonedDateTime): String {
    val now = java.time.ZonedDateTime.now()
    val duration = java.time.Duration.between(timestamp, now)
    
    return when {
        duration.toMinutes() < 1 -> "Just now"
        duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
        duration.toHours() < 24 -> "${duration.toHours()}h ago"
        duration.toDays() < 7 -> "${duration.toDays()}d ago"
        else -> "${duration.toDays() / 7}w ago"
    }
}
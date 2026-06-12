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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import android.content.res.Configuration
import com.app_muslim.surah_yasin.core.ui.theme.TahlilTheme
import com.app_muslim.surah_yasin.feature.community.model.*
import java.time.ZonedDateTime

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

// ========================================
// PREVIEW IMPLEMENTATIONS
// ========================================

@Preview(
    name = "Community Achievements - With Badges",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewCommunityAchievementsCardWithBadges() {
    TahlilTheme {
        CommunityAchievementsCard(
            userBadges = getSampleBadges(),
            recentMilestones = getSampleMilestones(),
            currentStreak = 15
        )
    }
}

@Preview(
    name = "Community Achievements - Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewCommunityAchievementsCardDark() {
    TahlilTheme {
        CommunityAchievementsCard(
            userBadges = getSampleBadges(),
            recentMilestones = getSampleMilestones(),
            currentStreak = 42
        )
    }
}

@Preview(
    name = "Empty Badges State",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewCommunityAchievementsCardEmpty() {
    TahlilTheme {
        CommunityAchievementsCard(
            userBadges = emptyList(),
            recentMilestones = emptyList(),
            currentStreak = 0
        )
    }
}

@Preview(
    name = "High Streak - Diamond Badges",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewCommunityAchievementsCardHighStreak() {
    TahlilTheme {
        CommunityAchievementsCard(
            userBadges = getDiamondBadges(),
            recentMilestones = getHighLevelMilestones(),
            currentStreak = 100
        )
    }
}

@Preview(
    name = "Leadership Badges Category",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewCommunityAchievementsCardLeadership() {
    TahlilTheme {
        CommunityAchievementsCard(
            userBadges = getLeadershipBadges(),
            recentMilestones = getLeadershipMilestones(),
            currentStreak = 28
        )
    }
}

@Preview(
    name = "Community Badges Only",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewCommunityAchievementsCardCommunity() {
    TahlilTheme {
        CommunityAchievementsCard(
            userBadges = getCommunityBadges(),
            recentMilestones = getCommunityMilestones(),
            currentStreak = 7
        )
    }
}

@Preview(
    name = "New User - Bronze Badges",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewCommunityAchievementsCardNewUser() {
    TahlilTheme {
        CommunityAchievementsCard(
            userBadges = getBronzeBadges(),
            recentMilestones = getNewUserMilestones(),
            currentStreak = 3
        )
    }
}

@Preview(
    name = "Special Event Badges",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewCommunityAchievementsCardSpecial() {
    TahlilTheme {
        CommunityAchievementsCard(
            userBadges = getSpecialBadges(),
            recentMilestones = getSpecialMilestones(),
            currentStreak = 60
        )
    }
}

@Preview(
    name = "Mixed Badge Levels",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewCommunityAchievementsCardMixed() {
    TahlilTheme {
        CommunityAchievementsCard(
            userBadges = getMixedLevelBadges(),
            recentMilestones = getMixedMilestones(),
            currentStreak = 21
        )
    }
}

// Preview Parameter Providers
class BadgeCollectionPreviewProvider : PreviewParameterProvider<List<PrayerBadge>> {
    override val values = sequenceOf(
        emptyList(),
        getSampleBadges(),
        getDiamondBadges(),
        getBronzeBadges()
    )
}

@Preview(
    name = "Dynamic Badge Collections",
    showBackground = true,
    backgroundColor = 0xFFFFFFF
)
@Composable
private fun PreviewCommunityAchievementsCardDynamic(
    @PreviewParameter(BadgeCollectionPreviewProvider::class) badges: List<PrayerBadge>
) {
    TahlilTheme {
        CommunityAchievementsCard(
            userBadges = badges,
            recentMilestones = getSampleMilestones(),
            currentStreak = when (badges.size) {
                0 -> 0
                in 1..3 -> 5
                in 4..6 -> 20
                else -> 50
            }
        )
    }
}

// Sample Data Functions
private fun getSampleBadges(): List<PrayerBadge> = listOf(
    PrayerBadge(
        badgeId = "participation_silver",
        name = "Prayer Participant",
        description = "Joined 50 community prayer sessions",
        iconUrl = "",
        category = BadgeCategory.PARTICIPATION,
        level = BadgeLevel.SILVER,
        earnedAt = ZonedDateTime.now().minusDays(5),
        requirements = "Join 50 prayer sessions"
    ),
    PrayerBadge(
        badgeId = "dedication_gold",
        name = "Dedicated Believer",
        description = "Maintained 30-day prayer streak",
        iconUrl = "",
        category = BadgeCategory.DEDICATION,
        level = BadgeLevel.GOLD,
        earnedAt = ZonedDateTime.now().minusDays(2),
        requirements = "30-day streak"
    ),
    PrayerBadge(
        badgeId = "community_bronze",
        name = "Community Helper",
        description = "Helped 10 families with memorial prayers",
        iconUrl = "",
        category = BadgeCategory.COMMUNITY,
        level = BadgeLevel.BRONZE,
        earnedAt = ZonedDateTime.now().minusDays(10),
        requirements = "Help 10 families"
    )
)

private fun getDiamondBadges(): List<PrayerBadge> = listOf(
    PrayerBadge(
        badgeId = "leadership_diamond",
        name = "Prayer Leader",
        description = "Led 500+ community sessions",
        iconUrl = "",
        category = BadgeCategory.LEADERSHIP,
        level = BadgeLevel.DIAMOND,
        earnedAt = ZonedDateTime.now().minusDays(1),
        requirements = "Lead 500 sessions"
    ),
    PrayerBadge(
        badgeId = "milestone_diamond",
        name = "Million Prayers",
        description = "Completed 1,000,000 prayers",
        iconUrl = "",
        category = BadgeCategory.MILESTONE,
        level = BadgeLevel.DIAMOND,
        earnedAt = ZonedDateTime.now().minusHours(6),
        requirements = "1M prayers"
    )
)

private fun getBronzeBadges(): List<PrayerBadge> = listOf(
    PrayerBadge(
        badgeId = "first_prayer",
        name = "First Prayer",
        description = "Completed your first memorial prayer",
        iconUrl = "",
        category = BadgeCategory.MILESTONE,
        level = BadgeLevel.BRONZE,
        earnedAt = ZonedDateTime.now().minusDays(1),
        requirements = "Complete 1 prayer"
    )
)

private fun getLeadershipBadges(): List<PrayerBadge> = listOf(
    PrayerBadge(
        badgeId = "session_host_gold",
        name = "Session Host",
        description = "Successfully hosted 100 prayer sessions",
        iconUrl = "",
        category = BadgeCategory.LEADERSHIP,
        level = BadgeLevel.GOLD,
        earnedAt = ZonedDateTime.now().minusDays(3),
        requirements = "Host 100 sessions"
    ),
    PrayerBadge(
        badgeId = "mentor_platinum",
        name = "Community Mentor",
        description = "Guided 50 new members",
        iconUrl = "",
        category = BadgeCategory.LEADERSHIP,
        level = BadgeLevel.PLATINUM,
        earnedAt = ZonedDateTime.now().minusDays(7),
        requirements = "Guide 50 members"
    )
)

private fun getCommunityBadges(): List<PrayerBadge> = listOf(
    PrayerBadge(
        badgeId = "family_support_gold",
        name = "Family Support",
        description = "Supported 25 grieving families",
        iconUrl = "",
        category = BadgeCategory.COMMUNITY,
        level = BadgeLevel.GOLD,
        earnedAt = ZonedDateTime.now().minusDays(4),
        requirements = "Support 25 families"
    )
)

private fun getSpecialBadges(): List<PrayerBadge> = listOf(
    PrayerBadge(
        badgeId = "ramadan_special",
        name = "Ramadan Devotion",
        description = "Participated in all Ramadan community prayers",
        iconUrl = "",
        category = BadgeCategory.SPECIAL,
        level = BadgeLevel.PLATINUM,
        earnedAt = ZonedDateTime.now().minusDays(30),
        requirements = "30 days Ramadan prayers"
    )
)

private fun getMixedLevelBadges(): List<PrayerBadge> = listOf(
    getBronzeBadges().first(),
    getSampleBadges()[1], // Gold
    getDiamondBadges().first(), // Diamond
    PrayerBadge(
        badgeId = "silver_participant",
        name = "Active Participant", 
        description = "Regular prayer participation",
        iconUrl = "",
        category = BadgeCategory.PARTICIPATION,
        level = BadgeLevel.SILVER,
        earnedAt = ZonedDateTime.now().minusDays(8),
        requirements = "50 prayers"
    )
)

private fun getSampleMilestones(): List<PrayerActivityItem> = listOf(
    PrayerActivityItem(
        activityId = "milestone1",
        activityType = ActivityType.MILESTONE_REACHED,
        userId = "user1",
        userDisplayName = "Ahmad Ibn Abdullah",
        message = "Reached 1000 total prayers milestone! 🎉",
        regionCode = "SA",
        timestamp = ZonedDateTime.now().minusHours(2)
    ),
    PrayerActivityItem(
        activityId = "badge1",
        activityType = ActivityType.BADGE_EARNED,
        userId = "user1",
        userDisplayName = "Ahmad Ibn Abdullah", 
        message = "Earned 'Dedicated Believer' gold badge",
        regionCode = "SA",
        timestamp = ZonedDateTime.now().minusHours(6)
    ),
    PrayerActivityItem(
        activityId = "streak1",
        activityType = ActivityType.STREAK_ACHIEVEMENT,
        userId = "user1",
        userDisplayName = "Ahmad Ibn Abdullah",
        message = "Achieved 15-day prayer streak! 🔥",
        regionCode = "SA", 
        timestamp = ZonedDateTime.now().minusDays(1)
    )
)

private fun getHighLevelMilestones(): List<PrayerActivityItem> = listOf(
    PrayerActivityItem(
        activityId = "milestone_high",
        activityType = ActivityType.MILESTONE_REACHED,
        userId = "user_pro",
        userDisplayName = "Khadijah Bint Muhammad",
        message = "Reached 100,000 prayers milestone! 💎",
        regionCode = "EG",
        timestamp = ZonedDateTime.now().minusMinutes(30)
    ),
    PrayerActivityItem(
        activityId = "leadership_milestone",
        activityType = ActivityType.SESSION_HOSTED,
        userId = "user_pro",
        userDisplayName = "Khadijah Bint Muhammad",
        message = "Successfully hosted 500th community session",
        regionCode = "EG",
        timestamp = ZonedDateTime.now().minusHours(4)
    )
)

private fun getLeadershipMilestones(): List<PrayerActivityItem> = listOf(
    PrayerActivityItem(
        activityId = "host_milestone",
        activityType = ActivityType.SESSION_HOSTED,
        userId = "leader1",
        userDisplayName = "Omar Al-Faruq",
        message = "Hosted successful prayer session for 50 participants",
        regionCode = "ID",
        timestamp = ZonedDateTime.now().minusHours(1)
    )
)

private fun getCommunityMilestones(): List<PrayerActivityItem> = listOf(
    PrayerActivityItem(
        activityId = "community_help",
        activityType = ActivityType.PRAYER_COMPLETED,
        userId = "helper1",
        userDisplayName = "Fatima Az-Zahra",
        message = "Completed 500 prayers for the Al-Hassan family memorial",
        regionCode = "MY",
        timestamp = ZonedDateTime.now().minusMinutes(45)
    )
)

private fun getNewUserMilestones(): List<PrayerActivityItem> = listOf(
    PrayerActivityItem(
        activityId = "first_badge",
        activityType = ActivityType.BADGE_EARNED,
        userId = "newbie1",
        userDisplayName = "Aisha Bint Abu Bakr",
        message = "Earned first badge: 'First Prayer'! 🌟",
        regionCode = "PK",
        timestamp = ZonedDateTime.now().minusMinutes(15)
    )
)

private fun getSpecialMilestones(): List<PrayerActivityItem> = listOf(
    PrayerActivityItem(
        activityId = "special_event",
        activityType = ActivityType.BADGE_EARNED,
        userId = "special1",
        userDisplayName = "Ali Ibn Abu Talib",
        message = "Earned rare 'Ramadan Devotion' platinum badge! 🌙",
        regionCode = "IQ",
        timestamp = ZonedDateTime.now().minusDays(2)
    )
)

private fun getMixedMilestones(): List<PrayerActivityItem> = listOf(
    getSampleMilestones()[0],
    getNewUserMilestones()[0], 
    PrayerActivityItem(
        activityId = "mixed_session",
        activityType = ActivityType.SESSION_JOINED,
        userId = "mixed1",
        userDisplayName = "Zainab Bint Ali",
        message = "Joined global unity prayer session (500 participants)",
        regionCode = "TR",
        timestamp = ZonedDateTime.now().minusHours(3)
    )
)
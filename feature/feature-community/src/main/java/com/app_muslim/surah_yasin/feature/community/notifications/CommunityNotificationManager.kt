package com.app_muslim.surah_yasin.feature.community.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app_muslim.surah_yasin.feature.community.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Community Notification Manager
 * Handles real-time prayer notifications and community engagement alerts
 */
@Singleton
class CommunityNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    private val notificationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    companion object {
        private const val COMMUNITY_CHANNEL_ID = "community_prayers"
        private const val PRAYER_SESSION_CHANNEL_ID = "prayer_sessions"
        private const val ACHIEVEMENTS_CHANNEL_ID = "achievements"
        private const val FAMILY_CHANNEL_ID = "family_prayers"
        
        private const val NOTIFICATION_ID_PRAYER_MILESTONE = 1001
        private const val NOTIFICATION_ID_SESSION_INVITATION = 1002
        private const val NOTIFICATION_ID_ACHIEVEMENT_EARNED = 1003
        private const val NOTIFICATION_ID_FAMILY_PRAYER = 1004
        private const val NOTIFICATION_ID_GLOBAL_MILESTONE = 1005
        private const val NOTIFICATION_ID_SESSION_REMINDER = 1006
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    COMMUNITY_CHANNEL_ID,
                    "Community Prayers",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Community prayer participation and milestones"
                },
                
                NotificationChannel(
                    PRAYER_SESSION_CHANNEL_ID,
                    "Prayer Sessions",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Live prayer session invitations and updates"
                },
                
                NotificationChannel(
                    ACHIEVEMENTS_CHANNEL_ID,
                    "Achievements",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Prayer badges and achievements"
                },
                
                NotificationChannel(
                    FAMILY_CHANNEL_ID,
                    "Family Prayers",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Family memorial sharing and prayer invitations"
                }
            )
            
            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
    
    /**
     * Show prayer milestone celebration notification
     */
    fun showPrayerMilestoneNotification(
        milestone: PrayerMilestone,
        memorialName: String
    ) {
        val intent = createCommunityHomeIntent()
        val pendingIntent = createPendingIntent(intent, NOTIFICATION_ID_PRAYER_MILESTONE)
        
        val notification = NotificationCompat.Builder(context, COMMUNITY_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay) // Replace with app icon
            .setContentTitle("🎉 Prayer Milestone Reached!")
            .setContentText("${milestone.count} prayers completed for ${memorialName}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Congratulations! You've completed ${milestone.count} prayers for ${memorialName}. " +
                            "May Allah accept your prayers and grant peace to the soul.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_PRAYER_MILESTONE, notification)
    }
    
    /**
     * Show community prayer session invitation
     */
    fun showSessionInvitationNotification(
        session: CommunityPrayerSession,
        inviterName: String
    ) {
        val intent = createSessionIntent(session.sessionId)
        val pendingIntent = createPendingIntent(intent, NOTIFICATION_ID_SESSION_INVITATION)
        
        val notification = NotificationCompat.Builder(context, PRAYER_SESSION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("🤲 Prayer Session Invitation")
            .setContentText("${inviterName} invited you to join a ${session.prayerType.displayName} session")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${inviterName} invited you to join a community prayer session. " +
                            "Prayer: ${session.prayerType.displayName} • Target: ${session.targetPrayerCount} prayers")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(createJoinSessionAction(session.sessionId))
            .addAction(createDeclineSessionAction(session.sessionId))
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_SESSION_INVITATION, notification)
    }
    
    /**
     * Show achievement earned notification
     */
    fun showAchievementEarnedNotification(
        badge: PrayerBadge
    ) {
        val intent = createAchievementsIntent()
        val pendingIntent = createPendingIntent(intent, NOTIFICATION_ID_ACHIEVEMENT_EARNED)
        
        val badgeEmoji = when (badge.level) {
            BadgeLevel.BRONZE -> "🥉"
            BadgeLevel.SILVER -> "🥈"
            BadgeLevel.GOLD -> "🥇"
            BadgeLevel.PLATINUM -> "💎"
            BadgeLevel.DIAMOND -> "💠"
        }
        
        val notification = NotificationCompat.Builder(context, ACHIEVEMENTS_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("${badgeEmoji} New Badge Earned!")
            .setContentText("${badge.name} - ${badge.level.name}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Congratulations! You've earned the ${badge.name} badge (${badge.level.name} level). " +
                            "${badge.description}")
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_ACHIEVEMENT_EARNED, notification)
    }
    
    /**
     * Show family prayer invitation notification
     */
    fun showFamilyPrayerInvitationNotification(
        invitation: PrayerInvitation
    ) {
        val intent = createFamilySharingIntent()
        val pendingIntent = createPendingIntent(intent, NOTIFICATION_ID_FAMILY_PRAYER)
        
        val urgentEmoji = if (invitation.isUrgent) "⚡ " else "👨‍👩‍👧‍👦 "
        
        val notification = NotificationCompat.Builder(context, FAMILY_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("${urgentEmoji}Family Prayer Invitation")
            .setContentText("${invitation.inviterName} invited you to pray for ${invitation.memorialName}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${invitation.inviterName} invited you to join a family prayer session for ${invitation.memorialName}. " +
                            "Prayer: ${invitation.prayerType.displayName} • Target: ${invitation.targetPrayerCount} prayers")
            )
            .setPriority(if (invitation.isUrgent) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(createJoinFamilyPrayerAction(invitation.invitationId))
            .addAction(createDeclineFamilyPrayerAction(invitation.invitationId))
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_FAMILY_PRAYER, notification)
    }
    
    /**
     * Show global community milestone notification
     */
    fun showGlobalMilestoneNotification(
        milestone: GlobalMilestone
    ) {
        val intent = createCommunityHomeIntent()
        val pendingIntent = createPendingIntent(intent, NOTIFICATION_ID_GLOBAL_MILESTONE)
        
        val notification = NotificationCompat.Builder(context, COMMUNITY_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("🌍 Global Community Milestone!")
            .setContentText("${milestone.totalPrayers} prayers completed worldwide today")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Amazing! The global Muslim community has completed ${milestone.totalPrayers} prayers today. " +
                            "Together we're ${milestone.participantCount} participants strong. May Allah accept all our prayers.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_GLOBAL_MILESTONE, notification)
    }
    
    /**
     * Show prayer session reminder notification
     */
    fun showSessionReminderNotification(
        session: CommunityPrayerSession,
        minutesUntilStart: Int
    ) {
        val intent = createSessionIntent(session.sessionId)
        val pendingIntent = createPendingIntent(intent, NOTIFICATION_ID_SESSION_REMINDER)
        
        val notification = NotificationCompat.Builder(context, PRAYER_SESSION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("⏰ Prayer Session Starting Soon")
            .setContentText("${session.prayerType.displayName} session starts in $minutesUntilStart minutes")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Your ${session.prayerType.displayName} session with ${session.hostDisplayName} starts in $minutesUntilStart minutes. " +
                            "Prepare for prayer and join the community.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(createJoinSessionAction(session.sessionId))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_SESSION_REMINDER, notification)
    }
    
    // Intent creation helpers
    private fun createCommunityHomeIntent(): Intent {
        // TODO: Replace with actual Community Home Activity/Fragment
        return Intent().apply {
            putExtra("destination", "community_home")
        }
    }
    
    private fun createSessionIntent(sessionId: String): Intent {
        // TODO: Replace with actual Session Activity/Fragment
        return Intent().apply {
            putExtra("destination", "community_session")
            putExtra("sessionId", sessionId)
        }
    }
    
    private fun createAchievementsIntent(): Intent {
        // TODO: Replace with actual Achievements Activity/Fragment
        return Intent().apply {
            putExtra("destination", "achievements")
        }
    }
    
    private fun createFamilySharingIntent(): Intent {
        // TODO: Replace with actual Family Sharing Activity/Fragment
        return Intent().apply {
            putExtra("destination", "family_sharing")
        }
    }
    
    private fun createPendingIntent(intent: Intent, requestCode: Int): PendingIntent {
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    // Action creation helpers
    private fun createJoinSessionAction(sessionId: String): NotificationCompat.Action {
        val intent = Intent().apply {
            putExtra("action", "join_session")
            putExtra("sessionId", sessionId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            "Join",
            pendingIntent
        ).build()
    }
    
    private fun createDeclineSessionAction(sessionId: String): NotificationCompat.Action {
        val intent = Intent().apply {
            putExtra("action", "decline_session")
            putExtra("sessionId", sessionId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionId.hashCode() + 1000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Decline",
            pendingIntent
        ).build()
    }
    
    private fun createJoinFamilyPrayerAction(invitationId: String): NotificationCompat.Action {
        val intent = Intent().apply {
            putExtra("action", "join_family_prayer")
            putExtra("invitationId", invitationId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            invitationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            "Join Prayer",
            pendingIntent
        ).build()
    }
    
    private fun createDeclineFamilyPrayerAction(invitationId: String): NotificationCompat.Action {
        val intent = Intent().apply {
            putExtra("action", "decline_family_prayer")
            putExtra("invitationId", invitationId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            invitationId.hashCode() + 2000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Decline",
            pendingIntent
        ).build()
    }
    
    /**
     * Cancel specific notification
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
    
    /**
     * Cancel all community notifications
     */
    fun cancelAllCommunityNotifications() {
        notificationManager.cancelAll()
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        notificationScope.cancel()
    }
}

// Supporting data classes for notifications
data class PrayerMilestone(
    val count: Int,
    val type: String,
    val memorialId: String
)

data class GlobalMilestone(
    val totalPrayers: Long,
    val participantCount: Long,
    val regionCount: Int
)
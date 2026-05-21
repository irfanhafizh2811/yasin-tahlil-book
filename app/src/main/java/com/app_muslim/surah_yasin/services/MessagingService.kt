package com.app_muslim.surah_yasin.services

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.tasks.await

class MessagingService(private val messaging: FirebaseMessaging) {
    
    companion object {
        // Topic names for different types of notifications
        const val TOPIC_MEMORIAL_PRAYERS = "memorial_prayers"
        const val TOPIC_FRIDAY_REMINDERS = "friday_reminders"
        const val TOPIC_PRAYER_REMINDERS = "prayer_reminders"
        const val TOPIC_COMMUNITY_UPDATES = "community_updates"
    }
    
    // FCM Token Management
    suspend fun getFCMToken(): Result<String> {
        return try {
            val token = messaging.token.await()
            Result.success(token)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun deleteToken(): Result<Unit> {
        return try {
            messaging.deleteToken().await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Topic Subscription Management
    suspend fun subscribeToMemorialPrayers(): Result<Unit> {
        return try {
            messaging.subscribeToTopic(TOPIC_MEMORIAL_PRAYERS).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun unsubscribeFromMemorialPrayers(): Result<Unit> {
        return try {
            messaging.unsubscribeFromTopic(TOPIC_MEMORIAL_PRAYERS).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun subscribeToFridayReminders(): Result<Unit> {
        return try {
            messaging.subscribeToTopic(TOPIC_FRIDAY_REMINDERS).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun unsubscribeFromFridayReminders(): Result<Unit> {
        return try {
            messaging.unsubscribeFromTopic(TOPIC_FRIDAY_REMINDERS).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun subscribeToPrayerReminders(): Result<Unit> {
        return try {
            messaging.subscribeToTopic(TOPIC_PRAYER_REMINDERS).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun unsubscribeFromPrayerReminders(): Result<Unit> {
        return try {
            messaging.unsubscribeFromTopic(TOPIC_PRAYER_REMINDERS).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun subscribeToCommunityUpdates(): Result<Unit> {
        return try {
            messaging.subscribeToTopic(TOPIC_COMMUNITY_UPDATES).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun unsubscribeFromCommunityUpdates(): Result<Unit> {
        return try {
            messaging.unsubscribeFromTopic(TOPIC_COMMUNITY_UPDATES).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Bulk topic subscription management
    suspend fun subscribeToAllTopics(): Result<Unit> {
        return try {
            subscribeToMemorialPrayers()
            subscribeToPrayerReminders()
            subscribeToCommunityUpdates()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun unsubscribeFromAllTopics(): Result<Unit> {
        return try {
            unsubscribeFromMemorialPrayers()
            unsubscribeFromFridayReminders()
            unsubscribeFromPrayerReminders()
            unsubscribeFromCommunityUpdates()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Subscribe to user-specific memorial notifications
    suspend fun subscribeToMemorialNotifications(memorialId: String): Result<Unit> {
        return try {
            val topicName = "memorial_$memorialId"
            messaging.subscribeToTopic(topicName).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    suspend fun unsubscribeFromMemorialNotifications(memorialId: String): Result<Unit> {
        return try {
            val topicName = "memorial_$memorialId"
            messaging.unsubscribeFromTopic(topicName).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Enable/disable messaging entirely
    suspend fun setMessagingEnabled(enabled: Boolean): Result<Unit> {
        return try {
            messaging.isAutoInitEnabled = enabled
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    // Handle incoming message data
    fun parseMessageData(remoteMessage: RemoteMessage): Map<String, String> {
        return remoteMessage.data
    }
    
    fun getNotificationPayload(remoteMessage: RemoteMessage): RemoteMessage.Notification? {
        return remoteMessage.notification
    }
    
    // Helper methods for notification handling
    fun isMemorialPrayerNotification(remoteMessage: RemoteMessage): Boolean {
        return remoteMessage.data["type"] == "memorial_prayer" ||
               remoteMessage.from?.contains(TOPIC_MEMORIAL_PRAYERS) == true
    }
    
    fun isFridayReminderNotification(remoteMessage: RemoteMessage): Boolean {
        return remoteMessage.data["type"] == "friday_reminder" ||
               remoteMessage.from?.contains(TOPIC_FRIDAY_REMINDERS) == true
    }
    
    fun isPrayerReminderNotification(remoteMessage: RemoteMessage): Boolean {
        return remoteMessage.data["type"] == "prayer_reminder" ||
               remoteMessage.from?.contains(TOPIC_PRAYER_REMINDERS) == true
    }
    
    fun isCommunityUpdateNotification(remoteMessage: RemoteMessage): Boolean {
        return remoteMessage.data["type"] == "community_update" ||
               remoteMessage.from?.contains(TOPIC_COMMUNITY_UPDATES) == true
    }
    
    // Extract memorial ID from notification
    fun getMemorialIdFromNotification(remoteMessage: RemoteMessage): String? {
        return remoteMessage.data["memorial_id"]
    }
    
    // Extract prayer type from notification
    fun getPrayerTypeFromNotification(remoteMessage: RemoteMessage): String? {
        return remoteMessage.data["prayer_type"] // "tahlil", "yasin", "fatihah"
    }
    
    // Check if notification should show in foreground
    fun shouldShowInForeground(remoteMessage: RemoteMessage): Boolean {
        return remoteMessage.data["show_in_foreground"]?.toBoolean() ?: true
    }
    
    // Get notification priority
    fun getNotificationPriority(remoteMessage: RemoteMessage): Int {
        return remoteMessage.priority // RemoteMessage.PRIORITY_HIGH or PRIORITY_NORMAL
    }
    
    // Data class for notification preferences
    data class NotificationPreferences(
        val memorialPrayers: Boolean = true,
        val fridayReminders: Boolean = true,
        val prayerReminders: Boolean = true,
        val communityUpdates: Boolean = false,
        val soundEnabled: Boolean = true,
        val vibrationEnabled: Boolean = true,
        val ledEnabled: Boolean = false
    )
    
    // Apply notification preferences
    suspend fun applyNotificationPreferences(preferences: NotificationPreferences): Result<Unit> {
        return try {
            if (preferences.memorialPrayers) {
                subscribeToMemorialPrayers()
            } else {
                unsubscribeFromMemorialPrayers()
            }
            
            if (preferences.fridayReminders) {
                subscribeToFridayReminders()
            } else {
                unsubscribeFromFridayReminders()
            }
            
            if (preferences.prayerReminders) {
                subscribeToPrayerReminders()
            } else {
                unsubscribeFromPrayerReminders()
            }
            
            if (preferences.communityUpdates) {
                subscribeToCommunityUpdates()
            } else {
                unsubscribeFromCommunityUpdates()
            }
            
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
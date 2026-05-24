package com.app_muslim.surah_yasin.feature.memorial.repository

import android.content.Context
// Remove WorkManager for now - will add in dedicated module
// import androidx.work.*
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
// import java.util.concurrent.TimeUnit  // Remove for now
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnniversaryReminderRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val context: Context
) {
    
    companion object {
        private const val ANNIVERSARY_REMINDERS_COLLECTION = "anniversary_reminders"
        private const val REMINDER_NOTIFICATIONS_COLLECTION = "reminder_notifications"
        private const val ISLAMIC_ANNIVERSARIES_COLLECTION = "islamic_anniversaries"
        private const val REMINDER_SETTINGS_COLLECTION = "reminder_settings"
        private const val ANNIVERSARY_WORK_TAG = "anniversary_reminder_work"
    }

    suspend fun createAnniversaryReminder(
        memorialId: String,
        reminderType: ReminderType,
        originalDate: Date,
        notificationSettings: NotificationSettings = NotificationSettings(),
        customMessage: String = ""
    ): String {
        val currentUser = auth.currentUser
            ?: throw IllegalStateException("User must be authenticated")

        val reminderId = firestore.collection(ANNIVERSARY_REMINDERS_COLLECTION).document().id
        
        // Calculate next reminder date based on type
        val nextReminderDate = calculateNextReminderDate(originalDate, reminderType)
        val anniversaryCount = calculateAnniversaryCount(originalDate, reminderType)
        
        val reminder = AnniversaryReminder(
            id = reminderId,
            memorialId = memorialId,
            userId = currentUser.uid,
            reminderType = reminderType,
            reminderDate = nextReminderDate,
            originalDate = originalDate,
            notificationSettings = notificationSettings,
            customMessage = customMessage,
            anniversaryCount = anniversaryCount,
            nextScheduled = nextReminderDate
        )

        firestore.collection(ANNIVERSARY_REMINDERS_COLLECTION)
            .document(reminderId)
            .set(reminder.toFirestoreMap())
            .await()

        // Schedule notification work
        scheduleReminderNotification(reminder)

        return reminderId
    }

    suspend fun updateAnniversaryReminder(reminder: AnniversaryReminder) {
        firestore.collection(ANNIVERSARY_REMINDERS_COLLECTION)
            .document(reminder.id)
            .set(reminder.toFirestoreMap())
            .await()

        // Reschedule notification
        scheduleReminderNotification(reminder)
    }

    suspend fun deleteAnniversaryReminder(reminderId: String) {
        val currentUser = auth.currentUser
            ?: throw IllegalStateException("User must be authenticated")

        val reminderDoc = firestore.collection(ANNIVERSARY_REMINDERS_COLLECTION)
            .document(reminderId)
            .get()
            .await()

        if (!reminderDoc.exists()) {
            throw IllegalArgumentException("Reminder not found")
        }

        val reminder = reminderDoc.toAnniversaryReminder()
        if (reminder?.userId != currentUser.uid) {
            throw SecurityException("Only the creator can delete the reminder")
        }

        // Cancel scheduled work
        cancelReminderNotification(reminderId)

        // Delete reminder
        firestore.collection(ANNIVERSARY_REMINDERS_COLLECTION)
            .document(reminderId)
            .delete()
            .await()
    }

    fun getUserAnniversaryReminders(userId: String): Flow<List<AnniversaryReminder>> {
        return firestore.collection(ANNIVERSARY_REMINDERS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("isActive", true)
            .orderBy("reminderDate", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toAnniversaryReminder() }
            }
    }

    fun getMemorialAnniversaryReminders(memorialId: String): Flow<List<AnniversaryReminder>> {
        return firestore.collection(ANNIVERSARY_REMINDERS_COLLECTION)
            .whereEqualTo("memorialId", memorialId)
            .whereEqualTo("isActive", true)
            .orderBy("reminderDate", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toAnniversaryReminder() }
            }
    }

    suspend fun getUpcomingAnniversaries(
        userId: String,
        daysAhead: Int = 30
    ): List<UpcomingAnniversary> {
        val endDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, daysAhead)
        }.time

        val remindersSnapshot = firestore.collection(ANNIVERSARY_REMINDERS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("isActive", true)
            .whereGreaterThanOrEqualTo("reminderDate", Date())
            .whereLessThanOrEqualTo("reminderDate", endDate)
            .orderBy("reminderDate", Query.Direction.ASCENDING)
            .get()
            .await()

        return remindersSnapshot.documents.mapNotNull { doc ->
            val reminder = doc.toAnniversaryReminder() ?: return@mapNotNull null
            
            // Get memorial details
            val memorial = getMemorialDetails(reminder.memorialId)
            memorial?.let {
                val daysUntil = calculateDaysUntil(reminder.reminderDate)
                val yearsAgo = calculateYearsAgo(reminder.originalDate)
                
                UpcomingAnniversary(
                    memorialId = reminder.memorialId,
                    deceasedName = memorial.deceasedName,
                    anniversaryDate = reminder.reminderDate,
                    anniversaryType = reminder.reminderType,
                    daysUntil = daysUntil,
                    yearsAgo = yearsAgo,
                    reminderSettings = reminder.notificationSettings,
                    suggestedActions = generateReminderSuggestedActions(reminder.reminderType, yearsAgo)
                )
            }
        }
    }

    suspend fun createIslamicAnniversary(
        memorialId: String,
        hijriDate: HijriDate,
        eventType: IslamicEventType,
        description: String,
        arabicDescription: String = ""
    ): String {
        val anniversaryId = firestore.collection(ISLAMIC_ANNIVERSARIES_COLLECTION).document().id
        
        val islamicAnniversary = IslamicAnniversary(
            id = anniversaryId,
            memorialId = memorialId,
            hijriDate = hijriDate,
            gregorianDate = convertHijriToGregorian(hijriDate),
            eventType = eventType,
            description = description,
            arabicDescription = arabicDescription,
            recommendedPrayers = getRecommendedPrayers(eventType),
            significance = EventSignificance.PERSONAL
        )

        firestore.collection(ISLAMIC_ANNIVERSARIES_COLLECTION)
            .document(anniversaryId)
            .set(islamicAnniversary.toFirestoreMap())
            .await()

        return anniversaryId
    }

    suspend fun getUserReminderSettings(userId: String): ReminderSettings {
        val settingsDoc = firestore.collection(REMINDER_SETTINGS_COLLECTION)
            .document(userId)
            .get()
            .await()

        return if (settingsDoc.exists()) {
            settingsDoc.toReminderSettings() ?: ReminderSettings(userId = userId)
        } else {
            ReminderSettings(userId = userId)
        }
    }

    suspend fun updateUserReminderSettings(settings: ReminderSettings) {
        firestore.collection(REMINDER_SETTINGS_COLLECTION)
            .document(settings.userId)
            .set(settings.toFirestoreMap())
            .await()
    }

    suspend fun triggerAnniversaryReminder(reminderId: String): Boolean {
        try {
            val reminderDoc = firestore.collection(ANNIVERSARY_REMINDERS_COLLECTION)
                .document(reminderId)
                .get()
                .await()

            val reminder = reminderDoc.toAnniversaryReminder() ?: return false
            
            // Create notification
            val notification = createReminderNotification(reminder)
            
            // Save notification record
            firestore.collection(REMINDER_NOTIFICATIONS_COLLECTION)
                .document(notification.id)
                .set(notification.toFirestoreMap())
                .await()

            // Update reminder with last triggered time and calculate next
            val updatedReminder = reminder.copy(
                lastTriggered = Date(),
                nextScheduled = calculateNextReminderDate(reminder.originalDate, reminder.reminderType),
                anniversaryCount = reminder.anniversaryCount + 1
            )

            firestore.collection(ANNIVERSARY_REMINDERS_COLLECTION)
                .document(reminderId)
                .set(updatedReminder.toFirestoreMap())
                .await()

            // Schedule next reminder if recurring
            if (reminder.notificationSettings.frequency != ReminderFrequency.ONCE) {
                scheduleReminderNotification(updatedReminder)
            }

            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun scheduleReminderNotification(reminder: AnniversaryReminder) {
        val delay = reminder.nextScheduled?.time?.minus(System.currentTimeMillis()) ?: 0
        
        if (delay > 0) {
            // TODO: Implement WorkManager scheduling when module is created
            println("Scheduling reminder notification for: ${reminder.id} with delay: ${delay}ms")
        }
    }

    private fun cancelReminderNotification(reminderId: String) {
        // TODO: Implement WorkManager cancellation when module is created
        println("Cancelling reminder notification for: $reminderId")
    }

    private fun calculateNextReminderDate(originalDate: Date, reminderType: ReminderType): Date {
        val calendar = Calendar.getInstance()
        calendar.time = originalDate

        when (reminderType) {
            ReminderType.YEARLY_ANNIVERSARY -> {
                calendar.add(Calendar.YEAR, 1)
            }
            ReminderType.MONTHLY_REMEMBRANCE -> {
                calendar.add(Calendar.MONTH, 1)
            }
            ReminderType.WEEKLY_PRAYER -> {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
            }
            ReminderType.FORTY_DAYS -> {
                calendar.add(Calendar.DAY_OF_MONTH, 40)
            }
            ReminderType.HUNDRED_DAYS -> {
                calendar.add(Calendar.DAY_OF_MONTH, 100)
            }
            ReminderType.FIRST_YEAR -> {
                calendar.add(Calendar.YEAR, 1)
            }
            else -> {
                calendar.add(Calendar.YEAR, 1) // Default to yearly
            }
        }

        return calendar.time
    }

    private fun calculateAnniversaryCount(originalDate: Date, reminderType: ReminderType): Int {
        val now = Calendar.getInstance()
        val original = Calendar.getInstance().apply { time = originalDate }

        return when (reminderType) {
            ReminderType.YEARLY_ANNIVERSARY, ReminderType.FIRST_YEAR -> {
                now.get(Calendar.YEAR) - original.get(Calendar.YEAR)
            }
            ReminderType.MONTHLY_REMEMBRANCE -> {
                val yearDiff = now.get(Calendar.YEAR) - original.get(Calendar.YEAR)
                val monthDiff = now.get(Calendar.MONTH) - original.get(Calendar.MONTH)
                yearDiff * 12 + monthDiff
            }
            ReminderType.WEEKLY_PRAYER -> {
                ((now.timeInMillis - original.timeInMillis) / (1000 * 60 * 60 * 24 * 7)).toInt()
            }
            else -> 1
        }
    }

    private fun calculateDaysUntil(date: Date): Int {
        val now = System.currentTimeMillis()
        val target = date.time
        return ((target - now) / (1000 * 60 * 60 * 24)).toInt()
    }

    private fun calculateYearsAgo(date: Date): Int {
        val now = Calendar.getInstance()
        val past = Calendar.getInstance().apply { time = date }
        return now.get(Calendar.YEAR) - past.get(Calendar.YEAR)
    }

    private suspend fun getMemorialDetails(memorialId: String): MemorialData? {
        return try {
            val doc = firestore.collection("memorials")
                .document(memorialId)
                .get()
                .await()
            
            if (doc.exists()) {
                MemorialData(
                    id = doc.getString("id") ?: "",
                    deceasedName = doc.getString("deceasedName") ?: "",
                    // Other fields would be populated here
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun generateReminderSuggestedActions(reminderType: ReminderType, yearsAgo: Int): List<ReminderSuggestedAction> {
        val actions = mutableListOf<ReminderSuggestedAction>()

        when (reminderType) {
            ReminderType.YEARLY_ANNIVERSARY -> {
                actions.addAll(listOf(
                    ReminderSuggestedAction(
                        title = "Recite Surah Al-Fatihah",
                        description = "Offer prayers for the deceased",
                        actionType = ActionType.PRAYER,
                        estimatedDuration = "5 minutes",
                        difficultyLevel = DifficultyLevel.EASY
                    ),
                    ReminderSuggestedAction(
                        title = "Give Charity (Sadaqah)",
                        description = "Give charity in their memory",
                        actionType = ActionType.CHARITY,
                        estimatedDuration = "Variable",
                        difficultyLevel = DifficultyLevel.EASY
                    ),
                    ReminderSuggestedAction(
                        title = "Gather with Family",
                        description = "Share memories and pray together",
                        actionType = ActionType.FAMILY_GATHERING,
                        estimatedDuration = "1-2 hours",
                        difficultyLevel = DifficultyLevel.MODERATE
                    )
                ))
            }
            ReminderType.WEEKLY_PRAYER -> {
                actions.add(
                    ReminderSuggestedAction(
                        title = "Weekly Memorial Prayer",
                        description = "Short prayer session for remembrance",
                        actionType = ActionType.PRAYER,
                        estimatedDuration = "10-15 minutes",
                        difficultyLevel = DifficultyLevel.EASY
                    )
                )
            }
            ReminderType.FORTY_DAYS -> {
                actions.add(
                    ReminderSuggestedAction(
                        title = "Complete Quran Reading",
                        description = "Complete a Khatm al-Quran in their memory",
                        actionType = ActionType.QURAN_RECITATION,
                        estimatedDuration = "Several hours",
                        difficultyLevel = DifficultyLevel.CHALLENGING
                    )
                )
            }
            else -> {
                actions.add(
                    ReminderSuggestedAction(
                        title = "Prayer and Remembrance",
                        description = "Offer prayers and remember them",
                        actionType = ActionType.PRAYER,
                        estimatedDuration = "10-15 minutes",
                        difficultyLevel = DifficultyLevel.EASY
                    )
                )
            }
        }

        return actions
    }

    private fun createReminderNotification(reminder: AnniversaryReminder): ReminderNotification {
        val memorial = runCatching { 
            // This would be implemented to fetch memorial details
            MemorialData(deceasedName = "Beloved")
        }.getOrNull()

        val title = when (reminder.reminderType) {
            ReminderType.YEARLY_ANNIVERSARY -> "Anniversary Remembrance"
            ReminderType.MONTHLY_REMEMBRANCE -> "Monthly Remembrance"
            ReminderType.WEEKLY_PRAYER -> "Weekly Prayer Reminder"
            ReminderType.FORTY_DAYS -> "40-Day Memorial"
            ReminderType.HUNDRED_DAYS -> "100-Day Memorial"
            else -> "Memorial Reminder"
        }

        val message = if (reminder.customMessage.isNotEmpty()) {
            reminder.customMessage
        } else {
            "Time to remember and pray for ${memorial?.deceasedName ?: "your loved one"}"
        }

        return ReminderNotification(
            id = UUID.randomUUID().toString(),
            reminderId = reminder.id,
            memorialId = reminder.memorialId,
            userId = reminder.userId,
            title = title,
            message = message,
            scheduledTime = reminder.reminderDate,
            notificationType = NotificationType.PUSH,
            actionButtons = listOf(
                NotificationAction(
                    id = "pray",
                    label = "Start Prayer",
                    action = "prayer://start/${reminder.memorialId}"
                ),
                NotificationAction(
                    id = "postpone",
                    label = "Remind Later",
                    action = "reminder://postpone/${reminder.id}"
                )
            )
        )
    }

    private fun convertHijriToGregorian(hijriDate: HijriDate): Date {
        // Simplified conversion - in production, use proper Hijri calendar library
        val gregorianYear = ((hijriDate.year * 32 / 33) + 622)
        val calendar = Calendar.getInstance()
        calendar.set(gregorianYear, hijriDate.month - 1, hijriDate.day)
        return calendar.time
    }

    private fun getRecommendedPrayers(eventType: IslamicEventType): List<PrayerType> {
        return when (eventType) {
            IslamicEventType.MEMORIAL_ANNIVERSARY -> listOf(PrayerType.FATIHAH, PrayerType.YASIN, PrayerType.DUA)
            IslamicEventType.RAMADAN -> listOf(PrayerType.TAHLIL, PrayerType.DUA)
            IslamicEventType.EID -> listOf(PrayerType.FATIHAH, PrayerType.DUA)
            IslamicEventType.FRIDAY_PRAYER -> listOf(PrayerType.FATIHAH)
            else -> listOf(PrayerType.FATIHAH, PrayerType.DUA)
        }
    }

    // Extension functions for Firestore mapping
    private fun AnniversaryReminder.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "memorialId" to memorialId,
            "userId" to userId,
            "reminderType" to reminderType.name,
            "reminderDate" to reminderDate,
            "originalDate" to originalDate,
            "isActive" to isActive,
            "notificationSettings" to notificationSettings.toFirestoreMap(),
            "customMessage" to customMessage,
            "anniversaryCount" to anniversaryCount,
            "createdAt" to createdAt,
            "lastTriggered" to lastTriggered,
            "nextScheduled" to nextScheduled
        )
    }

    private fun NotificationSettings.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "enablePushNotification" to enablePushNotification,
            "enableEmailNotification" to enableEmailNotification,
            "enableSMSNotification" to enableSMSNotification,
            "reminderTime" to reminderTime,
            "advanceNotice" to advanceNotice.name,
            "frequency" to frequency.name,
            "customSound" to customSound,
            "priority" to priority.name
        )
    }

    private fun IslamicAnniversary.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "memorialId" to memorialId,
            "hijriDate" to hijriDate.toFirestoreMap(),
            "gregorianDate" to gregorianDate,
            "eventType" to eventType.name,
            "description" to description,
            "arabicDescription" to arabicDescription,
            "recommendedPrayers" to recommendedPrayers.map { it.name },
            "significance" to significance.name,
            "isRecurring" to isRecurring,
            "notificationSettings" to notificationSettings.toFirestoreMap()
        )
    }

    private fun ReminderSettings.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "globalNotificationsEnabled" to globalNotificationsEnabled,
            "defaultReminderTime" to defaultReminderTime,
            "defaultAdvanceNotice" to defaultAdvanceNotice.name,
            "enableIslamicCalendar" to enableIslamicCalendar,
            "enableHijriDates" to enableHijriDates,
            "timezone" to timezone,
            "language" to language,
            "culturalPreferences" to culturalPreferences.toFirestoreMap(),
            "privacySettings" to privacySettings.toFirestoreMap()
        )
    }

    private fun CulturalPreferences.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "observeFortyDayTradition" to observeFortyDayTradition,
            "observeHundredDayTradition" to observeHundredDayTradition,
            "observeBirthdayRemembrance" to observeBirthdayRemembrance,
            "preferHijriCalendar" to preferHijriCalendar,
            "includePrayerTimings" to includePrayerTimings,
            "includeQiblaDirection" to includeQiblaDirection,
            "customTraditions" to customTraditions
        )
    }

    private fun ReminderPrivacySettings.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "shareWithFamily" to shareWithFamily,
            "allowCommunityNotifications" to allowCommunityNotifications,
            "showInPublicCalendar" to showInPublicCalendar,
            "allowReminderSharing" to allowReminderSharing
        )
    }

    private fun ReminderNotification.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "reminderId" to reminderId,
            "memorialId" to memorialId,
            "userId" to userId,
            "title" to title,
            "message" to message,
            "arabicMessage" to arabicMessage,
            "scheduledTime" to scheduledTime,
            "sentTime" to sentTime,
            "wasDelivered" to wasDelivered,
            "wasOpened" to wasOpened,
            "notificationType" to notificationType.name,
            "actionButtons" to actionButtons.map { it.toFirestoreMap() },
            "metadata" to metadata
        )
    }

    private fun NotificationAction.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "label" to label,
            "action" to action,
            "icon" to icon
        )
    }

    private fun HijriDate.toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "year" to year,
            "month" to month,
            "day" to day,
            "monthName" to monthName,
            "yearName" to yearName
        )
    }

    // Extension functions for converting from Firestore
    private fun com.google.firebase.firestore.DocumentSnapshot.toAnniversaryReminder(): AnniversaryReminder? {
        return try {
            AnniversaryReminder(
                id = getString("id") ?: "",
                memorialId = getString("memorialId") ?: "",
                userId = getString("userId") ?: "",
                reminderType = getString("reminderType")?.let { 
                    try { ReminderType.valueOf(it) } catch (e: Exception) { ReminderType.YEARLY_ANNIVERSARY }
                } ?: ReminderType.YEARLY_ANNIVERSARY,
                reminderDate = getDate("reminderDate") ?: Date(),
                originalDate = getDate("originalDate") ?: Date(),
                isActive = getBoolean("isActive") ?: true,
                customMessage = getString("customMessage") ?: "",
                anniversaryCount = getLong("anniversaryCount")?.toInt() ?: 1,
                createdAt = getDate("createdAt") ?: Date(),
                lastTriggered = getDate("lastTriggered"),
                nextScheduled = getDate("nextScheduled")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toReminderSettings(): ReminderSettings? {
        return try {
            ReminderSettings(
                userId = getString("userId") ?: "",
                globalNotificationsEnabled = getBoolean("globalNotificationsEnabled") ?: true,
                defaultReminderTime = getString("defaultReminderTime") ?: "09:00",
                defaultAdvanceNotice = getString("defaultAdvanceNotice")?.let {
                    try { AdvanceNotice.valueOf(it) } catch (e: Exception) { AdvanceNotice.ON_DATE }
                } ?: AdvanceNotice.ON_DATE,
                enableIslamicCalendar = getBoolean("enableIslamicCalendar") ?: true,
                enableHijriDates = getBoolean("enableHijriDates") ?: true,
                timezone = getString("timezone") ?: "UTC",
                language = getString("language") ?: "en"
            )
        } catch (e: Exception) {
            null
        }
    }
}

// Worker class for handling anniversary reminders
// TODO: Move to dedicated WorkManager module when created
/*
class AnniversaryReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val reminderId = inputData.getString("reminderId") ?: return Result.failure()
        
        // Implementation would trigger the actual notification
        // This is a placeholder for the notification logic
        
        return Result.success()
    }
}
*/
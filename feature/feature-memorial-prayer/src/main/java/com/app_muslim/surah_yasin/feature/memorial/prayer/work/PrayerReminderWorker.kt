package com.app_muslim.surah_yasin.feature.memorial.prayer.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.app_muslim.surah_yasin.feature.memorial.prayer.repository.MemorialPrayerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker for prayer reminders
 * Integrates with Firebase Cloud Messaging for push notifications
 */
@HiltWorker
class PrayerReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val repository: MemorialPrayerRepository,
    private val auth: FirebaseAuth,
    private val messaging: FirebaseMessaging
) : CoroutineWorker(context, params) {
    
    companion object {
        const val WORK_NAME = "prayer_reminder_work"
        const val MEMORIAL_ID_KEY = "memorial_id"
        const val PRAYER_TYPE_KEY = "prayer_type"
        const val CHANNEL_ID = "prayer_reminders"
        const val NOTIFICATION_ID = 1001
        
        fun scheduleDaily(
            context: Context,
            memorialId: String,
            prayerType: String,
            hourOfDay: Int = 19, // Default to 7 PM
            minute: Int = 0
        ) {
            val workManager = WorkManager.getInstance(context)
            
            val inputData = workDataOf(
                MEMORIAL_ID_KEY to memorialId,
                PRAYER_TYPE_KEY to prayerType
            )
            
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val dailyWorkRequest = PeriodicWorkRequestBuilder<PrayerReminderWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            )
                .setInputData(inputData)
                .setConstraints(constraints)
                .setInitialDelay(calculateInitialDelay(hourOfDay, minute), TimeUnit.MILLISECONDS)
                .build()
            
            workManager.enqueueUniquePeriodicWork(
                "${WORK_NAME}_${memorialId}",
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
            )
        }
        
        fun cancelReminder(context: Context, memorialId: String) {
            WorkManager.getInstance(context)
                .cancelUniqueWork("${WORK_NAME}_${memorialId}")
        }
        
        private fun calculateInitialDelay(hourOfDay: Int, minute: Int): Long {
            val now = System.currentTimeMillis()
            val calendar = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, hourOfDay)
                set(java.util.Calendar.MINUTE, minute)
                set(java.util.Calendar.SECOND, 0)
                
                // If time has passed today, schedule for tomorrow
                if (timeInMillis <= now) {
                    add(java.util.Calendar.DAY_OF_YEAR, 1)
                }
            }
            
            return calendar.timeInMillis - now
        }
    }
    
    override suspend fun doWork(): Result {
        return try {
            val memorialId = inputData.getString(MEMORIAL_ID_KEY) ?: return Result.failure()
            val prayerType = inputData.getString(PRAYER_TYPE_KEY) ?: return Result.failure()
            
            // Check if user is authenticated
            val currentUser = auth.currentUser ?: return Result.failure()
            
            // Get memorial statistics to personalize the reminder
            val stats = repository.getPrayerStatistics(memorialId)
            
            // Create notification channel (Android 8.0+)
            createNotificationChannel()
            
            // Send local notification
            sendLocalNotification(memorialId, prayerType, stats.totalSessions.toInt())
            
            // Send FCM notification for cross-device sync
            sendFCMNotification(currentUser.uid, memorialId, prayerType)
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Prayer Reminders"
            val descriptionText = "Daily prayer session reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
                setSound(
                    android.net.Uri.parse("android.resource://${context.packageName}/raw/prayer_bell"),
                    null
                )
            }
            
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun sendLocalNotification(memorialId: String, prayerType: String, sessionCount: Int) {
        // Create intent to open memorial prayer session
        val intent = Intent().apply {
            // Set intent to open the specific memorial prayer session
            putExtra("memorial_id", memorialId)
            putExtra("prayer_type", prayerType)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_agenda) // TODO: Use Islamic icon
            .setContentTitle("🕌 Time for Memorial Prayers")
            .setContentText("Remember your loved one with ${prayerType.lowercase()} prayers")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(buildString {
                        append("Remember your loved one with ${prayerType.lowercase()} prayers. ")
                        if (sessionCount > 0) {
                            append("You've completed $sessionCount prayer sessions. ")
                        }
                        append("May Allah accept your prayers and grant them peace. 🤲")
                    })
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .addAction(
                android.R.drawable.ic_media_play,
                "Start Session",
                pendingIntent
            )
            .build()
        
        // Show notification
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, notification)
        }
    }
    
    private suspend fun sendFCMNotification(userId: String, memorialId: String, prayerType: String) {
        try {
            // Get FCM token for this user
            val token = messaging.token.await()
            
            // Send notification data to Firebase Functions for cross-device delivery
            // This would typically call a Cloud Function
            val notificationData = mapOf(
                "type" to "prayer_reminder",
                "userId" to userId,
                "memorialId" to memorialId,
                "prayerType" to prayerType,
                "timestamp" to System.currentTimeMillis()
            )
            
            // TODO: Call Cloud Function to send notification
            // functions.getHttpsCallable("sendPrayerReminder").call(notificationData)
            
        } catch (e: Exception) {
            // Log error but don't fail the work
        }
    }
}

/**
 * Prayer reminder scheduler for managing multiple memorial reminders
 */
class PrayerReminderScheduler(private val context: Context) {
    
    fun scheduleMemorialReminder(
        memorialId: String,
        prayerType: String,
        reminderTime: PrayerReminderTime
    ) {
        PrayerReminderWorker.scheduleDaily(
            context = context,
            memorialId = memorialId,
            prayerType = prayerType,
            hourOfDay = reminderTime.hour,
            minute = reminderTime.minute
        )
    }
    
    fun cancelMemorialReminder(memorialId: String) {
        PrayerReminderWorker.cancelReminder(context, memorialId)
    }
    
    fun scheduleMultipleReminders(reminders: List<MemorialReminder>) {
        reminders.forEach { reminder ->
            scheduleMemorialReminder(
                memorialId = reminder.memorialId,
                prayerType = reminder.prayerType,
                reminderTime = reminder.time
            )
        }
    }
}

/**
 * Data classes for prayer reminder configuration
 */
data class PrayerReminderTime(
    val hour: Int, // 24-hour format
    val minute: Int
)

data class MemorialReminder(
    val memorialId: String,
    val prayerType: String,
    val time: PrayerReminderTime,
    val isEnabled: Boolean = true
)

/**
 * Pre-defined Islamic prayer times for reminders
 */
object IslamicReminderTimes {
    val AFTER_MAGHRIB = PrayerReminderTime(hour = 19, minute = 0) // 7:00 PM
    val AFTER_ISHA = PrayerReminderTime(hour = 21, minute = 0)    // 9:00 PM
    val BEFORE_FAJR = PrayerReminderTime(hour = 5, minute = 0)    // 5:00 AM
    val FRIDAY_AFTERNOON = PrayerReminderTime(hour = 14, minute = 0) // 2:00 PM
    val MORNING_DHIKR = PrayerReminderTime(hour = 8, minute = 0)   // 8:00 AM
}

/**
 * Prayer reminder preferences repository
 */
interface PrayerReminderPreferences {
    suspend fun saveReminderTime(memorialId: String, time: PrayerReminderTime)
    suspend fun getReminderTime(memorialId: String): PrayerReminderTime?
    suspend fun enableReminders(memorialId: String, enabled: Boolean)
    suspend fun areRemindersEnabled(memorialId: String): Boolean
    suspend fun getAllMemorialReminders(): List<MemorialReminder>
}

/**
 * Implementation using SharedPreferences for reminder settings
 */
class SharedPreferencesReminderRepository(
    private val context: Context
) : PrayerReminderPreferences {
    
    private val prefs = context.getSharedPreferences("prayer_reminders", Context.MODE_PRIVATE)
    
    override suspend fun saveReminderTime(memorialId: String, time: PrayerReminderTime) {
        prefs.edit()
            .putInt("${memorialId}_hour", time.hour)
            .putInt("${memorialId}_minute", time.minute)
            .apply()
    }
    
    override suspend fun getReminderTime(memorialId: String): PrayerReminderTime? {
        val hour = prefs.getInt("${memorialId}_hour", -1)
        val minute = prefs.getInt("${memorialId}_minute", -1)
        
        return if (hour >= 0 && minute >= 0) {
            PrayerReminderTime(hour, minute)
        } else null
    }
    
    override suspend fun enableReminders(memorialId: String, enabled: Boolean) {
        prefs.edit()
            .putBoolean("${memorialId}_enabled", enabled)
            .apply()
    }
    
    override suspend fun areRemindersEnabled(memorialId: String): Boolean {
        return prefs.getBoolean("${memorialId}_enabled", true)
    }
    
    override suspend fun getAllMemorialReminders(): List<MemorialReminder> {
        val reminders = mutableListOf<MemorialReminder>()
        val allKeys = prefs.all.keys
        
        val memorialIds = allKeys
            .filter { it.endsWith("_hour") }
            .map { it.removeSuffix("_hour") }
            .distinct()
        
        memorialIds.forEach { memorialId ->
            val time = getReminderTime(memorialId)
            val enabled = areRemindersEnabled(memorialId)
            
            if (time != null) {
                reminders.add(
                    MemorialReminder(
                        memorialId = memorialId,
                        prayerType = "tahlil", // Default prayer type
                        time = time,
                        isEnabled = enabled
                    )
                )
            }
        }
        
        return reminders
    }
}
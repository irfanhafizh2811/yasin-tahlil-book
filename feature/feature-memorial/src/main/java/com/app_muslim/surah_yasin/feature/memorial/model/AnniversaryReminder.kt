package com.app_muslim.surah_yasin.feature.memorial.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class AnniversaryReminder(
    val id: String = "",
    val memorialId: String = "",
    val userId: String = "",
    val reminderType: ReminderType = ReminderType.YEARLY_ANNIVERSARY,
    val reminderDate: Date = Date(),
    val originalDate: Date = Date(), // The original date of death or memorial creation
    val isActive: Boolean = true,
    val notificationSettings: NotificationSettings = NotificationSettings(),
    val customMessage: String = "",
    val anniversaryCount: Int = 1, // Which anniversary (1st, 2nd, etc.)
    val createdAt: Date = Date(),
    val lastTriggered: Date? = null,
    val nextScheduled: Date? = null
) : Parcelable

@Parcelize
data class NotificationSettings(
    val enablePushNotification: Boolean = true,
    val enableEmailNotification: Boolean = false,
    val enableSMSNotification: Boolean = false,
    val reminderTime: String = "09:00", // Time of day to send reminder
    val advanceNotice: AdvanceNotice = AdvanceNotice.ON_DATE,
    val frequency: ReminderFrequency = ReminderFrequency.YEARLY,
    val customSound: String? = null,
    val priority: NotificationPriority = NotificationPriority.HIGH
) : Parcelable

enum class ReminderType(val displayName: String, val description: String) {
    YEARLY_ANNIVERSARY("Yearly Anniversary", "Annual remembrance of passing"),
    MONTHLY_REMEMBRANCE("Monthly Remembrance", "Monthly prayer reminder"),
    WEEKLY_PRAYER("Weekly Prayer", "Weekly memorial prayer"),
    ISLAMIC_CALENDAR("Islamic Calendar", "Based on Hijri calendar dates"),
    PERSONAL_MILESTONE("Personal Milestone", "Custom significant dates"),
    FORTY_DAYS("40 Days", "Traditional 40-day remembrance"),
    HUNDRED_DAYS("100 Days", "100-day memorial tradition"),
    FIRST_YEAR("First Year", "One year anniversary"),
    BIRTHDAY_REMEMBRANCE("Birthday Remembrance", "Remember on their birthday")
}

enum class AdvanceNotice(val displayName: String, val days: Int) {
    ONE_WEEK_BEFORE("1 week before", 7),
    THREE_DAYS_BEFORE("3 days before", 3),
    ONE_DAY_BEFORE("1 day before", 1),
    ON_DATE("On the date", 0),
    MORNING_OF("Morning of the day", 0)
}

enum class ReminderFrequency {
    ONCE,
    YEARLY,
    MONTHLY,
    WEEKLY,
    CUSTOM
}

enum class NotificationPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

@Parcelize
data class IslamicAnniversary(
    val id: String = "",
    val memorialId: String = "",
    val hijriDate: HijriDate = HijriDate(),
    val gregorianDate: Date = Date(),
    val eventType: IslamicEventType = IslamicEventType.MEMORIAL_ANNIVERSARY,
    val description: String = "",
    val arabicDescription: String = "",
    val recommendedPrayers: List<PrayerType> = emptyList(),
    val significance: EventSignificance = EventSignificance.PERSONAL,
    val isRecurring: Boolean = true,
    val notificationSettings: NotificationSettings = NotificationSettings()
) : Parcelable

@Parcelize
data class ReminderNotification(
    val id: String = "",
    val reminderId: String = "",
    val memorialId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val arabicMessage: String = "",
    val scheduledTime: Date = Date(),
    val sentTime: Date? = null,
    val wasDelivered: Boolean = false,
    val wasOpened: Boolean = false,
    val notificationType: NotificationType = NotificationType.PUSH,
    val actionButtons: List<NotificationAction> = emptyList(),
    val metadata: Map<String, String> = emptyMap()
) : Parcelable

@Parcelize
data class NotificationAction(
    val id: String = "",
    val label: String = "",
    val action: String = "", // Deep link or action identifier
    val icon: String? = null
) : Parcelable

enum class NotificationType {
    PUSH,
    EMAIL,
    SMS,
    IN_APP
}

@Parcelize
data class AnniversaryCalendar(
    val userId: String = "",
    val upcomingAnniversaries: List<UpcomingAnniversary> = emptyList(),
    val pastAnniversaries: List<PastAnniversary> = emptyList(),
    val islamicCalendarEvents: List<IslamicEvent> = emptyList(),
    val personalMilestones: List<PersonalMilestone> = emptyList()
) : Parcelable

@Parcelize
data class UpcomingAnniversary(
    val memorialId: String = "",
    val deceasedName: String = "",
    val anniversaryDate: Date = Date(),
    val anniversaryType: ReminderType = ReminderType.YEARLY_ANNIVERSARY,
    val daysUntil: Int = 0,
    val yearsAgo: Int = 0,
    val reminderSettings: NotificationSettings = NotificationSettings(),
    val suggestedActions: List<ReminderSuggestedAction> = emptyList()
) : Parcelable

@Parcelize
data class PastAnniversary(
    val memorialId: String = "",
    val deceasedName: String = "",
    val anniversaryDate: Date = Date(),
    val commemorated: Boolean = false,
    val prayersOffered: Int = 0,
    val communityParticipation: Int = 0,
    val notes: String = ""
) : Parcelable

@Parcelize
data class PersonalMilestone(
    val id: String = "",
    val memorialId: String = "",
    val title: String = "",
    val date: Date = Date(),
    val description: String = "",
    val isRecurring: Boolean = false,
    val reminderSettings: NotificationSettings = NotificationSettings()
) : Parcelable

@Parcelize
data class ReminderSuggestedAction(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val actionType: ActionType = ActionType.PRAYER,
    val estimatedDuration: String = "",
    val difficultyLevel: DifficultyLevel = DifficultyLevel.EASY
) : Parcelable

enum class ActionType {
    PRAYER,
    CHARITY,
    QURAN_RECITATION,
    VISIT_GRAVE,
    FAMILY_GATHERING,
    COMMUNITY_PRAYER,
    FASTING,
    DHIKR,
    DUA_SUPPLICATION
}

enum class DifficultyLevel {
    EASY,
    MODERATE,
    CHALLENGING
}

@Parcelize
data class ReminderSettings(
    val userId: String = "",
    val globalNotificationsEnabled: Boolean = true,
    val defaultReminderTime: String = "09:00",
    val defaultAdvanceNotice: AdvanceNotice = AdvanceNotice.ON_DATE,
    val enableIslamicCalendar: Boolean = true,
    val enableHijriDates: Boolean = true,
    val timezone: String = "UTC",
    val language: String = "en",
    val culturalPreferences: CulturalPreferences = CulturalPreferences(),
    val privacySettings: ReminderPrivacySettings = ReminderPrivacySettings()
) : Parcelable

@Parcelize
data class CulturalPreferences(
    val observeFortyDayTradition: Boolean = true,
    val observeHundredDayTradition: Boolean = false,
    val observeBirthdayRemembrance: Boolean = true,
    val preferHijriCalendar: Boolean = false,
    val includePrayerTimings: Boolean = true,
    val includeQiblaDirection: Boolean = true,
    val customTraditions: List<String> = emptyList()
) : Parcelable

@Parcelize
data class ReminderPrivacySettings(
    val shareWithFamily: Boolean = true,
    val allowCommunityNotifications: Boolean = true,
    val showInPublicCalendar: Boolean = false,
    val allowReminderSharing: Boolean = true
) : Parcelable
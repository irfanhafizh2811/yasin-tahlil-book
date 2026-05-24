package com.app_muslim.surah_yasin.feature.memorial.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class MemorialData(
    val id: String = "",
    val creatorId: String = "",
    val creatorName: String = "",
    val deceasedName: String = "",
    val deceasedNameArabic: String? = null,
    val memorialMessage: String = "",
    val memorialMessageArabic: String? = null,
    val dateOfDeath: Date = Date(),
    val dateOfDeathHijri: HijriDate = HijriDate(),
    val photoUrl: String? = null,
    val privacyLevel: PrivacyLevel = PrivacyLevel.PRIVATE,
    val prayerType: PrayerType = PrayerType.TAHLIL,
    val createdAt: Date = Date(),
    val expiresAt: Date = Date(), // 40 days from creation (Islamic tradition)
    val isActive: Boolean = true,
    val prayerCount: Long = 0,
    val participantCount: Long = 0,
    val familyMembers: List<String> = emptyList(), // User IDs for family access
    val tags: List<String> = emptyList(),
    val region: String = "",
    val schoolOfThought: String = ""
) : Parcelable

@Parcelize
data class HijriDate(
    val year: Int = 1445,
    val month: Int = 1,
    val day: Int = 1,
    val monthName: String = "",
    val yearName: String = ""
) : Parcelable

enum class PrivacyLevel(val displayName: String, val description: String) {
    PRIVATE("Private", "Only you can see this memorial"),
    FAMILY("Family", "Only family members can participate"),
    COMMUNITY("Community", "Local Muslim community can participate"),
    PUBLIC("Global", "Muslims worldwide can participate")
}

enum class PrayerType(
    val displayName: String, 
    val arabicName: String,
    val description: String,
    val estimatedDuration: String
) {
    TAHLIL("Tahlil", "تهليل", "Complete Tahlil prayer for the deceased", "30-45 minutes"),
    YASIN("Surah Yasin", "سورة يس", "Recitation of Surah Yasin", "15-20 minutes"),
    FATIHAH("Al-Fatihah", "الفاتحة", "Recitation of Al-Fatihah", "2-3 minutes"),
    DUA("Dua for Deceased", "دعاء للميت", "Special prayers for the departed soul", "5-10 minutes"),
    FULL_CEREMONY("Full Memorial", "مراسم تأبين كاملة", "Complete memorial ceremony", "60-90 minutes")
}

@Parcelize
data class MemorialParticipation(
    val id: String = "",
    val memorialId: String = "",
    val participantId: String = "",
    val participantName: String = "",
    val prayerType: PrayerType = PrayerType.TAHLIL,
    val participatedAt: Date = Date(),
    val prayerDuration: Long = 0, // in seconds
    val isVerified: Boolean = false,
    val notes: String = ""
) : Parcelable

@Parcelize
data class MemorialStats(
    val totalPrayers: Long = 0,
    val totalParticipants: Long = 0,
    val prayersByType: Map<PrayerType, Long> = emptyMap(),
    val participantsByRegion: Map<String, Long> = emptyMap(),
    val dailyPrayerCounts: Map<String, Long> = emptyMap(), // Date string to count
    val lastPrayerAt: Date? = null
) : Parcelable

@Parcelize
data class UserMemorialsSummary(
    val totalMemorials: Long = 0,
    val activeMemorials: Long = 0,
    val expiredMemorials: Long = 0,
    val totalPrayers: Long = 0,
    val totalParticipants: Long = 0,
    val mostPopularMemorial: MemorialData? = null,
    val recentMemorial: MemorialData? = null
) : Parcelable
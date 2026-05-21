package com.app_muslim.surah_yasin.data.model

import java.util.Date
import java.util.UUID

data class Memorial(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val arabicName: String? = null, // Optional Arabic name
    val dateOfBirth: Date? = null,
    val dateOfDeath: Date,
    val hijriDateOfDeath: String? = null, // Hijri calendar date
    val relationship: RelationshipType,
    val gender: Gender,
    val photoUrl: String? = null,
    val memorialMessage: String? = null,
    val description: String? = memorialMessage, // Alias for repository compatibility
    val arabicMemorialMessage: String? = null,
    val privacy: PrivacyLevel = PrivacyLevel.PRIVATE,
    val createdBy: String, // User ID who created the memorial
    val familyMembers: List<String> = emptyList(), // User IDs of family members
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val expirationDate: Date? = calculateExpirationDate(dateOfDeath), // 40-day Islamic tradition
    val expiresAt: Date? = expirationDate, // Alias for repository compatibility
    val duration: Int = 40, // Duration in days (Islamic tradition)
    val isExpired: Boolean = false,
    val totalPrayers: Int = 0,
    val totalParticipants: Int = 0,
    val location: String? = null, // Place of death or burial
    val additionalInfo: Map<String, String> = emptyMap() // Flexible additional information
) {
    
    companion object {
        // Islamic tradition: 40 days after death
        private fun calculateExpirationDate(dateOfDeath: Date): Date {
            val calendar = java.util.Calendar.getInstance()
            calendar.time = dateOfDeath
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 40)
            return calendar.time
        }
    }
    
    // Helper methods
    fun isWithinPrayerPeriod(): Boolean {
        val now = Date()
        return expirationDate?.after(now) == true && !isExpired
    }
    
    fun getDaysRemaining(): Int {
        if (isExpired || expirationDate == null) return 0
        val now = Date()
        val diffInMs = expirationDate.time - now.time
        return (diffInMs / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    }
    
    fun canBeAccessedBy(userId: String): Boolean {
        return when (privacy) {
            PrivacyLevel.PRIVATE -> createdBy == userId
            PrivacyLevel.FAMILY -> createdBy == userId || familyMembers.contains(userId)
            PrivacyLevel.COMMUNITY -> true
        }
    }
    
    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "arabicName" to arabicName,
            "dateOfBirth" to dateOfBirth,
            "dateOfDeath" to dateOfDeath,
            "hijriDateOfDeath" to hijriDateOfDeath,
            "relationship" to relationship.name,
            "gender" to gender.name,
            "photoUrl" to photoUrl,
            "memorialMessage" to memorialMessage,
            "arabicMemorialMessage" to arabicMemorialMessage,
            "privacy" to privacy.name,
            "createdBy" to createdBy,
            "familyMembers" to familyMembers,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "expirationDate" to expirationDate,
            "isExpired" to isExpired,
            "totalPrayers" to totalPrayers,
            "totalParticipants" to totalParticipants,
            "location" to location,
            "additionalInfo" to additionalInfo
        )
    }
}

enum class RelationshipType {
    PARENT, CHILD, SPOUSE, SIBLING, GRANDPARENT, GRANDCHILD,
    UNCLE, AUNT, COUSIN, FRIEND, RELATIVE, OTHER;
    
    fun getDisplayName(): String {
        return when (this) {
            PARENT -> "Parent"
            CHILD -> "Child" 
            SPOUSE -> "Spouse"
            SIBLING -> "Sibling"
            GRANDPARENT -> "Grandparent"
            GRANDCHILD -> "Grandchild"
            UNCLE -> "Uncle"
            AUNT -> "Aunt"
            COUSIN -> "Cousin"
            FRIEND -> "Friend"
            RELATIVE -> "Relative"
            OTHER -> "Other"
        }
    }
    
    fun getArabicDisplayName(): String {
        return when (this) {
            PARENT -> "والد/والدة"
            CHILD -> "ابن/ابنة" 
            SPOUSE -> "زوج/زوجة"
            SIBLING -> "أخ/أخت"
            GRANDPARENT -> "جد/جدة"
            GRANDCHILD -> "حفيد/حفيدة"
            UNCLE -> "عم/خال"
            AUNT -> "عمة/خالة"
            COUSIN -> "ابن عم/ابن خال"
            FRIEND -> "صديق/صديقة"
            RELATIVE -> "قريب/قريبة"
            OTHER -> "آخر"
        }
    }
}

enum class PrivacyLevel {
    PRIVATE, FAMILY, COMMUNITY;
    
    fun getDisplayName(): String {
        return when (this) {
            PRIVATE -> "Private (Only Me)"
            FAMILY -> "Family Members"
            COMMUNITY -> "Community (Public)"
        }
    }
    
    fun getDescription(): String {
        return when (this) {
            PRIVATE -> "Only you can see and pray for this memorial"
            FAMILY -> "Family members you add can see and pray for this memorial"
            COMMUNITY -> "Anyone in the community can see and pray for this memorial"
        }
    }
}

enum class Gender {
    MALE, FEMALE;
    
    fun getDisplayName(): String {
        return when (this) {
            MALE -> "Male"
            FEMALE -> "Female"
        }
    }
    
    fun getArabicDisplayName(): String {
        return when (this) {
            MALE -> "ذكر"
            FEMALE -> "أنثى"
        }
    }
    
    fun getPrayerSuffix(): String {
        return when (this) {
            MALE -> "رحمه الله" // "May Allah have mercy on him"
            FEMALE -> "رحمها الله" // "May Allah have mercy on her"
        }
    }
}

// Extension functions for convenience
fun Memorial.isRecentlyCreated(): Boolean {
    val threeDaysAgo = java.util.Calendar.getInstance().apply {
        add(java.util.Calendar.DAY_OF_YEAR, -3)
    }.time
    return createdAt.after(threeDaysAgo)
}

fun Memorial.getAgeAtDeath(): Int? {
    if (dateOfBirth == null) return null
    val calendar = java.util.Calendar.getInstance()
    calendar.time = dateOfDeath
    val deathYear = calendar.get(java.util.Calendar.YEAR)
    calendar.time = dateOfBirth
    val birthYear = calendar.get(java.util.Calendar.YEAR)
    return deathYear - birthYear
}

fun Memorial.getFormattedName(): String {
    val baseName = if (arabicName.isNullOrBlank()) name else "$name ($arabicName)"
    return "$baseName ${gender.getPrayerSuffix()}"
}

// Extension function for Memorial companion object
fun Memorial.Companion.fromFirestoreMap(data: Map<String, Any>): Memorial {
    return Memorial(
        id = data["id"] as? String ?: "",
        name = data["name"] as? String ?: "",
        arabicName = data["arabicName"] as? String,
        dateOfDeath = data["dateOfDeath"] as? Date ?: Date(),
        relationship = RelationshipType.valueOf(data["relationship"] as? String ?: "OTHER"),
        gender = Gender.valueOf(data["gender"] as? String ?: "MALE"),
        privacy = PrivacyLevel.valueOf(data["privacy"] as? String ?: "PRIVATE"),
        createdBy = data["createdBy"] as? String ?: "",
        createdAt = data["createdAt"] as? Date ?: Date(),
        expirationDate = data["expirationDate"] as? Date
    )
}
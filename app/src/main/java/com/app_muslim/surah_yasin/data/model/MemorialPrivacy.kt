package com.app_muslim.surah_yasin.data.model

/**
 * Enumeration of different memorial privacy levels
 */
enum class MemorialPrivacy(
    val displayName: String,
    val description: String
) {
    PRIVATE(
        "Private",
        "Only visible to you"
    ),
    
    FAMILY(
        "Family Only",
        "Visible to invited family members only"
    ),
    
    COMMUNITY(
        "Community",
        "Visible to the Islamic community"
    ),
    
    PUBLIC(
        "Public", 
        "Visible to all users"
    );

    /**
     * Check if this privacy level allows public access
     */
    fun isPubliclyVisible(): Boolean = when (this) {
        PUBLIC -> true
        else -> false
    }

    /**
     * Check if this privacy level allows community access
     */
    fun isCommunityVisible(): Boolean = when (this) {
        COMMUNITY, PUBLIC -> true
        else -> false
    }

    /**
     * Check if this privacy level allows family access
     */
    fun isFamilyVisible(): Boolean = when (this) {
        FAMILY, COMMUNITY, PUBLIC -> true
        else -> false
    }

    /**
     * Get recommended privacy level based on Islamic tradition
     */
    companion object {
        fun getDefaultPrivacy(): MemorialPrivacy = FAMILY
        
        fun fromString(value: String): MemorialPrivacy {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: PRIVATE
        }
    }
}
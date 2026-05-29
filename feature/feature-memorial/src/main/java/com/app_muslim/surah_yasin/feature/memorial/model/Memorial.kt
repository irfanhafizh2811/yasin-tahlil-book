package com.app_muslim.surah_yasin.feature.memorial.model

data class Memorial(
    val id: String = "",
    val title: String = "",
    val deceasedName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val totalPrayers: Int = 0,
    val description: String? = null,
    val photoUrl: String? = null,
    val familyName: String? = null,
    val isPublic: Boolean = true,
    val ownerId: String = ""
)
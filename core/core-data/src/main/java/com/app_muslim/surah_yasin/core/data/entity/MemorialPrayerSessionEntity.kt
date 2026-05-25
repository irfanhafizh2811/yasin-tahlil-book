package com.app_muslim.surah_yasin.core.data.entity

import androidx.room.*

/**
 * Room entity for offline memorial prayer session storage
 */
@Entity(tableName = "memorial_prayer_sessions")
data class MemorialPrayerSessionEntity(
    @PrimaryKey
    val id: String,
    val memorialId: String,
    val userId: String,
    val prayerType: String, // MemorialPrayerType.name
    val currentCount: Int,
    val targetCount: Int,
    val state: String, // PrayerSessionState.name
    val startTime: Long?, // timestamp
    val endTime: Long?, // timestamp
    val pausedDuration: Long,
    val isCompleted: Boolean,
    val memorialName: String,
    val memorialPhotoUrl: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false
)
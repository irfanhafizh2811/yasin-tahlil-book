package com.app_muslim.surah_yasin.feature.memorial.prayer.model

/**
 * UI state models specifically for Memorial Prayer screens
 * Separated from core models for better organization
 */

// Main UI State for Memorial Prayer Screen
data class MemorialPrayerUiState(
    val memorialPrayerState: MemorialPrayerState = MemorialPrayerState.Idle,
    val currentSession: MemorialPrayerSession? = null,
    val recentSessions: List<MemorialPrayerSession> = emptyList(),
    val statistics: MemorialPrayerStats? = null,
    val globalStats: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val isRealTimeSync: Boolean = true,
    val errorMessage: String? = null,
    val celebrationState: CelebrationState = CelebrationState.None,
    val settings: MemorialPrayerSettings = MemorialPrayerSettings()
)

// Note: We use MemorialPrayerStats from MemorialPrayerModels.kt for statistics

// Enhanced Memorial Prayer State with Active state
sealed class MemorialPrayerState {
    object Idle : MemorialPrayerState()
    object Loading : MemorialPrayerState()
    object Active : MemorialPrayerState() // Missing state
    data class InProgress(val session: MemorialPrayerSession, val progress: PrayerProgress) : MemorialPrayerState()
    data class Paused(val session: MemorialPrayerSession, val progress: PrayerProgress) : MemorialPrayerState()
    data class Completed(val session: MemorialPrayerSession) : MemorialPrayerState()
    data class Error(val message: String, val throwable: Throwable? = null) : MemorialPrayerState()
}

// Enhanced Prayer Progress with all required parameters
data class PrayerProgress(
    val currentCount: Int,
    val targetCount: Int,
    val prayerType: PrayerType,
    val duration: Long, // session duration in milliseconds
    val percentage: Float = (currentCount.toFloat() / targetCount.toFloat() * 100f),
    val estimatedTimeRemaining: Long = 0L,
    val averagePrayerSpeed: Float = 0f
)

// Celebration states for UI feedback
sealed class CelebrationState {
    object None : CelebrationState()
    data class Milestone(val percentage: Int, val prayerType: String) : CelebrationState()
    data class Completion(val session: MemorialPrayerSession) : CelebrationState()
}

// Settings for memorial prayer features
data class MemorialPrayerSettings(
    val isVibrationEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val autoSyncEnabled: Boolean = true,
    val showGlobalStats: Boolean = true,
    val celebrationsEnabled: Boolean = true,
    val offlineMode: Boolean = false
)

// Real-time sync status
data class SyncStatus(
    val isConnected: Boolean = true,
    val lastSyncTime: Long = System.currentTimeMillis(),
    val pendingChanges: Int = 0,
    val syncError: String? = null
)
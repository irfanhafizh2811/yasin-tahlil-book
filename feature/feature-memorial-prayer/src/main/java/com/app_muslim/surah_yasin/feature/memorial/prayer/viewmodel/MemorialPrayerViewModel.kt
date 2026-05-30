package com.app_muslim.surah_yasin.feature.memorial.prayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.feature.memorial.prayer.model.*
import com.app_muslim.surah_yasin.feature.memorial.prayer.repository.MemorialPrayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Memorial Prayer Sessions
 * Manages prayer state, progress tracking, and UI interactions
 */
@HiltViewModel
class MemorialPrayerViewModel @Inject constructor(
    private val memorialPrayerRepository: MemorialPrayerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MemorialPrayerUiState())
    val uiState: StateFlow<MemorialPrayerUiState> = _uiState.asStateFlow()
    
    private val _currentSession = MutableStateFlow<MemorialPrayerSession?>(null)
    val currentSession: StateFlow<MemorialPrayerSession?> = _currentSession.asStateFlow()
    
    // Global prayer statistics flow
    val globalPrayerStats: StateFlow<Map<String, Any>> = memorialPrayerRepository
        .getGlobalPrayerStatsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
    
    fun handleEvent(event: MemorialPrayerEvent) {
        when (event) {
            is MemorialPrayerEvent.StartSession -> startPrayerSession(
                event.memorialId,
                event.prayerType,
                event.targetCount
            )
            is MemorialPrayerEvent.PauseSession -> pauseSession()
            is MemorialPrayerEvent.ResumeSession -> resumeSession()
            is MemorialPrayerEvent.CompleteSession -> completeSession()
            is MemorialPrayerEvent.IncrementPrayer -> incrementPrayer()
            is MemorialPrayerEvent.UpdateSettings -> updateCulturalSettings(event.culturalSettings)
            is MemorialPrayerEvent.LoadRecentSessions -> loadRecentSessions()
            is MemorialPrayerEvent.LoadStatistics -> loadStatistics()
            is MemorialPrayerEvent.DeleteSession -> deleteSession(event.sessionId)
        }
    }
    
    private fun startPrayerSession(memorialId: String, prayerType: PrayerType, targetCount: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val culturalSettings = CulturalSettings(
                schoolOfThought = SchoolOfThought.SHAFII,
                language = "en",
                regionCode = "US",
                prayerTradition = PrayerTradition.INDIVIDUAL
            )
            
            val result = memorialPrayerRepository.startPrayerSession(
                memorialId = memorialId,
                prayerType = prayerType,
                targetCount = targetCount,
                culturalSettings = culturalSettings
            )
            
            result.fold(
                onSuccess = { session ->
                    _currentSession.value = session
                    val progress = PrayerProgress(
                        currentCount = 0,
                        targetCount = targetCount,
                        prayerType = prayerType,
                        duration = 0L,
                        percentage = 0f,
                        estimatedTimeRemaining = 0L,
                        averagePrayerSpeed = 0f
                    )
                    _uiState.value = _uiState.value.copy(
                        memorialPrayerState = MemorialPrayerState.InProgress(session, progress),
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        memorialPrayerState = MemorialPrayerState.Error(error.message ?: "Unknown error"),
                        isLoading = false
                    )
                }
            )
        }
    }
    
    private fun pauseSession() {
        viewModelScope.launch {
            _currentSession.value?.let { session ->
                val result = memorialPrayerRepository.pausePrayerSession(session.sessionId)
                result.fold(
                    onSuccess = { updatedSession ->
                        _currentSession.value = updatedSession
                        val currentState = _uiState.value.memorialPrayerState
                        if (currentState is MemorialPrayerState.InProgress) {
                            _uiState.value = _uiState.value.copy(
                                memorialPrayerState = MemorialPrayerState.Paused(updatedSession, currentState.progress)
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message
                        )
                    }
                )
            }
        }
    }
    
    private fun resumeSession() {
        viewModelScope.launch {
            _currentSession.value?.let { session ->
                val result = memorialPrayerRepository.resumePrayerSession(session.sessionId)
                result.fold(
                    onSuccess = { updatedSession ->
                        _currentSession.value = updatedSession
                        val currentState = _uiState.value.memorialPrayerState
                        if (currentState is MemorialPrayerState.Paused) {
                            _uiState.value = _uiState.value.copy(
                                memorialPrayerState = MemorialPrayerState.InProgress(updatedSession, currentState.progress)
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message
                        )
                    }
                )
            }
        }
    }
    
    private fun completeSession() {
        viewModelScope.launch {
            _currentSession.value?.let { session ->
                val result = memorialPrayerRepository.completePrayerSession(session.sessionId)
                result.fold(
                    onSuccess = { completedSession ->
                        _currentSession.value = completedSession
                        _uiState.value = _uiState.value.copy(
                            memorialPrayerState = MemorialPrayerState.Completed(completedSession)
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message
                        )
                    }
                )
            }
        }
    }
    
    private fun incrementPrayer() {
        viewModelScope.launch {
            _currentSession.value?.let { session ->
                val newCount = session.prayerCount + 1
                val result = memorialPrayerRepository.updatePrayerProgress(session.sessionId, newCount)
                result.fold(
                    onSuccess = { updatedSession ->
                        _currentSession.value = updatedSession
                        updateProgressState(updatedSession)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message
                        )
                    }
                )
            }
        }
    }
    
    private fun updateProgressState(session: MemorialPrayerSession) {
        val currentState = _uiState.value.memorialPrayerState
        if (currentState is MemorialPrayerState.InProgress) {
            val targetCount = session.prayerType.defaultCount
            val progress = PrayerProgress(
                currentCount = session.prayerCount,
                targetCount = targetCount,
                prayerType = session.prayerType,
                duration = System.currentTimeMillis() - session.startTime.toInstant().toEpochMilli(),
                percentage = (session.prayerCount.toFloat() / targetCount.toFloat()) * 100f,
                estimatedTimeRemaining = 0L, // TODO: Calculate based on speed
                averagePrayerSpeed = 0f // TODO: Calculate based on history
            )
            _uiState.value = _uiState.value.copy(
                memorialPrayerState = MemorialPrayerState.InProgress(session, progress)
            )
        }
    }
    
    private fun updateCulturalSettings(culturalSettings: CulturalSettings) {
        // TODO: Save cultural settings to preferences
    }
    
    private fun loadRecentSessions() {
        viewModelScope.launch {
            // TODO: Load recent sessions from repository
        }
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            // TODO: Load statistics from repository
        }
    }
    
    private fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            val result = memorialPrayerRepository.deletePrayerSession(sessionId)
            result.fold(
                onSuccess = {
                    loadRecentSessions()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message
                    )
                }
            )
        }
    }
    
    /**
     * Get real-time prayer statistics flow for a memorial
     */
    fun getPrayerStatisticsFlow(memorialId: String): StateFlow<MemorialPrayerStats> {
        return memorialPrayerRepository
            .getPrayerStatisticsFlow(memorialId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = MemorialPrayerStats(0, 0, 0, "Evening", PrayerType.FATIHAH, 0, 0)
            )
    }
    
    /**
     * Get real-time recent sessions for a memorial
     */
    fun getRecentSessionsFlow(memorialId: String): StateFlow<List<MemorialPrayerSession>> {
        return memorialPrayerRepository
            .getRecentSessionsFlow(memorialId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    /**
     * Sync offline data when network becomes available
     */
    fun syncOfflineData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = memorialPrayerRepository.syncOfflineData()
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Sync failed: ${error.message}"
                    )
                }
            )
        }
    }
}
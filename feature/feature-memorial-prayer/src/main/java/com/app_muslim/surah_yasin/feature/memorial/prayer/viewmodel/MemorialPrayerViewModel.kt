package com.app_muslim.surah_yasin.feature.memorial.prayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.feature.memorial.prayer.model.*
import com.app_muslim.surah_yasin.feature.memorial.prayer.repository.MemorialPrayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Memorial Prayer Sessions
 * Manages prayer counter state and session lifecycle
 */
@HiltViewModel
class MemorialPrayerViewModel @Inject constructor(
    private val repository: MemorialPrayerRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(PrayerCounterUiState())
    val uiState: StateFlow<PrayerCounterUiState> = _uiState.asStateFlow()

    // Current active session
    private val _currentSession = MutableStateFlow<MemorialPrayerSession?>(null)
    val currentSession: StateFlow<MemorialPrayerSession?> = _currentSession.asStateFlow()

    // Prayer statistics
    private val _prayerStats = MutableStateFlow(PrayerSessionStats())
    val prayerStats: StateFlow<PrayerSessionStats> = _prayerStats.asStateFlow()

    // Recent sessions for display
    private val _recentSessions = MutableStateFlow<List<MemorialPrayerSession>>(emptyList())
    val recentSessions: StateFlow<List<MemorialPrayerSession>> = _recentSessions.asStateFlow()

    init {
        loadInitialData()
    }

    /**
     * Load initial data when ViewModel is created
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            loadActiveSession()
            loadPrayerStatistics()
            loadRecentSessions()
        }
    }

    /**
     * Start a new prayer session for a memorial
     */
    fun startNewPrayerSession(
        memorialId: String,
        memorialName: String,
        memorialPhotoUrl: String?,
        prayerType: MemorialPrayerType,
        customTargetCount: Int? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Check if there's already an active session
                val activeSessionResult = repository.getActiveSession("")
                if (activeSessionResult.isSuccess && activeSessionResult.getOrNull() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Please complete or cancel your current prayer session first"
                    )
                    return@launch
                }

                // Create new session
                val createResult = repository.createPrayerSession(
                    memorialId = memorialId,
                    memorialName = memorialName,
                    memorialPhotoUrl = memorialPhotoUrl,
                    prayerType = prayerType,
                    targetCount = customTargetCount
                )

                if (createResult.isSuccess) {
                    val session = createResult.getOrThrow()
                    
                    // Start the session
                    val startResult = repository.startPrayerSession(session.id)
                    if (startResult.isSuccess) {
                        _currentSession.value = startResult.getOrThrow()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            session = _currentSession.value,
                            canStartNewSession = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to start prayer session: ${startResult.exceptionOrNull()?.message}"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to create prayer session: ${createResult.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error starting prayer session: ${e.message}"
                )
            }
        }
    }

    /**
     * Increment prayer count (main prayer counter action)
     */
    fun incrementPrayerCount() {
        val currentSession = _currentSession.value ?: return
        
        if (currentSession.state != PrayerSessionState.IN_PROGRESS) {
            _uiState.value = _uiState.value.copy(
                error = "Prayer session is not active"
            )
            return
        }

        viewModelScope.launch {
            try {
                val result = repository.incrementPrayerCount(currentSession.id)
                
                if (result.isSuccess) {
                    val updatedSession = result.getOrThrow()
                    _currentSession.value = updatedSession
                    
                    // Update UI state
                    _uiState.value = _uiState.value.copy(
                        session = updatedSession,
                        showCompletionDialog = updatedSession.isCompleted,
                        error = null
                    )
                    
                    // If session completed, update stats
                    if (updatedSession.isCompleted) {
                        loadPrayerStatistics()
                        loadRecentSessions()
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to update prayer count: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error updating prayer count: ${e.message}"
                )
            }
        }
    }

    /**
     * Pause current prayer session
     */
    fun pausePrayerSession() {
        val currentSession = _currentSession.value ?: return

        viewModelScope.launch {
            try {
                val result = repository.pausePrayerSession(currentSession.id)
                
                if (result.isSuccess) {
                    _currentSession.value = result.getOrThrow()
                    _uiState.value = _uiState.value.copy(
                        session = _currentSession.value,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to pause session: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error pausing session: ${e.message}"
                )
            }
        }
    }

    /**
     * Resume paused prayer session
     */
    fun resumePrayerSession() {
        val currentSession = _currentSession.value ?: return

        viewModelScope.launch {
            try {
                val result = repository.resumePrayerSession(currentSession.id)
                
                if (result.isSuccess) {
                    _currentSession.value = result.getOrThrow()
                    _uiState.value = _uiState.value.copy(
                        session = _currentSession.value,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to resume session: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error resuming session: ${e.message}"
                )
            }
        }
    }

    /**
     * Complete current prayer session
     */
    fun completePrayerSession() {
        val currentSession = _currentSession.value ?: return

        viewModelScope.launch {
            try {
                val result = repository.completePrayerSession(currentSession.id)
                
                if (result.isSuccess) {
                    _currentSession.value = null
                    _uiState.value = _uiState.value.copy(
                        session = null,
                        showCompletionDialog = true,
                        canStartNewSession = true,
                        error = null
                    )
                    
                    // Refresh statistics and recent sessions
                    loadPrayerStatistics()
                    loadRecentSessions()
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to complete session: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error completing session: ${e.message}"
                )
            }
        }
    }

    /**
     * Cancel current prayer session
     */
    fun cancelPrayerSession() {
        val currentSession = _currentSession.value ?: return

        viewModelScope.launch {
            try {
                val result = repository.cancelPrayerSession(currentSession.id)
                
                if (result.isSuccess) {
                    _currentSession.value = null
                    _uiState.value = _uiState.value.copy(
                        session = null,
                        canStartNewSession = true,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to cancel session: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error cancelling session: ${e.message}"
                )
            }
        }
    }

    /**
     * Load active session on startup
     */
    private suspend fun loadActiveSession() {
        try {
            val result = repository.getActiveSession("")
            if (result.isSuccess) {
                val activeSession = result.getOrNull()
                _currentSession.value = activeSession
                _uiState.value = _uiState.value.copy(
                    session = activeSession,
                    canStartNewSession = activeSession == null
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to load active session: ${e.message}"
            )
        }
    }

    /**
     * Load prayer statistics for display
     */
    private suspend fun loadPrayerStatistics() {
        try {
            val result = repository.getPrayerStatistics("")
            if (result.isSuccess) {
                val stats = result.getOrThrow()
                _prayerStats.value = stats
                _uiState.value = _uiState.value.copy(stats = stats)
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to load statistics: ${e.message}"
            )
        }
    }

    /**
     * Load recent prayer sessions
     */
    private suspend fun loadRecentSessions() {
        try {
            val result = repository.getRecentSessions("", limit = 5)
            if (result.isSuccess) {
                val sessions = result.getOrThrow()
                _recentSessions.value = sessions
                _uiState.value = _uiState.value.copy(recentSessions = sessions)
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to load recent sessions: ${e.message}"
            )
        }
    }

    /**
     * Dismiss completion dialog
     */
    fun dismissCompletionDialog() {
        _uiState.value = _uiState.value.copy(showCompletionDialog = false)
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Refresh all data
     */
    fun refreshData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            loadInitialData()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    /**
     * Get prayer text for current session
     */
    fun getCurrentPrayerText(): PrayerTextContent? {
        val session = _currentSession.value ?: return null
        return MemorialPrayerTexts.getTextForPrayerType(session.prayerType)
    }

    /**
     * Manual sync with Firebase
     */
    fun syncPrayerSessions() {
        viewModelScope.launch {
            try {
                val result = repository.syncPrayerSessions("")
                if (result.isFailure) {
                    _uiState.value = _uiState.value.copy(
                        error = "Sync failed: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Sync error: ${e.message}"
                )
            }
        }
    }
}
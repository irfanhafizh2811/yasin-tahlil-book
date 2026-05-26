package com.app_muslim.surah_yasin.feature.community.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.feature.community.model.*
import com.app_muslim.surah_yasin.feature.community.repository.CommunityPrayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Community Home Screen
 * Manages global prayer statistics, active sessions, and community activity
 */
@HiltViewModel
class CommunityHomeViewModel @Inject constructor(
    private val communityRepository: CommunityPrayerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CommunityHomeUiState())
    val uiState: StateFlow<CommunityHomeUiState> = _uiState.asStateFlow()
    
    private val _selectedRegion = MutableStateFlow("global")
    val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()
    
    init {
        loadCommunityData()
    }
    
    fun handleEvent(event: CommunityEvent) {
        when (event) {
            is CommunityEvent.LoadGlobalStats -> loadGlobalStats()
            is CommunityEvent.LoadRegionalStats -> loadRegionalStats()
            is CommunityEvent.LoadActiveSessions -> loadActiveSessions()
            is CommunityEvent.LoadRecentActivity -> loadRecentActivity()
            is CommunityEvent.LoadUpcomingEvents -> loadUpcomingEvents()
            is CommunityEvent.JoinSession -> joinSession(event.sessionId)
            is CommunityEvent.LeaveSession -> leaveSession(event.sessionId)
            is CommunityEvent.CreateSession -> createSession(event.session)
            is CommunityEvent.UpdatePrayerProgress -> updatePrayerProgress(event.sessionId, event.prayerCount)
            is CommunityEvent.ChangeRegion -> changeRegion(event.regionCode)
            else -> { /* Handle other events in specific ViewModels */ }
        }
    }
    
    private fun loadCommunityData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Combine all data flows
            combine(
                communityRepository.getGlobalPrayerStatsFlow(),
                communityRepository.getRegionalStatsFlow(10),
                communityRepository.getActiveSessionsFlow(),
                communityRepository.getPrayerActivityFlow(limit = 20),
                communityRepository.getCommunityEventsFlow()
            ) { globalStats, regionalStats, activeSessions, recentActivity, upcomingEvents ->
                CommunityHomeUiState(
                    globalStats = globalStats,
                    regionalStats = regionalStats,
                    activeSessions = activeSessions,
                    recentActivity = recentActivity,
                    upcomingEvents = upcomingEvents,
                    userRegion = _selectedRegion.value,
                    isLoading = false,
                    errorMessage = null
                )
            }.catch { e ->
                emit(_uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error occurred"
                ))
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
    
    private fun loadGlobalStats() {
        viewModelScope.launch {
            communityRepository.getGlobalPrayerStatsFlow()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to load global statistics: ${e.message}"
                    )
                }
                .collect { stats ->
                    _uiState.value = _uiState.value.copy(globalStats = stats)
                }
        }
    }
    
    private fun loadRegionalStats() {
        viewModelScope.launch {
            communityRepository.getRegionalStatsFlow(10)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to load regional statistics: ${e.message}"
                    )
                }
                .collect { stats ->
                    _uiState.value = _uiState.value.copy(regionalStats = stats)
                }
        }
    }
    
    private fun loadActiveSessions() {
        viewModelScope.launch {
            val regionCode = if (_selectedRegion.value == "global") null else _selectedRegion.value
            
            communityRepository.getActiveSessionsFlow(regionCode)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to load active sessions: ${e.message}"
                    )
                }
                .collect { sessions ->
                    _uiState.value = _uiState.value.copy(activeSessions = sessions)
                }
        }
    }
    
    private fun loadRecentActivity() {
        viewModelScope.launch {
            val regionCode = if (_selectedRegion.value == "global") null else _selectedRegion.value
            
            communityRepository.getPrayerActivityFlow(regionCode, 20)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to load recent activity: ${e.message}"
                    )
                }
                .collect { activity ->
                    _uiState.value = _uiState.value.copy(recentActivity = activity)
                }
        }
    }
    
    private fun loadUpcomingEvents() {
        viewModelScope.launch {
            val regionCode = if (_selectedRegion.value == "global") null else _selectedRegion.value
            
            communityRepository.getCommunityEventsFlow(regionCode)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to load upcoming events: ${e.message}"
                    )
                }
                .collect { events ->
                    _uiState.value = _uiState.value.copy(upcomingEvents = events)
                }
        }
    }
    
    private fun joinSession(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Create participant object - in real app, get user details from auth/profile
            val participant = PrayerParticipant(
                userId = "current_user_id", // Get from auth
                displayName = "Current User", // Get from profile
                joinedAt = java.time.ZonedDateTime.now(),
                regionCode = _selectedRegion.value
            )
            
            val result = communityRepository.joinSession(sessionId, participant)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // Refresh sessions to show updated participant list
                    loadActiveSessions()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to join session: ${error.message}"
                    )
                }
            )
        }
    }
    
    private fun leaveSession(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = communityRepository.leaveSession(sessionId, "current_user_id") // Get from auth
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // Refresh sessions
                    loadActiveSessions()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to leave session: ${error.message}"
                    )
                }
            )
        }
    }
    
    private fun createSession(session: CommunityPrayerSession) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = communityRepository.createCommunitySession(session)
            result.fold(
                onSuccess = { sessionId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // Refresh sessions to show new session
                    loadActiveSessions()
                    loadRecentActivity()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to create session: ${error.message}"
                    )
                }
            )
        }
    }
    
    private fun updatePrayerProgress(sessionId: String, prayerCount: Int) {
        viewModelScope.launch {
            val result = communityRepository.updateSessionProgress(sessionId, prayerCount)
            result.fold(
                onSuccess = {
                    // Refresh sessions to show updated progress
                    loadActiveSessions()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to update progress: ${error.message}"
                    )
                }
            )
        }
    }
    
    private fun changeRegion(regionCode: String) {
        _selectedRegion.value = regionCode
        _uiState.value = _uiState.value.copy(userRegion = regionCode)
        
        // Reload data for new region
        loadActiveSessions()
        loadRecentActivity()
    }
    
    fun refreshData() {
        loadCommunityData()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
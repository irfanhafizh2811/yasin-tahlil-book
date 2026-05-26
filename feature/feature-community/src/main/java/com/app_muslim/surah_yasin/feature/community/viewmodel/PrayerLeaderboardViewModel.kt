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
 * ViewModel for Prayer Leaderboard Screen
 * Manages leaderboard data, rankings, and user statistics
 */
@HiltViewModel
class PrayerLeaderboardViewModel @Inject constructor(
    private val communityRepository: CommunityPrayerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PrayerLeaderboardUiState())
    val uiState: StateFlow<PrayerLeaderboardUiState> = _uiState.asStateFlow()
    
    private val _selectedTimeFrame = MutableStateFlow(LeaderboardTimeFrame.THIS_WEEK)
    val selectedTimeFrame: StateFlow<LeaderboardTimeFrame> = _selectedTimeFrame.asStateFlow()
    
    private val _selectedRegion = MutableStateFlow("global")
    val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()
    
    init {
        loadLeaderboardData()
        loadUserRank()
    }
    
    fun handleEvent(event: CommunityEvent) {
        when (event) {
            is CommunityEvent.LoadLeaderboard -> loadLeaderboard(event.timeFrame, event.region)
            is CommunityEvent.ChangeRegion -> changeRegion(event.regionCode)
            else -> { /* Handle specific leaderboard events */ }
        }
    }
    
    fun changeTimeFrame(timeFrame: LeaderboardTimeFrame) {
        _selectedTimeFrame.value = timeFrame
        _uiState.value = _uiState.value.copy(selectedTimeFrame = timeFrame)
        loadLeaderboard(timeFrame, _selectedRegion.value)
    }
    
    private fun loadLeaderboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            combine(
                communityRepository.getPrayerLeaderboardFlow(
                    timeFrame = _selectedTimeFrame.value,
                    region = if (_selectedRegion.value == "global") null else _selectedRegion.value,
                    limit = 100
                ),
                communityRepository.getUserRankFlow("current_user_id", _selectedTimeFrame.value)
            ) { leaderboard, userRank ->
                val globalLeaderboard = if (_selectedRegion.value == "global") leaderboard else emptyList()
                val regionalLeaderboard = if (_selectedRegion.value != "global") leaderboard else emptyList()
                
                PrayerLeaderboardUiState(
                    globalLeaderboard = globalLeaderboard,
                    regionalLeaderboard = regionalLeaderboard,
                    userRank = userRank,
                    selectedTimeFrame = _selectedTimeFrame.value,
                    selectedRegion = _selectedRegion.value,
                    isLoading = false,
                    errorMessage = null
                )
            }.catch { e ->
                emit(_uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load leaderboard"
                ))
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
    
    private fun loadLeaderboard(timeFrame: LeaderboardTimeFrame, region: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val regionCode = if (region == "global") null else region
            
            communityRepository.getPrayerLeaderboardFlow(timeFrame, regionCode, 100)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load leaderboard: ${e.message}"
                    )
                }
                .collect { leaderboard ->
                    val globalLeaderboard = if (region == "global") leaderboard else _uiState.value.globalLeaderboard
                    val regionalLeaderboard = if (region != "global") leaderboard else _uiState.value.regionalLeaderboard
                    
                    _uiState.value = _uiState.value.copy(
                        globalLeaderboard = globalLeaderboard,
                        regionalLeaderboard = regionalLeaderboard,
                        selectedTimeFrame = timeFrame,
                        selectedRegion = region,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }
    
    private fun loadUserRank() {
        viewModelScope.launch {
            communityRepository.getUserRankFlow("current_user_id", _selectedTimeFrame.value)
                .catch { e ->
                    // User rank is optional, don't show error for this
                }
                .collect { userRank ->
                    _uiState.value = _uiState.value.copy(userRank = userRank)
                }
        }
    }
    
    private fun changeRegion(regionCode: String) {
        _selectedRegion.value = regionCode
        _uiState.value = _uiState.value.copy(selectedRegion = regionCode)
        loadLeaderboard(_selectedTimeFrame.value, regionCode)
    }
    
    fun refreshLeaderboard() {
        loadLeaderboardData()
        loadUserRank()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    // Helper functions for UI
    fun getTopThreeEntries(): List<PrayerLeaderboardEntry> {
        val currentLeaderboard = if (_selectedRegion.value == "global") {
            _uiState.value.globalLeaderboard
        } else {
            _uiState.value.regionalLeaderboard
        }
        return currentLeaderboard.take(3)
    }
    
    fun getRestOfLeaderboard(): List<PrayerLeaderboardEntry> {
        val currentLeaderboard = if (_selectedRegion.value == "global") {
            _uiState.value.globalLeaderboard
        } else {
            _uiState.value.regionalLeaderboard
        }
        return currentLeaderboard.drop(3)
    }
    
    fun getCurrentLeaderboard(): List<PrayerLeaderboardEntry> {
        return if (_selectedRegion.value == "global") {
            _uiState.value.globalLeaderboard
        } else {
            _uiState.value.regionalLeaderboard
        }
    }
    
    fun getTimeFrameDisplayName(timeFrame: LeaderboardTimeFrame): String {
        return when (timeFrame) {
            LeaderboardTimeFrame.TODAY -> "Today"
            LeaderboardTimeFrame.THIS_WEEK -> "This Week"
            LeaderboardTimeFrame.THIS_MONTH -> "This Month"
            LeaderboardTimeFrame.ALL_TIME -> "All Time"
        }
    }
}
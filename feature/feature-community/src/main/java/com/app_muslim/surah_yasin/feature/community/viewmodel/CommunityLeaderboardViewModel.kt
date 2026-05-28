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
 * ViewModel for Community Leaderboard Screen
 * Manages leaderboard data, filtering, and user interactions
 */
@HiltViewModel
class CommunityLeaderboardViewModel @Inject constructor(
    private val communityRepository: CommunityPrayerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CommunityLeaderboardUiState())
    val uiState: StateFlow<CommunityLeaderboardUiState> = _uiState.asStateFlow()
    
    private val _selectedTimeFrame = MutableStateFlow(LeaderboardTimeFrame.THIS_WEEK)
    val selectedTimeFrame: StateFlow<LeaderboardTimeFrame> = _selectedTimeFrame.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow(LeaderboardCategory.TOTAL_PRAYERS)
    val selectedCategory: StateFlow<LeaderboardCategory> = _selectedCategory.asStateFlow()
    
    private val _selectedRegion = MutableStateFlow<String?>(null)
    val selectedRegion: StateFlow<String?> = _selectedRegion.asStateFlow()
    
    init {
        // Observe filter changes and reload leaderboard
        viewModelScope.launch {
            combine(
                selectedTimeFrame,
                selectedCategory,
                selectedRegion
            ) { timeFrame, category, region ->
                Triple(timeFrame, category, region)
            }.distinctUntilChanged()
                .collect { (timeFrame, category, region) ->
                    loadLeaderboard(timeFrame, category, region)
                }
        }
    }
    
    fun refreshLeaderboards() {
        loadLeaderboard(
            _selectedTimeFrame.value,
            _selectedCategory.value,
            _selectedRegion.value
        )
    }
    
    fun updateTimeFrame(timeFrame: LeaderboardTimeFrame) {
        _selectedTimeFrame.value = timeFrame
    }
    
    fun updateCategory(category: LeaderboardCategory) {
        _selectedCategory.value = category
    }
    
    fun updateRegion(region: String?) {
        _selectedRegion.value = region
    }
    
    private fun loadLeaderboard(
        timeFrame: LeaderboardTimeFrame,
        category: LeaderboardCategory,
        region: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            try {
                // Get leaderboard data
                val leaderboard = getLeaderboardData(timeFrame, category, region)
                
                // Get current user position
                val currentUserId = getCurrentUserId()
                val userPosition = if (currentUserId != null) {
                    findUserPosition(leaderboard.entries, currentUserId)
                } else null
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    leaderboard = leaderboard,
                    currentUserPosition = userPosition
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load leaderboard: ${e.message}"
                )
            }
        }
    }
    
    private suspend fun getLeaderboardData(
        timeFrame: LeaderboardTimeFrame,
        category: LeaderboardCategory,
        region: String?
    ): CommunityLeaderboard {
        // For this implementation, we'll create mock data
        // In a real app, this would come from the repository
        val mockEntries = generateMockLeaderboardEntries(category, region)
        
        return CommunityLeaderboard(
            leaderboardId = "leaderboard_${category.categoryId}_${timeFrame.name}_${region ?: "global"}",
            type = LeaderboardType.COMMUNITY,
            category = category,
            timeFrame = timeFrame,
            region = region,
            entries = mockEntries,
            totalEntries = mockEntries.size,
            lastUpdated = java.time.ZonedDateTime.now(),
            isRealTime = false
        )
    }
    
    private fun generateMockLeaderboardEntries(
        category: LeaderboardCategory,
        region: String?
    ): List<LeaderboardEntry> {
        val names = listOf(
            "Ahmad Abdullah", "Fatimah Hassan", "Omar Khalil", "Aisha Rahman",
            "Youssef Mahmoud", "Maryam Al-Zahra", "Ali Qasim", "Khadijah Said",
            "Ibrahim Nasir", "Zaynab Karim", "Hassan Muhammad", "Layla Farid",
            "Khalid Bashir", "Nour El-Din", "Rania Othman", "Tariq Mansour",
            "Samira Youssef", "Amjad Saleh", "Lina Fares", "Saad Najjar"
        )
        
        val regions = listOf("Middle East", "Southeast Asia", "North Africa", "South Asia", "Europe", "North America")
        
        return names.mapIndexed { index, name ->
            val baseScore = when (category.categoryId) {
                "total_prayers" -> 1200L - (index * 50L)
                "memorial_participation" -> 25L - (index * 1L)
                "community_engagement" -> 180L - (index * 8L)
                "helping_families" -> 15L - (index * 1L)
                "consistency" -> 30L - (index * 2L)
                else -> 100L - (index * 5L)
            }
            
            LeaderboardEntry(
                userId = "user_${index + 1}",
                displayName = name,
                profilePictureUrl = null,
                rank = index + 1,
                score = baseScore,
                previousRank = index + 1,
                change = when {
                    index < 3 -> RankChange.UP
                    index < 10 -> RankChange.NO_CHANGE  
                    else -> RankChange.DOWN
                },
                regionCode = region ?: regions.random(),
                regionName = region ?: regions.random(),
                badges = emptyList(),
                statistics = mapOf(
                    "total_prayers" to baseScore,
                    "sessions" to baseScore / 10
                ),
                lastActiveAt = java.time.ZonedDateTime.now().minusDays(kotlin.random.Random.nextLong(1, 30))
            )
        }
    }
    
    private fun findUserPosition(entries: List<LeaderboardEntry>, userId: String): LeaderboardEntry? {
        return entries.find { it.userId == userId }
    }
    
    fun getAvailableRegions(): List<String> {
        return listOf(
            "All Regions",
            "Middle East", 
            "Southeast Asia", 
            "North Africa", 
            "South Asia",
            "Europe",
            "North America",
            "Central Asia",
            "West Africa",
            "East Africa"
        )
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    // Helper methods - would normally get from user service/preferences
    private fun getCurrentUserId(): String? {
        // TODO: Get from auth service
        return "user_8" // Mock current user ID
    }
}

/**
 * UI State for Community Leaderboard Screen
 */
data class CommunityLeaderboardUiState(
    val isLoading: Boolean = false,
    val leaderboard: CommunityLeaderboard? = null,
    val currentUserPosition: LeaderboardEntry? = null,
    val availableRegions: List<String> = emptyList(),
    val error: String? = null
)
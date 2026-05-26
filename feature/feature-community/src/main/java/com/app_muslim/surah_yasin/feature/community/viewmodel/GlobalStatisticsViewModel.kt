package com.app_muslim.surah_yasin.feature.community.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.app_muslim.surah_yasin.feature.community.model.*
import com.app_muslim.surah_yasin.feature.community.repository.CommunityPrayerRepository
import com.app_muslim.surah_yasin.feature.community.ui.components.MilestoneCelebration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Global Prayer Statistics
 * Manages real-time data flow for global analytics, world map, and milestone celebrations
 */
@HiltViewModel
class GlobalStatisticsViewModel @Inject constructor(
    private val communityRepository: CommunityPrayerRepository
) : ViewModel() {
    
    // UI State for global statistics screen
    private val _uiState = MutableStateFlow(GlobalStatisticsUiState())
    val uiState: StateFlow<GlobalStatisticsUiState> = _uiState.asStateFlow()
    
    // Active celebrations
    private val _activeCelebrations = MutableStateFlow<List<MilestoneCelebration>>(emptyList())
    val activeCelebrations: StateFlow<List<MilestoneCelebration>> = _activeCelebrations.asStateFlow()
    
    // Selected timeframe for analytics
    private val _selectedTimeframe = MutableStateFlow(AnalyticsTimeframe.DAILY)
    val selectedTimeframe: StateFlow<AnalyticsTimeframe> = _selectedTimeframe.asStateFlow()
    
    init {
        initializeDataStreams()
        loadGlobalStatistics()
    }
    
    private fun initializeDataStreams() {
        // Combine all real-time data streams
        viewModelScope.launch {
            combine(
                communityRepository.getGlobalPrayerStatsFlow(),
                communityRepository.getCountryPrayerStatsFlow(),
                communityRepository.getGlobalMilestonesFlow(),
                communityRepository.getDailyPrayerAnalyticsFlow(30),
                communityRepository.getWeeklyPrayerAnalyticsFlow(12),
                _selectedTimeframe
            ) { flows ->
                val globalStats = flows[0] as GlobalPrayerStats
                val countryStats = flows[1] as List<CountryPrayerStats>
                val milestones = flows[2] as List<GlobalMilestone>
                val dailyAnalytics = flows[3] as List<DailyPrayerAnalytics>
                val weeklyAnalytics = flows[4] as List<WeeklyPrayerAnalytics>
                val timeframe = flows[5] as AnalyticsTimeframe
                
                GlobalStatisticsUiState(
                    globalStats = globalStats,
                    countryStats = countryStats.sortedByDescending { it.totalPrayers },
                    milestones = milestones,
                    dailyAnalytics = dailyAnalytics.sortedBy { it.date },
                    weeklyAnalytics = weeklyAnalytics.sortedBy { it.weekStartDate },
                    selectedTimeframe = timeframe,
                    isLoading = false,
                    errorMessage = null
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load global statistics: ${e.message}"
                )
            }.collect { newState ->
                _uiState.value = newState
                
                // Check for newly completed milestones to trigger celebrations
                checkForNewMilestones(newState.milestones)
            }
        }
    }
    
    private fun checkForNewMilestones(milestones: List<GlobalMilestone>) {
        val newlyCompleted = milestones.filter { milestone ->
            milestone.isCompleted && milestone.achievedAt != null &&
            // Check if this milestone wasn't already celebrated
            _activeCelebrations.value.none { it.id == milestone.milestoneId }
        }
        
        if (newlyCompleted.isNotEmpty()) {
            triggerMilestoneCelebrations(newlyCompleted)
        }
    }
    
    private fun triggerMilestoneCelebrations(milestones: List<GlobalMilestone>) {
        val celebrations = milestones.map { milestone ->
            MilestoneCelebration(
                id = milestone.milestoneId,
                title = milestone.title,
                description = milestone.description,
                celebrationMessage = milestone.celebrationMessage.ifEmpty { 
                    "🎉 Alhamdulillah! The global Muslim community has achieved ${milestone.title}!" 
                },
                achievedValue = milestone.currentValue,
                participatingCountries = milestone.participatingCountries,
                icon = milestone.icon,
                timestamp = milestone.achievedAt ?: java.time.ZonedDateTime.now()
            )
        }
        
        _activeCelebrations.value = _activeCelebrations.value + celebrations
    }
    
    fun handleEvent(event: GlobalStatisticsEvent) {
        when (event) {
            is GlobalStatisticsEvent.ChangeTimeframe -> {
                changeTimeframe(event.timeframe)
            }
            is GlobalStatisticsEvent.SelectCountry -> {
                selectCountry(event.country)
            }
            is GlobalStatisticsEvent.DismissCelebration -> {
                dismissCelebration(event.celebrationId)
            }
            is GlobalStatisticsEvent.RefreshData -> {
                refreshAllData()
            }
            is GlobalStatisticsEvent.SelectMilestone -> {
                selectMilestone(event.milestone)
            }
            GlobalStatisticsEvent.ClearError -> {
                clearError()
            }
        }
    }
    
    private fun changeTimeframe(timeframe: AnalyticsTimeframe) {
        _selectedTimeframe.value = timeframe
        _uiState.value = _uiState.value.copy(selectedTimeframe = timeframe)
    }
    
    private fun selectCountry(country: CountryPrayerStats?) {
        _uiState.value = _uiState.value.copy(selectedCountry = country)
    }
    
    private fun selectMilestone(milestone: GlobalMilestone) {
        _uiState.value = _uiState.value.copy(selectedMilestone = milestone)
    }
    
    private fun dismissCelebration(celebrationId: String) {
        _activeCelebrations.value = _activeCelebrations.value.filterNot { it.id == celebrationId }
    }
    
    private fun refreshAllData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        // Data will be automatically refreshed through the flow combinations
    }
    
    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    private fun loadGlobalStatistics() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        // The actual loading happens through the flow combinations in initializeDataStreams()
    }
    
    // Computed properties for UI
    fun getTopCountries(limit: Int = 10): List<CountryPrayerStats> {
        return _uiState.value.countryStats.take(limit)
    }
    
    fun getActiveMilestones(): List<GlobalMilestone> {
        return _uiState.value.milestones.filterNot { it.isCompleted }
    }
    
    fun getCompletedMilestones(): List<GlobalMilestone> {
        return _uiState.value.milestones.filter { it.isCompleted }
    }
    
    fun getCurrentAnalytics(): List<DailyPrayerAnalytics> {
        return when (_selectedTimeframe.value) {
            AnalyticsTimeframe.DAILY -> _uiState.value.dailyAnalytics.takeLast(30)
            else -> _uiState.value.dailyAnalytics.takeLast(7)
        }
    }
    
    fun getGlobalTrendData(): GlobalTrendData {
        val dailyData = _uiState.value.dailyAnalytics.takeLast(7)
        val weeklyData = _uiState.value.weeklyAnalytics.takeLast(4)
        
        return GlobalTrendData(
            dailyGrowth = calculateDailyGrowth(dailyData),
            weeklyGrowth = calculateWeeklyGrowth(weeklyData),
            trendDirection = _uiState.value.globalStats.globalTrend,
            peakHours = findPeakHours(dailyData),
            mostActiveRegions = findMostActiveRegions()
        )
    }
    
    private fun calculateDailyGrowth(dailyData: List<DailyPrayerAnalytics>): Float {
        if (dailyData.size < 2) return 0f
        
        val yesterday = dailyData[dailyData.size - 2].totalPrayers
        val today = dailyData.last().totalPrayers
        
        return if (yesterday > 0) {
            ((today - yesterday).toFloat() / yesterday * 100f)
        } else 0f
    }
    
    private fun calculateWeeklyGrowth(weeklyData: List<WeeklyPrayerAnalytics>): Float {
        if (weeklyData.size < 2) return 0f
        
        val lastWeek = weeklyData[weeklyData.size - 2].totalPrayers
        val thisWeek = weeklyData.last().totalPrayers
        
        return if (lastWeek > 0) {
            ((thisWeek - lastWeek).toFloat() / lastWeek * 100f)
        } else 0f
    }
    
    private fun findPeakHours(dailyData: List<DailyPrayerAnalytics>): List<Int> {
        val hourCounts = IntArray(24)
        
        dailyData.forEach { daily ->
            hourCounts[daily.peakHour]++
        }
        
        return hourCounts.withIndex()
            .sortedByDescending { it.value }
            .take(3)
            .map { it.index }
    }
    
    private fun findMostActiveRegions(): List<String> {
        return _uiState.value.countryStats
            .sortedByDescending { it.heatLevel }
            .take(5)
            .map { it.countryName }
    }
}

// UI State for Global Statistics
data class GlobalStatisticsUiState(
    val globalStats: GlobalPrayerStats = GlobalPrayerStats(),
    val countryStats: List<CountryPrayerStats> = emptyList(),
    val milestones: List<GlobalMilestone> = emptyList(),
    val dailyAnalytics: List<DailyPrayerAnalytics> = emptyList(),
    val weeklyAnalytics: List<WeeklyPrayerAnalytics> = emptyList(),
    val selectedCountry: CountryPrayerStats? = null,
    val selectedMilestone: GlobalMilestone? = null,
    val selectedTimeframe: AnalyticsTimeframe = AnalyticsTimeframe.DAILY,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

// Events for Global Statistics
sealed class GlobalStatisticsEvent {
    data class ChangeTimeframe(val timeframe: AnalyticsTimeframe) : GlobalStatisticsEvent()
    data class SelectCountry(val country: CountryPrayerStats?) : GlobalStatisticsEvent()
    data class SelectMilestone(val milestone: GlobalMilestone) : GlobalStatisticsEvent()
    data class DismissCelebration(val celebrationId: String) : GlobalStatisticsEvent()
    object RefreshData : GlobalStatisticsEvent()
    object ClearError : GlobalStatisticsEvent()
}

// Analytics timeframe enum
enum class AnalyticsTimeframe(
    val displayName: String,
    val icon: ImageVector
) {
    DAILY("Daily", Icons.Default.DateRange),
    WEEKLY("Weekly", Icons.Default.CalendarMonth),
    GEOGRAPHIC("Geographic", Icons.Default.Language)
}

// Global trend data
data class GlobalTrendData(
    val dailyGrowth: Float,
    val weeklyGrowth: Float,
    val trendDirection: TrendDirection,
    val peakHours: List<Int>,
    val mostActiveRegions: List<String>
)
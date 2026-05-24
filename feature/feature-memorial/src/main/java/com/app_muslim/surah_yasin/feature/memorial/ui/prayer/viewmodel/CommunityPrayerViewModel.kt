package com.app_muslim.surah_yasin.feature.memorial.ui.prayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.app_muslim.surah_yasin.feature.memorial.repository.PrayerTrackingRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CommunityPrayerViewModel @Inject constructor(
    private val prayerTrackingRepository: PrayerTrackingRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityPrayerUiState())
    val uiState: StateFlow<CommunityPrayerUiState> = _uiState.asStateFlow()

    private val _communityStats = MutableStateFlow<CommunityParticipation?>(null)
    val communityStats: StateFlow<CommunityParticipation?> = _communityStats.asStateFlow()

    private var currentMemorialId: String = ""

    fun loadCommunityData(memorialId: String) {
        currentMemorialId = memorialId
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // Load community participation stats
                val stats = prayerTrackingRepository.getCommunityPrayerParticipation(memorialId)
                _communityStats.value = stats

                // Load upcoming community groups
                val userId = auth.currentUser?.uid ?: ""
                prayerTrackingRepository.getUpcomingCommunityPrayers(userId)
                    .collect { groups ->
                        _uiState.value = _uiState.value.copy(
                            upcomingGroups = groups,
                            isLoading = false
                        )
                    }

                // Load active sessions and recent activity
                loadActiveSessionsAndActivity(memorialId)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load community data"
                )
            }
        }
    }

    private suspend fun loadActiveSessionsAndActivity(memorialId: String) {
        viewModelScope.launch {
            try {
                // Load recent memorial prayer sessions
                prayerTrackingRepository.getMemorialPrayerSessions(memorialId, 20)
                    .collect { sessions ->
                        val activeSessions = sessions.filter { 
                            it.endTime == null && isRecentSession(it.startTime) 
                        }
                        val recentActivity = sessions.filter { it.isCompleted }

                        _uiState.value = _uiState.value.copy(
                            activeSessions = activeSessions,
                            recentActivity = recentActivity
                        )
                    }
            } catch (e: Exception) {
                // Log error but don't update UI state for secondary data
            }
        }
    }

    fun refreshCommunityData() {
        if (currentMemorialId.isNotEmpty()) {
            loadCommunityData(currentMemorialId)
        }
    }

    fun startCommunityPrayerSession() {
        val currentUser = auth.currentUser
        if (currentUser == null || currentMemorialId.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Authentication required")
            return
        }

        viewModelScope.launch {
            try {
                val sessionId = prayerTrackingRepository.startPrayerSession(
                    memorialId = currentMemorialId,
                    prayerType = PrayerType.TAHLIL // Default, user can change later
                )
                
                // Navigate to prayer session screen would happen via navigation callback
                _uiState.value = _uiState.value.copy(
                    activePrayerSessionId = sessionId
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to start prayer session"
                )
            }
        }
    }

    fun joinPrayerGroup(groupId: String) {
        viewModelScope.launch {
            try {
                // Implementation for joining an existing prayer group
                // This would update the group's participant list
                _uiState.value = _uiState.value.copy(
                    joinedGroupId = groupId
                )
                
                // Refresh data to show updated group membership
                refreshCommunityData()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to join prayer group"
                )
            }
        }
    }

    fun createPrayerGroup(groupName: String, prayerType: PrayerType, scheduledTime: Date) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _uiState.value = _uiState.value.copy(error = "Authentication required")
            return
        }

        viewModelScope.launch {
            try {
                val groupId = prayerTrackingRepository.addCommunityPrayerGroup(
                    memorialId = currentMemorialId,
                    groupName = groupName,
                    participants = listOf(currentUser.uid),
                    scheduledTime = scheduledTime,
                    prayerType = prayerType
                )

                _uiState.value = _uiState.value.copy(
                    showCreateGroupDialog = false,
                    createdGroupId = groupId
                )

                // Refresh data to show the new group
                refreshCommunityData()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to create prayer group"
                )
            }
        }
    }

    fun showCreateGroupDialog() {
        _uiState.value = _uiState.value.copy(showCreateGroupDialog = true)
    }

    fun hideCreateGroupDialog() {
        _uiState.value = _uiState.value.copy(showCreateGroupDialog = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun isRecentSession(startTime: Date): Boolean {
        val oneHourAgo = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, -1)
        }.time
        return startTime.after(oneHourAgo)
    }
}

data class CommunityPrayerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeSessions: List<PrayerSession> = emptyList(),
    val upcomingGroups: List<CommunityPrayerGroup> = emptyList(),
    val recentActivity: List<PrayerSession> = emptyList(),
    val showCreateGroupDialog: Boolean = false,
    val activePrayerSessionId: String? = null,
    val joinedGroupId: String? = null,
    val createdGroupId: String? = null
)
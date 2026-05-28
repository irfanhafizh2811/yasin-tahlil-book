package com.app_muslim.surah_yasin.feature.memorial.ui.sharing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.core.firebase.sharing.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Shared Memorial Details Screen
 * Handles shared memorial access, authentication, and permission management
 */
@HiltViewModel
class SharedMemorialDetailsViewModel @Inject constructor(
    private val sharingRepository: MemorialSharingRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SharedMemorialDetailsUiState())
    val uiState: StateFlow<SharedMemorialDetailsUiState> = _uiState.asStateFlow()
    
    fun loadSharedMemorial(memorialId: String, sharerUserId: String?, sharingType: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Check if user is authenticated
                val isAuthenticated = getCurrentUserId() != null
                
                // Check access permissions if authenticated
                val accessPermissions = if (isAuthenticated) {
                    sharingRepository.checkMemorialAccess(memorialId, getCurrentUserId()!!)
                        .getOrNull()
                } else {
                    null
                }
                
                // Load memorial information (would be from repository)
                val memorial = loadMemorialInfo(memorialId, sharerUserId)
                
                if (memorial != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        memorial = memorial,
                        isAuthenticated = isAuthenticated,
                        accessPermissions = accessPermissions
                    )
                    
                    // Track sharing link click
                    sharingRepository.trackSharingEvent(
                        memorialId = memorialId,
                        platform = SharingPlatform.GENERIC,
                        action = "shared_link_viewed"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Memorial not found or access denied"
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load memorial: ${e.message}"
                )
            }
        }
    }
    
    fun requestAccess(memorialId: String) {
        viewModelScope.launch {
            val userId = getCurrentUserId()
            
            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    message = "Please sign in to request access"
                )
                return@launch
            }
            
            try {
                // In a real implementation, this would send a request to the memorial owner
                // For now, we'll grant basic access
                val result = sharingRepository.grantMemorialAccess(
                    memorialId = memorialId,
                    userId = userId,
                    permissions = MemorialSharingPermissions.PRAY_AND_VIEW
                )
                
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            accessPermissions = MemorialSharingPermissions.PRAY_AND_VIEW,
                            message = "Access granted! You can now join the prayers."
                        )
                        
                        // Track access granted event
                        sharingRepository.trackSharingEvent(
                            memorialId = memorialId,
                            platform = SharingPlatform.GENERIC,
                            action = "access_requested"
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            message = "Failed to request access: ${exception.message}"
                        )
                    }
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Error requesting access: ${e.message}"
                )
            }
        }
    }
    
    fun joinPrayerSession(memorialId: String) {
        viewModelScope.launch {
            // Track prayer participation
            sharingRepository.trackSharingEvent(
                memorialId = memorialId,
                platform = SharingPlatform.GENERIC,
                action = "prayer_session_joined"
            )
        }
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    private fun getCurrentUserId(): String? {
        // In a real implementation, this would get the current authenticated user ID
        return null // Placeholder
    }
    
    private suspend fun loadMemorialInfo(memorialId: String, sharerUserId: String?): SharedMemorialInfo? {
        // In a real implementation, this would fetch memorial data from repository
        // For now, return mock data
        return SharedMemorialInfo(
            memorialId = memorialId,
            deceasedName = "Beloved Family Member",
            sharerName = "Family Member",
            memorialMessage = "A loving soul who touched many hearts. May Allah grant them Jannatul Firdaus.",
            prayerTypes = listOf("Tahlil", "Yasin", "Fatihah"),
            photoUrl = null,
            deathDate = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000), // 30 days ago
            privacyLevel = "Family",
            familyMemberCount = 12,
            totalPrayers = 156L
        )
    }
}

/**
 * UI State for Shared Memorial Details Screen
 */
data class SharedMemorialDetailsUiState(
    val isLoading: Boolean = false,
    val memorial: SharedMemorialInfo? = null,
    val isAuthenticated: Boolean = false,
    val accessPermissions: MemorialSharingPermissions? = null,
    val error: String? = null,
    val message: String? = null
)
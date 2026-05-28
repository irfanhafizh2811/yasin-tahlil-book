package com.app_muslim.surah_yasin.feature.memorial.ui.sharing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.core.firebase.sharing.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Family Invitation Screen
 * Manages family member invitations and memorial access
 */
@HiltViewModel
class FamilyInvitationViewModel @Inject constructor(
    private val sharingRepository: MemorialSharingRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FamilyInvitationUiState())
    val uiState: StateFlow<FamilyInvitationUiState> = _uiState.asStateFlow()
    
    private var currentMemorialId: String? = null
    
    fun loadMemorialInvitations(memorialId: String) {
        currentMemorialId = memorialId
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Load pending invitations
                sharingRepository.getMemorialInvitationsFlow(memorialId)
                    .collect { invitations ->
                        val pendingInvitations = invitations.filter { 
                            it.status == InvitationStatus.PENDING 
                        }
                        
                        _uiState.value = _uiState.value.copy(
                            pendingInvitations = pendingInvitations,
                            isLoading = false
                        )
                    }
                
                // Load family members with access
                sharingRepository.getMemorialAccessListFlow(memorialId)
                    .collect { accessList ->
                        _uiState.value = _uiState.value.copy(
                            familyMembersWithAccess = accessList
                        )
                    }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load invitations: ${e.message}"
                )
            }
        }
    }
    
    fun sendFamilyInvitation(
        memorialId: String,
        inviteeEmail: String,
        inviteePhone: String?,
        personalMessage: String,
        permissions: MemorialSharingPermissions
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            // Get current user name (would normally come from user profile)
            val inviterName = getCurrentUserName()
            
            val result = sharingRepository.sendFamilyInvitation(
                memorialId = memorialId,
                inviterName = inviterName,
                inviteeEmail = inviteeEmail,
                inviteePhone = inviteePhone,
                personalMessage = personalMessage,
                permissions = permissions
            )
            
            result.fold(
                onSuccess = { invitationId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Invitation sent successfully to $inviteeEmail"
                    )
                    
                    // Track analytics
                    sharingRepository.trackSharingEvent(
                        memorialId = memorialId,
                        platform = SharingPlatform.EMAIL,
                        action = "family_invitation_sent"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to send invitation: ${exception.message}"
                    )
                }
            )
        }
    }
    
    fun resendInvitation(invitationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Get invitation details
                val invitation = _uiState.value.pendingInvitations.find { it.id == invitationId }
                
                if (invitation != null && currentMemorialId != null) {
                    val result = sharingRepository.sendFamilyInvitation(
                        memorialId = currentMemorialId!!,
                        inviterName = getCurrentUserName(),
                        inviteeEmail = invitation.inviteeEmail,
                        inviteePhone = null,
                        personalMessage = invitation.personalMessage,
                        permissions = invitation.permissions
                    )
                    
                    result.fold(
                        onSuccess = {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                successMessage = "Invitation resent to ${invitation.inviteeEmail}"
                            )
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "Failed to resend invitation: ${exception.message}"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to resend invitation: ${e.message}"
                )
            }
        }
    }
    
    fun cancelInvitation(invitationId: String) {
        viewModelScope.launch {
            // In a real implementation, we would update the invitation status to CANCELLED
            _uiState.value = _uiState.value.copy(
                pendingInvitations = _uiState.value.pendingInvitations.filter { 
                    it.id != invitationId 
                },
                successMessage = "Invitation cancelled"
            )
        }
    }
    
    fun revokeAccess(memorialId: String, userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = sharingRepository.revokeMemorialAccess(memorialId, userId)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Access revoked successfully"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to revoke access: ${exception.message}"
                    )
                }
            )
        }
    }
    
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
    
    private fun getCurrentUserName(): String {
        // In a real implementation, this would fetch from user profile
        return "Family Member"
    }
}

/**
 * UI State for Family Invitation Screen
 */
data class FamilyInvitationUiState(
    val isLoading: Boolean = false,
    val pendingInvitations: List<MemorialInvitation> = emptyList(),
    val familyMembersWithAccess: List<MemorialAccess> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)
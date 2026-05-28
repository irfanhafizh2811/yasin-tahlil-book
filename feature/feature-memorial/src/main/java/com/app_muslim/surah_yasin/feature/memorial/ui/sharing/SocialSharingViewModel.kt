package com.app_muslim.surah_yasin.feature.memorial.ui.sharing

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.core.firebase.sharing.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Social Media Sharing Screen
 * Manages social media sharing, link generation, and analytics
 */
@HiltViewModel
class SocialSharingViewModel @Inject constructor(
    private val sharingRepository: MemorialSharingRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SocialSharingUiState())
    val uiState: StateFlow<SocialSharingUiState> = _uiState.asStateFlow()
    
    private var currentSharingData: MemorialSharingData? = null
    
    fun loadSharingData(memorialId: String, memorialName: String, deceasedName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Load sharing analytics
                sharingRepository.getSharingAnalyticsFlow(memorialId)
                    .collect { analytics ->
                        _uiState.value = _uiState.value.copy(
                            analytics = analytics,
                            isLoading = false
                        )
                    }
                
                // Prepare sharing data (without link initially)
                currentSharingData = MemorialSharingData(
                    memorialId = memorialId,
                    deceasedName = deceasedName,
                    sharerName = getCurrentUserName(),
                    prayerType = "memorial prayers",
                    sharingLink = "",
                    memorialPhotoUrl = null
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Failed to load sharing data: ${e.message}"
                )
            }
        }
    }
    
    fun shareToSocialMedia(context: Context, platform: SharingPlatform) {
        viewModelScope.launch {
            val sharingData = currentSharingData ?: return@launch
            
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Generate sharing link if not available
                val linkResult = if (sharingData.sharingLink.isEmpty()) {
                    sharingRepository.generateSharingLink(
                        memorialId = sharingData.memorialId,
                        sharingType = SharingType.FAMILY_PRIVATE
                    )
                } else {
                    Result.success(sharingData.sharingLink)
                }
                
                linkResult.fold(
                    onSuccess = { link ->
                        val updatedSharingData = sharingData.copy(
                            sharingLink = link,
                            prayerType = getPrayerTypeForSharing(platform)
                        )
                        
                        // Apply custom message if available
                        val finalSharingData = if (_uiState.value.customMessage.isNotBlank()) {
                            updatedSharingData
                        } else {
                            updatedSharingData
                        }
                        
                        // Create and launch sharing intent
                        val intent = sharingRepository.createSocialSharingIntent(
                            context = context,
                            memorialData = finalSharingData,
                            platform = platform
                        )
                        
                        try {
                            context.startActivity(intent)
                            
                            // Track sharing event
                            sharingRepository.trackSharingEvent(
                                memorialId = sharingData.memorialId,
                                platform = platform,
                                action = "social_media_share"
                            )
                            
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                message = "Sharing to ${platform.name.lowercase()}"
                            )
                            
                        } catch (e: Exception) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                message = "No ${platform.name.lowercase()} app found. Please install it first."
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            message = "Failed to generate sharing link: ${exception.message}"
                        )
                    }
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Failed to share: ${e.message}"
                )
            }
        }
    }
    
    fun generateSharingLink(sharingType: SharingType) {
        viewModelScope.launch {
            val sharingData = currentSharingData ?: return@launch
            
            _uiState.value = _uiState.value.copy(isGeneratingLink = true)
            
            val result = sharingRepository.generateSharingLink(
                memorialId = sharingData.memorialId,
                sharingType = sharingType,
                expirationDays = getSharingLinkExpiration(sharingType)
            )
            
            result.fold(
                onSuccess = { link ->
                    _uiState.value = _uiState.value.copy(
                        isGeneratingLink = false,
                        sharingLink = link,
                        message = "Sharing link generated successfully"
                    )
                    
                    // Update current sharing data
                    currentSharingData = sharingData.copy(sharingLink = link)
                    
                    // Track link generation
                    sharingRepository.trackSharingEvent(
                        memorialId = sharingData.memorialId,
                        platform = SharingPlatform.GENERIC,
                        action = "link_generated"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isGeneratingLink = false,
                        message = "Failed to generate link: ${exception.message}"
                    )
                }
            )
        }
    }
    
    fun copyLinkToClipboard(context: Context, link: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Memorial Sharing Link", link)
            clipboard.setPrimaryClip(clip)
            
            _uiState.value = _uiState.value.copy(
                message = "Link copied to clipboard"
            )
            
            // Track copy event
            val sharingData = currentSharingData
            if (sharingData != null) {
                viewModelScope.launch {
                    sharingRepository.trackSharingEvent(
                        memorialId = sharingData.memorialId,
                        platform = SharingPlatform.GENERIC,
                        action = "link_copied"
                    )
                }
            }
            
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                message = "Failed to copy link"
            )
        }
    }
    
    fun updateCustomMessage(message: String) {
        _uiState.value = _uiState.value.copy(
            customMessage = message
        )
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
    
    private fun getCurrentUserName(): String {
        // In a real implementation, this would fetch from user profile
        return "Family Member"
    }
    
    private fun getPrayerTypeForSharing(platform: SharingPlatform): String {
        return when (platform) {
            SharingPlatform.WHATSAPP, SharingPlatform.TELEGRAM -> "Tahlil and Yasin prayers"
            SharingPlatform.EMAIL -> "memorial prayers including Tahlil, Yasin, and Fatihah"
            SharingPlatform.SMS -> "memorial prayers"
            SharingPlatform.FACEBOOK -> "memorial prayers and du'a"
            SharingPlatform.GENERIC -> "Islamic memorial prayers"
        }
    }
    
    private fun getSharingLinkExpiration(sharingType: SharingType): Int {
        return when (sharingType) {
            SharingType.FAMILY_PRIVATE -> 30  // 30 days for family
            SharingType.CLOSE_FRIENDS -> 14   // 14 days for friends
            SharingType.COMMUNITY_OPEN -> 7   // 7 days for community
            SharingType.PUBLIC_MEMORIAL -> 3  // 3 days for public
        }
    }
}

/**
 * UI State for Social Media Sharing Screen
 */
data class SocialSharingUiState(
    val isLoading: Boolean = false,
    val isGeneratingLink: Boolean = false,
    val sharingLink: String? = null,
    val customMessage: String = "",
    val analytics: MemorialSharingAnalytics? = null,
    val message: String? = null
)
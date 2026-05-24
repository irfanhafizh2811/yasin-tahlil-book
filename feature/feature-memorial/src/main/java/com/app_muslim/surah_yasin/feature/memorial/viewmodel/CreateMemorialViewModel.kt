package com.app_muslim.surah_yasin.feature.memorial.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.app_muslim.surah_yasin.feature.memorial.repository.MemorialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateMemorialViewModel @Inject constructor(
    private val memorialRepository: MemorialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateMemorialUiState())
    val uiState: StateFlow<CreateMemorialUiState> = _uiState.asStateFlow()

    private val _validationState = MutableStateFlow(MemorialValidationResult(false, emptyList()))
    val validationState: StateFlow<MemorialValidationResult> = _validationState.asStateFlow()

    init {
        // Initialize with current date
        val currentDate = Date()
        _uiState.update { currentState ->
            currentState.copy(
                dateOfDeath = currentDate,
                hijriDate = HijriCalendarConverter.gregorianToHijri(currentDate)
            )
        }
        
        // Observe state changes and validate
        viewModelScope.launch {
            _uiState.collect { state ->
                validateMemorial(state)
            }
        }
    }

    fun updateDeceasedName(name: String) {
        _uiState.update { it.copy(deceasedName = name) }
    }

    fun updateDeceasedNameArabic(name: String) {
        _uiState.update { it.copy(deceasedNameArabic = name) }
    }

    fun updateMemorialMessage(message: String) {
        _uiState.update { it.copy(memorialMessage = message) }
    }

    fun updateMemorialMessageArabic(message: String) {
        _uiState.update { it.copy(memorialMessageArabic = message) }
    }

    fun updateDateOfDeath(date: Date) {
        val hijriDate = HijriCalendarConverter.gregorianToHijri(date)
        _uiState.update { 
            it.copy(
                dateOfDeath = date,
                hijriDate = hijriDate
            )
        }
    }

    fun updateHijriDate(hijriDate: HijriDate) {
        val gregorianDate = HijriCalendarConverter.hijriToGregorian(hijriDate)
        _uiState.update { 
            it.copy(
                dateOfDeath = gregorianDate,
                hijriDate = hijriDate
            )
        }
    }

    fun updatePrayerType(prayerType: PrayerType) {
        _uiState.update { it.copy(prayerType = prayerType) }
    }

    fun updatePrivacyLevel(privacy: PrivacyLevel) {
        _uiState.update { it.copy(privacyLevel = privacy) }
    }

    fun uploadPhoto(photoUri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true, error = null) }
            
            try {
                val photoUrl = memorialRepository.uploadMemorialPhoto(photoUri)
                _uiState.update { 
                    it.copy(
                        photoUrl = photoUrl,
                        isUploadingPhoto = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isUploadingPhoto = false,
                        error = "Failed to upload photo: ${e.message}"
                    )
                }
            }
        }
    }

    fun removePhoto() {
        val currentPhotoUrl = _uiState.value.photoUrl
        _uiState.update { it.copy(photoUrl = null) }
        
        // Delete from storage in background
        if (currentPhotoUrl != null) {
            viewModelScope.launch {
                try {
                    memorialRepository.deleteMemorialPhoto(currentPhotoUrl)
                } catch (e: Exception) {
                    // Log error but don't show to user since photo is already removed from UI
                }
            }
        }
    }

    fun createMemorial() {
        val currentState = _uiState.value
        
        if (!_validationState.value.isValid) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val memorial = createMemorialFromState(currentState)
                val memorialId = memorialRepository.createMemorial(memorial)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isMemorialCreated = true,
                        createdMemorialId = memorialId
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to create memorial: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun validateMemorial(state: CreateMemorialUiState) {
        val memorial = createMemorialFromState(state)
        val validationResult = MemorialValidator.validateMemorial(memorial)
        _validationState.value = validationResult
    }

    private fun createMemorialFromState(state: CreateMemorialUiState): MemorialData {
        val now = Date()
        val expirationDate = Calendar.getInstance().apply {
            time = now
            add(Calendar.DAY_OF_MONTH, 40) // 40 days Islamic tradition
        }.time

        return MemorialData(
            id = "", // Will be generated by repository
            creatorId = "", // Will be set by repository from auth
            creatorName = "", // Will be set by repository from auth
            deceasedName = state.deceasedName,
            deceasedNameArabic = state.deceasedNameArabic.takeIf { it.isNotBlank() },
            memorialMessage = state.memorialMessage,
            memorialMessageArabic = state.memorialMessageArabic.takeIf { it.isNotBlank() },
            dateOfDeath = state.dateOfDeath,
            dateOfDeathHijri = state.hijriDate,
            photoUrl = state.photoUrl,
            privacyLevel = state.privacyLevel,
            prayerType = state.prayerType,
            createdAt = now,
            expiresAt = expirationDate,
            isActive = true,
            prayerCount = 0,
            participantCount = 0,
            familyMembers = emptyList(),
            tags = emptyList(),
            region = "", // Will be set by repository from user profile
            schoolOfThought = "" // Will be set by repository from user profile
        )
    }
}

data class CreateMemorialUiState(
    val deceasedName: String = "",
    val deceasedNameArabic: String = "",
    val memorialMessage: String = "",
    val memorialMessageArabic: String = "",
    val dateOfDeath: Date = Date(),
    val hijriDate: HijriDate = HijriDate(),
    val prayerType: PrayerType = PrayerType.TAHLIL,
    val privacyLevel: PrivacyLevel = PrivacyLevel.PRIVATE,
    val photoUrl: String? = null,
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val isMemorialCreated: Boolean = false,
    val createdMemorialId: String = "",
    val error: String? = null
)
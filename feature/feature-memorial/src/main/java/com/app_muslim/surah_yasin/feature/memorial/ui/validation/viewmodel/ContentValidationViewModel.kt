package com.app_muslim.surah_yasin.feature.memorial.ui.validation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.feature.memorial.model.*
import com.app_muslim.surah_yasin.feature.memorial.repository.IslamicContentValidationRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ContentValidationViewModel @Inject constructor(
    private val validationRepository: IslamicContentValidationRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContentValidationUiState())
    val uiState: StateFlow<ContentValidationUiState> = _uiState.asStateFlow()

    private val _currentValidation = MutableStateFlow<IslamicContentValidation?>(null)
    val currentValidation: StateFlow<IslamicContentValidation?> = _currentValidation.asStateFlow()

    private val _scholarReview = MutableStateFlow<IslamicScholarReview?>(null)
    val scholarReview: StateFlow<IslamicScholarReview?> = _scholarReview.asStateFlow()

    fun loadValidation(validationId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val validation = validationRepository.getValidationResult(validationId)
                _currentValidation.value = validation
                _uiState.value = _uiState.value.copy(isLoading = false)
                
                if (validation == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Validation not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load validation"
                )
            }
        }
    }

    fun refreshValidation(validationId: String) {
        loadValidation(validationId)
    }

    fun requestScholarReview(validationId: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _uiState.value = _uiState.value.copy(error = "Authentication required")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isProcessing = true)
                
                // For now, we'll use a default scholar ID
                // In a real implementation, this would be selected by the user or assigned automatically
                val scholarId = "default_scholar"
                
                val reviewId = validationRepository.requestScholarReview(
                    validationId = validationId,
                    scholarId = scholarId,
                    urgencyLevel = UrgencyLevel.NORMAL
                )
                
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    scholarReviewRequested = true
                )
                
                // Reload validation to get updated status
                refreshValidation(validationId)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = e.message ?: "Failed to request scholar review"
                )
            }
        }
    }

    fun applySuggestions(validationId: String, suggestions: List<ContentSuggestion>) {
        val currentValidation = _currentValidation.value
        if (currentValidation == null) {
            _uiState.value = _uiState.value.copy(error = "No validation loaded")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isProcessing = true)
                
                // Apply suggestions to create corrected content
                var correctedContent = currentValidation.contentText
                suggestions.forEach { suggestion ->
                    correctedContent = correctedContent.replace(
                        suggestion.originalText,
                        suggestion.suggestedText
                    )
                }
                
                // Update validation with corrected content
                validationRepository.updateValidationStatus(
                    validationId = validationId,
                    status = ValidationStatus.UNDER_REVIEW,
                    approvedContent = correctedContent
                )
                
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    suggestionsApplied = true
                )
                
                // Reload validation
                refreshValidation(validationId)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = e.message ?: "Failed to apply suggestions"
                )
            }
        }
    }

    fun submitMemorialForValidation(
        title: String,
        description: String,
        region: IslamicRegion = IslamicRegion.GLOBAL,
        schoolOfThought: SchoolOfThought = SchoolOfThought.SUNNI
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isProcessing = true)
                
                val result = validationRepository.validateMemorialContent(
                    title = title,
                    description = description,
                    region = region,
                    schoolOfThought = schoolOfThought
                )
                
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    validationResult = result
                )
                
                if (!result.isApproved && result.humanReviewRequired) {
                    // Automatically create validation entry for human review
                    val request = IslamicContentValidationRequest(
                        contentText = "$title\n$description",
                        contentType = ContentType.MEMORIAL_DESCRIPTION,
                        userRegion = region,
                        userSchoolOfThought = schoolOfThought,
                        urgencyLevel = UrgencyLevel.NORMAL,
                        requestedValidationType = ValidationType.COMPREHENSIVE,
                        submittedBy = auth.currentUser?.uid ?: "",
                        submittedAt = Date()
                    )
                    
                    val validationId = validationRepository.submitContentForValidation(request)
                    loadValidation(validationId)
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = e.message ?: "Failed to submit for validation"
                )
            }
        }
    }

    fun validatePrayerContent(
        prayerText: String,
        prayerType: PrayerType,
        language: String = "en"
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isProcessing = true)
                
                val validation = validationRepository.validatePrayerContent(
                    prayerText = prayerText,
                    prayerType = prayerType,
                    language = language
                )
                
                _currentValidation.value = validation
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    prayerValidationCompleted = true
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = e.message ?: "Failed to validate prayer content"
                )
            }
        }
    }

    fun loadValidationHistory() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _uiState.value = _uiState.value.copy(error = "Authentication required")
            return
        }

        viewModelScope.launch {
            validationRepository.getValidationHistory(currentUser.uid)
                .collect { history ->
                    _uiState.value = _uiState.value.copy(validationHistory = history)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearProcessingFlags() {
        _uiState.value = _uiState.value.copy(
            scholarReviewRequested = false,
            suggestionsApplied = false,
            prayerValidationCompleted = false
        )
    }

    fun retryValidation(validationId: String) {
        loadValidation(validationId)
    }
}

data class ContentValidationUiState(
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val error: String? = null,
    val validationResult: ContentModerationResult? = null,
    val validationHistory: List<IslamicContentValidation> = emptyList(),
    val scholarReviewRequested: Boolean = false,
    val suggestionsApplied: Boolean = false,
    val prayerValidationCompleted: Boolean = false
)
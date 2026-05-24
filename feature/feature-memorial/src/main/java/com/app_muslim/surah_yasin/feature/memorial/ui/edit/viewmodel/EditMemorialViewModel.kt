package com.app_muslim.surah_yasin.feature.memorial.ui.edit.viewmodel

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
class EditMemorialViewModel @Inject constructor(
    private val memorialRepository: MemorialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditMemorialUiState())
    val uiState: StateFlow<EditMemorialUiState> = _uiState.asStateFlow()

    private val _validationState = MutableStateFlow(EditMemorialValidationState())
    val validationState: StateFlow<EditMemorialValidationState> = _validationState.asStateFlow()

    private var originalMemorial: MemorialData? = null

    fun loadMemorial(memorialId: String) {
        if (_uiState.value.isInitialLoading) return // Prevent multiple loads

        _uiState.value = _uiState.value.copy(
            isInitialLoading = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val memorial = memorialRepository.getMemorial(memorialId)
                if (memorial != null) {
                    originalMemorial = memorial
                    _uiState.value = _uiState.value.copy(
                        memorial = memorial,
                        isInitialLoading = false,
                        error = null
                    )
                    validateAllFields()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isInitialLoading = false,
                        error = "Memorial not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isInitialLoading = false,
                    error = e.message ?: "Failed to load memorial"
                )
            }
        }
    }

    fun updateField(fieldName: String, value: Any) {
        val currentMemorial = _uiState.value.memorial ?: return
        
        val updatedMemorial = when (fieldName) {
            "deceasedName" -> currentMemorial.copy(deceasedName = value as String)
            "deceasedNameArabic" -> currentMemorial.copy(deceasedNameArabic = value as? String)
            "dateOfDeath" -> currentMemorial.copy(dateOfDeath = value as Date)
            "dateOfDeathHijri" -> currentMemorial.copy(dateOfDeathHijri = value as HijriDate)
            "memorialMessage" -> currentMemorial.copy(memorialMessage = value as String)
            "memorialMessageArabic" -> currentMemorial.copy(memorialMessageArabic = value as? String)
            "prayerType" -> currentMemorial.copy(prayerType = value as PrayerType)
            "privacyLevel" -> currentMemorial.copy(privacyLevel = value as PrivacyLevel)
            "photoUrl" -> currentMemorial.copy(photoUrl = value as? String)
            else -> currentMemorial
        }

        _uiState.value = _uiState.value.copy(memorial = updatedMemorial)
        validateField(fieldName, value)
    }

    fun updateMemorial() {
        val memorial = _uiState.value.memorial ?: return
        
        if (!_validationState.value.isValid) {
            validateAllFields() // Trigger validation to show all errors
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                memorialRepository.updateMemorial(memorial)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isUpdateComplete = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update memorial"
                )
            }
        }
    }

    private fun validateField(fieldName: String, value: Any) {
        val currentValidation = _validationState.value

        val updatedValidation = when (fieldName) {
            "deceasedName" -> {
                val name = value as String
                val error = when {
                    name.isBlank() -> "Deceased name is required"
                    name.length < 2 -> "Name must be at least 2 characters"
                    name.length > 100 -> "Name must not exceed 100 characters"
                    else -> null
                }
                currentValidation.copy(deceasedNameError = error)
            }
            
            "deceasedNameArabic" -> {
                val nameArabic = value as? String
                val error = if (nameArabic != null && nameArabic.length > 100) {
                    "Arabic name must not exceed 100 characters"
                } else null
                currentValidation.copy(deceasedNameArabicError = error)
            }
            
            "dateOfDeath" -> {
                val date = value as Date
                val error = when {
                    date.after(Date()) -> "Date of death cannot be in the future"
                    date.before(Date(0)) -> "Invalid date"
                    else -> null
                }
                currentValidation.copy(dateError = error)
            }
            
            "memorialMessage" -> {
                val message = value as String
                val error = when {
                    message.length > 1000 -> "Memorial message must not exceed 1000 characters"
                    else -> null
                }
                currentValidation.copy(memorialMessageError = error)
            }
            
            "memorialMessageArabic" -> {
                val messageArabic = value as? String
                val error = if (messageArabic != null && messageArabic.length > 1000) {
                    "Arabic memorial message must not exceed 1000 characters"
                } else null
                currentValidation.copy(memorialMessageArabicError = error)
            }
            
            "privacyLevel" -> {
                // Privacy level validation (always valid for enum)
                currentValidation.copy(privacyLevelError = null)
            }
            
            else -> currentValidation
        }

        _validationState.value = updatedValidation.copy(
            isValid = isFormValid(updatedValidation)
        )
    }

    private fun validateAllFields() {
        val memorial = _uiState.value.memorial ?: return

        val validation = EditMemorialValidationState(
            deceasedNameError = validateDeceasedName(memorial.deceasedName),
            deceasedNameArabicError = validateDeceasedNameArabic(memorial.deceasedNameArabic),
            dateError = validateDateOfDeath(memorial.dateOfDeath),
            memorialMessageError = validateMemorialMessage(memorial.memorialMessage),
            memorialMessageArabicError = validateMemorialMessageArabic(memorial.memorialMessageArabic),
            privacyLevelError = null // Privacy level is always valid for enum
        )

        _validationState.value = validation.copy(
            isValid = isFormValid(validation)
        )
    }

    private fun validateDeceasedName(name: String): String? {
        return when {
            name.isBlank() -> "Deceased name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            name.length > 100 -> "Name must not exceed 100 characters"
            else -> null
        }
    }

    private fun validateDeceasedNameArabic(nameArabic: String?): String? {
        return if (nameArabic != null && nameArabic.length > 100) {
            "Arabic name must not exceed 100 characters"
        } else null
    }

    private fun validateDateOfDeath(date: Date): String? {
        return when {
            date.after(Date()) -> "Date of death cannot be in the future"
            date.before(Date(0)) -> "Invalid date"
            else -> null
        }
    }

    private fun validateMemorialMessage(message: String): String? {
        return if (message.length > 1000) {
            "Memorial message must not exceed 1000 characters"
        } else null
    }

    private fun validateMemorialMessageArabic(messageArabic: String?): String? {
        return if (messageArabic != null && messageArabic.length > 1000) {
            "Arabic memorial message must not exceed 1000 characters"
        } else null
    }

    private fun isFormValid(validation: EditMemorialValidationState): Boolean {
        return validation.deceasedNameError == null &&
                validation.deceasedNameArabicError == null &&
                validation.dateError == null &&
                validation.memorialMessageError == null &&
                validation.memorialMessageArabicError == null &&
                validation.privacyLevelError == null
    }

    fun hasChanges(): Boolean {
        val current = _uiState.value.memorial
        val original = originalMemorial
        return current != original
    }

    fun resetChanges() {
        originalMemorial?.let { memorial ->
            _uiState.value = _uiState.value.copy(memorial = memorial)
            validateAllFields()
        }
    }
}

data class EditMemorialUiState(
    val memorial: MemorialData? = null,
    val isInitialLoading: Boolean = false,
    val isLoading: Boolean = false,
    val isUpdateComplete: Boolean = false,
    val error: String? = null
)

data class EditMemorialValidationState(
    val deceasedNameError: String? = null,
    val deceasedNameArabicError: String? = null,
    val dateError: String? = null,
    val memorialMessageError: String? = null,
    val memorialMessageArabicError: String? = null,
    val privacyLevelError: String? = null,
    val isValid: Boolean = false
)
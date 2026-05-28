package com.app_muslim.surah_yasin.feature.memorial.ui.sharing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.core.firebase.sharing.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Privacy Controls Screen
 * Manages memorial sharing privacy settings and access controls
 */
@HiltViewModel
class PrivacyControlsViewModel @Inject constructor(
    private val sharingRepository: MemorialSharingRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PrivacyControlsUiState())
    val uiState: StateFlow<PrivacyControlsUiState> = _uiState.asStateFlow()
    
    fun loadSharingSettings(memorialId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = sharingRepository.getSharingSettings(memorialId)
                
                result.fold(
                    onSuccess = { settings ->
                        _uiState.value = _uiState.value.copy(
                            settings = settings,
                            originalSettings = settings,
                            isLoading = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            message = "Failed to load settings: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Error loading settings: ${e.message}"
                )
            }
        }
    }
    
    fun updateSettings(newSettings: MemorialSharingSettings) {
        _uiState.value = _uiState.value.copy(
            settings = newSettings,
            hasUnsavedChanges = newSettings != _uiState.value.originalSettings
        )
    }
    
    fun saveSharingSettings(memorialId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            try {
                val result = sharingRepository.updateSharingSettings(
                    memorialId = memorialId,
                    settings = _uiState.value.settings
                )
                
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            originalSettings = _uiState.value.settings,
                            hasUnsavedChanges = false,
                            message = "Settings saved successfully"
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            message = "Failed to save settings: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = "Error saving settings: ${e.message}"
                )
            }
        }
    }
    
    fun resetToDefaults() {
        val defaultSettings = MemorialSharingSettings()
        _uiState.value = _uiState.value.copy(
            settings = defaultSettings,
            hasUnsavedChanges = defaultSettings != _uiState.value.originalSettings
        )
    }
    
    fun discardChanges() {
        _uiState.value = _uiState.value.copy(
            settings = _uiState.value.originalSettings,
            hasUnsavedChanges = false
        )
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

/**
 * UI State for Privacy Controls Screen
 */
data class PrivacyControlsUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val settings: MemorialSharingSettings = MemorialSharingSettings(),
    val originalSettings: MemorialSharingSettings = MemorialSharingSettings(),
    val hasUnsavedChanges: Boolean = false,
    val message: String? = null
)
package com.app_muslim.surah_yasin.core.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.core.firebase.SessionManager
import com.app_muslim.surah_yasin.core.firebase.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NavigationUiState(
    val sessionState: SessionState = SessionState(),
    val showGuestModeDialog: Boolean = false,
    val showEmailVerificationDialog: Boolean = false,
    val showSignOutDialog: Boolean = false,
    val isSigningOut: Boolean = false,
    val signOutError: String? = null,
    val pendingRoute: String? = null
)

@HiltViewModel
class NavigationManagerViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    val navigationManager: NavigationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NavigationUiState())
    val uiState: StateFlow<NavigationUiState> = _uiState.asStateFlow()

    init {
        // Observe session state changes
        viewModelScope.launch {
            sessionManager.sessionState.collect { sessionState ->
                _uiState.value = _uiState.value.copy(
                    sessionState = sessionState
                )
            }
        }
    }

    fun checkRouteAccess(route: String): NavigationAccessResult {
        val sessionState = _uiState.value.sessionState
        return navigationManager.canAccessRoute(route, sessionState)
    }

    fun handleGuestModeRestriction(route: String) {
        _uiState.value = _uiState.value.copy(
            showGuestModeDialog = true,
            pendingRoute = route
        )
    }

    fun handleEmailVerificationRestriction(route: String) {
        _uiState.value = _uiState.value.copy(
            showEmailVerificationDialog = true,
            pendingRoute = route
        )
    }

    fun dismissGuestModeDialog() {
        _uiState.value = _uiState.value.copy(
            showGuestModeDialog = false,
            pendingRoute = null
        )
    }

    fun dismissEmailVerificationDialog() {
        _uiState.value = _uiState.value.copy(
            showEmailVerificationDialog = false,
            pendingRoute = null
        )
    }

    fun sendEmailVerification() {
        viewModelScope.launch {
            try {
                val result = sessionManager.sendEmailVerification()
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        showEmailVerificationDialog = false
                    )
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun upgradeGuestAccount(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = sessionManager.upgradeGuestAccount(email, password)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        showGuestModeDialog = false
                    )
                    // Navigate to pending route if available
                    val pendingRoute = _uiState.value.pendingRoute
                    if (pendingRoute != null) {
                        // Navigation would be handled by the composable
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun showSignOutDialog() {
        _uiState.value = _uiState.value.copy(
            showSignOutDialog = true
        )
    }

    fun dismissSignOutDialog() {
        _uiState.value = _uiState.value.copy(
            showSignOutDialog = false
        )
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSigningOut = true,
                signOutError = null
            )
            
            try {
                val result = sessionManager.signOut()
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isSigningOut = false,
                        showSignOutDialog = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSigningOut = false,
                        signOutError = "Failed to sign out. Please try again."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSigningOut = false,
                    signOutError = e.message ?: "An error occurred during sign out"
                )
            }
        }
    }

    fun signInAsGuest() {
        viewModelScope.launch {
            try {
                val result = sessionManager.signInAsGuest()
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        showGuestModeDialog = false
                    )
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
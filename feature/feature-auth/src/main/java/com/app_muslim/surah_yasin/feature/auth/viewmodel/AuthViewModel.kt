package com.app_muslim.surah_yasin.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.feature.auth.model.*
import com.app_muslim.surah_yasin.core.firebase.FirebaseAuthService
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: FirebaseAuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _loginFormState = MutableStateFlow(LoginFormState())
    val loginFormState: StateFlow<LoginFormState> = _loginFormState.asStateFlow()

    private val _registerFormState = MutableStateFlow(RegisterFormState())
    val registerFormState: StateFlow<RegisterFormState> = _registerFormState.asStateFlow()

    init {
        checkAuthState()
    }

    fun handleAuthEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailLogin -> emailLogin(event.email, event.password)
            is AuthEvent.EmailRegister -> emailRegister(event.email, event.password, event.name)
            is AuthEvent.ForgotPassword -> forgotPassword(event.email)
            is AuthEvent.GoogleSignIn -> googleSignIn()
            is AuthEvent.AnonymousSignIn -> anonymousSignIn()
            is AuthEvent.SignOut -> signOut()
            is AuthEvent.ClearError -> clearError()
        }
    }

    fun updateLoginForm(email: String = _loginFormState.value.email, password: String = _loginFormState.value.password) {
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)
        
        _loginFormState.value = _loginFormState.value.copy(
            email = email,
            password = password,
            emailError = emailError,
            passwordError = passwordError,
            isValid = emailError == null && passwordError == null && email.isNotEmpty() && password.isNotEmpty()
        )
    }

    fun updateRegisterForm(
        name: String = _registerFormState.value.name,
        email: String = _registerFormState.value.email,
        password: String = _registerFormState.value.password,
        confirmPassword: String = _registerFormState.value.confirmPassword
    ) {
        val nameError = validateName(name)
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)
        val confirmPasswordError = validateConfirmPassword(password, confirmPassword)
        
        _registerFormState.value = _registerFormState.value.copy(
            name = name,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            nameError = nameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            isValid = nameError == null && emailError == null && passwordError == null && 
                     confirmPasswordError == null && name.isNotEmpty() && email.isNotEmpty() && 
                     password.isNotEmpty() && confirmPassword.isNotEmpty()
        )
    }

    private fun checkAuthState() {
        val currentUser = authService.currentUser
        if (currentUser != null) {
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                currentUser = mapFirebaseUser(currentUser)
            )
        }
    }

    private fun emailLogin(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = authService.signInWithEmailAndPassword(email, password)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = user?.let { mapFirebaseUser(it) }
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    private fun emailRegister(email: String, password: String, name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = authService.createUserWithEmailAndPassword(email, password)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    // Update display name
                    authService.updateUserProfile(name, null)
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = user?.let { mapFirebaseUser(it) }
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Registration failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    private fun forgotPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = authService.sendPasswordResetEmail(email)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Password reset email sent successfully"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to send reset email"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    private fun googleSignIn() {
        // Will be implemented with Activity Result API in the UI layer
        _uiState.value = _uiState.value.copy(errorMessage = "Google Sign-In not yet implemented")
    }

    private fun anonymousSignIn() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = authService.signInAnonymously()
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = user?.let { mapFirebaseUser(it) }
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Anonymous sign-in failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            authService.signOut()
            _uiState.value = AuthUiState()
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun mapFirebaseUser(firebaseUser: FirebaseUser): AuthUser {
        return AuthUser(
            uid = firebaseUser.uid,
            email = firebaseUser.email,
            displayName = firebaseUser.displayName,
            photoUrl = firebaseUser.photoUrl?.toString(),
            providerId = firebaseUser.providerId
        )
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            !password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !password.any { !it.isLetterOrDigit() } -> "Password must contain at least one special character"
            else -> null
        }
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Please confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }
}
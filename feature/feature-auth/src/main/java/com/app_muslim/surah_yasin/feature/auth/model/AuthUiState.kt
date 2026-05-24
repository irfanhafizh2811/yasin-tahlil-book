package com.app_muslim.surah_yasin.feature.auth.model

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val currentUser: AuthUser? = null
)

data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val providerId: String
)

sealed class AuthEvent {
    data class EmailLogin(val email: String, val password: String) : AuthEvent()
    data class EmailRegister(val email: String, val password: String, val name: String) : AuthEvent()
    data class ForgotPassword(val email: String) : AuthEvent()
    object GoogleSignIn : AuthEvent()
    object AnonymousSignIn : AuthEvent()
    object SignOut : AuthEvent()
    object ClearError : AuthEvent()
}

data class RegisterFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isValid: Boolean = false
)

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isValid: Boolean = false
)

enum class AuthProvider {
    EMAIL,
    GOOGLE,
    APPLE,
    PHONE,
    ANONYMOUS
}
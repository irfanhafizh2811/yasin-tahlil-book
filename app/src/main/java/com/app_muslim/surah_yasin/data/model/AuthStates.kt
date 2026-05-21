package com.app_muslim.surah_yasin.data.model

import com.google.firebase.auth.FirebaseUser

/**
 * Sealed classes representing different authentication states
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class ProfileCreationState {
    object Idle : ProfileCreationState()
    object Loading : ProfileCreationState()
    object Success : ProfileCreationState()
    data class Error(val message: String) : ProfileCreationState()
}

sealed class PhoneVerificationState {
    object Idle : PhoneVerificationState()
    object CodeSending : PhoneVerificationState()
    data class CodeSent(val verificationId: String) : PhoneVerificationState()
    object Verifying : PhoneVerificationState()
    object Success : PhoneVerificationState()
    data class Error(val message: String) : PhoneVerificationState()
}

sealed class PasswordResetState {
    object Idle : PasswordResetState()
    object Sending : PasswordResetState()
    object Sent : PasswordResetState()
    data class Error(val message: String) : PasswordResetState()
}
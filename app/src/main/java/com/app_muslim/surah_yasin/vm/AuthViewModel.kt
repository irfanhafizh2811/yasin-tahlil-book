package com.app_muslim.surah_yasin.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app_muslim.surah_yasin.data.model.*
import com.app_muslim.surah_yasin.services.FirebaseAuthService
import com.app_muslim.surah_yasin.services.FirestoreService
import com.app_muslim.surah_yasin.data.preference.AuthPreference
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService,
    private val authPreference: AuthPreference
) : ViewModel() {
    
    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState
    
    private val _profileCreationState = MutableLiveData<ProfileCreationState>(ProfileCreationState.Idle)
    val profileCreationState: LiveData<ProfileCreationState> = _profileCreationState
    
    private val _passwordResetState = MutableLiveData<PasswordResetState>(PasswordResetState.Idle)
    val passwordResetState: LiveData<PasswordResetState> = _passwordResetState
    
    private val _phoneVerificationState = MutableLiveData<PhoneVerificationState>(PhoneVerificationState.Idle)
    val phoneVerificationState: LiveData<PhoneVerificationState> = _phoneVerificationState
    
    /**
     * Sign in with email and password
     */
    fun signInWithEmail(email: String, password: String) {
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            try {
                val result = authService.signInWithEmailAndPassword(email, password)
                result.fold(
                    onSuccess = { user ->
                        _authState.value = AuthState.Success(user)
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Error(
                            exception.message ?: "Sign in failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
    }
    
    /**
     * Register with email and password
     */
    fun registerWithEmail(email: String, password: String, displayName: String) {
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            try {
                val result = authService.createUserWithEmailAndPassword(email, password)
                result.fold(
                    onSuccess = { user ->
                        // Update display name
                        authService.updateProfile(displayName, null)
                        _authState.value = AuthState.Success(user)
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Error(
                            exception.message ?: "Registration failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
    }
    
    /**
     * Sign in with Google ID token
     */
    fun signInWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            try {
                val result = authService.signInWithGoogle(idToken)
                result.fold(
                    onSuccess = { user ->
                        _authState.value = AuthState.Success(user)
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Error(
                            exception.message ?: "Google sign in failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "Google sign in failed"
                )
            }
        }
    }
    
    /**
     * Start phone number verification
     */
    fun startPhoneVerification(phoneNumber: String) {
        _phoneVerificationState.value = PhoneVerificationState.CodeSending
        
        viewModelScope.launch {
            try {
                val result = authService.startPhoneNumberVerification(phoneNumber)
                result.fold(
                    onSuccess = { verificationId ->
                        _phoneVerificationState.value = PhoneVerificationState.CodeSent(verificationId)
                    },
                    onFailure = { exception ->
                        _phoneVerificationState.value = PhoneVerificationState.Error(
                            exception.message ?: "Phone verification failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _phoneVerificationState.value = PhoneVerificationState.Error(
                    e.message ?: "Phone verification failed"
                )
            }
        }
    }
    
    /**
     * Verify phone number with SMS code
     */
    fun verifyPhoneNumber(verificationId: String, smsCode: String) {
        _phoneVerificationState.value = PhoneVerificationState.Verifying
        
        viewModelScope.launch {
            try {
                val result = authService.verifyPhoneNumberWithCode(verificationId, smsCode)
                result.fold(
                    onSuccess = { user ->
                        _phoneVerificationState.value = PhoneVerificationState.Success
                        _authState.value = AuthState.Success(user)
                    },
                    onFailure = { exception ->
                        _phoneVerificationState.value = PhoneVerificationState.Error(
                            exception.message ?: "Phone verification failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _phoneVerificationState.value = PhoneVerificationState.Error(
                    e.message ?: "Phone verification failed"
                )
            }
        }
    }
    
    /**
     * Sign in anonymously
     */
    fun signInAnonymously() {
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            try {
                val result = authService.signInAnonymously()
                result.fold(
                    onSuccess = { user ->
                        _authState.value = AuthState.Success(user)
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Error(
                            exception.message ?: "Anonymous sign in failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "Anonymous sign in failed"
                )
            }
        }
    }
    
    /**
     * Reset password with email
     */
    fun resetPassword(email: String) {
        _passwordResetState.value = PasswordResetState.Sending
        
        viewModelScope.launch {
            try {
                val result = authService.sendPasswordResetEmail(email)
                result.fold(
                    onSuccess = {
                        _passwordResetState.value = PasswordResetState.Sent
                    },
                    onFailure = { exception ->
                        _passwordResetState.value = PasswordResetState.Error(
                            exception.message ?: "Password reset failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _passwordResetState.value = PasswordResetState.Error(
                    e.message ?: "Password reset failed"
                )
            }
        }
    }
    
    /**
     * Create user profile after authentication
     */
    fun createUserProfile(userProfile: UserProfile) {
        _profileCreationState.value = ProfileCreationState.Loading
        
        viewModelScope.launch {
            try {
                val result = firestoreService.createUserProfile(userProfile)
                result.fold(
                    onSuccess = {
                        _profileCreationState.value = ProfileCreationState.Success
                    },
                    onFailure = { exception ->
                        _profileCreationState.value = ProfileCreationState.Error(
                            exception.message ?: "Profile creation failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _profileCreationState.value = ProfileCreationState.Error(
                    e.message ?: "Profile creation failed"
                )
            }
        }
    }
    
    /**
     * Sign out current user
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                authService.signOut()
                _authState.value = AuthState.Idle
                _profileCreationState.value = ProfileCreationState.Idle
                _passwordResetState.value = PasswordResetState.Idle
                _phoneVerificationState.value = PhoneVerificationState.Idle
            } catch (e: Exception) {
                // Handle sign out error if needed
            }
        }
    }
    
    /**
     * Get current authenticated user
     */
    fun getCurrentUser(): FirebaseUser? {
        return authService.getCurrentUser()
    }
    
    /**
     * Check if user is authenticated
     */
    fun isUserAuthenticated(): Boolean {
        return authService.getCurrentUser() != null
    }
    
    /**
     * Reset all states
     */
    fun resetStates() {
        _authState.value = AuthState.Idle
        _profileCreationState.value = ProfileCreationState.Idle
        _passwordResetState.value = PasswordResetState.Idle
        _phoneVerificationState.value = PhoneVerificationState.Idle
    }

    /**
     * Update local preferences with Firebase user data
     */
    private fun updateAuthPreference(user: FirebaseUser) {
        authPreference.apply {
            userId = user.uid
            isAuthenticated = true
            displayName = user.displayName ?: ""
            email = user.email ?: ""
            phoneNumber = user.phoneNumber ?: ""
            photoUrl = user.photoUrl?.toString() ?: ""
            isAnonymous = user.isAnonymous
            lastLoginTime = System.currentTimeMillis()
            
            // Determine auth provider
            authProvider = when {
                user.providerData.any { it.providerId == "google.com" } -> "google"
                user.providerData.any { it.providerId == "phone" } -> "phone"
                user.isAnonymous -> "anonymous"
                else -> "email"
            }
        }
    }

    /**
     * Get current auth state from preferences
     */
    fun isUserLoggedIn(): Boolean {
        return authPreference.isAuthenticated && authService.getCurrentUser() != null
    }

    /**
     * Check if cultural setup is needed
     */
    fun needsCulturalSetup(): Boolean {
        return authPreference.needsCulturalSetup()
    }
}
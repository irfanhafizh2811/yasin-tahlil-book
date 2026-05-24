package com.app_muslim.surah_yasin.core.firebase

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class SessionState(
    val isAuthenticated: Boolean = false,
    val user: FirebaseUser? = null,
    val isGuest: Boolean = false,
    val isEmailVerified: Boolean = false,
    val tokenExpirationTime: Long = 0L,
    val lastTokenRefresh: Long = 0L
)

@Singleton
class SessionManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val authService: FirebaseAuthService,
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val TAG = "SessionManager"
        private const val PREF_IS_GUEST_MODE = "is_guest_mode"
        private const val PREF_LAST_TOKEN_REFRESH = "last_token_refresh"
        private const val PREF_AUTO_REFRESH_ENABLED = "auto_refresh_enabled"
        private const val TOKEN_REFRESH_INTERVAL = 3600000L // 1 hour in milliseconds
        private const val TOKEN_EXPIRY_BUFFER = 300000L // 5 minutes in milliseconds
    }

    private val _sessionState = MutableStateFlow(SessionState())
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    val authStateFlow: Flow<SessionState> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            val isGuest = user?.isAnonymous == true
            val isEmailVerified = user?.isEmailVerified == true

            val newState = SessionState(
                isAuthenticated = user != null,
                user = user,
                isGuest = isGuest,
                isEmailVerified = isEmailVerified,
                tokenExpirationTime = getTokenExpirationTime(),
                lastTokenRefresh = getLastTokenRefresh()
            )
            
            _sessionState.value = newState
            trySend(newState)
            
            Log.d(TAG, "Auth state changed: authenticated=${user != null}, guest=$isGuest")
        }

        auth.addAuthStateListener(authStateListener)
        
        // Emit current state immediately
        val currentUser = auth.currentUser
        val currentState = SessionState(
            isAuthenticated = currentUser != null,
            user = currentUser,
            isGuest = currentUser?.isAnonymous == true,
            isEmailVerified = currentUser?.isEmailVerified == true,
            tokenExpirationTime = getTokenExpirationTime(),
            lastTokenRefresh = getLastTokenRefresh()
        )
        _sessionState.value = currentState
        trySend(currentState)

        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    // Session Persistence
    fun enableGuestMode() {
        sharedPreferences.edit()
            .putBoolean(PREF_IS_GUEST_MODE, true)
            .apply()
        Log.d(TAG, "Guest mode enabled")
    }

    fun disableGuestMode() {
        sharedPreferences.edit()
            .putBoolean(PREF_IS_GUEST_MODE, false)
            .apply()
        Log.d(TAG, "Guest mode disabled")
    }

    fun isGuestModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_IS_GUEST_MODE, false)
    }

    // Token Management
    suspend fun refreshTokenIfNeeded(): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.w(TAG, "No user authenticated, cannot refresh token")
                return Result.failure(Exception("No authenticated user"))
            }

            val lastRefresh = getLastTokenRefresh()
            val now = System.currentTimeMillis()
            
            // Check if token needs refresh
            if (now - lastRefresh < TOKEN_REFRESH_INTERVAL) {
                Log.d(TAG, "Token refresh not needed yet")
                return Result.success(Unit)
            }

            // Force token refresh
            val tokenResult = currentUser.getIdToken(true).await()
            val expirationTime = tokenResult.expirationTimestamp
            
            // Save refresh time
            sharedPreferences.edit()
                .putLong(PREF_LAST_TOKEN_REFRESH, now)
                .apply()

            Log.d(TAG, "Token refreshed successfully. Expires at: $expirationTime")
            
            // Update session state
            _sessionState.value = _sessionState.value.copy(
                tokenExpirationTime = expirationTime,
                lastTokenRefresh = now
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to refresh token", e)
            Result.failure(e)
        }
    }

    suspend fun getValidToken(): Result<String> {
        return try {
            val currentUser = auth.currentUser 
                ?: return Result.failure(Exception("No authenticated user"))

            // Check if token is about to expire
            val tokenResult = currentUser.getIdToken(false).await()
            val now = System.currentTimeMillis()
            val expiryTime = tokenResult.expirationTimestamp

            if (expiryTime - now < TOKEN_EXPIRY_BUFFER) {
                Log.d(TAG, "Token expiring soon, refreshing...")
                refreshTokenIfNeeded()
                // Get fresh token after refresh
                val newTokenResult = currentUser.getIdToken(false).await()
                Result.success(newTokenResult.token ?: "")
            } else {
                Result.success(tokenResult.token ?: "")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get valid token", e)
            Result.failure(e)
        }
    }

    // Guest Mode Authentication
    suspend fun signInAsGuest(): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "Signing in as guest...")
            val result = authService.signInAnonymously()
            
            if (result.isSuccess) {
                enableGuestMode()
                Log.d(TAG, "Guest sign-in successful")
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sign in as guest", e)
            Result.failure(e)
        }
    }

    // Account Verification
    suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("No authenticated user"))
            }

            if (currentUser.isEmailVerified) {
                Log.d(TAG, "Email already verified")
                return Result.success(Unit)
            }

            currentUser.sendEmailVerification().await()
            Log.d(TAG, "Email verification sent")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send email verification", e)
            Result.failure(e)
        }
    }

    suspend fun checkEmailVerificationStatus(): Result<Boolean> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("No authenticated user"))
            }

            // Reload user to get latest verification status
            currentUser.reload().await()
            val isVerified = currentUser.isEmailVerified
            
            Log.d(TAG, "Email verification status: $isVerified")
            
            // Update session state
            _sessionState.value = _sessionState.value.copy(
                isEmailVerified = isVerified
            )
            
            Result.success(isVerified)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check email verification", e)
            Result.failure(e)
        }
    }

    // Sign Out with Clean State
    suspend fun signOut(): Result<Unit> {
        return try {
            Log.d(TAG, "Signing out user...")
            
            // Clear guest mode
            disableGuestMode()
            
            // Clear token refresh preferences
            sharedPreferences.edit()
                .remove(PREF_LAST_TOKEN_REFRESH)
                .remove(PREF_AUTO_REFRESH_ENABLED)
                .apply()
            
            // Sign out from Firebase
            auth.signOut()
            
            // Reset session state
            _sessionState.value = SessionState()
            
            Log.d(TAG, "Sign out completed")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sign out", e)
            Result.failure(e)
        }
    }

    // Convert guest account to permanent account
    suspend fun upgradeGuestAccount(email: String, password: String): Result<FirebaseUser> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null || !currentUser.isAnonymous) {
                return Result.failure(Exception("No guest user to upgrade"))
            }

            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, password)
            val result = currentUser.linkWithCredential(credential).await()
            
            disableGuestMode()
            Log.d(TAG, "Guest account upgraded successfully")
            
            Result.success(result.user!!)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upgrade guest account", e)
            Result.failure(e)
        }
    }

    // Helper methods
    private fun getTokenExpirationTime(): Long {
        return try {
            // This would require cached token result, for now return 0
            0L
        } catch (e: Exception) {
            0L
        }
    }

    private fun getLastTokenRefresh(): Long {
        return sharedPreferences.getLong(PREF_LAST_TOKEN_REFRESH, 0L)
    }

    // Session validation
    fun isSessionValid(): Boolean {
        val state = _sessionState.value
        return state.isAuthenticated && (state.isGuest || state.isEmailVerified)
    }

    fun requiresEmailVerification(): Boolean {
        val state = _sessionState.value
        return state.isAuthenticated && !state.isGuest && !state.isEmailVerified
    }

    // Auto refresh control
    fun enableAutoTokenRefresh() {
        sharedPreferences.edit()
            .putBoolean(PREF_AUTO_REFRESH_ENABLED, true)
            .apply()
        Log.d(TAG, "Auto token refresh enabled")
    }

    fun disableAutoTokenRefresh() {
        sharedPreferences.edit()
            .putBoolean(PREF_AUTO_REFRESH_ENABLED, false)
            .apply()
        Log.d(TAG, "Auto token refresh disabled")
    }

    fun isAutoTokenRefreshEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_AUTO_REFRESH_ENABLED, true)
    }
}
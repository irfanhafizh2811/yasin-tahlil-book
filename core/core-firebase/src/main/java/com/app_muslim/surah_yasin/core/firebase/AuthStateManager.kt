package com.app_muslim.surah_yasin.core.firebase

import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor(
    private val sessionManager: SessionManager,
    private val context: Context
) : DefaultLifecycleObserver {

    companion object {
        private const val TAG = "AuthStateManager"
        private const val TOKEN_CHECK_INTERVAL = 30 * 60 * 1000L // 30 minutes
    }

    private var authStateJob: Job? = null
    private var tokenCheckJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d(TAG, "App moved to foreground")
        startAuthStateMonitoring()
        scheduleTokenRefresh()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d(TAG, "App moved to background")
        stopAuthStateMonitoring()
        stopTokenRefresh()
    }

    private fun startAuthStateMonitoring() {
        authStateJob?.cancel()
        authStateJob = coroutineScope.launch {
            sessionManager.authStateFlow.collect { sessionState ->
                handleAuthStateChange(sessionState)
            }
        }
    }

    private fun stopAuthStateMonitoring() {
        authStateJob?.cancel()
        authStateJob = null
    }

    private fun scheduleTokenRefresh() {
        tokenCheckJob?.cancel()
        tokenCheckJob = coroutineScope.launch {
            while (isActive) {
                try {
                    val sessionState = sessionManager.sessionState.value
                    if (sessionState.isAuthenticated && !sessionState.isGuest) {
                        Log.d(TAG, "Checking token refresh...")
                        sessionManager.refreshTokenIfNeeded()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error during scheduled token refresh", e)
                }
                delay(TOKEN_CHECK_INTERVAL)
            }
        }
    }

    private fun stopTokenRefresh() {
        tokenCheckJob?.cancel()
        tokenCheckJob = null
    }

    private suspend fun handleAuthStateChange(sessionState: SessionState) {
        Log.d(TAG, "Auth state changed: authenticated=${sessionState.isAuthenticated}, guest=${sessionState.isGuest}")
        
        when {
            sessionState.isAuthenticated && !sessionState.isGuest -> {
                // User authenticated with account
                startPeriodicTokenRefresh()
                checkEmailVerificationIfNeeded(sessionState)
            }
            sessionState.isAuthenticated && sessionState.isGuest -> {
                // Guest user
                Log.d(TAG, "Guest user authenticated")
                stopPeriodicTokenRefresh()
            }
            !sessionState.isAuthenticated -> {
                // User signed out
                Log.d(TAG, "User signed out")
                stopPeriodicTokenRefresh()
                clearAuthStateData()
            }
        }
    }

    private fun startPeriodicTokenRefresh() {
        sessionManager.enableAutoTokenRefresh()
        TokenRefreshWorker.scheduleTokenRefresh(context)
    }

    private fun stopPeriodicTokenRefresh() {
        TokenRefreshWorker.cancelTokenRefresh(context)
    }

    private suspend fun checkEmailVerificationIfNeeded(sessionState: SessionState) {
        if (!sessionState.isEmailVerified) {
            Log.d(TAG, "Email not verified, checking status...")
            delay(2000) // Give some time for potential verification
            sessionManager.checkEmailVerificationStatus()
        }
    }

    private fun clearAuthStateData() {
        // Clear any cached auth-related data
        // This could include clearing local databases, caches, etc.
        Log.d(TAG, "Clearing auth state data")
    }

    fun cleanUp() {
        stopAuthStateMonitoring()
        stopTokenRefresh()
        coroutineScope.cancel()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }
}

// Extension functions for easier session state management
suspend fun SessionManager.signOutWithCleanup(): Result<Unit> {
    return try {
        // Perform full sign out with state cleanup
        val signOutResult = signOut()
        
        if (signOutResult.isSuccess) {
            Log.d("SessionManager", "Sign out with cleanup completed")
        }
        
        signOutResult
    } catch (e: Exception) {
        Log.e("SessionManager", "Error during sign out with cleanup", e)
        Result.failure(e)
    }
}

suspend fun SessionManager.signInWithEmailAndUpgradeGuest(
    email: String,
    password: String
): Result<com.google.firebase.auth.FirebaseUser> {
    return try {
        val sessionState = sessionState.value
        
        if (sessionState.isGuest) {
            // Upgrade guest account
            Log.d("SessionManager", "Upgrading guest account to permanent account")
            upgradeGuestAccount(email, password)
        } else {
            // Regular sign in
            Log.d("SessionManager", "Signing in with email and password")
            // This would need to be implemented in FirebaseAuthService
            Result.failure(Exception("Regular sign in not implemented in this context"))
        }
    } catch (e: Exception) {
        Log.e("SessionManager", "Error during sign in with guest upgrade", e)
        Result.failure(e)
    }
}

fun SessionState.requiresAttention(): Boolean {
    return when {
        isAuthenticated && !isGuest && !isEmailVerified -> true
        else -> false
    }
}

fun SessionState.getStatusMessage(): String {
    return when {
        !isAuthenticated -> "Please sign in to access all features"
        isGuest -> "You're in guest mode. Create an account to save your progress"
        !isEmailVerified -> "Please verify your email to access all features"
        else -> "You're signed in and ready to go"
    }
}

fun SessionState.canAccessFeature(feature: String): Boolean {
    val restrictedFeatures = setOf("profile", "memorial_create", "community")
    
    return when {
        !isAuthenticated -> false
        feature in restrictedFeatures && isGuest -> false
        feature in restrictedFeatures && !isEmailVerified -> false
        else -> true
    }
}
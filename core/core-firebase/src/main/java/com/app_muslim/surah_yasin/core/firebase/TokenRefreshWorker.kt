package com.app_muslim.surah_yasin.core.firebase

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class TokenRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val sessionManager: SessionManager
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "token_refresh_worker"
        private const val TAG = "TokenRefreshWorker"
        
        fun scheduleTokenRefresh(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val refreshRequest = PeriodicWorkRequestBuilder<TokenRefreshWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                refreshRequest
            )
            
            Log.d(TAG, "Token refresh worker scheduled")
        }
        
        fun cancelTokenRefresh(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Log.d(TAG, "Token refresh worker canceled")
        }
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Token refresh worker started")
        
        return try {
            // Check if auto refresh is enabled
            if (!sessionManager.isAutoTokenRefreshEnabled()) {
                Log.d(TAG, "Auto token refresh is disabled, skipping")
                return Result.success()
            }
            
            // Check if user is authenticated
            val sessionState = sessionManager.sessionState.value
            if (!sessionState.isAuthenticated) {
                Log.d(TAG, "User not authenticated, skipping token refresh")
                return Result.success()
            }
            
            // Perform token refresh
            val result = sessionManager.refreshTokenIfNeeded()
            
            if (result.isSuccess) {
                Log.d(TAG, "Token refresh completed successfully")
                Result.success()
            } else {
                Log.w(TAG, "Token refresh failed: ${result.exceptionOrNull()?.message}")
                // Retry with exponential backoff
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Token refresh worker failed", e)
            Result.failure()
        }
    }
}

// Helper class to manage token refresh scheduling
class TokenRefreshManager(
    private val context: Context,
    private val sessionManager: SessionManager
) {
    
    fun startAutoTokenRefresh() {
        if (sessionManager.isAutoTokenRefreshEnabled()) {
            TokenRefreshWorker.scheduleTokenRefresh(context)
        }
    }
    
    fun stopAutoTokenRefresh() {
        TokenRefreshWorker.cancelTokenRefresh(context)
    }
    
    fun enableAutoRefreshForSession() {
        sessionManager.enableAutoTokenRefresh()
        TokenRefreshWorker.scheduleTokenRefresh(context)
    }
    
    fun disableAutoRefreshForSession() {
        sessionManager.disableAutoTokenRefresh()
        TokenRefreshWorker.cancelTokenRefresh(context)
    }
}
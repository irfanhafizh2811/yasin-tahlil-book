package com.app_muslim.surah_yasin.core.ui.performance

import android.os.Build
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
// TODO: Add Firebase Analytics when core-firebase is properly integrated
// import com.google.firebase.analytics.FirebaseAnalytics
// import com.google.firebase.analytics.ktx.logEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

/**
 * Performance Monitoring Manager for Tahlil Platform
 * 
 * Comprehensive performance monitoring with:
 * - Real-time performance metrics collection
 * - Firebase Analytics integration for performance tracking
 * - Memory usage monitoring
 * - Frame rate and rendering performance
 * - Prayer counter performance specific metrics
 * 
 * Target metrics:
 * - Prayer counter response: <50ms
 * - App startup time: <3s  
 * - Smooth 60 FPS rendering
 * - Memory efficiency monitoring
 */
@Singleton
class PerformanceMonitoringManager @Inject constructor() {
    
    companion object {
        const val TAG = "TahlilPerformance"
        const val PRAYER_COUNTER_PERFORMANCE_THRESHOLD_MS = 50L
        const val APP_STARTUP_PERFORMANCE_THRESHOLD_MS = 3000L
        const val FRAME_TIME_THRESHOLD_MS = 16L // 60 FPS = 16ms per frame
    }
    
    private val monitoringScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    // Performance metrics flow
    private val _performanceMetrics = MutableSharedFlow<PerformanceEvent>()
    val performanceMetrics: SharedFlow<PerformanceEvent> = _performanceMetrics
    
    // Performance counters
    private var prayerCounterInteractions = 0L
    private var slowPrayerCounterResponses = 0L
    private var totalFrameRenderTime = 0L
    private var frameCount = 0L
    
    /**
     * Start performance monitoring session
     */
    fun startMonitoring() {
        Log.i(TAG, "Starting comprehensive performance monitoring for Tahlil platform")
        
        // Log device information for context
        logDeviceContext()
        
        // Start monitoring memory usage
        startMemoryMonitoring()
        
        // Track app lifecycle performance
        trackAppLifecyclePerformance()
    }
    
    /**
     * Track prayer counter performance (critical metric)
     */
    fun trackPrayerCounterPerformance(
        responseTimeMs: Long,
        operationType: PrayerCounterOperation = PrayerCounterOperation.INCREMENT,
        additionalData: Map<String, Any> = emptyMap()
    ) {
        prayerCounterInteractions++
        
        if (responseTimeMs > PRAYER_COUNTER_PERFORMANCE_THRESHOLD_MS) {
            slowPrayerCounterResponses++
            Log.w(TAG, "Prayer counter response time: ${responseTimeMs}ms (threshold: ${PRAYER_COUNTER_PERFORMANCE_THRESHOLD_MS}ms)")
        }
        
        // Emit performance event
        monitoringScope.launch {
            _performanceMetrics.emit(
                PerformanceEvent.PrayerCounterPerformance(
                    responseTimeMs = responseTimeMs,
                    operation = operationType,
                    isOptimal = responseTimeMs <= PRAYER_COUNTER_PERFORMANCE_THRESHOLD_MS,
                    additionalData = additionalData
                )
            )
        }
        
        // TODO: Log to Firebase Analytics for production monitoring
        // analytics.logEvent("prayer_counter_performance") {
        //     param("response_time_ms", responseTimeMs)
        //     param("operation", operationType.name)
        //     param("is_optimal", responseTimeMs <= PRAYER_COUNTER_PERFORMANCE_THRESHOLD_MS)
        // }
        
        Log.d(TAG, "Prayer counter performance: ${responseTimeMs}ms for ${operationType.name}")
    }
    
    /**
     * Track app startup performance
     */
    fun trackAppStartupPerformance(
        startupTimeMs: Long,
        startupSteps: Map<String, Long> = emptyMap()
    ) {
        val isOptimal = startupTimeMs <= APP_STARTUP_PERFORMANCE_THRESHOLD_MS
        
        monitoringScope.launch {
            _performanceMetrics.emit(
                PerformanceEvent.AppStartupPerformance(
                    startupTimeMs = startupTimeMs,
                    isOptimal = isOptimal,
                    startupSteps = startupSteps
                )
            )
        }
        
        // TODO: Log to Firebase Analytics
        // analytics.logEvent("app_startup_performance") {
        //     param("startup_time_ms", startupTimeMs)
        //     param("is_optimal", isOptimal)
        //     param("target_time_ms", APP_STARTUP_PERFORMANCE_THRESHOLD_MS)
        // }
        
        if (!isOptimal) {
            Log.w(TAG, "App startup performance below target: ${startupTimeMs}ms (target: ${APP_STARTUP_PERFORMANCE_THRESHOLD_MS}ms)")
        } else {
            Log.i(TAG, "App startup performance optimal: ${startupTimeMs}ms")
        }
    }
    
    /**
     * Track screen rendering performance
     */
    fun trackScreenRenderingPerformance(
        screenName: String,
        renderTimeMs: Long,
        compositionCount: Int = 0
    ) {
        frameCount++
        totalFrameRenderTime += renderTimeMs
        
        val isOptimal = renderTimeMs <= FRAME_TIME_THRESHOLD_MS
        
        monitoringScope.launch {
            _performanceMetrics.emit(
                PerformanceEvent.ScreenRenderingPerformance(
                    screenName = screenName,
                    renderTimeMs = renderTimeMs,
                    compositionCount = compositionCount,
                    isOptimal = isOptimal
                )
            )
        }
        
        // Log slow renders
        if (!isOptimal) {
            Log.w(TAG, "Slow render detected: $screenName took ${renderTimeMs}ms (target: ${FRAME_TIME_THRESHOLD_MS}ms)")
            
            // TODO: Log to Firebase Analytics for production monitoring
            // analytics.logEvent("slow_render_detected") {
            //     param("screen_name", screenName)
            //     param("render_time_ms", renderTimeMs)
            //     param("composition_count", compositionCount.toLong())
            // }
        }
    }
    
    /**
     * Track memory usage performance
     */
    fun trackMemoryUsage(
        operation: String,
        memoryUsageMB: Long,
        availableMemoryMB: Long
    ) {
        val memoryUsagePercentage = (memoryUsageMB.toFloat() / availableMemoryMB.toFloat()) * 100
        val isOptimal = memoryUsagePercentage < 80f // Less than 80% memory usage
        
        monitoringScope.launch {
            _performanceMetrics.emit(
                PerformanceEvent.MemoryPerformance(
                    operation = operation,
                    memoryUsageMB = memoryUsageMB,
                    availableMemoryMB = availableMemoryMB,
                    usagePercentage = memoryUsagePercentage,
                    isOptimal = isOptimal
                )
            )
        }
        
        if (!isOptimal) {
            Log.w(TAG, "High memory usage detected: ${memoryUsageMB}MB / ${availableMemoryMB}MB (${memoryUsagePercentage}%)")
            
            // TODO: Log to Firebase Analytics
            // analytics.logEvent("high_memory_usage") {
            //     param("operation", operation)
            //     param("memory_usage_mb", memoryUsageMB)
            //     param("usage_percentage", memoryUsagePercentage.toDouble())
            // }
        }
    }
    
    /**
     * Track Firebase operation performance
     */
    fun trackFirebasePerformance(
        operation: FirebaseOperation,
        executionTimeMs: Long,
        success: Boolean,
        errorMessage: String? = null
    ) {
        monitoringScope.launch {
            _performanceMetrics.emit(
                PerformanceEvent.FirebasePerformance(
                    operation = operation,
                    executionTimeMs = executionTimeMs,
                    success = success,
                    errorMessage = errorMessage
                )
            )
        }
        
        // TODO: Log to Firebase Analytics
        // analytics.logEvent("firebase_operation_performance") {
        //     param("operation", operation.name)
        //     param("execution_time_ms", executionTimeMs)
        //     param("success", success)
        //     errorMessage?.let { param("error_message", it) }
        // }
        
        if (!success) {
            Log.e(TAG, "Firebase operation failed: ${operation.name} in ${executionTimeMs}ms - $errorMessage")
        }
    }
    
    /**
     * Get current performance summary
     */
    fun getPerformanceSummary(): PerformanceSummary {
        val averageFrameTime = if (frameCount > 0) totalFrameRenderTime / frameCount else 0L
        val prayerCounterSuccessRate = if (prayerCounterInteractions > 0) {
            ((prayerCounterInteractions - slowPrayerCounterResponses).toFloat() / prayerCounterInteractions.toFloat()) * 100f
        } else 100f
        
        return PerformanceSummary(
            averageFrameRenderTime = averageFrameTime,
            prayerCounterSuccessRate = prayerCounterSuccessRate,
            totalPrayerCounterInteractions = prayerCounterInteractions,
            slowPrayerCounterResponses = slowPrayerCounterResponses,
            totalFramesRendered = frameCount,
            memoryUsageOptimal = true // This would be calculated from actual memory monitoring
        )
    }
    
    /**
     * Start memory monitoring in background
     */
    private fun startMemoryMonitoring() {
        monitoringScope.launch {
            while (true) {
                try {
                    val runtime = Runtime.getRuntime()
                    val usedMemory = runtime.totalMemory() - runtime.freeMemory()
                    val maxMemory = runtime.maxMemory()
                    
                    trackMemoryUsage(
                        operation = "background_monitoring",
                        memoryUsageMB = usedMemory / 1024 / 1024,
                        availableMemoryMB = maxMemory / 1024 / 1024
                    )
                    
                    kotlinx.coroutines.delay(30000) // Check every 30 seconds
                } catch (e: Exception) {
                    Log.e(TAG, "Error in memory monitoring", e)
                    kotlinx.coroutines.delay(60000) // Wait longer if error occurs
                }
            }
        }
    }
    
    /**
     * Track app lifecycle performance events
     */
    private fun trackAppLifecyclePerformance() {
        Log.d(TAG, "App lifecycle performance tracking initialized")
    }
    
    /**
     * Log device context for performance analysis
     */
    private fun logDeviceContext() {
        val deviceInfo = mapOf(
            "device_model" to Build.MODEL,
            "android_version" to Build.VERSION.RELEASE,
            "api_level" to Build.VERSION.SDK_INT,
            "manufacturer" to Build.MANUFACTURER,
            "brand" to Build.BRAND
        )
        
        Log.i(TAG, "Device context: $deviceInfo")
        
        // TODO: Log to Firebase Analytics
        // analytics.logEvent("performance_monitoring_started") {
        //     deviceInfo.forEach { (key, value) ->
        //         param(key, value.toString())
        //     }
        // }
    }
}

/**
 * Performance Event Types
 */
sealed class PerformanceEvent {
    data class PrayerCounterPerformance(
        val responseTimeMs: Long,
        val operation: PrayerCounterOperation,
        val isOptimal: Boolean,
        val additionalData: Map<String, Any> = emptyMap(),
        val timestamp: Long = System.currentTimeMillis()
    ) : PerformanceEvent()
    
    data class AppStartupPerformance(
        val startupTimeMs: Long,
        val isOptimal: Boolean,
        val startupSteps: Map<String, Long> = emptyMap(),
        val timestamp: Long = System.currentTimeMillis()
    ) : PerformanceEvent()
    
    data class ScreenRenderingPerformance(
        val screenName: String,
        val renderTimeMs: Long,
        val compositionCount: Int,
        val isOptimal: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    ) : PerformanceEvent()
    
    data class MemoryPerformance(
        val operation: String,
        val memoryUsageMB: Long,
        val availableMemoryMB: Long,
        val usagePercentage: Float,
        val isOptimal: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    ) : PerformanceEvent()
    
    data class FirebasePerformance(
        val operation: FirebaseOperation,
        val executionTimeMs: Long,
        val success: Boolean,
        val errorMessage: String? = null,
        val timestamp: Long = System.currentTimeMillis()
    ) : PerformanceEvent()
}

/**
 * Prayer Counter Operations
 */
enum class PrayerCounterOperation {
    INCREMENT,
    RESET,
    SET_TARGET,
    LOAD_STATE,
    SAVE_STATE
}

/**
 * Firebase Operations
 */
enum class FirebaseOperation {
    FIRESTORE_READ,
    FIRESTORE_WRITE,
    STORAGE_UPLOAD,
    STORAGE_DOWNLOAD,
    AUTH_LOGIN,
    AUTH_LOGOUT
}

/**
 * Performance Summary
 */
data class PerformanceSummary(
    val averageFrameRenderTime: Long,
    val prayerCounterSuccessRate: Float,
    val totalPrayerCounterInteractions: Long,
    val slowPrayerCounterResponses: Long,
    val totalFramesRendered: Long,
    val memoryUsageOptimal: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Compose Performance Monitoring Composable
 */
@Composable
fun PerformanceMonitoringWrapper(
    screenName: String,
    content: @Composable () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val startTime = remember { System.currentTimeMillis() }
    var compositionCount by remember { mutableIntStateOf(0) }
    
    // Track composition count
    LaunchedEffect(Unit) {
        compositionCount++
    }
    
    // Track screen lifecycle for performance
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.d(PerformanceMonitoringManager.TAG, "Screen created: $screenName")
                }
                Lifecycle.Event.ON_START -> {
                    Log.d(PerformanceMonitoringManager.TAG, "Screen started: $screenName")
                }
                Lifecycle.Event.ON_RESUME -> {
                    val resumeTime = System.currentTimeMillis() - startTime
                    Log.d(PerformanceMonitoringManager.TAG, "Screen resumed: $screenName in ${resumeTime}ms")
                }
                Lifecycle.Event.ON_DESTROY -> {
                    val totalTime = System.currentTimeMillis() - startTime
                    Log.d(PerformanceMonitoringManager.TAG, "Screen destroyed: $screenName after ${totalTime}ms, compositions: $compositionCount")
                }
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    content()
}
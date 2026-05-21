package com.app_muslim.surah_yasin.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import android.util.Log
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.utils.TimerManager
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.greenrobot.eventbus.EventBus
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : MultiDexApplication() {

    lateinit var eventBus: EventBus
        private set

    lateinit var timerManager: TimerManager
        private set

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        
        // Initialize Firebase App and App Check
        FirebaseApp.initializeApp(this@App)
        initializeFirebaseAppCheck()
        initializeFirestore()
        
        createNotificationChannel()
        eventBus = EventBus()
        timerManager = TimerManager(eventBus)
    }
    
    private fun initializeFirestore() {
        try {
            // Configure Firestore offline persistence for modular architecture
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
            
            FirebaseFirestore.getInstance().firestoreSettings = settings
            Log.d("TahlilApp", "Firestore offline persistence initialized for modular architecture")
        } catch (e: Exception) {
            Log.e("TahlilApp", "Failed to initialize Firestore settings", e)
        }
    }

    private fun initializeFirebaseAppCheck() {
        try {
            // Initialize Firebase App Check with Play Integrity for anti-abuse protection
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
            Log.d("TahlilApp", "Firebase App Check initialized successfully for Tahlil security")
        } catch (e: Exception) {
            Log.e("TahlilApp", "Failed to initialize Firebase App Check", e)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        lateinit var instance: App
            private set
        const val CHANNEL_ID = "Tasbeeh.notification"
    }
}
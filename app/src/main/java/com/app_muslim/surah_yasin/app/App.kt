package com.app_muslim.surah_yasin.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.deps.libraries
import com.app_muslim.surah_yasin.utils.TimerManager
import com.google.common.eventbus.EventBus
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

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
        FirebaseApp.initializeApp(this@App)
        startKoin {
            modules(libraries)
            androidContext(this@App)
        }
        createNotificationChannel()
        eventBus = EventBus()
        timerManager = TimerManager(eventBus)
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
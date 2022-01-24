package com.icaali.tasbeeh.app

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.icaali.tasbeeh.deps.libraries
import com.google.firebase.FirebaseApp
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.preference.CorePreference
import com.icaali.tasbeeh.preference.SettingPreference
import com.icaali.tasbeeh.receiver.NotificationReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.*

class TasbeehApp : MultiDexApplication() {

    companion object {
        const val CHANNEL_ID = "Tasbeeh.notification"
    }

    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        FirebaseApp.initializeApp(this@TasbeehApp)
        startKoin {
            modules(libraries)
            androidContext(this@TasbeehApp)
        }
        createNotificationChannel()
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

}
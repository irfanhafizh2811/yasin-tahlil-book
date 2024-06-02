package com.icaali.tasbeeh.view.activity

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.github.florent37.viewanimator.ViewAnimator
import com.icaali.tasbeeh.databinding.ActivitySplashBinding
import com.icaali.tasbeeh.extension.activty.hasPermissions
import com.icaali.tasbeeh.extension.view.visible
import com.icaali.tasbeeh.preference.SettingPreference
import com.icaali.tasbeeh.receiver.NotificationReceiver
import com.icaali.tasbeeh.remote.CoreRemoteConfig
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject
import java.util.*

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val coreRemoteConfig by inject<CoreRemoteConfig>()
    private val settingPreference by inject<SettingPreference>()

    // Register a launcher for requesting exact alarm permission
    @RequiresApi(Build.VERSION_CODES.S)
    private val requestExactAlarmPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Handle the result of the permission request here
            if (alarmManager.canScheduleExactAlarms()) {
                // Permission granted, proceed with scheduling the exact alarm
                schedulePushNotification()
            } else {
                // Permission denied, handle accordingly
                // Show a message to the user explaining why the permission is needed
            }
        }

    private lateinit var alarmManager: AlarmManager

    companion object {
        const val ANIMATION_IMAGE_DURATION = 1000L
        const val ANIMATION_TEXT_DURATION = 1000L
        const val HOUR_TO_SHOW_PUSH = 19
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coreRemoteConfig.remoteConfig.fetchAndActivate()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            tvBismillah.visible()
            ivBismillah.visible()

            ViewAnimator.animate(tvBismillah).apply {
                fadeIn()
                duration(ANIMATION_TEXT_DURATION)
                onStop {
                    startActivity(intentFor<MainActivity>())
                    finish()
                }
            }.start()
            ViewAnimator.animate(ivBismillah).apply {
                fadeIn()
                duration(ANIMATION_IMAGE_DURATION)
            }.start()
            schedulePushNotification()
        }
    }

    private fun schedulePushNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Check if we can schedule exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API level 31 and above
            if (!alarmManager.canScheduleExactAlarms()) {
                // Request the permission using ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                requestExactAlarmPermissionLauncher.launch(intent)
                return
            }
        }
        if (hasPermissions(arrayOf(Manifest.permission.USE_EXACT_ALARM))) {
            if (settingPreference.timeNotification == 0L || System.currentTimeMillis() > settingPreference.timeNotification) {
                val intent = Intent(this, NotificationReceiver::class.java)
                val alarmPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    PendingIntent.getBroadcast(
                        this, 0, intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                else PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                val calendar = Calendar.getInstance().apply {
                    if (get(Calendar.HOUR_OF_DAY) >= HOUR_TO_SHOW_PUSH) {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                    set(Calendar.HOUR_OF_DAY, HOUR_TO_SHOW_PUSH)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)


                    settingPreference.timeNotification = timeInMillis
                }

                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    alarmPendingIntent
                )
            }
        }
    }

}
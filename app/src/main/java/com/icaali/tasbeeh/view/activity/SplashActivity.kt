package com.icaali.tasbeeh.view.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.github.florent37.viewanimator.ViewAnimator
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.app.TasbeehApp
import com.icaali.tasbeeh.extension.view.visible
import com.icaali.tasbeeh.preference.CorePreference
import com.icaali.tasbeeh.preference.SettingPreference
import com.icaali.tasbeeh.receiver.NotificationReceiver
import com.icaali.tasbeeh.remote.CoreRemoteConfig
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject
import java.util.*

class SplashActivity : BaseActivity() {

    private val coreRemoteConfig by inject<CoreRemoteConfig>()
    private val settingPreference by inject<SettingPreference>()

    companion object {
        const val ANIMATION_IMAGE_DURATION = 1000L
        const val ANIMATION_TEXT_DURATION = 1000L
        const val HOUR_TO_SHOW_PUSH = 19
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coreRemoteConfig.remoteConfig.fetchAndActivate()
        setContentView(R.layout.activity_splash)
        tvBismillah?.visible()
        ivBismillah?.visible()

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

    private fun schedulePushNotification() {
        if (settingPreference.timeNotification == 0L || System.currentTimeMillis() > settingPreference.timeNotification) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
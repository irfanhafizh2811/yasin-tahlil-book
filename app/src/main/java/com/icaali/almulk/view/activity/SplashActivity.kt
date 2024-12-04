package com.icaali.almulk.view.activity

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.github.florent37.viewanimator.ViewAnimator
import com.icaali.almulk.databinding.ActivitySplashBinding
import com.icaali.almulk.extension.activty.hasPermissions
import com.icaali.almulk.extension.common.clazz
import com.icaali.almulk.extension.view.visible
import com.icaali.almulk.preference.SettingPreference
import com.icaali.almulk.receiver.NotificationReceiver
import com.icaali.almulk.remote.CoreRemoteConfig
import org.koin.android.ext.android.inject

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val coreRemoteConfig by inject<CoreRemoteConfig>()
    private val settingPreference by inject<SettingPreference>()

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
                    startActivity(Intent(this@SplashActivity, clazz<MainSurahActivity>()))
                    finish()
                }
            }.start()
            ViewAnimator.animate(ivBismillah).apply {
                fadeIn()
                duration(ANIMATION_IMAGE_DURATION)
            }.start()
        }
    }
}
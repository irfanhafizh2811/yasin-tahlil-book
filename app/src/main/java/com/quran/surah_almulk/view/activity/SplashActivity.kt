package com.quran.surah_almulk.view.activity

import android.content.Intent
import android.os.Bundle
import com.quran.surah_almulk.extension.common.clazz
import com.quran.surah_almulk.remote.CoreRemoteConfig
import org.koin.android.ext.android.inject

class SplashActivity : BaseActivity() {

    private val coreRemoteConfig by inject<CoreRemoteConfig>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coreRemoteConfig.remoteConfig.fetchAndActivate()
        startActivity(Intent(this@SplashActivity, clazz<MainSurahActivity>()))
        finish()
    }
}
package com.quran.surah_yasin.view.activity

import android.content.Intent
import android.os.Bundle
import com.quran.surah_yasin.extension.common.clazz
import com.quran.surah_yasin.remote.CoreRemoteConfig
import com.quran.surah_yasin.vm.OnBoardViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity() {

    private val coreRemoteConfig by inject<CoreRemoteConfig>()
    private val viewModel by viewModel<OnBoardViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coreRemoteConfig.remoteConfig.fetchAndActivate()
        viewModel.apply {
            user.observe(this@SplashActivity) {
                val screen = if (it.hasInitialize) clazz<MainQuranActivity>()
                else clazz<OnBoardActivity>()
                startActivity(Intent(this@SplashActivity, screen))
                finish()
            }
        }.getUser()
    }
}
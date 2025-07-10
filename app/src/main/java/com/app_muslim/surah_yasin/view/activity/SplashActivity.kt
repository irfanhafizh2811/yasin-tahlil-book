package com.app_muslim.surah_yasin.view.activity

import android.content.Intent
import android.os.Bundle
import com.app_muslim.surah_yasin.extension.common.clazz
import com.app_muslim.surah_yasin.remote.CoreRemoteConfig
import com.app_muslim.surah_yasin.vm.OnBoardViewModel
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
                startActivity(Intent(this@SplashActivity, clazz<SurahActivity>()))
                finish()
            }
        }.getUser()
    }
}
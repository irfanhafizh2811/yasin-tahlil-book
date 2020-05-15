package com.icaali.tasbeeh.view.activity

import android.os.Bundle
import com.github.florent37.viewanimator.ViewAnimator
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.view.visible
import com.icaali.tasbeeh.remote.CoreRemoteConfig
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject

class SplashActivity : BaseActivity() {

    private val coreRemoteConfig by inject<CoreRemoteConfig>()

    companion object {
        const val ANIMATION_IMAGE_DURATION = 1000L
        const val ANIMATION_TEXT_DURATION = 1000L
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
    }

}
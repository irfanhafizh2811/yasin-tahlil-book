package com.dzikir.tasbeeh.view

import android.animation.Animator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dzikir.tasbeeh.R
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.intentFor

class SplashActivity : BaseActivity() {

    companion object {
        const val ANIMATION_ASSET_JSON = "anim_bismillah.json"
        const val ANIMATION_SPEED = 1f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        lav_splash?.run {
            setAnimation(ANIMATION_ASSET_JSON)
            speed = ANIMATION_SPEED
            playAnimation()
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animator: Animator?) {}

                override fun onAnimationEnd(animator: Animator?) {
                    startActivity(intentFor<MainActivity>())
                    finish()
                }

                override fun onAnimationCancel(animator: Animator?) {}
                override fun onAnimationStart(animator: Animator?) {}
            })
        }
    }

}
package com.app_muslim.surah_yasin.utils

import android.os.CountDownTimer
import android.text.format.DateUtils
import com.app_muslim.surah_yasin.data.model.TimerEvent
import org.greenrobot.eventbus.EventBus

class TimerManager(private val eventBus: EventBus) {
    private var timer: CountDownTimer? = null
    private var minutesElapsed = 0
    private var isRunning = false

    // Use a large, safe duration (e.g. 100 years)
    private val maxDuration = 100L * 365 * 24 * 60 * 60 * 1000 // 100 years in millis

    fun start() {
        if (isRunning) return
        isRunning = true
        minutesElapsed = 0

        timer = object : CountDownTimer(
            maxDuration,
            10 * DateUtils.MINUTE_IN_MILLIS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                minutesElapsed += 10
                eventBus.post(TimerEvent(minutesElapsed))
            }

            override fun onFinish() {
                isRunning = false
            }
        }.start()
    }

    fun stop() {
        timer?.cancel()
        timer = null
        isRunning = false
    }
}
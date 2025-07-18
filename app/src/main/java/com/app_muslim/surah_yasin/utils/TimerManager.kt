package com.app_muslim.surah_yasin.utils

import android.os.CountDownTimer
import com.app_muslim.surah_yasin.data.model.TimerEvent
import com.google.common.eventbus.EventBus

class TimerManager(private val eventBus: EventBus) {

    private var timer: CountDownTimer? = null
    private var minutesElapsed = 0
    private var isRunning = false

    fun start() {
        if (isRunning) return
        isRunning = true
        minutesElapsed = 0

        timer = object : CountDownTimer(Long.MAX_VALUE, 60_000L) { // every 1 minute
            override fun onTick(millisUntilFinished: Long) {
                minutesElapsed += 1
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
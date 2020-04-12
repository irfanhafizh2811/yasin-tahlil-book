package com.dzikir.tasbeeh.app

import android.media.MediaPlayer
import androidx.multidex.MultiDexApplication
import org.koin.android.ext.android.inject

class TasbeehApp : MultiDexApplication() {

    private val mediaPlayer by inject<MediaPlayer>()

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

}
package com.icaali.tasbeeh.app

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.icaali.tasbeeh.deps.libraries
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TasbeehApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        FirebaseApp.initializeApp(this@TasbeehApp)
        startKoin {
            modules(libraries)
            androidContext(this@TasbeehApp)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
    }

}
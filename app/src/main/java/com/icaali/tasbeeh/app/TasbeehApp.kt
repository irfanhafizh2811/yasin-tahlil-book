package com.icaali.tasbeeh.app

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.icaali.tasbeeh.deps.libraries
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TasbeehApp : MultiDexApplication() {

    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

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
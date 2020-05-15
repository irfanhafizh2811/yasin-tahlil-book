package com.icaali.tasbeeh.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.activty.appInstalledOrNot
import com.icaali.tasbeeh.extension.activty.launchApp
import com.icaali.tasbeeh.extension.activty.openPlaystore
import com.icaali.tasbeeh.remote.SourceAppsRemoteConfig
import com.icaali.tasbeeh.view.adapter.DeveloperAdapter
import kotlinx.android.synthetic.main.activity_developer_application.*
import org.koin.android.ext.android.inject

class DeveloperAppsActivity : BaseActivity() {

    private val appRemoteConfig by inject<SourceAppsRemoteConfig>()
    private val adapter by lazy {
        DeveloperAdapter {
            with(it.packageId) {
                when {
                    appInstalledOrNot(this) -> launchApp(this)
                    else -> openPlaystore(this)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer_application)
        ivBack?.setOnClickListener { finish() }

        rvApplication?.adapter = adapter
        rvApplication?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter.run {
            apps = appRemoteConfig.developerApp.applications
            notifyDataSetChanged()
        }
    }

}
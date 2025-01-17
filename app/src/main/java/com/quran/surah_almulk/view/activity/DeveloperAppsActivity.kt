package com.quran.surah_almulk.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quran.surah_almulk.databinding.ActivityDeveloperApplicationBinding
import com.quran.surah_almulk.extension.activty.appInstalledOrNot
import com.quran.surah_almulk.extension.activty.launchApp
import com.quran.surah_almulk.extension.activty.openPlaystore
import com.quran.surah_almulk.remote.SourceAppsRemoteConfig
import com.quran.surah_almulk.view.adapter.DeveloperAdapter
import org.koin.android.ext.android.inject

class DeveloperAppsActivity : BaseActivity() {

    private lateinit var binding: ActivityDeveloperApplicationBinding
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
        binding = ActivityDeveloperApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            ivBack?.setOnClickListener { finish() }

            rvApplication?.adapter = adapter
            rvApplication?.layoutManager =
                LinearLayoutManager(this@DeveloperAppsActivity, LinearLayoutManager.VERTICAL, false)
            adapter.run {
                apps = appRemoteConfig.developerApp.applications
                notifyDataSetChanged()
            }
        }
    }
}
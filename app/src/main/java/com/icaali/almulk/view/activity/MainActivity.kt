package com.icaali.almulk.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.MobileAds
import com.icaali.almulk.R
import com.icaali.almulk.databinding.ActivityMainBinding
import com.icaali.almulk.extension.activty.openPlaystore
import com.icaali.almulk.extension.common.clazz
import com.icaali.almulk.extension.view.gone
import com.icaali.almulk.extension.view.visible
import com.icaali.almulk.data.preference.CounterPreference
import com.icaali.almulk.data.preference.LanguagePreference
import com.icaali.almulk.data.preference.SettingPreference
import com.icaali.almulk.utils.TasbeehConst
import com.icaali.almulk.view.adapter.TasbeehAdapter
import com.icaali.almulk.view.dialog.AddCustomDialog
import com.icaali.almulk.view.dialog.GuideMainDialog
import com.icaali.almulk.view.dialog.LanguageDialog
import com.icaali.almulk.view.dialog.MoreDialog
import com.icaali.almulk.vm.DhikrViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    companion object {
        private const val CHROME_PACKAGE_NAME = "com.android.chrome"
        private const val MYDHIKR_INSTAGRAM_URL = "https://www.instagram.com/mydhikr.apps/"
    }

    lateinit var binding: ActivityMainBinding

    //***************** public variable *****************
    val languagePreference by inject<LanguagePreference>()
    val counterPreference: CounterPreference by inject()
    //***************************************************

    //***************** private variable *****************
    private val settingPreference by inject<SettingPreference>()
    private val dhikrViewModel: DhikrViewModel by viewModel()
    private val addCustomDialog by lazy { AddCustomDialog(this) }
    private val moreDialog by lazy { MoreDialog(this, settingPreference) }
    private val mainGuideDialog by lazy { GuideMainDialog(this, guidePref) }
    private var hasShownGuide = false
    //***************************************************

    private val dhikrAdapter by lazy {
        TasbeehAdapter {
            startActivityTasbeeh(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            cvSubhanallah?.setOnClickListener {
                startActivityTasbeeh(TasbeehConst.SUBHANALLAH)
            }
            cvAlhamdulillah?.setOnClickListener {
                startActivityTasbeeh(TasbeehConst.ALHAMDULILLAH)
            }
            cvAllahuAkbar?.setOnClickListener {
                startActivityTasbeeh(TasbeehConst.ALLAHU_AKBAR)
            }
            cvAstaghfirullah?.setOnClickListener {
                startActivityTasbeeh(TasbeehConst.ASTAGHFIRULLAH)
            }
            cvLaailaahaillallah?.setOnClickListener {
                startActivityTasbeeh(TasbeehConst.LAILAHAILALLAH)
            }
            rvAddDhikr.apply {
                layoutManager =
                    LinearLayoutManager(this@MainActivity).apply { reverseLayout = true }
                adapter = dhikrAdapter
            }
            cvAddDhikr.setOnClickListener {
                addCustomDialog.setOnPositiveListener {
                    dhikrViewModel.insert(it)
                    if (!hasShownGuide) loadAdMobInterstitial()
                }.show()
            }
            cvDhikrMorning.setOnClickListener { onStartDhikrActivity(true) }
            cvDhikrEvening.setOnClickListener { onStartDhikrActivity(false) }
            dhikrViewModel.dhikrs.observe(this@MainActivity) {
                when {
                    it.isEmpty() -> {
                        rvAddDhikr.gone()
                        tvYourDhikr.gone()
                    }

                    else -> {
                        rvAddDhikr.visible()
                        tvYourDhikr.visible()
                        dhikrAdapter.submitList(it)
                    }
                }
                loadBanner(adViewContainer)
                MobileAds.openAdInspector(this@MainActivity) {
                    // Error will be non-null if ad inspector closed due to an error.
                }
            }

            llMore.setOnClickListener {
                moreDialog.apply {
                    setOnSelectedListener {
                        when (it) {
                            MoreDialog.Menu.LANGUAGE -> showLanguageDialog()
                            MoreDialog.Menu.RATING_AND_REVIEW -> {
                                openPlaystore(packageName)
                            }

                            MoreDialog.Menu.SHARE -> shareMyDhikr()
                            MoreDialog.Menu.INSTAGRAM -> openInstagramMyDhikr()
                        }
                    }
                    show()
                }
            }
            mDisposable.addAll(observeGuide(DHIKR_SECOND_DELAY, guidePref) {
                if (!mainGuideDialog.isShowingAll()) {
                    mainGuideDialog.apply {
                        onTapTargetListener = { addCustomDialog.show() }
                    }.show()
                    hasShownGuide = true
                }
            })
        }
    }

    private fun onStartDhikrActivity(isMorning: Boolean) {
        val intent = Intent(this, clazz<DhikrActivity>())
        intent.putExtra(DhikrActivity.DHIKR_INTENT_EXTRA, isMorning)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        sync()
    }

    private fun showLanguageDialog() {
        localeManager?.let {
            LanguageDialog(this, languagePreference, it).show()
        }
    }

    private fun shareMyDhikr() {
        val shareText =
            "Alhamdulillah ini aplikasi favorit saya yang selalu digunakan ketika melakukan Zikir. Namanya MyDhikr, Kamu perlu coba! https://play.google.com/store/apps/details?id=com.icaali.tasbeeh"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.label_share_text)))
    }

    private fun openInstagramMyDhikr() {
        val uri = Uri.parse(MYDHIKR_INSTAGRAM_URL)
        val builder = CustomTabsIntent.Builder().apply {
            setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.colorPrimaryDark))
            setShowTitle(true)
        }.build()
        builder.intent.data = uri
        val resolveInfoList =
            packageManager.queryIntentActivities(builder.intent, PackageManager.MATCH_DEFAULT_ONLY)
        resolveInfoList.forEach {
            val packageName = it.activityInfo.packageName
            if (packageName.equals(CHROME_PACKAGE_NAME, true)) {
                builder.intent.setPackage(CHROME_PACKAGE_NAME)
            }
        }
        builder.intent.data?.let { uri ->
            builder.launchUrl(this, uri)
        }
    }
}
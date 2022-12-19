package com.icaali.tasbeeh.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.MobileAds
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.activty.openPlaystore
import com.icaali.tasbeeh.extension.view.gone
import com.icaali.tasbeeh.extension.view.visible
import com.icaali.tasbeeh.preference.CounterPreference
import com.icaali.tasbeeh.preference.LanguagePreference
import com.icaali.tasbeeh.preference.SettingPreference
import com.icaali.tasbeeh.utils.Analytic
import com.icaali.tasbeeh.utils.TasbeehConst
import com.icaali.tasbeeh.view.adapter.TasbeehAdapter
import com.icaali.tasbeeh.view.dialog.AddCustomDialog
import com.icaali.tasbeeh.view.dialog.GuideMainDialog
import com.icaali.tasbeeh.view.dialog.LanguageDialog
import com.icaali.tasbeeh.view.dialog.MoreDialog
import com.icaali.tasbeeh.vm.DhikrViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.adViewContainer
import kotlinx.android.synthetic.main.activity_main.llMore
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    companion object {
        private const val CHROME_PACKAGE_NAME = "com.android.chrome"
        private const val MYDHIKR_INSTAGRAM_URL = "https://www.instagram.com/mydhikr.apps/"
    }

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
            logSelectContent(it.latin)
            startActivityTasbeeh(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cvSubhanallah?.setOnClickListener {
            logSelectContent(getString(R.string.text_latin_subhanallah))
            startActivityTasbeeh(TasbeehConst.SUBHANALLAH)
        }
        cvAlhamdulillah?.setOnClickListener {
            logSelectContent(getString(R.string.text_latin_alhamdulillah))
            startActivityTasbeeh(TasbeehConst.ALHAMDULILLAH)
        }
        cvAllahuAkbar?.setOnClickListener {
            logSelectContent(getString(R.string.text_latin_allahu_akbar))
            startActivityTasbeeh(TasbeehConst.ALLAHU_AKBAR)
        }
        cvAstaghfirullah?.setOnClickListener {
            logSelectContent(getString(R.string.text_latin_astaghfirullah))
            startActivityTasbeeh(TasbeehConst.ASTAGHFIRULLAH)
        }
        cvLaailaahaillallah?.setOnClickListener {
            logSelectContent(getString(R.string.text_latin_laailaahaillallah))
            startActivityTasbeeh(TasbeehConst.LAILAHAILALLAH)
        }
        rvAddDhikr.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply { reverseLayout = true }
            adapter = dhikrAdapter
        }

        cvAddDhikr?.setOnClickListener {
            addCustomDialog.setOnPositiveListener {
                logAdd(it.latin)
                dhikrViewModel.insert(it)
                if (!hasShownGuide) loadAdMobInterstitial()
            }.show()
        }
        cvDhikrMorning?.setOnClickListener { onStartDhikrActivity(true) }
        cvDhikrEvening?.setOnClickListener { onStartDhikrActivity(false) }
        dhikrViewModel.dhikrs.observe(this) {
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
            MobileAds.openAdInspector(this) {
                // Error will be non-null if ad inspector closed due to an error.
            }
        }

        llMore?.setOnClickListener {
            moreDialog.apply {
                setOnSelectedListener {
                    when (it) {
                        MoreDialog.Menu.LANGUAGE -> showLanguageDialog()
                        MoreDialog.Menu.RATING_AND_REVIEW -> {
                            openPlaystore(packageName)
                            logClick(Analytic.CLICK_RATING_AND_REVIEW)
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

    private fun onStartDhikrActivity(isMorning: Boolean) {
        if (isMorning) logSelectContent(R.string.analytic_morning)
        else logSelectContent(R.string.analytic_evening)
        startActivity(intentFor<DhikrActivity>(DhikrActivity.DHIKR_INTENT_EXTRA to isMorning))
    }

    override fun onResume() {
        super.onResume()
        sync()
    }

    private fun showLanguageDialog() {
        localeManager?.let {
            LanguageDialog(this, languagePreference, it, firebaseAnalytics).show()
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
        logClick(Analytic.CLICK_SHARE_APP)
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
        builder.launchUrl(this, builder.intent.data)
        logClick(Analytic.CLICK_DIRECT_INSTAGRAM)
    }
}
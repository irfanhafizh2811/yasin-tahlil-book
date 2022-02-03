package com.icaali.tasbeeh.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.activty.openPlaystore
import com.icaali.tasbeeh.extension.context.getDrawableCompat
import com.icaali.tasbeeh.extension.view.gone
import com.icaali.tasbeeh.extension.view.visible
import com.icaali.tasbeeh.preference.CounterPreference
import com.icaali.tasbeeh.preference.GuidePreference
import com.icaali.tasbeeh.preference.SettingPreference
import com.icaali.tasbeeh.view.Tasbeeh
import com.icaali.tasbeeh.view.adapter.DhikrAdapter
import com.icaali.tasbeeh.view.dialog.AddCustomDialog
import com.icaali.tasbeeh.view.dialog.GuideMainDialog
import com.icaali.tasbeeh.view.dialog.MoreDialog
import com.icaali.tasbeeh.vm.DhikrViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    companion object {
        private const val CHROME_PACKAGE_NAME = "com.android.chrome"
        private const val MYDHIKR_INSTAGRAM_URL = "https://www.instagram.com/mydhikr/"
    }

    private val counterPreference: CounterPreference by inject()
    private val settingPreference by inject<SettingPreference>()
    internal val dhikrViewModel: DhikrViewModel by viewModel()
    private val addCustomDialog by lazy { AddCustomDialog(this) }
    private val moreDialog by lazy { MoreDialog(this, settingPreference) }
    private val mainGuideDialog by lazy { GuideMainDialog(this, guidePref) }

    private val dhikrAdapter by lazy {
        DhikrAdapter {
            if (!guidePref.hasShownAddDhikr) startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.CUSTOM,
                    TasbeehActivity.TASBEEH_LATIN_EXTRA to it.latin,
                    TasbeehActivity.TASBEEH_DHIKR_EXTRA to it
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cvSubhanallah?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.SUBHANALLAH,
                    TasbeehActivity.TASBEEH_LATIN_EXTRA to getString(R.string.text_latin_subhanallah)
                )
            )
        }
        cvAlhamdulillah?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.ALHAMDULILLAH,
                    TasbeehActivity.TASBEEH_LATIN_EXTRA to getString(R.string.text_latin_alhamdulillah)
                )
            )
        }
        cvAllahuAkbar?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.ALLAHU_AKBAR,
                    TasbeehActivity.TASBEEH_LATIN_EXTRA to getString(R.string.text_latin_allahu_akbar)
                )
            )
        }
        cvAstaghfirullah?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.ASTAGHFIRULLAH,
                    TasbeehActivity.TASBEEH_LATIN_EXTRA to getString(R.string.text_latin_astaghfirullah)
                )
            )
        }
        cvLaailaahaillallah?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.LAILAHAILALLAH,
                    TasbeehActivity.TASBEEH_LATIN_EXTRA to getString(R.string.text_latin_laailaahaillallah)
                )
            )
        }
        rvAddDhikr.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply { reverseLayout = true }
            adapter = dhikrAdapter
        }

        cvAddDhikr?.setOnClickListener {
            if (!guidePref.hasShownAddDhikr)
                addCustomDialog.setOnPositiveListener {
                    dhikrViewModel.insert(it)
                }.show()
        }

        dhikrViewModel.dhikrs.observe(this, {
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
        })

        llMore?.setOnClickListener {
            moreDialog.apply {
                setOnSelectedListener {
                    when (it) {
                        MoreDialog.Menu.LANGUAGE -> {
                        }
                        MoreDialog.Menu.RATING_AND_REVIEW -> openPlaystore(packageName)
                        MoreDialog.Menu.SHARE -> shareMyDhikr()
                        MoreDialog.Menu.INSTAGRAM -> openInstagramMyDhikr()
                    }
                }
                show()
            }
        }
        mDisposable.addAll(observeGuide(DHIKR_SECOND_DELAY, guidePref) {
            if (!mainGuideDialog.isShowingAll()) mainGuideDialog.apply {
                onTapTargetListener = { addCustomDialog.show() }
            }.show()
        })
    }

    override fun onResume() {
        super.onResume()
        tvSubhanallahCount?.text =
            getString(R.string.label_counter_x, counterPreference.subhanallah)
        tvAlhamdulillahCount?.text =
            getString(R.string.label_counter_x, counterPreference.alhamdulillah)
        tvAllahuAkbarCount?.text =
            getString(R.string.label_counter_x, counterPreference.allahukkbar)
        tvAstaghfirullahCount?.text =
            getString(R.string.label_counter_x, counterPreference.astaghfirullah)
        tvLaailaahaillallahCount?.text =
            getString(R.string.label_counter_x, counterPreference.lailahailallah)

        ivSubhanallah?.setImageDrawable(
            getDrawableCompat(
                R.drawable.ic_subhanallah,
                android.R.color.black
            )
        )
        ivAlhamdulillah?.setImageDrawable(
            getDrawableCompat(
                R.drawable.ic_alhamdulillah,
                android.R.color.black
            )
        )
        ivAllahuAkbar?.setImageDrawable(
            getDrawableCompat(
                R.drawable.ic_allahu_akbar,
                android.R.color.black
            )
        )
        ivAstaghfirullah?.setImageDrawable(
            getDrawableCompat(
                R.drawable.ic_astagfirllah,
                android.R.color.black
            )
        )
        ivLaailaahaillallah?.setImageDrawable(
            getDrawableCompat(
                R.drawable.ic_laailaahaillallah,
                android.R.color.black
            )
        )
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
        builder.launchUrl(this, builder.intent.data)
    }
}
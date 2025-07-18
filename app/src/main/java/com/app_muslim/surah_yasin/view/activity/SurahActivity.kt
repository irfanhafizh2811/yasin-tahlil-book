package com.app_muslim.surah_yasin.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.gson.Gson
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.data.model.Surah
import com.app_muslim.surah_yasin.data.model.surah.SurahInterface
import com.app_muslim.surah_yasin.data.model.surah.SurahQuran
import com.app_muslim.surah_yasin.data.preference.LanguagePreference
import com.app_muslim.surah_yasin.data.preference.SettingPreference
import com.app_muslim.surah_yasin.data.preference.SurahPreference
import com.app_muslim.surah_yasin.databinding.ActivitySurahBinding
import com.app_muslim.surah_yasin.extension.common.clazz
import com.app_muslim.surah_yasin.extension.context.getColorCompat
import com.app_muslim.surah_yasin.extension.context.readJsonAssetToString
import com.app_muslim.surah_yasin.extension.view.gone
import com.app_muslim.surah_yasin.extension.view.visible
import com.app_muslim.surah_yasin.utils.FontSize
import com.app_muslim.surah_yasin.utils.TextUtils
import com.app_muslim.surah_yasin.view.adapter.SurahAdapter
import com.app_muslim.surah_yasin.view.dialog.LanguageDialog
import com.app_muslim.surah_yasin.vm.LanguageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SurahActivity : BaseActivity() {

    private var surahName: String = TextUtils.BLANK
    private var data: Surah? = null
    private val surahAdapter by lazy { SurahAdapter() }
    private var isExpanded = false
    private var isReadLast = false
    private lateinit var binding: ActivitySurahBinding
    private lateinit var surah: SurahInterface
    private lateinit var currentFontSize: FontSize

    private val appBarTools by lazy { binding.appBarTools }
    private val ivBack by lazy { appBarTools.findViewById<ImageView>(R.id.iv_back) }
    private val tvSurah by lazy { appBarTools.findViewById<TextView>(R.id.tv_surah) }
    private val llMore by lazy { appBarTools.findViewById<LinearLayout>(R.id.ll_more) }
    private val tvPlusSize by lazy { appBarTools.findViewById<TextView>(R.id.tvPlusSize) }
    private val tvPlusSizeSymbol by lazy { appBarTools.findViewById<TextView>(R.id.tvPlusSizeSymbol) }
    private val tvMinusSize by lazy { appBarTools.findViewById<TextView>(R.id.tvMinusSize) }
    private val tvMinusSizeSymbol by lazy { appBarTools.findViewById<TextView>(R.id.tvMinusSizeSymbol) }
    private val scLatin by lazy { appBarTools.findViewById<SwitchCompat>(R.id.scLatin) }
    private val scTranslate by lazy { appBarTools.findViewById<SwitchCompat>(R.id.scTranslate) }
    private val ivLanguage by lazy { appBarTools.findViewById<ImageView>(R.id.iv_language) }
    private val tvLanguage by lazy { appBarTools.findViewById<TextView>(R.id.tv_language) }

    private val surahPref by inject<SurahPreference>()
    private val languagePref by inject<LanguagePreference>()
    private val settingPreference by inject<SettingPreference>()
    private val languageViewModel by inject<LanguageViewModel>()
    private val reviewManager by lazy {
        ReviewManagerFactory.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurahBinding.inflate(layoutInflater)
        surahName = SurahQuran.YASIN.name
        isReadLast = intent.getBooleanExtra(READ_LAST_SURAH_INTENT_EXTRA, false)
        surah = com.app_muslim.surah_yasin.data.model.surah.SurahFactory.generate(
            SurahQuran.valueOf(surahName)
        )
        setContentView(binding.root)
        setSurahView()
        scrollLastRead()
        requestRatingReviewPlaystore()
    }

    private fun isMaxSize(): Boolean = currentFontSize == FontSize.HUGE
    private fun isMinSize(): Boolean = currentFontSize == FontSize.SMALL

    private fun setSurahView() = with(binding) {
        data = Gson().fromJson(readJsonAssetToString(surah.sourceJson), clazz<Surah>())
        observeViewModel()
        setupView()
        onUILabelColor()
    }

    private fun setupView() = with(binding) {
        rvSurah.also {
            val surahModel = com.app_muslim.surah_yasin.data.model.surah.SurahFactory.generate(
                SurahQuran.valueOf(surahName)
            )
            it.layoutManager = LinearLayoutManager(this@SurahActivity)
            it.adapter = surahAdapter.apply {
                onBindListener = { surah -> latestSurahPref(surah) }
                fontSize = FontSize.LARGE
                surahInterface = surahModel
                sync(data?.surah ?: listOf())
            }
        }
        progressBar.visible()
        ivBack.setOnClickListener { finish() }
        tvSurah.text = surahName.replace(TextUtils.UNDERSCORE, TextUtils.EMPTY_SPACE)
        llMore.setOnClickListener {
            if (!isExpanded) appBarTools.expand()
            else appBarTools.collapse()
            isExpanded = !isExpanded
        }
        tvPlusSize.setOnClickListener { onRaiseFont() }
        tvMinusSize.setOnClickListener { onLowerFont() }
        scLatin.setOnCheckedChangeListener { _, checked ->
            settingPreference.showQuranLatin = checked
            surahAdapter.apply {
                showLatinQuran = checked
            }.sync(data?.surah ?: listOf())
        }
        scTranslate.setOnCheckedChangeListener { _, checked ->
            settingPreference.showQuranTranslation = checked
            surahAdapter.apply {
                showTranslationQuran = checked
            }.sync(data?.surah ?: listOf())
        }
        ivLanguage.setOnClickListener {
            if (isExpanded) {
                appBarTools.expand()
                appBarTools.collapse()
                isExpanded = false
            }
            showLanguage()
        }
        tvLanguage.setOnClickListener {
            if (isExpanded) {
                appBarTools.expand()
                appBarTools.collapse()
                isExpanded = false
            }
            showLanguage()
        }
    }

    private fun observeViewModel() {
        languageViewModel.language.observe(this) {
            surahAdapter.language = it
            surahAdapter.sync(data?.surah ?: listOf())
        }
        languageViewModel.getLanguage()
    }

    private fun showLanguage() {
        LanguageDialog(this, languagePref.language)
            .selectedLanguage {
                languageViewModel.setupLanguage(it)
                surahAdapter.language = it
                surahAdapter.sync(data?.surah ?: listOf())
            }
            .show()
    }

    private fun scrollLastRead() = with(binding) {
        rvSurah.also {
            if (isReadLast && surahPref.lastReadVerse < surahAdapter.verses.size) {
                it.scrollToPosition(surahPref.lastReadVerse)
                lifecycleScope.launch {
                    delay(LAST_READ_DELAY_MILLIS)
                    Log.d("Surah Last Read:", true.toString())
                    surahAdapter.enableLastRead = true
                    progressBar.gone()
                }
            } else {
                surahAdapter.enableLastRead = false
                progressBar.gone()
            }
        }
    }

    private fun onUILabelColor() {
        surahAdapter.fontSize = settingPreference.fontSize
        val textColorPlus = getColorCompat(
            if (settingPreference.fontSize == FontSize.HUGE) R.color.themeUnselected
            else R.color.colorAccentPurple
        )
        val textColorMinus = getColorCompat(
            if (settingPreference.fontSize == FontSize.SMALL) R.color.themeUnselected
            else R.color.colorAccentPurple
        )
        tvPlusSize.setTextColor(textColorPlus)
        tvPlusSizeSymbol.setTextColor(textColorPlus)
        tvMinusSize.setTextColor(textColorMinus)
        tvMinusSizeSymbol.setTextColor(textColorMinus)
        scLatin.isChecked = settingPreference.showQuranLatin
        scTranslate.isChecked = settingPreference.showQuranTranslation
    }

    private fun onUpdateFont(isRaise: Boolean) {
        if (isMaxSize() && isRaise) return
        if (isMinSize() && !isRaise) return
        onUILabelColor()
        surahAdapter.sync(data?.surah ?: listOf())
    }

    private fun onRaiseFont() {
        currentFontSize = settingPreference.fontSize
        val fontSize = raiseFont(surahAdapter.fontSize.name)
        settingPreference.fontSize = fontSize
        surahAdapter.fontSize = fontSize
        onUpdateFont(true)
    }

    private fun onLowerFont() {
        currentFontSize = settingPreference.fontSize
        val fontSize = lowerFont(surahAdapter.fontSize.name)
        settingPreference.fontSize = lowerFont(surahAdapter.fontSize.name)
        surahAdapter.fontSize = fontSize
        onUpdateFont(false)
    }

    private fun raiseFont(fontSize: String): FontSize = when (FontSize.valueOf(fontSize)) {
        FontSize.HUGE -> FontSize.HUGE
        FontSize.LARGE -> FontSize.HUGE
        FontSize.REGULAR -> FontSize.LARGE
        FontSize.SMALL -> FontSize.REGULAR
    }

    private fun lowerFont(fontSize: String): FontSize = when (FontSize.valueOf(fontSize)) {
        FontSize.SMALL -> FontSize.SMALL
        FontSize.REGULAR -> FontSize.SMALL
        FontSize.LARGE -> FontSize.REGULAR
        FontSize.HUGE -> FontSize.LARGE
    }

    private fun latestSurahPref(surah: Pair<SurahInterface, Int>) = with(surah) {
        val numberAyah = surah.second
        if (numberAyah < first.verses) {
            surahPref.anyReadSurah = true
            surahPref.lastReadSurah =
                first.surahQuran.name.replace(TextUtils.UNDERSCORE, TextUtils.EMPTY_SPACE)
            surahPref.lastReadVerse = numberAyah - 1
        } else {
            surahPref.anyReadSurah = false
            surahPref.lastReadSurah = TextUtils.BLANK
            surahPref.lastReadVerse = 0
        }
    }

    override fun onBackPressed() {
        if (settingPreference.noHasSubmitRating) {
            requestRatingReviewPlaystore()
            return
        }
        super.onBackPressed()
    }

    private fun requestRatingReviewPlaystore() {
        if (settingPreference.noHasSubmitRating) {
            val request = reviewManager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = reviewManager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener {
                        settingPreference.noHasSubmitRating = false
                    }
                } else {
                    task.exception?.let { it.printStackTrace() }
                }
            }
        }
    }

    companion object {
        const val SURAH_INTENT_EXTRA = "SURAH_INTENT_EXTRA"
        const val READ_LAST_SURAH_INTENT_EXTRA = "READ_LAST_SURAH_INTENT_EXTRA"
        const val LAST_READ_DELAY_MILLIS = 300L
    }
}
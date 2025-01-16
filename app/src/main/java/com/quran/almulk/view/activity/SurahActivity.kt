package com.quran.almulk.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.quran.almulk.R
import com.quran.almulk.databinding.ActivitySurahBinding
import com.quran.almulk.extension.common.clazz
import com.quran.almulk.extension.context.getColorCompat
import com.quran.almulk.extension.context.readJsonAssetToString
import com.quran.almulk.extension.view.gone
import com.quran.almulk.extension.view.visible
import com.quran.almulk.model.Surah
import com.quran.almulk.data.preference.SettingPreference
import com.quran.almulk.data.preference.SurahPreference
import com.quran.almulk.utils.FontSize
import com.quran.almulk.utils.TextUtils
import com.quran.almulk.view.adapter.SurahAdapter
import com.quran.almulk.view.surah.SurahFactory
import com.quran.almulk.view.surah.SurahInterface
import com.quran.almulk.view.surah.SurahQuran
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
    private val ivBack by lazy { appBarTools.findViewById<ImageView>(R.id.ivBack) }
    private val tvSurah by lazy { appBarTools.findViewById<TextView>(R.id.tvSurah) }
    private val llMore by lazy { appBarTools.findViewById<LinearLayout>(R.id.llMore) }
    private val tvPlusSize by lazy { appBarTools.findViewById<TextView>(R.id.tvPlusSize) }
    private val tvPlusSizeSymbol by lazy { appBarTools.findViewById<TextView>(R.id.tvPlusSizeSymbol) }
    private val tvMinusSize by lazy { appBarTools.findViewById<TextView>(R.id.tvMinusSize) }
    private val tvMinusSizeSymbol by lazy { appBarTools.findViewById<TextView>(R.id.tvMinusSizeSymbol) }
    private val scLatin by lazy { appBarTools.findViewById<SwitchCompat>(R.id.scLatin) }
    private val scTranslate by lazy { appBarTools.findViewById<SwitchCompat>(R.id.scTranslate) }

    private val surahPref by inject<SurahPreference>()
    private val settingPreference by inject<SettingPreference>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurahBinding.inflate(layoutInflater)
        surahName = intent.getStringExtra(SURAH_INTENT_EXTRA)
            ?.replace(TextUtils.EMPTY_SPACE, TextUtils.UNDERSCORE) ?: SurahQuran.AL_MULK.name
        isReadLast = intent.getBooleanExtra(READ_LAST_SURAH_INTENT_EXTRA, false)
        surah = SurahFactory.generate(SurahQuran.valueOf(surahName))
        setContentView(binding.root)
        setSurahView()
        scrollLastRead()
    }

    private fun isMaxSize(): Boolean = currentFontSize == FontSize.HUGE
    private fun isMinSize(): Boolean = currentFontSize == FontSize.SMALL

    private fun setSurahView() = with(binding) {
        data = Gson().fromJson(readJsonAssetToString(surah.sourceJson), clazz<Surah>())
        rvSurah.also {
            val surahModel = SurahFactory.generate(SurahQuran.valueOf(surahName))
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
        onUILabelColor()
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

    companion object {
        const val SURAH_INTENT_EXTRA = "SURAH_INTENT_EXTRA"
        const val READ_LAST_SURAH_INTENT_EXTRA = "READ_LAST_SURAH_INTENT_EXTRA"
        const val LAST_READ_DELAY_MILLIS = 300L
    }
}
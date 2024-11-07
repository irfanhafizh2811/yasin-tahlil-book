package com.icaali.almulk.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.icaali.almulk.R
import com.icaali.almulk.databinding.ActivitySurahBinding
import com.icaali.almulk.extension.common.clazz
import com.icaali.almulk.extension.context.readJsonAssetToString
import com.icaali.almulk.extension.view.gone
import com.icaali.almulk.extension.view.visible
import com.icaali.almulk.model.Surah
import com.icaali.almulk.preference.SurahPreference
import com.icaali.almulk.utils.FontSize
import com.icaali.almulk.utils.TextUtils
import com.icaali.almulk.view.adapter.SurahAdapter
import com.icaali.almulk.view.surah.SurahFactory
import com.icaali.almulk.view.surah.SurahInterface
import com.icaali.almulk.view.surah.SurahQuran
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

    private val appBarTools by lazy { binding.appBarTools }
    private val ivBack by lazy { appBarTools.findViewById<ImageView>(R.id.ivBack) }
    private val tvSurah by lazy { appBarTools.findViewById<TextView>(R.id.tvSurah) }
    private val llMore by lazy { appBarTools.findViewById<LinearLayout>(R.id.llMore) }

    private val surahPref by inject<SurahPreference>()

    companion object {
        const val SURAH_INTENT_EXTRA = "SURAH_INTENT_EXTRA"
        const val READ_LAST_SURAH_INTENT_EXTRA = "READ_LAST_SURAH_INTENT_EXTRA"

        const val LAST_READ_DELAY_MILLIS = 300L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurahBinding.inflate(layoutInflater)
        surahName = intent.getStringExtra(SURAH_INTENT_EXTRA)
            ?.replace(TextUtils.EMPTY_SPACE, TextUtils.UNDERSCORE) ?: SurahQuran.AL_MULK.name
        isReadLast = intent.getBooleanExtra(READ_LAST_SURAH_INTENT_EXTRA, false)
        val surah = SurahFactory.generate(SurahQuran.valueOf(surahName))
        setContentView(binding.root)
        with(binding) {
            progressBar.visible()
            data = Gson().fromJson(readJsonAssetToString(surah.sourceJson), clazz<Surah>())
            ivBack.setOnClickListener { finish() }
            tvSurah.text = surahName.replace(TextUtils.UNDERSCORE, TextUtils.EMPTY_SPACE)
            llMore.setOnClickListener {
                if (!isExpanded) appBarTools.expand()
                else appBarTools.collapse()
                isExpanded = !isExpanded
            }
            setSurahView()
        }
        scrollLastRead()
    }

    private fun setSurahView() = binding.rvSurah.also {
        val surahModel = SurahFactory.generate(SurahQuran.valueOf(surahName))
        it.layoutManager = LinearLayoutManager(this)
        it.adapter = surahAdapter.apply {
            onBindListener = { surah -> latestSurahPref(surah) }
            fontSize = FontSize.LARGE
            surahInterface = surahModel
            sync(data?.surah ?: listOf())
        }
    }

    private fun scrollLastRead() = with(binding) {
        rvSurah.also {
            if (isReadLast && surahPref.lastReadVerse < surahAdapter.verses.size) {
                it.scrollToPosition(surahPref.lastReadVerse)
                lifecycleScope.launch {
                    delay(LAST_READ_DELAY_MILLIS)
                    Log.d("Surah Last Read:", true.toString())
                    surahAdapter.enableLastRead = true
                    progressBar?.gone()
                }
            } else {
                surahAdapter.enableLastRead = false
                progressBar.gone()
            }
        }
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
}
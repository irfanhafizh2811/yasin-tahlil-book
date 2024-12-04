package com.icaali.almulk.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.icaali.almulk.R
import com.icaali.almulk.databinding.ActivityMainSurahBinding
import com.icaali.almulk.extension.common.clazz
import com.icaali.almulk.preference.SurahPreference
import com.icaali.almulk.utils.TextUtils
import com.icaali.almulk.view.adapter.MainSurahAdapter
import com.icaali.almulk.view.surah.SurahFactory
import com.icaali.almulk.view.surah.SurahQuran
import org.koin.android.ext.android.inject
import java.util.Locale

class MainSurahActivity : BaseActivity() {

    private val surahAdapter by lazy {
        MainSurahAdapter {
            startActivitySurah(it.surahQuran.name)
        }
    }
    private val surah = SurahFactory.generate(SurahQuran.AL_MULK)
    private val surahPreference by inject<SurahPreference>()
    private lateinit var binding: ActivityMainSurahBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainSurahBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val surahName = surah.surahQuran.name.replace(TextUtils.UNDERSCORE, TextUtils.EMPTY_SPACE)
        with(binding) {
//            tvTitleAlMulk.text = getString(R.string.label_surah_al_mulk)
            tvArabicSurah.text = surah.arabic
//            tvTitleAlMulkMean.text = getString(R.string.label_surah_mean_al_mulk)
            cvRead.setOnClickListener { startActivitySurah(SurahQuran.AL_MULK.name) }
        }
        updateLastRead()
        setOtherSurahView()
    }

    private fun setOtherSurahView() = binding.rvAddDhikr.apply {
        layoutManager = LinearLayoutManager(this@MainSurahActivity)
        adapter = surahAdapter.also {
            it.listSurahInterface = SurahFactory.listSurah
            it.notifyItemInserted(it.listSurahInterface.size)
        }
    }

    private fun startActivitySurah(surah: String, readLast: Boolean = false) {
        val intent = Intent(this, clazz<SurahActivity>())
        intent.putExtra(SurahActivity.SURAH_INTENT_EXTRA, surah)
        intent.putExtra(SurahActivity.READ_LAST_SURAH_INTENT_EXTRA, readLast)
        startActivity(intent)
    }

    private fun updateLastRead() = with(binding) {
        cvLastRead.isVisible = surahPreference.anyReadSurah
        if (surahPreference.anyReadSurah) {
            var surah = surahPreference.lastReadSurah.lowercase()
            surah = surah.trim().split("\\s+".toRegex())
                .map { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
                .joinToString(" ")
            val numberAyah = surahPreference.lastReadVerse + 1
            tvLastReadSurah.text = surah
            tvLastReadVerse.text = getString(R.string.label_number_of_ayah, numberAyah)
            cvContinue.setOnClickListener {
                startActivitySurah(
                    surahPreference.lastReadSurah,
                    true
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateLastRead()
    }
}
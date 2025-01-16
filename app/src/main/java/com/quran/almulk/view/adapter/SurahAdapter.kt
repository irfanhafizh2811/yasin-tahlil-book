package com.quran.almulk.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quran.almulk.databinding.ItemSurahVerseBinding
import com.quran.almulk.model.Verse
import com.quran.almulk.utils.FontSize
import com.quran.almulk.view.holder.SurahHolder
import com.quran.almulk.view.surah.SurahFactory
import com.quran.almulk.view.surah.SurahInterface
import com.quran.almulk.view.surah.SurahQuran

class SurahAdapter : RecyclerView.Adapter<SurahHolder>() {

    lateinit var fontSize: FontSize
    var surahHolder: SurahHolder? = null
    var verses: List<Verse> = arrayListOf()
    var surahInterface: SurahInterface = SurahFactory.generate(SurahQuran.AL_MULK)
    var onBindListener: ((Pair<SurahInterface, Int>) -> Unit)? = null
    var enableLastRead = false
    var showLatinQuran = false
    var showTranslationQuran = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahHolder = SurahHolder(
        ItemSurahVerseBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        surahInterface
    )

    override fun onBindViewHolder(holder: SurahHolder, position: Int) {
        surahHolder = holder.bind(
            verse = verses[position],
            fontSize = fontSize,
            posItem = position,
            visibleLatinQuran = showLatinQuran,
            visibleTranslationQuran = showTranslationQuran,
        )
        if (enableLastRead) {
            Log.d("Surah Last Read:", "Start Bind $position")
            onBindListener?.invoke(Pair(surahInterface, position))
        }
    }

    override fun getItemCount(): Int = verses.size

    fun sync(verses: List<Verse>): SurahAdapter {
        this.verses = verses
        notifyItemRangeChanged(0, verses.size)
        return this
    }
}
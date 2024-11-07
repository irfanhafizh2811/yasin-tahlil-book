package com.icaali.almulk.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.icaali.almulk.databinding.ItemSurahVerseBinding
import com.icaali.almulk.model.Verse
import com.icaali.almulk.utils.FontSize
import com.icaali.almulk.view.holder.SurahHolder
import com.icaali.almulk.view.surah.SurahFactory
import com.icaali.almulk.view.surah.SurahInterface
import com.icaali.almulk.view.surah.SurahQuran

class SurahAdapter : RecyclerView.Adapter<SurahHolder>() {

    lateinit var fontSize: FontSize
    var surahHolder: SurahHolder? = null
    var verses: List<Verse> = arrayListOf()
    var surahInterface: SurahInterface = SurahFactory.generate(SurahQuran.AL_MULK)
    var onBindListener: ((Pair<SurahInterface, Int>) -> Unit)? = null
    var enableLastRead = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahHolder = SurahHolder(
        ItemSurahVerseBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        surahInterface
    )

    override fun onBindViewHolder(holder: SurahHolder, position: Int) {
        surahHolder = holder.bind(verses[position], fontSize, position)
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
package com.quran.surah_yasin.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quran.surah_yasin.data.model.User
import com.quran.surah_yasin.data.model.Verse
import com.quran.surah_yasin.data.model.surah.SurahFactory
import com.quran.surah_yasin.data.model.surah.SurahInterface
import com.quran.surah_yasin.data.model.surah.SurahQuran
import com.quran.surah_yasin.databinding.ItemSurahVerseBinding
import com.quran.surah_yasin.utils.FontSize
import com.quran.surah_yasin.view.holder.SurahHolder

class SurahAdapter : RecyclerView.Adapter<SurahHolder>() {

    lateinit var fontSize: FontSize
    var surahHolder: SurahHolder? = null
    var verses: List<Verse> = arrayListOf()
    var surahInterface: SurahInterface = SurahFactory.generate(SurahQuran.AL_MULK)
    var onBindListener: ((Pair<SurahInterface, Int>) -> Unit)? = null
    var enableLastRead = false
    var showLatinQuran = false
    var showTranslationQuran = false
    var user: User = User()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahHolder = SurahHolder(
        ItemSurahVerseBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        surahInterface
    )

    override fun onBindViewHolder(holder: SurahHolder, position: Int) {
        surahHolder = holder.bind(
            surahInterface,
            user = user,
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
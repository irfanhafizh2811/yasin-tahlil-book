package com.quran.surah_almulk.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quran.surah_almulk.data.model.User
import com.quran.surah_almulk.data.model.surah.SurahInterface
import com.quran.surah_almulk.databinding.ItemMainSurahBinding
import com.quran.surah_almulk.view.holder.MainSurahHolder

class MainSurahAdapter(val onClickListener: (SurahInterface) -> Unit) :
    RecyclerView.Adapter<MainSurahHolder>() {

    var listSurahInterface: List<SurahInterface> = listOf()
    var user = User()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainSurahHolder =
        MainSurahHolder(
            ItemMainSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClickListener
        )

    override fun onBindViewHolder(holder: MainSurahHolder, position: Int) {
        holder.bind(user, listSurahInterface[position])
    }

    override fun getItemCount(): Int = listSurahInterface.size
}
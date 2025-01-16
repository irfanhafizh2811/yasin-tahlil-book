package com.quran.almulk.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quran.almulk.databinding.ItemMainSurahBinding
import com.quran.almulk.view.holder.MainSurahHolder
import com.quran.almulk.view.surah.SurahInterface

class MainSurahAdapter(val onClickListener: (SurahInterface) -> Unit) :
    RecyclerView.Adapter<MainSurahHolder>() {

    var listSurahInterface: List<SurahInterface> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainSurahHolder =
        MainSurahHolder(
            ItemMainSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClickListener
        )

    override fun onBindViewHolder(holder: MainSurahHolder, position: Int) {
        holder.bind(listSurahInterface[position])
    }

    override fun getItemCount(): Int = listSurahInterface.size
}
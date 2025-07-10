package com.app_muslim.surah_yasin.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app_muslim.surah_yasin.data.model.User
import com.app_muslim.surah_yasin.data.model.surah.SurahInterface
import com.app_muslim.surah_yasin.databinding.ItemMainSurahBinding
import com.app_muslim.surah_yasin.view.holder.MainSurahHolder

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
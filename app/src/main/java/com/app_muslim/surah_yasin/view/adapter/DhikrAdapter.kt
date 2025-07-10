package com.app_muslim.surah_yasin.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app_muslim.surah_yasin.databinding.ItemDhikrBinding
import com.app_muslim.surah_yasin.data.model.Dhikr
import com.app_muslim.surah_yasin.utils.FontSize
import com.app_muslim.surah_yasin.view.holder.DhikrHolder

class DhikrAdapter : RecyclerView.Adapter<DhikrHolder>() {

    private lateinit var binding: ItemDhikrBinding
    var dhikrs: ArrayList<Dhikr> = arrayListOf()
    lateinit var fontSize: FontSize
    var dhikrHolder: DhikrHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DhikrHolder {
        binding = ItemDhikrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DhikrHolder(binding)
    }

    override fun onBindViewHolder(holder: DhikrHolder, position: Int) {
        dhikrHolder = holder.bind(dhikrs[position], fontSize)
    }

    override fun getItemCount(): Int = dhikrs.size

    fun sync(dhikrs: ArrayList<Dhikr>): DhikrAdapter {
        this.dhikrs = dhikrs
        notifyItemRangeChanged(0, dhikrs.size)
        return this
    }
}
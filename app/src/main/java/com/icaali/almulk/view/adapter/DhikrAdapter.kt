package com.icaali.almulk.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.icaali.almulk.databinding.ItemDhikrBinding
import com.icaali.almulk.model.Dhikr
import com.icaali.almulk.utils.FontSize
import com.icaali.almulk.view.holder.DhikrHolder

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
package com.quran.almulk.view.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quran.almulk.data.database.entity.TasbeehEntity
import com.quran.almulk.databinding.ItemTasbeehBinding

class TasbeehHolder(private val binding: ItemTasbeehBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(onClickListener: (TasbeehEntity) -> Unit, dhikr: TasbeehEntity) {
        val (_, _, latin, count) = dhikr
        with(binding) {
            tvDhikrLatin.text = latin
            tvDhikrCount.text = "$count".plus("x")
            cvDhikr.setOnClickListener { onClickListener(dhikr) }
        }
    }

    companion object {
        fun create(parent: ViewGroup): TasbeehHolder {
            val binding = ItemTasbeehBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TasbeehHolder(binding)
        }
    }
}
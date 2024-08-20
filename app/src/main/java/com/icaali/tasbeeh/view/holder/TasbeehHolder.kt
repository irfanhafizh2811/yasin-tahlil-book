package com.icaali.tasbeeh.view.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.icaali.tasbeeh.database.table.Tasbeeh
import com.icaali.tasbeeh.databinding.ItemTasbeehBinding

class TasbeehHolder(private val binding: ItemTasbeehBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(onClickListener: (Tasbeeh) -> Unit, dhikr: Tasbeeh) {
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
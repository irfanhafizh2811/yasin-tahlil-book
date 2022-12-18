package com.icaali.tasbeeh.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.icaali.tasbeeh.database.table.Tasbeeh
import com.icaali.tasbeeh.view.holder.TasbeehHolder

class TasbeehAdapter(val onClickListener: (Tasbeeh) -> Unit) :
    ListAdapter<Tasbeeh, TasbeehHolder>(DhikrComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasbeehHolder =
        TasbeehHolder.create(parent)

    override fun onBindViewHolder(holder: TasbeehHolder, position: Int) =
        holder.bind(onClickListener, getItem(position))

    class DhikrComparator : DiffUtil.ItemCallback<Tasbeeh>() {
        override fun areItemsTheSame(oldItem: Tasbeeh, newItem: Tasbeeh): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Tasbeeh, newItem: Tasbeeh): Boolean {
            return oldItem.id == newItem.id
        }
    }

}
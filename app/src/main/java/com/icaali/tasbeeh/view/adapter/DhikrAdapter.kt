package com.icaali.tasbeeh.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.icaali.tasbeeh.database.table.Dhikr
import com.icaali.tasbeeh.view.holder.DhikrHolder

class DhikrAdapter : ListAdapter<Dhikr, DhikrHolder>(DhikrComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DhikrHolder =
        DhikrHolder.create(parent)

    override fun onBindViewHolder(holder: DhikrHolder, position: Int) =
        holder.bind(getItem(position))

    class DhikrComparator : DiffUtil.ItemCallback<Dhikr>() {
        override fun areItemsTheSame(oldItem: Dhikr, newItem: Dhikr): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Dhikr, newItem: Dhikr): Boolean {
            return oldItem.id == newItem.id
        }
    }

}
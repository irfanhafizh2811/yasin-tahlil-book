package com.quran.almulk.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.quran.almulk.data.database.entity.TasbeehEntity
import com.quran.almulk.view.holder.TasbeehHolder

class TasbeehAdapter(val onClickListener: (TasbeehEntity) -> Unit) :
    ListAdapter<TasbeehEntity, TasbeehHolder>(DhikrComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasbeehHolder =
        TasbeehHolder.create(parent)

    override fun onBindViewHolder(holder: TasbeehHolder, position: Int) =
        holder.bind(onClickListener, getItem(position))

    class DhikrComparator : DiffUtil.ItemCallback<TasbeehEntity>() {
        override fun areItemsTheSame(oldItem: TasbeehEntity, newItem: TasbeehEntity): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: TasbeehEntity, newItem: TasbeehEntity): Boolean {
            return oldItem.id == newItem.id
        }
    }

}
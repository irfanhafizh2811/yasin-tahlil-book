package com.icaali.tasbeeh.view.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.database.table.Dhikr
import kotlinx.android.synthetic.main.item_dhikr.view.*

class DhikrHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(dhikr: Dhikr) {
        val (_, _, latin, count) = dhikr
        with(itemView) {
            tvDhikrLatin.text = latin
            tvDhikrCount.text = count.toString()
        }
    }

    companion object {
        fun create(parent: ViewGroup): DhikrHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dhikr, parent, false)
            return DhikrHolder(view)
        }
    }
}
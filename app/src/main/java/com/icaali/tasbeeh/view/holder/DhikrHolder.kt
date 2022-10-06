package com.icaali.tasbeeh.view.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.database.table.Dhikr
import kotlinx.android.synthetic.main.item_dhikr.view.*

class DhikrHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(onClickListener: (Dhikr) -> Unit, dhikr: Dhikr) {
        val (_, _, latin, count) = dhikr
        with(itemView) {
            tvDhikrLatin.text = latin
            tvDhikrCount.text = "$count".plus("x")
            cvDhikr.setOnClickListener { onClickListener(dhikr) }
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
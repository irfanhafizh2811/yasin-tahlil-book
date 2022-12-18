package com.icaali.tasbeeh.view.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.database.table.Tasbeeh
import kotlinx.android.synthetic.main.item_tasbeeh.view.*

class TasbeehHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(onClickListener: (Tasbeeh) -> Unit, dhikr: Tasbeeh) {
        val (_, _, latin, count) = dhikr
        with(itemView) {
            tvDhikrLatin.text = latin
            tvDhikrCount.text = "$count".plus("x")
            cvDhikr.setOnClickListener { onClickListener(dhikr) }
        }
    }

    companion object {
        fun create(parent: ViewGroup): TasbeehHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tasbeeh, parent, false)
            return TasbeehHolder(view)
        }
    }
}
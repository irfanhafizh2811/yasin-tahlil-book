package com.icaali.tasbeeh.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.model.Dhikr
import com.icaali.tasbeeh.utils.FontSize
import com.icaali.tasbeeh.view.holder.DhikrHolder

class DhikrAdapter : RecyclerView.Adapter<DhikrHolder>() {

    var dhikrs: ArrayList<Dhikr> = arrayListOf()
    lateinit var fontSize: FontSize
    var analytics: FirebaseAnalytics? = null
    var dhikrHolder: DhikrHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DhikrHolder = DhikrHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_dhikr, parent, false),
        analytics
    )

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
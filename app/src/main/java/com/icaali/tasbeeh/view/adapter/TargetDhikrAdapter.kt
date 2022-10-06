package com.icaali.tasbeeh.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.context.getColorCompat
import com.icaali.tasbeeh.extension.context.getDrawableCompat
import com.icaali.tasbeeh.model.TargetDhikr
import kotlinx.android.synthetic.main.item_target_dhikr.view.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColor

class TargetDhikrAdapter(private val onTargetClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<TargetDhikrAdapter.TargetDhikrViewHolder>() {

    private var targetDhikr = mutableListOf<TargetDhikr>(
        TargetDhikr("3x", 3),
        TargetDhikr("33x", 33),
        TargetDhikr("100x", 100),
        TargetDhikr("1000x", 1000)
    )

    class TargetDhikrViewHolder(
        val view: View,
        private val onItemClickListener: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        fun bind(target: TargetDhikr) {
            with(view) {
                tvTargetDhikr.text = target.name
                when (target.isSelected) {
                    true -> selected(this)
                    else -> unselected(this)
                }
                clTargetDhikr.setOnClickListener {
                    onItemClickListener.invoke(target.count)
                }
            }
        }

        private fun selected(view: View) {
            with(view) {
                tvTargetDhikr.run {
                    backgroundDrawable = context.getDrawableCompat(R.drawable.bg_btn_negative)
                    textColor = context.getColorCompat(android.R.color.white)
                }
            }
        }

        private fun unselected(view: View) {
            with(view) {
                tvTargetDhikr.run {
                    backgroundDrawable = context.getDrawableCompat(R.drawable.bg_unselected_grey)
                    textColor = context.getColorCompat(android.R.color.black)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TargetDhikrViewHolder =
        TargetDhikrViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_target_dhikr,
                parent, false
            )
        ) { targetCount ->
            selectedTarget(targetCount)
            onTargetClickListener.invoke(targetCount)
        }

    fun selectedTarget(targetCount: Int) {
        targetDhikr.forEachIndexed { index, targetDhikr ->
            targetDhikr.isSelected = targetDhikr.count == targetCount
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = targetDhikr.size

    override fun onBindViewHolder(holder: TargetDhikrViewHolder, position: Int) {
        holder.bind(targetDhikr[position])
    }
}
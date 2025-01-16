package com.quran.almulk.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quran.almulk.R
import com.quran.almulk.databinding.ItemTargetDhikrBinding
import com.quran.almulk.extension.context.getColorCompat
import com.quran.almulk.model.TargetDhikr

class TargetDhikrAdapter(private val onTargetClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<TargetDhikrAdapter.TargetDhikrViewHolder>() {

    private lateinit var binding: ItemTargetDhikrBinding
    private var targetDhikr = mutableListOf<TargetDhikr>(
        TargetDhikr("3x", 3),
        TargetDhikr("33x", 33),
        TargetDhikr("100x", 100),
        TargetDhikr("1000x", 1000)
    )

    class TargetDhikrViewHolder(
        private val binding: ItemTargetDhikrBinding,
        private val onItemClickListener: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(target: TargetDhikr) {
            with(binding) {
                tvTargetDhikr.text = target.name
                when (target.isSelected) {
                    true -> selected()
                    else -> unselected()
                }
                clTargetDhikr.setOnClickListener {
                    onItemClickListener.invoke(target.count)
                }
            }
        }

        private fun selected() {
            with(binding) {
                tvTargetDhikr.run {
                    setBackgroundResource(R.drawable.bg_btn_negative)
                    setTextColor(context.getColorCompat(android.R.color.white))
                }
            }
        }

        private fun unselected() {
            with(binding) {
                tvTargetDhikr.run {
                    setBackgroundResource(R.drawable.bg_unselected_grey)
                    setTextColor(context.getColorCompat(android.R.color.black))
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TargetDhikrViewHolder {
        binding = ItemTargetDhikrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TargetDhikrViewHolder(binding) { targetCount ->
            selectedTarget(targetCount)
            onTargetClickListener.invoke(targetCount)
        }
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
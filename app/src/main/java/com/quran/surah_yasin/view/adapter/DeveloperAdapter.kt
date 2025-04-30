package com.quran.surah_yasin.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quran.surah_yasin.databinding.ItemApplicationBinding
import com.quran.surah_yasin.extension.glide.loadFromUrl
import com.quran.surah_yasin.data.model.Application

class DeveloperAdapter(private val onItemClickListener: (Application) -> Unit) :
    RecyclerView.Adapter<DeveloperAdapter.DeveloperVH>() {

    private lateinit var binding: ItemApplicationBinding
    var apps = listOf<Application>()

    class DeveloperVH(
        private val onItemClickListener: (Application) -> Unit,
        private val binding: ItemApplicationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(application: Application) {
            with(binding) {
                llApplication.setOnClickListener {
                    onItemClickListener.invoke(application)
                }
                ivApplicationLauncher.loadFromUrl(application.imageUrl)
                tvApplicationName.text = application.name
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeveloperVH {
        binding = ItemApplicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeveloperVH(onItemClickListener, binding)
    }

    override fun getItemCount(): Int = apps.size

    override fun onBindViewHolder(holder: DeveloperVH, position: Int) {
        holder.bind(apps[position])
    }

}
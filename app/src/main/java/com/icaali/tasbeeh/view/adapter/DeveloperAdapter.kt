package com.icaali.tasbeeh.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.extension.glide.loadFromUrl
import com.icaali.tasbeeh.model.Application
import kotlinx.android.synthetic.main.item_application.view.*

class DeveloperAdapter(val onItemClickListener: (Application) -> Unit) :
    RecyclerView.Adapter<DeveloperAdapter.DeveloperVH>() {

    var apps = listOf<Application>()

    class DeveloperVH(
        val onItemClickListener: (Application) -> Unit,
        val view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bind(application: Application) {
            with(view) {
                llApplication?.setOnClickListener {
                    onItemClickListener.invoke(application)
                }
                ivApplicationLauncher.loadFromUrl(application.imageUrl)
                tvApplicationName.text = application.name
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeveloperVH =
        DeveloperVH(
            onItemClickListener,
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_application,
                parent, false
            )
        )

    override fun getItemCount(): Int = apps.size

    override fun onBindViewHolder(holder: DeveloperVH, position: Int) {
        holder.bind(apps[position])
    }

}
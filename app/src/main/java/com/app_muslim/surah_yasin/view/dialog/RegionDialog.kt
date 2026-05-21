package com.app_muslim.surah_yasin.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.databinding.DialogRegionSelectionBinding
import com.app_muslim.surah_yasin.data.model.IslamicRegion
import com.app_muslim.surah_yasin.view.adapter.RegionAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RegionDialog : DialogFragment() {

    companion object {
        private const val TAG = "RegionDialog"
        
        fun newInstance(): RegionDialog {
            return RegionDialog()
        }
    }

    private var _binding: DialogRegionSelectionBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var regionAdapter: RegionAdapter
    private var onRegionSelectedListener: ((IslamicRegion) -> Unit)? = null
    private var selectedRegion: IslamicRegion? = null

    fun setOnRegionSelectedListener(listener: (IslamicRegion) -> Unit) {
        onRegionSelectedListener = listener
    }

    fun setSelectedRegion(region: IslamicRegion?) {
        selectedRegion = region
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.Theme_Islamic_Dialog)
            .setView(createView())
            .setTitle(getString(R.string.select_islamic_region))
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                selectedRegion?.let { region ->
                    onRegionSelectedListener?.invoke(region)
                }
                dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dismiss()
            }
            .create()
    }

    private fun createView(): View {
        _binding = DialogRegionSelectionBinding.inflate(LayoutInflater.from(context))
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val regions = getIslamicRegions()
        
        regionAdapter = RegionAdapter(
            regions = regions,
            selectedRegion = selectedRegion
        ) { region ->
            selectedRegion = region
        }
        
        binding.recyclerViewRegions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = regionAdapter
            setHasFixedSize(true)
        }
    }

    private fun getIslamicRegions(): List<RegionItem> {
        return listOf(
            RegionItem(
                region = IslamicRegion.SOUTHEAST_ASIA,
                countries = listOf("Indonesia", "Malaysia", "Thailand", "Philippines", "Singapore", "Brunei"),
                description = getString(R.string.southeast_asia_description),
                languages = listOf("Indonesian", "Malay", "Thai", "Tagalog", "English")
            ),
            RegionItem(
                region = IslamicRegion.MIDDLE_EAST,
                countries = listOf("Saudi Arabia", "UAE", "Egypt", "Jordan", "Lebanon", "Iraq", "Syria"),
                description = getString(R.string.middle_east_description),
                languages = listOf("Arabic", "Turkish", "Persian", "English")
            ),
            RegionItem(
                region = IslamicRegion.SOUTH_ASIA,
                countries = listOf("Pakistan", "India", "Bangladesh", "Afghanistan", "Sri Lanka"),
                description = getString(R.string.south_asia_description),
                languages = listOf("Urdu", "Hindi", "Bengali", "English")
            ),
            RegionItem(
                region = IslamicRegion.NORTH_AFRICA,
                countries = listOf("Morocco", "Algeria", "Tunisia", "Libya", "Sudan"),
                description = getString(R.string.north_africa_description),
                languages = listOf("Arabic", "French", "English")
            ),
            RegionItem(
                region = IslamicRegion.SUB_SAHARAN_AFRICA,
                countries = listOf("Nigeria", "Mali", "Senegal", "Ghana", "Kenya", "Tanzania"),
                description = getString(R.string.sub_saharan_africa_description),
                languages = listOf("Swahili", "Hausa", "French", "English")
            ),
            RegionItem(
                region = IslamicRegion.CENTRAL_ASIA,
                countries = listOf("Kazakhstan", "Uzbekistan", "Kyrgyzstan", "Tajikistan", "Turkmenistan"),
                description = getString(R.string.central_asia_description),
                languages = listOf("Kazakh", "Uzbek", "Kyrgyz", "Russian", "English")
            ),
            RegionItem(
                region = IslamicRegion.EUROPE,
                countries = listOf("Turkey", "Albania", "Bosnia", "Kosovo", "France", "Germany", "UK"),
                description = getString(R.string.europe_description),
                languages = listOf("Turkish", "Albanian", "Bosnian", "French", "German", "English")
            ),
            RegionItem(
                region = IslamicRegion.NORTH_AMERICA,
                countries = listOf("United States", "Canada", "Mexico"),
                description = getString(R.string.north_america_description),
                languages = listOf("English", "Spanish", "French")
            ),
            RegionItem(
                region = IslamicRegion.SOUTH_AMERICA,
                countries = listOf("Brazil", "Argentina", "Guyana", "Suriname"),
                description = getString(R.string.south_america_description),
                languages = listOf("Portuguese", "Spanish", "English")
            ),
            RegionItem(
                region = IslamicRegion.OCEANIA,
                countries = listOf("Australia", "New Zealand", "Fiji"),
                description = getString(R.string.oceania_description),
                languages = listOf("English", "Fijian")
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class RegionItem(
        val region: IslamicRegion,
        val countries: List<String>,
        val description: String,
        val languages: List<String>
    )
}

// Region Adapter
class RegionAdapter(
    private val regions: List<RegionDialog.RegionItem>,
    private var selectedRegion: IslamicRegion?,
    private val onRegionSelected: (IslamicRegion) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<RegionAdapter.ViewHolder>() {

    class ViewHolder(private val binding: com.app_muslim.surah_yasin.databinding.ItemRegionBinding) : 
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            regionItem: RegionDialog.RegionItem, 
            isSelected: Boolean,
            onRegionSelected: (IslamicRegion) -> Unit
        ) {
            binding.apply {
                textRegionName.text = regionItem.region.displayName
                textRegionDescription.text = regionItem.description
                textCountries.text = regionItem.countries.joinToString(", ")
                textLanguages.text = "Languages: ${regionItem.languages.joinToString(", ")}"
                
                // Set selection state
                radioButtonRegion.isChecked = isSelected
                cardRegion.isSelected = isSelected
                
                // Handle selection
                root.setOnClickListener {
                    onRegionSelected(regionItem.region)
                }
                
                radioButtonRegion.setOnClickListener {
                    onRegionSelected(regionItem.region)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = com.app_muslim.surah_yasin.databinding.ItemRegionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val regionItem = regions[position]
        val isSelected = selectedRegion == regionItem.region
        
        holder.bind(regionItem, isSelected) { region ->
            val previousSelected = selectedRegion
            selectedRegion = region
            
            // Update UI for previous and current selection
            previousSelected?.let { prevRegion ->
                val prevIndex = regions.indexOfFirst { it.region == prevRegion }
                if (prevIndex != -1) {
                    notifyItemChanged(prevIndex)
                }
            }
            
            notifyItemChanged(position)
            onRegionSelected(region)
        }
    }

    override fun getItemCount(): Int = regions.size
}
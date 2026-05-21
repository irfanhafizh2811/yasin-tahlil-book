package com.app_muslim.surah_yasin.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.databinding.DialogSchoolOfThoughtSelectionBinding
import com.app_muslim.surah_yasin.data.model.SchoolOfThought
import com.app_muslim.surah_yasin.view.adapter.SchoolOfThoughtAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SchoolOfThoughtDialog : DialogFragment() {

    companion object {
        private const val TAG = "SchoolOfThoughtDialog"
        
        fun newInstance(): SchoolOfThoughtDialog {
            return SchoolOfThoughtDialog()
        }
    }

    private var _binding: DialogSchoolOfThoughtSelectionBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var schoolAdapter: SchoolOfThoughtAdapter
    private var onSchoolSelectedListener: ((SchoolOfThought) -> Unit)? = null
    private var selectedSchool: SchoolOfThought? = null

    fun setOnSchoolSelectedListener(listener: (SchoolOfThought) -> Unit) {
        onSchoolSelectedListener = listener
    }

    fun setSelectedSchool(school: SchoolOfThought?) {
        selectedSchool = school
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.Theme_Islamic_Dialog)
            .setView(createView())
            .setTitle(getString(R.string.select_school_of_thought))
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                selectedSchool?.let { school ->
                    onSchoolSelectedListener?.invoke(school)
                }
                dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dismiss()
            }
            .create()
    }

    private fun createView(): View {
        _binding = DialogSchoolOfThoughtSelectionBinding.inflate(LayoutInflater.from(context))
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val schools = getIslamicSchoolsOfThought()
        
        schoolAdapter = SchoolOfThoughtAdapter(
            schools = schools,
            selectedSchool = selectedSchool
        ) { school ->
            selectedSchool = school
        }
        
        binding.recyclerViewSchools.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = schoolAdapter
            setHasFixedSize(true)
        }
    }

    private fun getIslamicSchoolsOfThought(): List<SchoolItem> {
        return listOf(
            SchoolItem(
                school = SchoolOfThought.HANAFI,
                description = getString(R.string.hanafi_description),
                regions = listOf("Turkey", "Central Asia", "South Asia", "Parts of Middle East"),
                characteristics = listOf(
                    "Emphasizes reasoning and opinion (ra'y)",
                    "Flexible approach to jurisprudence",
                    "Founded by Imam Abu Hanifa"
                ),
                practices = listOf(
                    "Longer recitation in prayers",
                    "Specific hand positions in prayer",
                    "Particular inheritance laws"
                )
            ),
            SchoolItem(
                school = SchoolOfThought.MALIKI,
                description = getString(R.string.maliki_description),
                regions = listOf("North Africa", "West Africa", "Parts of Middle East"),
                characteristics = listOf(
                    "Emphasizes Medina practices",
                    "Strong reliance on hadith",
                    "Founded by Imam Malik ibn Anas"
                ),
                practices = listOf(
                    "Hands at sides during prayer",
                    "Specific funeral prayer practices",
                    "Emphasis on local customs (urf)"
                )
            ),
            SchoolItem(
                school = SchoolOfThought.SHAFI,
                description = getString(R.string.shafi_description),
                regions = listOf("Southeast Asia", "East Africa", "Parts of Middle East"),
                characteristics = listOf(
                    "Systematic approach to jurisprudence",
                    "Balance between hadith and reasoning",
                    "Founded by Imam Al-Shafi'i"
                ),
                practices = listOf(
                    "Loud 'Ameen' after Fatiha",
                    "Hands on chest during prayer",
                    "Specific prayer timings"
                )
            ),
            SchoolItem(
                school = SchoolOfThought.HANBALI,
                description = getString(R.string.hanbali_description),
                regions = listOf("Saudi Arabia", "Parts of Levant"),
                characteristics = listOf(
                    "Strict adherence to Quran and Sunnah",
                    "Conservative approach",
                    "Founded by Imam Ahmad ibn Hanbal"
                ),
                practices = listOf(
                    "Literal interpretation of texts",
                    "Specific prayer postures",
                    "Conservative social practices"
                )
            ),
            SchoolItem(
                school = SchoolOfThought.JAFARI,
                description = getString(R.string.jafari_description),
                regions = listOf("Iran", "Iraq", "Lebanon", "Parts of Gulf"),
                characteristics = listOf(
                    "Twelver Shia jurisprudence",
                    "Emphasis on Imams' teachings",
                    "Founded by Imam Ja'far al-Sadiq"
                ),
                practices = listOf(
                    "Prostration on clay tablets",
                    "Combination of some prayers",
                    "Temporary marriage (mut'ah)"
                )
            ),
            SchoolItem(
                school = SchoolOfThought.OTHER,
                description = getString(R.string.other_school_description),
                regions = listOf("Various regions"),
                characteristics = listOf(
                    "Other Islamic traditions",
                    "Regional variations",
                    "Local scholarly interpretations"
                ),
                practices = listOf(
                    "Varied practices",
                    "Local customs",
                    "Regional interpretations"
                )
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class SchoolItem(
        val school: SchoolOfThought,
        val description: String,
        val regions: List<String>,
        val characteristics: List<String>,
        val practices: List<String>
    )
}

// School of Thought Adapter
class SchoolOfThoughtAdapter(
    private val schools: List<SchoolOfThoughtDialog.SchoolItem>,
    private var selectedSchool: SchoolOfThought?,
    private val onSchoolSelected: (SchoolOfThought) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<SchoolOfThoughtAdapter.ViewHolder>() {

    class ViewHolder(private val binding: com.app_muslim.surah_yasin.databinding.ItemSchoolOfThoughtBinding) : 
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            schoolItem: SchoolOfThoughtDialog.SchoolItem, 
            isSelected: Boolean,
            onSchoolSelected: (SchoolOfThought) -> Unit
        ) {
            binding.apply {
                textSchoolName.text = schoolItem.school.displayName
                textSchoolArabicName.text = schoolItem.school.arabicName
                textSchoolDescription.text = schoolItem.description
                textRegions.text = "Common in: ${schoolItem.regions.joinToString(", ")}"
                textCharacteristics.text = "Key features:\n• ${schoolItem.characteristics.joinToString("\n• ")}"
                
                // Set selection state
                radioButtonSchool.isChecked = isSelected
                cardSchool.isSelected = isSelected
                
                // Handle selection
                root.setOnClickListener {
                    onSchoolSelected(schoolItem.school)
                }
                
                radioButtonSchool.setOnClickListener {
                    onSchoolSelected(schoolItem.school)
                }
                
                // Show/hide expanded content
                if (isSelected) {
                    layoutExpanded.visibility = View.VISIBLE
                    textPractices.text = "Practices:\n• ${schoolItem.practices.joinToString("\n• ")}"
                } else {
                    layoutExpanded.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = com.app_muslim.surah_yasin.databinding.ItemSchoolOfThoughtBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schoolItem = schools[position]
        val isSelected = selectedSchool == schoolItem.school
        
        holder.bind(schoolItem, isSelected) { school ->
            val previousSelected = selectedSchool
            selectedSchool = school
            
            // Update UI for previous and current selection
            previousSelected?.let { prevSchool ->
                val prevIndex = schools.indexOfFirst { it.school == prevSchool }
                if (prevIndex != -1) {
                    notifyItemChanged(prevIndex)
                }
            }
            
            notifyItemChanged(position)
            onSchoolSelected(school)
        }
    }

    override fun getItemCount(): Int = schools.size
}
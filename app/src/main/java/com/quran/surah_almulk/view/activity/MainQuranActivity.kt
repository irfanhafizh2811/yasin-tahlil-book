package com.quran.surah_almulk.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quran.surah_almulk.R
import com.quran.surah_almulk.data.model.User
import com.quran.surah_almulk.data.model.gender.Gender
import com.quran.surah_almulk.data.model.surah.SurahFactory
import com.quran.surah_almulk.data.model.surah.SurahQuran
import com.quran.surah_almulk.databinding.ActivityMainQuranBinding
import com.quran.surah_almulk.extension.common.clazz
import com.quran.surah_almulk.extension.context.getColorCompat
import com.quran.surah_almulk.extension.context.getDrawableCompat
import com.quran.surah_almulk.view.adapter.MainSurahAdapter
import com.quran.surah_almulk.vm.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainQuranActivity : BaseActivity() {

    private var userApp = User()
    private val viewModel by viewModel<MainViewModel>()
    private val surahAdapter by lazy {
        MainSurahAdapter {
            startActivitySurah(it.surahQuran.name)
        }
    }
    private lateinit var binding: ActivityMainQuranBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainQuranBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
    }

    private fun setupView() = with(binding) {
        when (userApp.gender) {
            Gender.MALE -> {
                tvTitleQuran.setTextColor(getColorCompat(R.color.colorAccentMale))
                tvTitleQuran.setTextColor(getColorCompat(R.color.colorPrimaryMale))
                ivBgCharQuran.setImageDrawable(getDrawableCompat(R.drawable.bg_main_top_banner_male))
                ivCharReadQuran.setImageDrawable(getDrawableCompat(R.drawable.ic_char_gender_male))
                cvRead.setCardBackgroundColor(getColorCompat(R.color.colorAccentFemale))
            }

            Gender.FEMALE -> {
                tvTitleQuran.setTextColor(getColorCompat(R.color.colorAccentFemale))
                tvTitleQuran.setTextColor(getColorCompat(R.color.colorPrimaryFemale))
                ivBgCharQuran.setImageDrawable(getDrawableCompat(R.drawable.bg_main_top_banner_female))
                ivCharReadQuran.setImageDrawable(getDrawableCompat(R.drawable.ic_char_gender_female))
                cvRead.setCardBackgroundColor(getColorCompat(R.color.colorAccentMale))
            }
        }
        cvRead.setOnClickListener { startActivitySurah(SurahQuran.AL_MULK.name) }
        loadBanner(flAdsBanner)
    }

    private fun observeViewModel() = with(viewModel) {
        user.observe(this@MainQuranActivity) { user ->
            userApp = user
            setupView()
            setOtherSurahView()
        }
        getUser()
    }

    private fun setOtherSurahView() = binding.rvSurah.apply {
        layoutManager = LinearLayoutManager(this@MainQuranActivity)
        adapter = surahAdapter.also {
            it.user = userApp
            it.listSurahInterface = SurahFactory.listSurah
            it.notifyItemInserted(it.listSurahInterface.size)
        }
    }

    private fun startActivitySurah(surah: String, readLast: Boolean = false) {
        val intent = Intent(this, clazz<QuranSurahActivity>())
        intent.putExtra(SurahActivity.SURAH_INTENT_EXTRA, surah)
        intent.putExtra(SurahActivity.READ_LAST_SURAH_INTENT_EXTRA, readLast)
        startActivity(intent)
    }
}
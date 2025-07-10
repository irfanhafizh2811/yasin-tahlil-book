package com.app_muslim.surah_yasin.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.data.model.User
import com.app_muslim.surah_yasin.data.model.gender.Gender
import com.app_muslim.surah_yasin.data.model.surah.SurahFactory
import com.app_muslim.surah_yasin.data.model.surah.SurahQuran
import com.app_muslim.surah_yasin.databinding.ActivityMainQuranBinding
import com.app_muslim.surah_yasin.extension.common.clazz
import com.app_muslim.surah_yasin.extension.context.getColorCompat
import com.app_muslim.surah_yasin.extension.context.getDrawableCompat
import com.app_muslim.surah_yasin.view.adapter.MainSurahAdapter
import com.app_muslim.surah_yasin.vm.MainViewModel
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
        cvRead.setOnClickListener { startActivitySurah(SurahQuran.YASIN.name) }
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
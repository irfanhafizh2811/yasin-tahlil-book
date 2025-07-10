package com.app_muslim.surah_yasin.view.activity

import android.content.Intent
import android.os.Bundle
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.data.model.gender.Gender
import com.app_muslim.surah_yasin.databinding.ActivityOnBoardBinding
import com.app_muslim.surah_yasin.extension.common.clazz
import com.app_muslim.surah_yasin.extension.context.getColorCompat
import com.app_muslim.surah_yasin.extension.context.getDrawableCompat
import com.app_muslim.surah_yasin.extension.view.visible
import com.app_muslim.surah_yasin.vm.OnBoardViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnBoardActivity : BaseActivity() {

    private val viewModel by viewModel<OnBoardViewModel>()
    private lateinit var binding: ActivityOnBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        observeViewModel()
    }

    private fun setupView() = with(binding) {
        ivThemeCharFemale.setOnClickListener { onSelectedFemale() }
        ivCharGenderFemale.setOnClickListener { onSelectedFemale() }
        tvFemale.setOnClickListener { onSelectedFemale() }
        ivThemeCharMale.setOnClickListener { onSelectedMale() }
        ivCharGenderMale.setOnClickListener { onSelectedMale() }
        tvMale.setOnClickListener { onSelectedMale() }
        btnStart.setOnClickListener {
            finish()
            val intent = Intent(this@OnBoardActivity, MainQuranActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.also {
            it.user.observe(this) { user ->
                if (user.hasInitialize) startActivity(Intent(this, clazz<MainQuranActivity>()))
            }
        }.getUser()
    }

    private fun onSelectedFemale() = with(binding) {
        viewModel.setGender(Gender.FEMALE)
        ivCharGenderFemale.setImageDrawable(getDrawableCompat(R.drawable.bg_char_main_female))
        ivCharGenderMale.setImageDrawable(getDrawableCompat(R.drawable.bg_char_main_quran))
        tvFemale.setTextColor(getColorCompat(R.color.colorPrimaryFemale))
        tvMale.setTextColor(getColorCompat(R.color.textHintOutputBlackWhite))
        btnStart.visible()
    }

    private fun onSelectedMale() = with(binding) {
        viewModel.setGender(Gender.MALE)
        ivCharGenderMale.setImageDrawable(getDrawableCompat(R.drawable.bg_char_main_male))
        ivCharGenderFemale.setImageDrawable(getDrawableCompat(R.drawable.bg_char_main_quran))
        tvMale.setTextColor(getColorCompat(R.color.colorPrimaryMale))
        tvFemale.setTextColor(getColorCompat(R.color.textHintOutputBlackWhite))
        btnStart.visible()
    }
}
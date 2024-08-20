package com.icaali.tasbeeh.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.databinding.ActivityDhikrBinding
import com.icaali.tasbeeh.extension.common.clazz
import com.icaali.tasbeeh.extension.context.getColorCompat
import com.icaali.tasbeeh.extension.context.getDrawableCompat
import com.icaali.tasbeeh.extension.context.readJsonAssetToString
import com.icaali.tasbeeh.model.DhikrDaily
import com.icaali.tasbeeh.preference.SettingPreference
import com.icaali.tasbeeh.utils.FontSize
import com.icaali.tasbeeh.view.adapter.DhikrAdapter
import org.koin.android.ext.android.inject

class DhikrActivity : BaseActivity() {

    companion object {
        const val DHIKR_INTENT_EXTRA = "DHIKR_INTENT_EXTRA"
        const val DHIKR_MORNING = "dhikr_morning.json"
        const val DHIKR_EVENING = "dhikr_evening.json"
    }

    private lateinit var binding: ActivityDhikrBinding
    private var isEvening = false
    private var currentPage: Int = 0
    private lateinit var currentFontSize: FontSize
    private val dhikrAdapter by lazy { DhikrAdapter() }
    private val dhikrsCollection by lazy {
        Gson().fromJson(
            readJsonAssetToString(if (isEvening) DHIKR_EVENING else DHIKR_MORNING),
            clazz<DhikrDaily>()
        )
    }

    private val settingPreference by inject<SettingPreference>()

    private fun hasNext(): Boolean = currentPage < dhikrAdapter.dhikrs.size - 1
    private fun hasPrev(): Boolean = currentPage > 0
    private fun isMaxSize(): Boolean = currentFontSize == FontSize.HUGE
    private fun isMinSize(): Boolean = currentFontSize == FontSize.SMALL
    private fun nextPage(): Int = currentPage + 1
    private fun prevPage(): Int = currentPage - 1
    private fun totalPage(): Int = dhikrsCollection.dhikrs.size

    private fun onPageChangeCallback() = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            currentPage = position
            onViewPageChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isEvening = !intent.getBooleanExtra(DHIKR_INTENT_EXTRA, false)
        binding = ActivityDhikrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onUIView()
        setViewPager()
        with(binding) {
            ivBack.setOnClickListener { finish() }
            ivNext.setOnClickListener { setNextEvent() }
            ivPrev.setOnClickListener { setPrevEvent() }
            tvPlusSize.setOnClickListener { onRaiseFont() }
            tvMinusSize.setOnClickListener { onLowerFont() }
            loadBanner(adViewContainer)
        }
    }

    private fun onUIView() = with(binding) {
        window.setBackgroundDrawable(
            if (!isEvening) getDrawableCompat(R.drawable.bg_activity_dhikr_morning)
            else getDrawableCompat(R.drawable.bg_activity_dhikr_evening)
        )
        ivDhikrTime.setImageDrawable(
            if (!isEvening) getDrawableCompat(R.drawable.ic_footer_sun)
            else getDrawableCompat(R.drawable.ic_footer_evening)
        )
        tvTitle.text = if (!isEvening) getString(R.string.label_dhikr_morning)
        else getString(R.string.label_dhikr_evening)
        onUILabelColor()
    }

    private fun onUILabelColor() = with(binding) {
        val textColorPlus = getColorCompat(
            if (settingPreference.fontSize == FontSize.HUGE) R.color.themeUnselected
            else R.color.colorBlack
        )
        val textColorMinus = getColorCompat(
            if (settingPreference.fontSize == FontSize.SMALL) R.color.themeUnselected
            else R.color.colorBlack
        )
        tvPlusSize.setTextColor(textColorPlus)
        tvPlusSizeSymbol.setTextColor(textColorPlus)
        tvMinusSize.setTextColor(textColorMinus)
        tvMinusSizeSymbol.setTextColor(textColorMinus)
    }

    private fun setViewPager() = with(binding) {
        currentFontSize = settingPreference.fontSize
        with(viewPagerDhikr) {
            adapter = dhikrAdapter.apply {
                fontSize = settingPreference.fontSize
            }
            offscreenPageLimit = 1
            registerOnPageChangeCallback(onPageChangeCallback())
        }
        dhikrAdapter.sync(dhikrsCollection.dhikrs)
        onViewPageChanged()
    }

    private fun onUpdateFont(isRaise: Boolean) {
        onUILabelColor()
        if (isMaxSize() && isRaise) return
        if (isMinSize() && !isRaise) return
        dhikrAdapter.sync(dhikrsCollection.dhikrs)
    }

    private fun onRaiseFont() {
        currentFontSize = settingPreference.fontSize
        val fontSize = raiseFont(dhikrAdapter.fontSize.name)
        settingPreference.fontSize = fontSize
        dhikrAdapter.fontSize = fontSize
        onUpdateFont(true)
    }

    private fun onLowerFont() {
        currentFontSize = settingPreference.fontSize
        val fontSize = lowerFont(dhikrAdapter.fontSize.name)
        settingPreference.fontSize = lowerFont(dhikrAdapter.fontSize.name)
        dhikrAdapter.fontSize = fontSize
        onUpdateFont(false)
    }

    private fun setNextEvent() = with(binding) {
        if (!hasNext()) nextScreen()
        else viewPagerDhikr.currentItem = nextPage()
    }

    private fun setPrevEvent() = with(binding) {
        if (!hasPrev()) return
        else viewPagerDhikr.currentItem = prevPage()
    }

    private fun onViewPageChanged() = with(binding) {
        onUpdateNext()
        onUpdatePrev()
        val page = (currentPage + 1).toString()
        tvPage.text = page.plus("/").plus(totalPage().toString())
    }

    private fun onUpdateNext() = with(binding.ivNext) {
        setImageDrawable(getDrawableCompat(R.drawable.ic_arrow_back, R.color.colorBlack))
    }

    private fun onUpdatePrev() = with(binding.ivPrev) {
        isClickable = hasPrev()
        setImageDrawable(
            if (hasPrev()) getDrawableCompat(R.drawable.ic_arrow_back, R.color.colorBlack)
            else getDrawableCompat(R.drawable.ic_arrow_back, R.color.themeUnselected)
        )
    }

    private fun raiseFont(fontSize: String): FontSize = when (FontSize.valueOf(fontSize)) {
        FontSize.SMALL -> FontSize.REGULAR
        FontSize.REGULAR -> FontSize.LARGE
        FontSize.LARGE -> FontSize.HUGE
        FontSize.HUGE -> FontSize.HUGE
    }

    private fun lowerFont(fontSize: String): FontSize = when (FontSize.valueOf(fontSize)) {
        FontSize.SMALL -> FontSize.SMALL
        FontSize.REGULAR -> FontSize.SMALL
        FontSize.LARGE -> FontSize.REGULAR
        FontSize.HUGE -> FontSize.LARGE
    }

    private fun nextScreen() {
        val intent = Intent(this, clazz<DhikrCompleteActivity>())
        intent.putExtra(DhikrCompleteActivity.DHIKR_INTENT_EXTRA, isEvening)
        startActivity(intent)
    }
}
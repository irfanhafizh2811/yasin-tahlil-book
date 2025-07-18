package com.app_muslim.surah_yasin.view.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.LocaleList
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.data.database.entity.TasbeehEntity
import com.app_muslim.surah_yasin.extension.common.clazz
import com.app_muslim.surah_yasin.extension.context.getDrawableCompat
import com.app_muslim.surah_yasin.data.preference.LanguagePreference
import com.app_muslim.surah_yasin.utils.TasbeehConst
import java.util.Locale

fun MainActivity.startActivityTasbeeh(tasbeehEntity: TasbeehEntity) {
    val intent = Intent(this, clazz<TasbeehActivity>())
    intent.putExtra(TasbeehActivity.TYPE_EXTRA, TasbeehConst.CUSTOM)
    intent.putExtra(TasbeehActivity.TASBEEH_LATIN_EXTRA, tasbeehEntity.latin)
    intent.putExtra(TasbeehActivity.TASBEEH_DHIKR_EXTRA_ID, tasbeehEntity.id)
    startActivity(intent)
}

fun MainActivity.startActivityTasbeeh(type: String) {
    val typeExtra = when (type) {
        TasbeehConst.SUBHANALLAH -> TasbeehConst.SUBHANALLAH
        TasbeehConst.ALHAMDULILLAH -> TasbeehConst.ALHAMDULILLAH
        TasbeehConst.ALLAHU_AKBAR -> TasbeehConst.ALLAHU_AKBAR
        TasbeehConst.ASTAGHFIRULLAH -> TasbeehConst.ASTAGHFIRULLAH
        TasbeehConst.LAILAHAILALLAH -> TasbeehConst.LAILAHAILALLAH
        else -> TasbeehConst.SUBHANALLAH
    }
    val latin = when (type) {
        TasbeehConst.SUBHANALLAH -> getString(R.string.text_latin_subhanallah)
        TasbeehConst.ALHAMDULILLAH -> getString(R.string.text_latin_alhamdulillah)
        TasbeehConst.ALLAHU_AKBAR -> getString(R.string.text_latin_allahu_akbar)
        TasbeehConst.ASTAGHFIRULLAH -> getString(R.string.text_latin_astaghfirullah)
        TasbeehConst.LAILAHAILALLAH -> getString(R.string.text_latin_laailaahaillallah)
        else -> getString(R.string.text_latin_subhanallah)
    }
    val intent = Intent(this, clazz<TasbeehActivity>())
    intent.putExtra(TasbeehActivity.TYPE_EXTRA, typeExtra)
    intent.putExtra(TasbeehActivity.TASBEEH_LATIN_EXTRA, latin)
    startActivity(intent)
}

fun MainActivity.sync() = with(binding) {
    val counter = R.string.label_counter_x
    tvSubhanallahCount?.text = getString(counter, counterPreference.subhanallah)
    tvAlhamdulillahCount?.text = getString(counter, counterPreference.alhamdulillah)
    tvAllahuAkbarCount?.text = getString(counter, counterPreference.allahukkbar)
    tvAstaghfirullahCount?.text = getString(counter, counterPreference.astaghfirullah)
    tvLaailaahaillallahCount?.text = getString(counter, counterPreference.lailahailallah)

    ivSubhanallah?.setImageDrawable(getIcon(TasbeehConst.SUBHANALLAH))
    ivAlhamdulillah?.setImageDrawable(getIcon(TasbeehConst.ALHAMDULILLAH))
    ivAllahuAkbar?.setImageDrawable(getIcon(TasbeehConst.ALLAHU_AKBAR))
    ivAstaghfirullah?.setImageDrawable(getIcon(TasbeehConst.ASTAGHFIRULLAH))
    ivLaailaahaillallah?.setImageDrawable(getIcon(TasbeehConst.LAILAHAILALLAH))
}

fun MainActivity.getIcon(tasbeeh: String): Drawable? = when (tasbeeh) {
    TasbeehConst.SUBHANALLAH -> getDrawableCompat(
        R.drawable.ic_subhanallah, android.R.color.black
    )

    TasbeehConst.ALHAMDULILLAH -> getDrawableCompat(
        R.drawable.ic_alhamdulillah, android.R.color.black
    )

    TasbeehConst.ALLAHU_AKBAR -> getDrawableCompat(
        R.drawable.ic_allahu_akbar, android.R.color.black
    )

    TasbeehConst.ASTAGHFIRULLAH -> getDrawableCompat(
        R.drawable.ic_astagfirllah, android.R.color.black
    )

    TasbeehConst.LAILAHAILALLAH -> getDrawableCompat(
        R.drawable.ic_laailaahaillallah, android.R.color.black
    )

    else -> getDrawableCompat(
        R.drawable.ic_subhanallah, android.R.color.black
    )
}
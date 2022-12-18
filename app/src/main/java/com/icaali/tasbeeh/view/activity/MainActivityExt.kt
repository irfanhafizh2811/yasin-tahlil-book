package com.icaali.tasbeeh.view.activity

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.LocaleList
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.database.table.Tasbeeh
import com.icaali.tasbeeh.extension.context.getDrawableCompat
import com.icaali.tasbeeh.preference.LanguagePreference
import com.icaali.tasbeeh.utils.TasbeehConst
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import java.util.Locale

fun MainActivity.startActivityTasbeeh(tasbeeh: Tasbeeh) {
    startActivity(
        intentFor<TasbeehActivity>(
            TasbeehActivity.TYPE_EXTRA to TasbeehConst.CUSTOM,
            TasbeehActivity.TASBEEH_LATIN_EXTRA to tasbeeh.latin,
            TasbeehActivity.TASBEEH_DHIKR_EXTRA to tasbeeh
        )
    )
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
    val intent = intentFor<TasbeehActivity>(
        TasbeehActivity.TYPE_EXTRA to typeExtra,
        TasbeehActivity.TASBEEH_LATIN_EXTRA to latin
    )
    startActivity(intent)
}

fun MainActivity.sync() {
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

fun MainActivity.setLanguage() {
    if (Build.VERSION.SDK_INT >= 33) {
        when {
            languagePreference.language.contains(LanguagePreference.LANGUAGE_ENGLISH) -> {
                localeManager?.applicationLocales =
                    LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_ENGLISH))
            }
            languagePreference.language.contains(LanguagePreference.LANGUAGE_TURKEY) -> {
                localeManager?.applicationLocales =
                    LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_TURKEY))
            }
            languagePreference.language.contains(LanguagePreference.LANGUAGE_RUSSIAN) -> {
                localeManager?.applicationLocales =
                    LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_RUSSIAN))
            }
            languagePreference.language.contains(LanguagePreference.LANGUAGE_SAUDI_ARABIA) -> {
                localeManager?.applicationLocales =
                    LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_SAUDI_ARABIA))
            }
            languagePreference.language.contains(LanguagePreference.LANGUAGE_INDONESIA) -> {
                localeManager?.applicationLocales =
                    LocaleList(Locale.forLanguageTag(LanguagePreference.LANGUAGE_INDONESIA))
            }
        }
    }
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
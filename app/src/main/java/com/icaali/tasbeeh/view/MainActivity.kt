package com.icaali.tasbeeh.view

import android.os.Bundle
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.preference.CounterPreference
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity() {

    private val counterPreference by inject<CounterPreference>()
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cvSubhanallah?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.SUBHANALLAH,
                    TasbeehActivity.TASBEEH_LATIN_EXTRA to getString(R.string.text_latin_subhanallah)
                )
            )
        }
        cvAlhamdulillah?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.ALHAMDULILLAH,
                    TasbeehActivity.TASBEEH_LATIN_EXTRA to getString(R.string.text_latin_alhamdulillah)
                )
            )
        }
        cvAllahuAkbar?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.ALLAHU_AKBAR,
                    TasbeehActivity.TASBEEH_LATIN_EXTRA to getString(R.string.text_latin_allahu_akbar)
                )
            )
        }
        cvAstaghfirullah?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.ASTAGHFIRULLAH,
                    TasbeehActivity.TASBEEH_LATIN_EXTRA to getString(R.string.text_latin_astaghfirullah)
                )
            )
        }
        cvLaailaahaillallah?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.LAILAHAILALLAH,
                    TasbeehActivity.TASBEEH_LATIN_EXTRA to getString(R.string.text_latin_laailaahaillallah)
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        tvSubhanallahCount?.text =
            getString(R.string.label_counter_x, counterPreference.subhanallah)
        tvAlhamdulillahCount?.text =
            getString(R.string.label_counter_x, counterPreference.alhamdulillah)
        tvAllahuAkbarCount?.text =
            getString(R.string.label_counter_x, counterPreference.allahukkbar)
        tvAstaghfirullahCount?.text =
            getString(R.string.label_counter_x, counterPreference.astaghfirullah)
        tvLaailaahaillallahCount?.text =
            getString(R.string.label_counter_x, counterPreference.lailahailallah)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
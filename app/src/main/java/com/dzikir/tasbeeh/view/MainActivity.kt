package com.dzikir.tasbeeh.view

import android.os.Bundle
import com.dzikir.tasbeeh.R
import com.dzikir.tasbeeh.preference.CounterPreference
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
                    TasbeehActivity.TASBEEH_ARABIC_EXTRA to tvSubhanallah.text
                )
            )
        }
        cvAlhamdulillah?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.ALHAMDULILLAH,
                    TasbeehActivity.TASBEEH_ARABIC_EXTRA to tvAlhamdulillah.text
                )
            )
        }
        cvLaila?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.LAILAHAILALLAH,
                    TasbeehActivity.TASBEEH_ARABIC_EXTRA to tvLaila.text
                )
            )
        }
        cvAllahuAkbar?.setOnClickListener {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.ALLAHU_AKBAR,
                    TasbeehActivity.TASBEEH_ARABIC_EXTRA to tvAllahuAkbar.text
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        fmSubhanallah?.setValue(counterPreference.subhanallah, true)
        fmAlhamdulillah?.setValue(counterPreference.alhamdulillah, true)
        fmLaila?.setValue(counterPreference.lailahailallah, true)
        fmAllahuAkbar?.setValue(counterPreference.allahukkbar, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

}
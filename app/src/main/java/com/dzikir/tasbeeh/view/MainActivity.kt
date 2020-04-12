package com.dzikir.tasbeeh.view

import android.os.Bundle
import com.dzikir.tasbeeh.R
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    private var subhanallahCounter = 0
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        disposable.add(
            RxView.clicks(cvSubhanallah)
                .throttleFirst(200L,
                    TimeUnit.MILLISECONDS,
                    AndroidSchedulers.mainThread()
                )
                .subscribe {
                    subhanallahCounter += 1
                    fsSubhanallah?.setValue(subhanallahCounter, true)
                }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

}
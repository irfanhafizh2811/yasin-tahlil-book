package com.icaali.tasbeeh.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.icaali.tasbeeh.R
import com.icaali.tasbeeh.database.table.Dhikr
import com.icaali.tasbeeh.extension.context.getDrawableCompat
import com.icaali.tasbeeh.extension.view.gone
import com.icaali.tasbeeh.extension.view.visible
import com.icaali.tasbeeh.preference.CounterPreference
import com.icaali.tasbeeh.view.Tasbeeh
import com.icaali.tasbeeh.view.adapter.DhikrAdapter
import com.icaali.tasbeeh.view.dialog.AddCustomDialog
import com.icaali.tasbeeh.vm.DhikrViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val counterPreference: CounterPreference by inject()
    val dhikrViewModel: DhikrViewModel by viewModel()
    private val disposable = CompositeDisposable()

    private val dhikrAdapter by lazy {
        DhikrAdapter {
            startActivity(
                intentFor<TasbeehActivity>(
                    TasbeehActivity.TYPE_EXTRA to Tasbeeh.CUSTOM,
                    TasbeehActivity.TASBEEH_LATIN_EXTRA to it.latin,
                    TasbeehActivity.TASBEEH_DHIKR_EXTRA to it
                )
            )
        }
    }

    private val addCustomDialog by lazy { AddCustomDialog(this) }

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
        rvAddDhikr.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply { reverseLayout = true }
            adapter = dhikrAdapter
        }

        cvAddDhikr?.setOnClickListener {
            addCustomDialog.setOnPositiveListener {
                dhikrViewModel.insert(it)
            }.show()
        }

        dhikrViewModel.dhikrs.observe(this, {
            when {
                it.isEmpty() -> {
                    rvAddDhikr.gone()
                    tvYourDhikr.gone()
                }
                else -> {
                    rvAddDhikr.visible()
                    tvYourDhikr.visible()
                    dhikrAdapter.submitList(it)
                }
            }
        })
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

        ivSubhanallah?.setImageDrawable(
            getDrawableCompat(
                R.drawable.ic_subhanallah,
                android.R.color.black
            )
        )
        ivAlhamdulillah?.setImageDrawable(
            getDrawableCompat(
                R.drawable.ic_alhamdulillah,
                android.R.color.black
            )
        )
        ivAllahuAkbar?.setImageDrawable(
            getDrawableCompat(
                R.drawable.ic_allahu_akbar,
                android.R.color.black
            )
        )
        ivAstaghfirullah?.setImageDrawable(
            getDrawableCompat(
                R.drawable.ic_astagfirllah,
                android.R.color.black
            )
        )
        ivLaailaahaillallah?.setImageDrawable(
            getDrawableCompat(
                R.drawable.ic_laailaahaillallah,
                android.R.color.black
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
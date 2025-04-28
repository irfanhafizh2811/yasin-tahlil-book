package com.quran.surah_almulk.coroutine

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    fun main(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
}

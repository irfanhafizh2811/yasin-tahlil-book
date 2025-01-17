package com.quran.surah_almulk.repository

import androidx.annotation.WorkerThread
import com.quran.surah_almulk.data.database.dao.DhikrDao
import com.quran.surah_almulk.data.database.entity.TasbeehEntity
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class TasbeehRepository(private val dhikrDao: DhikrDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val dhikrs: Flow<List<TasbeehEntity>> = dhikrDao.getDhikrs()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(dhikr: TasbeehEntity) {
        dhikrDao.insert(dhikr)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(dhikr: TasbeehEntity) {
        dhikrDao.update(dhikr)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(dhikr: TasbeehEntity) {
        dhikrDao.delete(dhikr)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        dhikrDao.deleteAll()
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getDhikrs(): Flow<List<TasbeehEntity>> {
        return dhikrDao.getDhikrs()
    }
}
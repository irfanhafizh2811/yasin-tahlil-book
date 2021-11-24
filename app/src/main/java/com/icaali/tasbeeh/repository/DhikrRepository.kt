package com.icaali.tasbeeh.repository

import androidx.annotation.WorkerThread
import com.icaali.tasbeeh.database.dao.DhikrDao
import com.icaali.tasbeeh.database.table.Dhikr
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class DhikrRepository(private val dhikrDao: DhikrDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val dhikrs: Flow<List<Dhikr>> = dhikrDao.getDhikrs()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(dhikr: Dhikr) {
        dhikrDao.insert(dhikr)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        dhikrDao.deleteAll()
    }
}
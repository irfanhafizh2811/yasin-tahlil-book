package com.icaali.tasbeeh.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.icaali.tasbeeh.database.table.Dhikr
import kotlinx.coroutines.flow.Flow

@Dao
interface DhikrDao {

    @Query("SELECT * FROM dhirk_table")
    fun getDhikrs(): Flow<List<Dhikr>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dhikr: Dhikr)

    @Query("DELETE FROM dhirk_table")
    suspend fun deleteAll()
}
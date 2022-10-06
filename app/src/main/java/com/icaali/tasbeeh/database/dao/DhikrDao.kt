package com.icaali.tasbeeh.database.dao

import androidx.room.*
import com.icaali.tasbeeh.database.table.Dhikr
import kotlinx.coroutines.flow.Flow

@Dao
interface DhikrDao {

    @Query("SELECT * FROM dhirk_table")
    fun getDhikrs(): Flow<List<Dhikr>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dhikr: Dhikr)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(dhikr: Dhikr)

    @Delete
    suspend fun delete(dhikr: Dhikr)

    @Query("DELETE FROM dhirk_table")
    suspend fun deleteAll()
}
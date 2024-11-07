package com.icaali.almulk.database.dao

import androidx.room.*
import com.icaali.almulk.database.table.Tasbeeh
import kotlinx.coroutines.flow.Flow

@Dao
interface DhikrDao {

    @Query("SELECT * FROM dhirk_table")
    fun getDhikrs(): Flow<List<Tasbeeh>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dhikr: Tasbeeh)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(dhikr: Tasbeeh)

    @Delete
    suspend fun delete(dhikr: Tasbeeh)

    @Query("DELETE FROM dhirk_table")
    suspend fun deleteAll()
}
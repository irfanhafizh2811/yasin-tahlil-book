package com.icaali.almulk.database.dao

import androidx.room.*
import com.icaali.almulk.database.entity.TasbeehEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DhikrDao {

    @Query("SELECT * FROM dhirk_table")
    fun getDhikrs(): Flow<List<TasbeehEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dhikr: TasbeehEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(dhikr: TasbeehEntity)

    @Delete
    suspend fun delete(dhikr: TasbeehEntity)

    @Query("DELETE FROM dhirk_table")
    suspend fun deleteAll()
}
package com.example.movieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieapp.data.local.entity.SavedItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<SavedItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SavedItemEntity)

    @Query("DELETE FROM saved_items_table WHERE itemId = :itemId")
    suspend fun delete(itemId: Long)

    @Query("SELECT * FROM saved_items_table")
    fun getAllItem(): Flow<List<SavedItemEntity>>

    @Query("SELECT COUNT(*) FROM saved_items_table WHERE itemId = :itemId")
    suspend fun countItemsById(itemId: Long): Int
}
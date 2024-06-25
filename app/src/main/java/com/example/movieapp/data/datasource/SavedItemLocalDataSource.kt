package com.example.movieapp.data.datasource

import android.util.Log
import com.example.movieapp.data.local.dao.SavedItemDao
import com.example.movieapp.data.local.entity.SavedItemEntity
import com.example.movieapp.utils.tempTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SavedItemLocalDataSource(private val savedItemDao: SavedItemDao) {

    suspend fun insertSavedItem(item: SavedItemEntity) = withContext(Dispatchers.IO) {
        savedItemDao.insert(item)
    }

    suspend fun deleteSavedItem(item: SavedItemEntity) = withContext(Dispatchers.IO) {
        Log.i(tempTag(),"deleting item $item")
        savedItemDao.delete(item.itemId)
    }

    fun getAllSavedItemList(): Flow<List<SavedItemEntity>> = savedItemDao.getAllItem()

    suspend fun isItemSaved(itemId: Long): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext savedItemDao.countItemsById(itemId) >= 1
        }

}
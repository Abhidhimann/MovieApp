package com.example.movieapp.data.repository.common

import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.local.entity.SavedItemEntity
import com.example.movieapp.data.remote.model.common.RecommendationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


// can also make interface in domain/ i.e. SavedItemRepository then implement it SavedItemRepositoryImpl
@Singleton
class SavedItemRepository @Inject constructor(private val dataSource: SavedItemLocalDataSource) {

    // better to change SavedItemEntity -> ToSavedItemModel or use same ToRecommendationItem
    // as in future if we change table then, don't have to change lot of code
   fun getAllItemList(): Flow<List<RecommendationItem>> =
        dataSource.getAllSavedItemList()
            .map { list -> list.map { RecommendationItem.fromSavedItemEntity(it) } }


    suspend fun deleteSavedItem(item: SavedItemEntity) = withContext(Dispatchers.IO) {
        dataSource.insertSavedItem(item)
    }
}
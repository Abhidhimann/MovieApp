package com.example.movieapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.movieapp.data.remote.model.common.RecommendationItem
import com.example.movieapp.data.datasource.RecommendationPagingSource
import com.example.movieapp.data.datasource.MovieDataSource

class TrendingPagingViewModel(private val dataSource: MovieDataSource) : ViewModel() {
    private val pageSize = 1
    private val prefetchDistance = 30

    fun getRecommendationItems(startPage: Int): LiveData<PagingData<RecommendationItem>> =
        Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                RecommendationPagingSource(dataSource, startPage)
            },
        ).liveData.cachedIn(viewModelScope)
}

class TrendingPagingViewModelFactory(private val dataSource: MovieDataSource) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrendingPagingViewModel::class.java)) { //If the modelClass is not compatible with HomePageViewModel, it throws an IllegalArgumentException with the message "Unknown ViewModel class." This is a safety check to ensure that you only create instances of the expected ViewModel class.
            return TrendingPagingViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

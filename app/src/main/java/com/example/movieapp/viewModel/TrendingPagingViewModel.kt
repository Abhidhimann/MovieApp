package com.example.movieapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.movieapp.data.remote.model.common.RecommendationItem
import com.example.movieapp.data.datasource.RecommendationPagingSource
import com.example.movieapp.data.datasource.MovieDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrendingPagingViewModel @Inject constructor(private val dataSource: MovieDataSource) : ViewModel() {
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


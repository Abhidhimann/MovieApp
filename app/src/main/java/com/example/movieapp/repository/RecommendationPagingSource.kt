package com.example.movieapp.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapp.model.common.RecommendationItem
import com.example.movieapp.repository.movie.MovieDataSource

class RecommendationPagingSource(private val dataSource: MovieDataSource, private val startPage: Int) :
    PagingSource<Int, RecommendationItem>() {
    override fun getRefreshKey(state: PagingState<Int, RecommendationItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecommendationItem> {
        val position = params.key ?: startPage
        return try {
            val response = dataSource.getTrendingRecommendationByPage(position)
            LoadResult.Page(
                data = response.recommendationList,
                prevKey = null,
                nextKey = if (position >= response.totalPages) null else (position + 1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
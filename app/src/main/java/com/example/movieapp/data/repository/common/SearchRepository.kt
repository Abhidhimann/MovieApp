package com.example.movieapp.data.repository.common

import com.example.movieapp.data.datasource.MovieDataSource
import com.example.movieapp.data.datasource.SeriesDataSource
import com.example.movieapp.data.remote.model.movies.MoviesItemListResponse
import com.example.movieapp.data.remote.model.tvSeries.SeriesItemListResponse
import com.example.movieapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepository(
    private val movieDataSource: MovieDataSource,
    private val seriesDataSource: SeriesDataSource
) {
    suspend fun searchMovie(query: String, page: Int): Result<MoviesItemListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieDataSource.searchMovie(query, page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun searchSeries(query: String, page: Int): Result<SeriesItemListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = seriesDataSource.searchSeries(query, page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

}
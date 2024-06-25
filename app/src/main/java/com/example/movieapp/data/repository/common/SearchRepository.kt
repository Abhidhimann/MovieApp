package com.example.movieapp.data.repository.common

import com.example.movieapp.data.datasource.MovieDataSource
import com.example.movieapp.data.datasource.SeriesDataSource
import com.example.movieapp.data.remote.model.common.RecommendationItem
import com.example.movieapp.data.remote.model.common.RecommendationResponse
import com.example.movieapp.data.remote.model.movies.MoviesItemListResponse
import com.example.movieapp.data.remote.model.tvSeries.SeriesItemListResponse
import com.example.movieapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepository(
    private val movieDataSource: MovieDataSource,
    private val seriesDataSource: SeriesDataSource
) {

//    suspend fun searchResult(
//        query: String,
//        moviePageNum: Int,
//        seriesPageNum: Int
//    ): Result<Pair<RecommendationResponse, RecommendationResponse>> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val movies = movieDataSource.searchMovie(query, moviePageNum)
//                val series = seriesDataSource.searchSeries(query, seriesPageNum)
//                Result.Success(Pair(movies, series))
//            } catch (e: Exception) {
//                Result.Error(e)
//            }
//        }
//    }


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
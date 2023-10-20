package com.example.movieapp.repository.series

import android.util.Log
import com.example.movieapp.model.tvSeries.SeriesItemListResponse
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.CustomeApiFailedException
import com.example.movieapp.api.TvApiInterface
import com.example.movieapp.model.common.RecommendationResponse
import com.example.movieapp.model.common.ReviewResponse
import com.example.movieapp.model.tvSeries.SeriesDetails
import com.example.movieapp.model.tvSeries.SeriesGenresResponse
import com.example.movieapp.model.tvSeries.SeriesImagesResponse
import com.example.movieapp.utils.getClassTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Response

class SeriesDataSource(private val apiService: TvApiInterface) {

    private suspend inline fun <reified T> getDataFromApiWithRetry(
        crossinline apiCall: suspend () -> Response<T>,
        retryCount: Int = 3,
        retryLimitSec: Long = 2000L,
        tag: String
    ): T = withContext(Dispatchers.IO) {
        var currentRetry = 0

        while (currentRetry < retryCount) {
            try {
                val response = apiCall.invoke()
                if (response.isSuccessful && response.body() != null) {
                    Log.d(tag, "Response successful : $response")
                    return@withContext response.body()!!
                } else {
                    Log.d(tag, "Response unsuccessful: $response")
                }
            } catch (e: Exception) {
                Log.d(tag, "Error in network request: $e")
            }

            currentRetry++
            delay(retryLimitSec)
        }

        throw CustomeApiFailedException("series api request failed after $retryCount attempts")
    }

    suspend fun getTrendingSeriesInWeek(page: Int): SeriesItemListResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTrendingTvSeriesInWeek(Api.API_KEY.getValue(),page)
        })

    suspend fun getPopularSeries(page: Int): SeriesItemListResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getPopularTvSeries(Api.API_KEY.getValue(), page)
        })

    suspend fun getImdbTopRatedSeries(page: Int): SeriesItemListResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getImdbTopRatedTvSeries(Api.API_KEY.getValue(), page)
        })

    suspend fun getAiringSeries(page: Int): SeriesItemListResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getAiringTvSeries(Api.API_KEY.getValue(), page)
        })

    suspend fun getSeriesByGenre(genreId: Int, page: Int): SeriesItemListResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTvSeriesByGenre(Api.API_KEY.getValue(), genreId, page)
        })

    suspend fun searchSeries(query: String, page: Int): SeriesItemListResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.searchTvSeries(Api.API_KEY.getValue(), query, page)
        })

    suspend fun getSeriesDetails(movieId: Long): SeriesDetails =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTvSeriesDetails(apiKey = Api.API_KEY.getValue(), id = movieId)
        })

    suspend fun getReviewsOnSeries(movieId: Long): ReviewResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getReviewsOnTvSeries(movieId,Api.API_KEY.getValue())
        })

    suspend fun getSeriesImages(movieId: Long): SeriesImagesResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTvSeriesImages(movieId,Api.API_KEY.getValue())
        })

    suspend fun getSeriesRecommendations(movieId: Long): RecommendationResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getRecommendationsByTvSeries(movieId,Api.API_KEY.getValue())
        })

    suspend fun getSeriesGenreList(): SeriesGenresResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTvSeriesGenres(Api.API_KEY.getValue())
        })

    suspend fun getTrendingRecommendation(): RecommendationResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTrendingRecommendation(Api.API_KEY.getValue())
        })
}
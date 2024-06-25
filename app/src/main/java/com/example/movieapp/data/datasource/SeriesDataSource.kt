package com.example.movieapp.data.datasource

import android.util.Log
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.CustomeApiFailedException
import com.example.movieapp.data.remote.network.TvApiService
import com.example.movieapp.data.remote.model.common.RecommendationResponse
import com.example.movieapp.data.remote.model.common.ReviewResponse
import com.example.movieapp.data.remote.model.common.VideoResponse
import com.example.movieapp.data.remote.model.tvSeries.SeriesDetails
import com.example.movieapp.data.remote.model.tvSeries.SeriesGenresResponse
import com.example.movieapp.data.remote.model.tvSeries.SeriesImagesResponse
import com.example.movieapp.data.remote.model.tvSeries.SeriesItemListResponse
import com.example.movieapp.data.remote.model.tvSeries.TvSeasonDetails
import com.example.movieapp.utils.getClassTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Response

class SeriesDataSource(private val apiService: TvApiService) {

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

    suspend fun getSeriesDetails(seriesId: Long): SeriesDetails =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTvSeriesDetails(apiKey = Api.API_KEY.getValue(), id = seriesId)
        })

    suspend fun getReviewsOnSeries(seriesId: Long): ReviewResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getReviewsOnTvSeries(seriesId,Api.API_KEY.getValue())
        })

    suspend fun getSeriesImages(seriesId: Long): SeriesImagesResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTvSeriesImages(seriesId,Api.API_KEY.getValue())
        })

    suspend fun getSeriesVideos(seriesId: Long): VideoResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getSeriesVideos(seriesId,Api.API_KEY.getValue())
        })

    suspend fun getSeriesRecommendations(seriesId: Long): RecommendationResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getRecommendationsByTvSeries(seriesId,Api.API_KEY.getValue())
        })

    suspend fun getSeriesGenreList(): SeriesGenresResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTvSeriesGenres(Api.API_KEY.getValue())
        })

    suspend fun getTrendingRecommendation(): RecommendationResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTrendingRecommendation(Api.API_KEY.getValue())
        })

    suspend fun getSeasonDetails(id: Long, seasonNum: Int): TvSeasonDetails =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
        apiService.getSeasonDetails(id, seasonNum, Api.API_KEY.getValue())
    })
}
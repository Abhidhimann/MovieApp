package com.example.movieapp.repository.series

import com.example.movieapp.model.common.RecommendationResponse
import com.example.movieapp.model.tvSeries.SeriesDetails
import com.example.movieapp.model.tvSeries.SeriesItemListResponse
import com.example.movieapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.math.min

class SeriesDataRepository(private val dataSource: SeriesDataSource) {

    suspend fun getTrendingMoviesInWeek(page: Int): Result<SeriesItemListResponse> {
        return withContext(Dispatchers.IO){
            try {
                val response = dataSource.getTrendingSeriesInWeek(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getPopularSeries(page: Int): Result<SeriesItemListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dataSource.getPopularSeries(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getImdbRatedSeries(page: Int): Result<SeriesItemListResponse>{
        return withContext(Dispatchers.IO) {
            try {
                val response = dataSource.getImdbTopRatedSeries(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getAiringSeries(page: Int): Result<SeriesItemListResponse>{
        return withContext(Dispatchers.IO) {
            try {
                val response = dataSource.getAiringSeries(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun searchSeries(query: String ,page: Int): Result<SeriesItemListResponse>{
        return withContext(Dispatchers.IO) {
            try {
                val response = dataSource.searchSeries(query,page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }




    suspend fun getSeriesDetails(movieId: Long): Result<SeriesDetails> {
        return withContext(Dispatchers.IO) {
            try {
                // 3 independent network calls
                val responseDeff = async {
                    dataSource.getSeriesDetails(movieId)
                }
                val reviewsOnSeriesDeff = async {
                    dataSource.getReviewsOnSeries(movieId)
                }
                val SeriesImagesDeff = async {
                    dataSource.getSeriesImages(movieId)
                }

                val recommendationsDeff = async {
                    dataSource.getSeriesRecommendations(movieId)
                }

                awaitAll(responseDeff,reviewsOnSeriesDeff,SeriesImagesDeff,recommendationsDeff)  // time = max of 4
                val seriesDetails = responseDeff.await()
                val reviews = reviewsOnSeriesDeff.await().reviews
                val imagesUrl = SeriesImagesDeff.await().images
                val recommendations = recommendationsDeff.await()
                seriesDetails.setReviews(reviews.take(min(4, reviews.size)))
//  only taking 5 images & 4 reviews
                seriesDetails.setSeriesImages(imagesUrl.take(min(5, imagesUrl.size)).map { it.url })
                seriesDetails.setRecommendationList(recommendations.recommendationList)
                Result.Success(seriesDetails)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getTrendingRecommendation(): Result<RecommendationResponse>{
        return withContext(Dispatchers.IO){
            try {
                val trendingRecommendation = dataSource.getTrendingRecommendation()
                Result.Success(trendingRecommendation)
            }catch (e: Exception){
                Result.Error(e)
            }
        }
    }
}
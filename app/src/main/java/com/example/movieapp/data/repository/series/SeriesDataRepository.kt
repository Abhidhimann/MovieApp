package com.example.movieapp.data.repository.series

import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.datasource.SeriesDataSource
import com.example.movieapp.data.remote.model.common.RecommendationResponse
import com.example.movieapp.data.remote.model.movies.MovieDetails
import com.example.movieapp.data.remote.model.movies.MovieDetails.Companion.toSavedItemEntity
import com.example.movieapp.data.remote.model.tvSeries.SeriesDetails
import com.example.movieapp.data.remote.model.tvSeries.SeriesDetails.Companion.toSavedItemEntity
import com.example.movieapp.data.remote.model.tvSeries.SeriesItemListResponse
import com.example.movieapp.data.remote.model.tvSeries.TvSeasonDetails
import com.example.movieapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.math.min

class SeriesDataRepository(private val seriesDataSource: SeriesDataSource, private val savedItemLocalDataSource: SavedItemLocalDataSource) {

    suspend fun getTrendingMoviesInWeek(page: Int): Result<SeriesItemListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = seriesDataSource.getTrendingSeriesInWeek(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getPopularSeries(page: Int): Result<SeriesItemListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = seriesDataSource.getPopularSeries(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getImdbRatedSeries(page: Int): Result<SeriesItemListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = seriesDataSource.getImdbTopRatedSeries(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getAiringSeries(page: Int): Result<SeriesItemListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = seriesDataSource.getAiringSeries(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getSeriesDetails(seriesId: Long): Result<SeriesDetails> {
        return withContext(Dispatchers.IO) {
            try {
                // 3 independent network calls
                val responseDeff = async {
                    seriesDataSource.getSeriesDetails(seriesId)
                }
                val reviewsOnSeriesDeff = async {
                    seriesDataSource.getReviewsOnSeries(seriesId)
                }
                val seriesImagesDeff = async {
                    seriesDataSource.getSeriesImages(seriesId)
                }

                val seriesVideosDeff = async {
                    seriesDataSource.getSeriesVideos(seriesId)
                }

                val recommendationsDeff = async {
                    seriesDataSource.getSeriesRecommendations(seriesId)
                }

                awaitAll(
                    responseDeff,
                    reviewsOnSeriesDeff,
                    seriesImagesDeff,
                    recommendationsDeff,
                    seriesVideosDeff
                )  // time = max of 4

                val seriesDetails = responseDeff.await()
                val reviews = reviewsOnSeriesDeff.await().reviews
                val imagesUrl = seriesImagesDeff.await().images
                val recommendations = recommendationsDeff.await()
                val trailer = seriesVideosDeff.await().trailers.getOrNull(0)
                seriesDetails.setReviews(reviews.take(min(4, reviews.size)))
//  only taking 5 images & 4 reviews
                seriesDetails.setSeriesImages(imagesUrl.take(min(5, imagesUrl.size)).map { it.url })
                seriesDetails.setRecommendationList(recommendations.recommendationList)
                seriesDetails.setYouTubeTrailer(trailer)
                Result.Success(seriesDetails)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getTvSeasonDetails(seriesId: Long, seasonNumber: Int): Result<TvSeasonDetails> {
        return withContext(Dispatchers.IO) {
            try {
                val tvSeasonDetails = seriesDataSource.getSeasonDetails(seriesId, seasonNumber)
                Result.Success(tvSeasonDetails)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun saveSeries(seriesDetails: SeriesDetails){
        savedItemLocalDataSource.insertSavedItem(seriesDetails.toSavedItemEntity())
    }

    suspend fun deleteSeries(seriesDetails: SeriesDetails){
        savedItemLocalDataSource.deleteSavedItem(seriesDetails.toSavedItemEntity())
    }

    suspend fun isSeriesSaved(itemId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext savedItemLocalDataSource.isItemSaved(itemId)
    }
}
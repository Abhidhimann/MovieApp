package com.example.movieapp.data.repository.movie

import com.example.movieapp.data.datasource.MovieDataSource
import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.remote.model.movies.MovieDetails
import com.example.movieapp.data.remote.model.movies.MoviesItemListResponse
import com.example.movieapp.data.remote.model.common.RecommendationResponse
import com.example.movieapp.data.remote.model.movies.MovieDetails.Companion.toSavedItemEntity
import com.example.movieapp.utils.Result
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

@Singleton
class MovieDataRepository @Inject constructor(private val movieDataSource: MovieDataSource, private val savedItemLocalDataSource: SavedItemLocalDataSource) {

    suspend fun getTrendingMoviesInWeek(page: Int): Result<MoviesItemListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieDataSource.getTrendingMoviesInWeek(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getPopularMovies(page: Int): Result<MoviesItemListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieDataSource.getPopularMovies(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getImdbRatedMovies(page: Int): Result<MoviesItemListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieDataSource.getImdbTopRatedMovies(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getUpcomingMovies(page: Int): Result<MoviesItemListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieDataSource.getUpComingMovies(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }


    suspend fun getMovieDetails(movieId: Long): Result<MovieDetails> {
        return withContext(Dispatchers.IO) {
            try {
                // 3 independent network calls
                val responseDeff = async {
                    movieDataSource.getMovieDetails(movieId)
                }
                val reviewsOnMoviesDeff = async {
                    movieDataSource.getReviewsOnMovie(movieId)
                }
                val movieImagesDeff = async {
                    movieDataSource.getMovieImages(movieId)
                }
                val recommendationsDeff = async {
                    movieDataSource.getMoviesRecommendations(movieId)
                }
                val movieVideosDeff = async {
                    movieDataSource.getMovieVideos(movieId)
                }
                awaitAll(
                    responseDeff,
                    reviewsOnMoviesDeff,
                    movieImagesDeff,
                    recommendationsDeff,
                    movieVideosDeff
                )  // time = max of 4
                val movieDetails = responseDeff.await()
                val reviews = reviewsOnMoviesDeff.await().reviews
                val imagesUrl = movieImagesDeff.await().images
                val recommendations = recommendationsDeff.await()
                val trailer = movieVideosDeff.await().trailers.getOrNull(0)
                movieDetails.setReviews(reviews.take(min(4, reviews.size)))
//  only taking 5 images & 4 reviews
                movieDetails.setMovieImages(imagesUrl.take(min(5, imagesUrl.size)).map { it.url })
                movieDetails.setRecommendationList(recommendations.recommendationList)
                movieDetails.setYouTubeTrailer(trailer)
                Result.Success(movieDetails)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getTrendingRecommendation(): Result<RecommendationResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val trendingRecommendation = movieDataSource.getTrendingRecommendation()
                Result.Success(trendingRecommendation)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun saveMovie(movieDetails: MovieDetails){
        savedItemLocalDataSource.insertSavedItem(movieDetails.toSavedItemEntity())
    }

    suspend fun deleteMovie(movieDetails: MovieDetails){
        savedItemLocalDataSource.deleteSavedItem(movieDetails.toSavedItemEntity())
    }

    suspend fun isMovieSaved(itemId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext savedItemLocalDataSource.isItemSaved(itemId)
    }
}
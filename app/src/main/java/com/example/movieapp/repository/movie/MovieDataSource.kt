package com.example.movieapp.repository.movie

import android.util.Log
import com.example.movieapp.api.MovieApiInterface
import com.example.movieapp.model.common.RecommendationResponse
import com.example.movieapp.model.common.ReviewResponse
import com.example.movieapp.model.movies.*
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.CustomeApiFailedException
import com.example.movieapp.utils.Tags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Response

class MovieDataSource(private val apiService: MovieApiInterface) {

    // maybe will put it in helper functions class
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

        throw CustomeApiFailedException("movie api request failed after $retryCount attempts")
    }


    suspend fun getPopularMovies(page: Int): MoviesItemListResponse =
        getDataFromApiWithRetry(tag = Tags.MOVIE_DATA_SOURCE.getTag(), apiCall = {
            apiService.getPopularMovies(Api.API_KEY.getValue(), page)
        })

    suspend fun getTrendingMoviesInWeek(page: Int): MoviesItemListResponse =
        getDataFromApiWithRetry(tag = Tags.MOVIE_DATA_SOURCE.getTag(), apiCall = {
            apiService.getTrendingMoviesInWeek(Api.API_KEY.getValue(), page)
        })

    suspend fun getImdbTopRatedMovies(page: Int): MoviesItemListResponse =
        getDataFromApiWithRetry(tag = Tags.MOVIE_DATA_SOURCE.getTag(), apiCall = {
            apiService.getImdbTopRatedMovies(Api.API_KEY.getValue(), page)
        })

    suspend fun getUpComingMovies(page: Int): MoviesItemListResponse =
        getDataFromApiWithRetry(tag = Tags.MOVIE_DATA_SOURCE.getTag(), apiCall = {
            apiService.getUpComingMovies(Api.API_KEY.getValue(), page)
        })

    suspend fun getMoviesByGenre(genreId: Int, page: Int): MoviesItemListResponse =
        getDataFromApiWithRetry(tag = Tags.MOVIE_DATA_SOURCE.getTag(), apiCall = {
            apiService.getMoviesByGenre(Api.API_KEY.getValue(), genreId, page)
        })

    suspend fun searchMovie(query: String, page: Int): MoviesItemListResponse =
    getDataFromApiWithRetry(tag = Tags.MOVIE_DATA_SOURCE.getTag(), apiCall = {
        apiService.searchMovie(Api.API_KEY.getValue(), query, page)
    })

    suspend fun getMovieDetails(movieId: Long): MovieDetails =
        getDataFromApiWithRetry(tag = Tags.MOVIE_DATA_SOURCE.getTag(), apiCall = {
            apiService.getMovieDetails(apiKey = Api.API_KEY.getValue(), id = movieId)
        })

    suspend fun getReviewsOnMovie(movieId: Long): ReviewResponse =
        getDataFromApiWithRetry(tag = Tags.MOVIE_DATA_SOURCE.getTag(), apiCall = {
            apiService.getReviewsOnMovie(movieId,Api.API_KEY.getValue())
        })

    suspend fun getMovieImages(movieId: Long): MovieImagesResponse =
        getDataFromApiWithRetry(tag = Tags.MOVIE_DATA_SOURCE.getTag(), apiCall = {
            apiService.getMovieImages(movieId,Api.API_KEY.getValue())
        })

    suspend fun getMoviesRecommendations(movieId: Long): RecommendationResponse =
        getDataFromApiWithRetry(tag = Tags.MOVIE_DATA_SOURCE.getTag(), apiCall = {
            apiService.getRecommendationsByMovie(movieId,Api.API_KEY.getValue())
        })

    suspend fun getMovieGenreList(): MovieGenresResponse =
        getDataFromApiWithRetry(tag = Tags.MOVIE_DATA_SOURCE.getTag(), apiCall = {
            apiService.getMoviesGenres(Api.API_KEY.getValue())
        })

    suspend fun getTrendingRecommendation(): RecommendationResponse =
        getDataFromApiWithRetry(tag = Tags.MOVIE_DATA_SOURCE.getTag(), apiCall = {
            apiService.getTrendingRecommendation(Api.API_KEY.getValue())
        })
}

//    suspend fun getPopularMovies(page: Int): MoviesItemListResponse = withContext(Dispatchers.IO) {
//        var currentRetry = 0
//
//        while (currentRetry < retryCount) {
//            try {
//                val response = apiService.getUpComingMovies(Api.MOVIE_DB_API_KEY.getValue(), page)
//                if (response.isSuccessful && response.body() != null) {
//                    Log.d(TAG, "Response successful : $response")
//                    return@withContext response.body()!!
//                } else {
//                    Log.d(TAG, "Response unsuccessful : $response")
//                }
//            } catch (e: Exception) {
//                Log.d(TAG, "Error in network request: ${e.message}")
//            }
//
//            // Increment the retry count and wait for a delay before the next attempt
//            currentRetry++
//            delay(retryLimitSec)
//        }
//
//        throw CustomeApiFailedException("Popular movie fetching failed after $retryCount attempts")
//    }
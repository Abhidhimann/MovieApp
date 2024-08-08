package com.example.movieapp.data.datasource

import android.util.Log
import com.example.movieapp.data.remote.network.MovieApiService
import com.example.movieapp.data.remote.model.common.RecommendationResponse
import com.example.movieapp.data.remote.model.common.ReviewResponse
import com.example.movieapp.data.remote.model.common.VideoResponse
import com.example.movieapp.data.remote.model.movies.MovieDetails
import com.example.movieapp.data.remote.model.movies.MovieGenresResponse
import com.example.movieapp.data.remote.model.movies.MovieImagesResponse
import com.example.movieapp.data.remote.model.movies.MoviesItemListResponse
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.CustomApiFailedException
import com.example.movieapp.utils.getClassTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDataSource @Inject constructor(private val apiService: MovieApiService) {

    // maybe will put it in helper functions class
    private suspend inline fun <reified T> getDataFromApiWithRetry(
        crossinline apiCall: suspend () -> Response<T>,
        retryCount: Int = 3,
        retryLimitSec: Long = 3000L,
        tag: String
    ): T = withContext(Dispatchers.IO) {
        var currentRetry = 0

        while (currentRetry < retryCount) {
            try {
                val response = apiCall.invoke()
                if (response.isSuccessful && response.body() != null) {
//                    Log.d(tag, "Response successful : $response")
                    return@withContext response.body()!!
                } else {
                    Log.d(tag, "Response unsuccessful")
                }
            } catch (e: Exception) {
                Log.d(tag, "Error in network request: $e")
            }

            currentRetry++
            delay(retryLimitSec)
        }

        throw CustomApiFailedException("movie api request failed after $retryCount attempts")
    }


    suspend fun getPopularMovies(page: Int): MoviesItemListResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getPopularMovies(Api.API_KEY.getValue(), page)
        })

    suspend fun getTrendingMoviesInWeek(page: Int): MoviesItemListResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTrendingMoviesInWeek(Api.API_KEY.getValue(), page)
        })

    suspend fun getImdbTopRatedMovies(page: Int): MoviesItemListResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getImdbTopRatedMovies(Api.API_KEY.getValue(), page)
        })

    suspend fun getUpComingMovies(page: Int): MoviesItemListResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getUpComingMovies(Api.API_KEY.getValue(), page)
        })

    suspend fun getMoviesByGenre(genreId: Int, page: Int): MoviesItemListResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getMoviesByGenre(Api.API_KEY.getValue(), genreId, page)
        })

    suspend fun searchMovie(query: String, page: Int): MoviesItemListResponse =
    getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
        apiService.searchMovie(Api.API_KEY.getValue(), query, page)
    })

    suspend fun getMovieDetails(movieId: Long): MovieDetails =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getMovieDetails(apiKey = Api.API_KEY.getValue(), id = movieId)
        })

    suspend fun getReviewsOnMovie(movieId: Long): ReviewResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getReviewsOnMovie(movieId,Api.API_KEY.getValue())
        })

    suspend fun getMovieImages(movieId: Long): MovieImagesResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getMovieImages(movieId,Api.API_KEY.getValue())
        })

    suspend fun getMovieVideos(movieId: Long): VideoResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getMovieVideos(movieId,Api.API_KEY.getValue())
        })

    suspend fun getMoviesRecommendations(movieId: Long): RecommendationResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getRecommendationsByMovie(movieId,Api.API_KEY.getValue())
        })

    suspend fun getMovieGenreList(): MovieGenresResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getMoviesGenres(Api.API_KEY.getValue())
        })

    suspend fun getTrendingRecommendation(): RecommendationResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTrendingRecommendation(Api.API_KEY.getValue())
        })

    suspend fun getTrendingRecommendationByPage(page: Int): RecommendationResponse =
        getDataFromApiWithRetry(tag = getClassTag(), apiCall = {
            apiService.getTrendingRecommendationByPage(Api.API_KEY.getValue(), page)
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
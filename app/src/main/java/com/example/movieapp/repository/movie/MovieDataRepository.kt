package com.example.movieapp.repository.movie

import com.example.movieapp.model.movies.MovieDetails
import com.example.movieapp.model.movies.MoviesItemListResponse
import com.example.movieapp.model.RecommendationResponse
import com.example.movieapp.utils.Result
import kotlinx.coroutines.*
import kotlin.math.min

class MovieDataRepository(private val dataSource: MovieDataSource) {

    suspend fun getTrendingMoviesInWeek(page: Int): Result<MoviesItemListResponse>{
        return withContext(Dispatchers.IO){
            try {
                val response = dataSource.getTrendingMoviesInWeek(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getPopularMovies(page: Int): Result<MoviesItemListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = dataSource.getPopularMovies(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getImdbRatedMovies(page: Int): Result<MoviesItemListResponse>{
        return withContext(Dispatchers.IO) {
            try {
                val response = dataSource.getImdbTopRatedMovies(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getUpcomingMovies(page: Int): Result<MoviesItemListResponse>{
        return withContext(Dispatchers.IO) {
            try {
                val response = dataSource.getUpComingMovies(page)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun searchMovie(query: String ,page: Int): Result<MoviesItemListResponse>{
        return withContext(Dispatchers.IO) {
            try {
                val response = dataSource.searchMovie(query,page)
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
                    dataSource.getMovieDetails(movieId)
                }
                val reviewsOnMoviesDeff = async {
                    dataSource.getReviewsOnMovie(movieId)
                }
                val movieImagesDeff = async {
                    dataSource.getMovieImages(movieId)
                }

                val recommendationsDeff = async {
                    dataSource.getMoviesRecommendations(movieId)
                }

                awaitAll(responseDeff,reviewsOnMoviesDeff,movieImagesDeff,recommendationsDeff)  // time = max of 4
                val movieDetails = responseDeff.await()
                val reviews = reviewsOnMoviesDeff.await().reviews
                val imagesUrl = movieImagesDeff.await().images
                val recommendations = recommendationsDeff.await()
                movieDetails.setReviews(reviews.take(min(4, reviews.size)))
//  only taking 5 images & 4 reviews
                movieDetails.setMovieImages(imagesUrl.take(min(5, imagesUrl.size)).map { it.url })
                movieDetails.setRecommendationList(recommendations.recommendationList)
                Result.Success(movieDetails)
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
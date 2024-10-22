package com.example.movieapp.data.remote.network

import com.example.movieapp.data.remote.model.common.RecommendationResponse
import com.example.movieapp.data.remote.model.common.ReviewResponse
import com.example.movieapp.data.remote.model.common.VideoResponse
import com.example.movieapp.data.remote.model.movies.MovieDetails
import com.example.movieapp.data.remote.model.movies.MovieGenresResponse
import com.example.movieapp.data.remote.model.movies.MovieImagesResponse
import com.example.movieapp.data.remote.model.movies.MoviesItemListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("genre/movie/list")
    suspend fun getMoviesGenres(
        @Query("api_key") apiKey: String,
    ): Response<MovieGenresResponse>

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<MoviesItemListResponse>

    @GET("trending/movie/week")
    suspend fun getTrendingMoviesInWeek(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<MoviesItemListResponse>

    @GET("movie/top_rated")
    suspend fun getImdbTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<MoviesItemListResponse>

    @GET("movie/upcoming")
    suspend fun getUpComingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<MoviesItemListResponse>

    @GET("movie/{movie_id}/recommendations")
    suspend fun getRecommendationsByMovie(
        @Path("movie_id") id: Long,
        @Query("api_key") apiKey: String
    ): Response<RecommendationResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") id: Long,
        @Query("api_key") apiKey: String
    ): Response<MovieDetails>


    @GET("movie/{movie_id}/images")
    suspend fun getMovieImages(
        @Path("movie_id") id: Long,
        @Query("api_key") apiKey: String
    ): Response<MovieImagesResponse>

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") id: Long,
        @Query("api_key") apiKey: String
    ): Response<VideoResponse>

    @GET("movie/{movie_id}/reviews")
    suspend fun getReviewsOnMovie(
        @Path("movie_id") id: Long,
        @Query("api_key") apiKey: String
    ): Response<ReviewResponse>

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int
    ): Response<MoviesItemListResponse>

    @GET("search/movie")
    suspend fun searchMovie(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<MoviesItemListResponse>

    @GET("trending/all/week?language=en-US")
    suspend fun getTrendingRecommendation(
        @Query("api_key") apiKey: String
    ): Response<RecommendationResponse>

    @GET("trending/all/week?language=en-US")
    suspend fun getTrendingRecommendationByPage(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<RecommendationResponse>

}
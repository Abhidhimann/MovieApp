package com.example.movieapp.data.remote.network

import com.example.movieapp.data.remote.model.common.RecommendationResponse
import com.example.movieapp.data.remote.model.common.ReviewResponse
import com.example.movieapp.data.remote.model.common.VideoResponse
import com.example.movieapp.data.remote.model.tvSeries.SeriesDetails
import com.example.movieapp.data.remote.model.tvSeries.SeriesGenresResponse
import com.example.movieapp.data.remote.model.tvSeries.SeriesImagesResponse
import com.example.movieapp.data.remote.model.tvSeries.SeriesItemListResponse
import com.example.movieapp.data.remote.model.tvSeries.TvSeasonDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvApiService {

    @GET("genre/tv/list")
    suspend fun getTvSeriesGenres(
        @Query("api_key") apiKey: String,
    ): Response<SeriesGenresResponse>

    @GET("tv/popular")
    suspend fun getPopularTvSeries(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<SeriesItemListResponse>

    @GET("trending/tv/week")
    suspend fun getTrendingTvSeriesInWeek(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<SeriesItemListResponse>

    @GET("tv/top_rated")
    suspend fun getImdbTopRatedTvSeries(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<SeriesItemListResponse>

    @GET("tv/on_the_air")
    suspend fun getAiringTvSeries(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<SeriesItemListResponse>

    @GET("tv/{series_id}/recommendations")
    suspend fun getRecommendationsByTvSeries(
        @Path("series_id") id: Long,
        @Query("api_key") apiKey: String
    ): Response<RecommendationResponse>

    @GET("tv/{series_id}")
    suspend fun getTvSeriesDetails(
        @Path("series_id") id: Long,
        @Query("api_key") apiKey: String
    ): Response<SeriesDetails>


    @GET("tv/{series_id}/images")
    suspend fun getTvSeriesImages(
        @Path("series_id") id: Long,
        @Query("api_key") apiKey: String
    ): Response<SeriesImagesResponse>

    @GET("tv/{series_id}/videos")
    suspend fun getSeriesVideos(
        @Path("series_id") id: Long,
        @Query("api_key") apiKey: String
    ): Response<VideoResponse>


    @GET("tv/{series_id}/reviews")
    suspend fun getReviewsOnTvSeries(
        @Path("series_id") id: Long,
        @Query("api_key") apiKey: String
    ): Response<ReviewResponse>

    @GET("discover/tvSeries")
    suspend fun getTvSeriesByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int
    ): Response<SeriesItemListResponse>

    @GET("search/tv")
    suspend fun searchTvSeries(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<SeriesItemListResponse>

    @GET("trending/all/week")
    suspend fun getTrendingRecommendation(
        @Query("api_key") apiKey: String
    ): Response<RecommendationResponse>

    @GET("tv/{series_id}/season/{season_number}")
    suspend fun getSeasonDetails(
        @Path("series_id") id: Long,
        @Path("seasons_number") seasonNumber: Int,
        @Query("api_key") apiKey: String
    ): Response<TvSeasonDetails>
}
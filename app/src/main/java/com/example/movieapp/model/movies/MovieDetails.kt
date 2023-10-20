package com.example.movieapp.model.movies

import com.example.movieapp.model.common.RecommendationItem
import com.example.movieapp.model.common.Review
import com.google.gson.annotations.SerializedName

data class MovieDetails(
    @SerializedName("title")
    val title: String,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("overview")
    val movieOverview: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val rating: Float,
    @SerializedName("vote_count")
    val totalVotes: Int,
    @SerializedName("poster_path")
    val posterImg: String,
    @SerializedName("runtime")
    val length: Int,
    @SerializedName("genres")
    val genres: List<MovieGenre>,
    private var images: List<String>,  // these we will set later by different apis
    private var reviews: List<Review>,
    private var recommendationList: List<RecommendationItem>
){
    fun setMovieImages( movieImages: List<String>){
        this.images = movieImages
    }

    fun getMovieImages(): List<String>{
        return this.images
    }

    fun getReviews(): List<Review>{
        return this.reviews
    }

    fun setReviews(movieReviews: List<Review>){
       this.reviews = movieReviews
    }

    fun setRecommendationList(recommendationList: List<RecommendationItem>){
        this.recommendationList = recommendationList
    }

    fun getRecommendationList(): List<RecommendationItem>{
        return this.recommendationList
    }
}

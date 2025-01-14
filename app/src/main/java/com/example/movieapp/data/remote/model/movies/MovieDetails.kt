package com.example.movieapp.data.remote.model.movies

import com.example.movieapp.data.local.entity.SavedItemEntity
import com.example.movieapp.data.remote.model.common.RecommendationItem
import com.example.movieapp.data.remote.model.common.Review
import com.example.movieapp.data.remote.model.common.Trailer
import com.example.movieapp.utils.Constants
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
    val posterImg: String?,
    @SerializedName("runtime")
    val length: Int,
    @SerializedName("genres")
    val genres: List<MovieGenre>,
    private var images: List<String>,  // these we will set later by different apis
    private var reviews: List<Review>,
    private var recommendationList: List<RecommendationItem>,
    private var youTubeTrailer: Trailer?
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

    fun setYouTubeTrailer(trailer: Trailer?){
        this.youTubeTrailer = trailer
    }

    fun getYouTubeTrailer(): Trailer? {
        return this.youTubeTrailer
    }
    companion object {
        fun MovieDetails.toSavedItemEntity(): SavedItemEntity {
            return SavedItemEntity(
                itemId = this.id,
                title = this.title,
                mediaType = Constants.MOVIE.getValue(),
                rating = this.rating,
                releaseDate = this.releaseDate,
                thumbnail = this.posterImg?: ""
            )
        }
    }
}

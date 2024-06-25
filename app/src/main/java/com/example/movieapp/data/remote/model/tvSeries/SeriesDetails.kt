package com.example.movieapp.data.remote.model.tvSeries

import com.example.movieapp.data.local.entity.SavedItemEntity
import com.example.movieapp.data.remote.model.common.RecommendationItem
import com.example.movieapp.data.remote.model.common.Review
import com.example.movieapp.data.remote.model.common.Trailer
import com.example.movieapp.data.remote.model.movies.MovieDetails
import com.example.movieapp.data.remote.model.movies.MovieGenre
import com.example.movieapp.utils.Constants
import com.google.gson.annotations.SerializedName

data class SeriesDetails(
    @SerializedName("name")
    val title: String,
    @SerializedName("original_name")
    val originalTitle: String,
    @SerializedName("overview")
    val seriesOverview: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("first_air_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val rating: Float,
    @SerializedName("vote_count")
    val totalVotes: Int,
    @SerializedName("poster_path")
    val posterImg: String,
    @SerializedName("last_episode_to_air")
    val runTimeDetails: SeriesRunTimeDetails,
    @SerializedName("genres")
    val genres: List<MovieGenre>,
    @SerializedName("seasons")
    val seasons: List<TvSeasonDetails>,
    private var images: List<String>,  // these we will set later by different apis
    private var reviews: List<Review>,
    private var recommendationList: List<RecommendationItem>,
    private var youTubeTrailer: Trailer,
) {
    fun setSeriesImages(movieImages: List<String>) {
        this.images = movieImages
    }

    fun getSeriesImages(): List<String> {
        return this.images
    }

    fun getReviews(): List<Review> {
        return this.reviews
    }

    fun setReviews(movieReviews: List<Review>) {
        this.reviews = movieReviews
    }

    fun setRecommendationList(recommendationList: List<RecommendationItem>) {
        this.recommendationList = recommendationList
    }

    fun getRecommendationList(): List<RecommendationItem> {
        return this.recommendationList
    }

    fun setYouTubeTrailer(trailer: Trailer){
        this.youTubeTrailer = trailer
    }

    fun getYouTubeTrailer(): Trailer {
        return this.youTubeTrailer
    }

    companion object {
        fun SeriesDetails.toSavedItemEntity(): SavedItemEntity {
            return SavedItemEntity(
                itemId = this.id,
                title = this.title,
                mediaType = Constants.TV_SERIES.getValue(),
                rating = this.rating,
                releaseDate = this.releaseDate,
                thumbnail = this.posterImg
            )
        }
    }
}

data class SeriesRunTimeDetails(
    @SerializedName("runtime")
    val averageRunTime: Int
)

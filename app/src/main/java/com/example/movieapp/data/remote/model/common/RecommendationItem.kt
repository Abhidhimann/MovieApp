package com.example.movieapp.data.remote.model.common

import com.example.movieapp.data.local.entity.SavedItemEntity
import com.example.movieapp.utils.Constants
import com.google.gson.annotations.SerializedName

data class RecommendationItem(
    @SerializedName("title")
    val movieTitle: String?,
    @SerializedName("name")
    val tvSeriesTitle: String?,
    @SerializedName("id")
    val id: Long,
    @SerializedName("vote_average")
    val rating: Float,
    @SerializedName("poster_path")
    val posterImg: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("first_air_date")
    val airingDate: String?,
    @SerializedName("media_type")
    val mediaType: String
) {
    val title: String?
        get() = movieTitle ?: tvSeriesTitle
    val postingDate: String?
        get() = releaseDate ?: airingDate

    companion object {
        fun fromSavedItemEntity(itemEntity: SavedItemEntity): RecommendationItem {
            return RecommendationItem(
                movieTitle = if (itemEntity.mediaType == Constants.MOVIE.getValue()) itemEntity.title else null,
                tvSeriesTitle = if (itemEntity.mediaType == Constants.TV_SERIES.getValue()) itemEntity.title else null,
                id = itemEntity.itemId,
                rating = itemEntity.rating,
                posterImg = itemEntity.thumbnail,
                releaseDate = if (itemEntity.mediaType == Constants.MOVIE.getValue()) itemEntity.releaseDate else null,
                airingDate = if (itemEntity.mediaType == Constants.TV_SERIES.getValue()) itemEntity.releaseDate else null,
                mediaType = itemEntity.mediaType,
            )
        }

        fun RecommendationItem.toSavedItemEntity(): SavedItemEntity {
            return SavedItemEntity(
                itemId = this.id,
                title = this.title ?: "No Title",
                rating = this.rating,
                releaseDate = this.postingDate ?: "",
                mediaType = this.mediaType,
                thumbnail = this.posterImg?: ""
            )
        }
    }
}

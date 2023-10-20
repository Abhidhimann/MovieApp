package com.example.movieapp.model.common

import com.google.gson.annotations.SerializedName

data class RecommendationItem(
    @SerializedName("title")
    val movieTitle: String,
    @SerializedName("name")
    val TvSeriesTitle: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("vote_average")
    val rating: Float,
    @SerializedName("poster_path")
    val posterImg: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("first_air_date")
    val airingDate: String,
    @SerializedName("media_type")
    val mediaType: String
){
    val title: String
        get() = movieTitle ?: TvSeriesTitle
    val postingDate: String
        get() = releaseDate?: airingDate
}

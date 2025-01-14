package com.example.movieapp.data.remote.model.tvSeries

import com.google.gson.annotations.SerializedName

data class SeriesItem(
    @SerializedName("name")
    val title: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("vote_average")
    val rating: Float,
    @SerializedName("poster_path")
    val posterImg: String?,
    @SerializedName("first_air_date")
    val releaseDate: String,
    @SerializedName("number_of_seasons")
    val seasonNumber: String
)

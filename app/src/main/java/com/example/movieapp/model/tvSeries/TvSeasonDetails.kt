package com.example.movieapp.model.tvSeries

import com.google.gson.annotations.SerializedName

data class TvSeasonDetails(
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("air_date")
    val airDate: String,
    @SerializedName("episode_count")
    val episodes: Int
)

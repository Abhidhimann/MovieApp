package com.example.movieapp.model.movies

import com.google.gson.annotations.SerializedName

data class MovieItem(
    @SerializedName("title")
    val title: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("vote_average")
    val rating: Float,
    @SerializedName("poster_path")
    val posterImg: String,
    @SerializedName("release_date")
    val releaseDate: String
)

package com.example.movieapp.data.remote.model.movies

import com.google.gson.annotations.SerializedName

data class MovieGenre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class MovieGenresResponse(
    @SerializedName("genres")
    val genres: List<MovieGenre>
)
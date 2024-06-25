package com.example.movieapp.data.remote.model.tvSeries


import com.google.gson.annotations.SerializedName

data class SeriesGenre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class SeriesGenresResponse(
    @SerializedName("genres")
    val genres: List<SeriesGenre>
)
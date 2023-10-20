package com.example.movieapp.model.movies

import com.google.gson.annotations.SerializedName

data class MovieImagesResponse(
    @SerializedName("posters")
    val images: List<MovieImages>
)
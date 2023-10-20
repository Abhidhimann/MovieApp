package com.example.movieapp.model.movies

import com.google.gson.annotations.SerializedName

data class MovieImages(
    @SerializedName("file_path")
    val url: String
)
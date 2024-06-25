package com.example.movieapp.data.remote.model.movies

import com.google.gson.annotations.SerializedName

data class MovieImages(
    @SerializedName("file_path")
    val url: String
)
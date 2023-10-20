package com.example.movieapp.model.tvSeries

import com.example.movieapp.model.movies.MovieImages
import com.google.gson.annotations.SerializedName

data class SeriesImages(
    @SerializedName("file_path")
    val url: String
)

data class SeriesImagesResponse(
    @SerializedName("posters")
    val images: List<MovieImages>
)
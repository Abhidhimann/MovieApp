package com.example.movieapp.data.remote.model.common

import com.google.gson.annotations.SerializedName

data class VideoResponse(
    @SerializedName("results")
    val trailers: List<Trailer>,
    @SerializedName("id")
    val id: Long
)

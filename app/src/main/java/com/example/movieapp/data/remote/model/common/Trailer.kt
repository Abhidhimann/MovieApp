package com.example.movieapp.data.remote.model.common

import com.google.gson.annotations.SerializedName

data class Trailer(
    @SerializedName("id")
    val trailerId: String,
    @SerializedName("key")
    val trailerKey: String,
)

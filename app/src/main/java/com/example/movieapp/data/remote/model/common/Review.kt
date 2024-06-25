package com.example.movieapp.data.remote.model.common

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("author")
    val author: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("created_at")
    val createdAt: String,
)

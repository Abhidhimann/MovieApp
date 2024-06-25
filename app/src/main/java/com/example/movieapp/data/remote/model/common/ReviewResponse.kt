package com.example.movieapp.data.remote.model.common

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val reviews: List<Review>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int,
    @SerializedName("id")
    val id: Long
)

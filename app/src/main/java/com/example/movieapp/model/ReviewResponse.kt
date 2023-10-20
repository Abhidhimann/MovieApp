package com.example.movieapp.model

import com.example.movieapp.model.Review
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

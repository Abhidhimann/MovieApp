package com.example.movieapp.model.common

import com.example.movieapp.model.common.RecommendationItem
import com.google.gson.annotations.SerializedName

data class RecommendationResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val recommendationList: List<RecommendationItem>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)
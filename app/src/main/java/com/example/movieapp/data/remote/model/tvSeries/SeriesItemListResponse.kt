package com.example.movieapp.data.remote.model.tvSeries

import com.google.gson.annotations.SerializedName

data class SeriesItemListResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val seriesList: List<SeriesItem>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

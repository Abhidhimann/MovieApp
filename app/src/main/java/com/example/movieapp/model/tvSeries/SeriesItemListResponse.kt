package com.example.movieapp.model.tvSeries

import com.example.movieapp.model.movies.MovieItem
import com.google.gson.annotations.SerializedName

data class SeriesItemListResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val SeriesList: List<SeriesItem>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

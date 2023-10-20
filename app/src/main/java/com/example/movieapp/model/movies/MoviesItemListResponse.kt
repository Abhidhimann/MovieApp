package com.example.movieapp.model.movies

import com.google.gson.annotations.SerializedName

// Some fields are not necessary will see if removing them is better
data class MoviesItemListResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val movieList: List<MovieItem>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)
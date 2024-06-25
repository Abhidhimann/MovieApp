package com.example.movieapp.utils

import com.example.movieapp.data.datasource.MovieDataSource
import com.example.movieapp.ui.activities.MovieDetailsUi
import com.example.movieapp.ui.fragments.series.HomeSeriesList

enum class Tags{
    TEMP_TAG,
    MOVIE_DETAILS_UI,
    MOVIE_DATA_SOURCE,
    SERIES_LIST_FRAGMENT;

    fun getTag(): String
         = when (this) {
            TEMP_TAG -> "Temp Tag"
            MOVIE_DETAILS_UI -> MovieDetailsUi::class.java.simpleName
            MOVIE_DATA_SOURCE -> MovieDataSource::class.java.simpleName
            SERIES_LIST_FRAGMENT -> HomeSeriesList::class.java.simpleName
        }
}

// this is good, i guess for tags
fun Any.getClassTag(): String {
    return this::class.java.simpleName
}

fun Any.tempTag(): String {
    return "temp tag"
}
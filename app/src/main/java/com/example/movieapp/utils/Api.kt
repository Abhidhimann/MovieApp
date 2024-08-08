package com.example.movieapp.utils

import com.example.movieapp.BuildConfig

enum class Api(private val url: String) {
    BASE_URL("https://api.themoviedb.org/3/"),
    API_KEY(BuildConfig.MOVIE_DB_API_KEY),
    POSTER_BASE_URL("https://image.tmdb.org/t/p/w342"),
    TIME_OUT("6");

    fun getValue(): String{
        return url
    }
}
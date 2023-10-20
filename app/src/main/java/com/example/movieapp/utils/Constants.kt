package com.example.movieapp.utils

enum class Constants(private val value: String) {
    MOVIE_ID("MOVIE_ID"),
    MOVIE("movie"),
    TV_SERIES("tv"),
    TV_SERIES_ID("TV_SERIES_ID");

    fun getValue(): String{
        return value
    }

   enum class MoviesType(val value: String){
       POPULAR_MOVIES("popular_movies"),
       IMDB_RATED_MOVIES("imdb_movies"),
       UPCOMING_MOVIES("upcoming_movies");
   }
}
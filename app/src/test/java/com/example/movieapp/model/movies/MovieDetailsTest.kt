package com.example.movieapp.model.movies

import com.example.movieapp.data.remote.model.common.RecommendationItem
import com.example.movieapp.data.remote.model.common.Review
import com.example.movieapp.data.remote.model.common.Trailer
import com.example.movieapp.data.remote.model.movies.MovieDetails
import com.example.movieapp.data.remote.model.movies.MovieGenre
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MovieDetailsTest{

    private lateinit var movieDetails: MovieDetails
    private lateinit var reviews: List<Review>
    private lateinit var recommendationList: List<RecommendationItem>

    @Before
    fun setUp() {
        movieDetails = MovieDetails(
            "Movie Title",
            "Original Title",
            "Movie Overview",
            1L,
            "2023-10-31",
            4.5f,
            1000,
            "poster.jpg",
            120,
            listOf(MovieGenre(12,"Name")),
            emptyList(),
            emptyList(),
            emptyList(),
            Trailer("noId", "noKey")
        )

        reviews = listOf(Review("User1", "Great movie", "2012"), Review("User2", "Awesome", "2012"))

        recommendationList = listOf(RecommendationItem("sf", "sf",12,(9.3).toFloat(),"sdf","2013","sf","movie" ))

    }

    @Test
    fun `set and get movie images`() {
        val images = listOf("image1.jpg", "image2.jpg")
        movieDetails.setMovieImages(images)
        assertEquals(images, movieDetails.getMovieImages())
    }

    @Test
    fun `set and get reviews`() {
        movieDetails.setReviews(reviews)
        assertEquals(reviews, movieDetails.getReviews())
    }

    @Test
    fun `set and get recommendation list`() {
        movieDetails.setRecommendationList(recommendationList)
        assertEquals(recommendationList, movieDetails.getRecommendationList())
    }
}
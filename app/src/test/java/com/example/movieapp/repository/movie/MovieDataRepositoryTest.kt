package com.example.movieapp.repository.movie

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.movieapp.data.remote.model.movies.MovieItem
import com.example.movieapp.data.remote.model.movies.MoviesItemListResponse
import com.example.movieapp.data.repository.movie.MovieDataRepository
import com.example.movieapp.data.datasource.MovieDataSource
import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.example.movieapp.utils.Result
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MovieDataRepositoryTest {
    @Mock
    private lateinit var movieDataSource: MovieDataSource
    @Mock
    private lateinit var savedItemLocalDataSource: SavedItemLocalDataSource
    private lateinit var repository: MovieDataRepository

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val initialPage = 1
    private val mockMoviesList = listOf(MovieItem("Iron man", 999, (6.3).toFloat(), "temp", "2021"))
    private val mockMoviesResponse = MoviesItemListResponse(
        page = 1,
        movieList = mockMoviesList,
        totalPages = 1,
        totalResults = mockMoviesList.size
    )
    private val mockError = Exception("An error occurred")

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        repository = MovieDataRepository(movieDataSource, savedItemLocalDataSource)
    }

    @Test
    fun `getTrendingMoviesInWeek Success`() = runTest {
        `when`(movieDataSource.getTrendingMoviesInWeek(initialPage)).thenReturn(mockMoviesResponse)
        val result = repository.getTrendingMoviesInWeek(initialPage)
        verify(movieDataSource).getTrendingMoviesInWeek(initialPage)
        assert(result is Result.Success)
        assert(result.data == mockMoviesResponse)
    }

    @Test
    fun `getTrendingMoviesInWeek Error`() = runTest {
        val runtimeException = java.lang.RuntimeException("temp")
        `when`(movieDataSource.getTrendingMoviesInWeek(initialPage)).thenThrow(runtimeException)
        val result = repository.getTrendingMoviesInWeek(initialPage)
        verify(movieDataSource).getTrendingMoviesInWeek(initialPage)
        assert(result is Result.Error)
        assert(result.exception == runtimeException)
    }
}
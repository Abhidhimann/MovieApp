package com.example.movieapp.viewModel.movie

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.movieapp.data.remote.model.movies.MovieItem
import com.example.movieapp.data.remote.model.movies.MoviesItemListResponse
import com.example.movieapp.data.repository.movie.MovieDataRepository
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.CustomApiFailedException
import com.example.movieapp.utils.getOrAwaitValue
import junit.framework.TestCase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import com.example.movieapp.utils.Result

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeoutException

class BaseMovieListViewModelTest {

    @Mock
    private lateinit var repository: MovieDataRepository
    private lateinit var viewModel: BaseMovieListViewModel

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
    private val popularMovies = Constants.MoviesType.POPULAR_MOVIES.value
    private val imdbRatedMovies = Constants.MoviesType.IMDB_RATED_MOVIES.value
    private val upComingMovies = Constants.MoviesType.UPCOMING_MOVIES.value

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = BaseMovieListViewModel(repository)
    }


    @Test
    fun `getPopularMovies success`() = runTest {
        Mockito.`when`(repository.getPopularMovies(initialPage))
            .thenReturn(Result.Success(mockMoviesResponse))

        viewModel.processMoviesType(popularMovies, initialPage)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.loadingState.getOrAwaitValue())
        assertEquals(viewModel.moviesList.getOrAwaitValue(), mockMoviesResponse)
    }

    @Test
    fun `getPopularMovies error`() = runTest {
        Mockito.`when`(repository.getPopularMovies(initialPage))
            .thenReturn(Result.Error(CustomApiFailedException("sdf")))

        viewModel.processMoviesType(popularMovies, initialPage)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.loadingState.getOrAwaitValue())
        assertTrue(viewModel.errorState.getOrAwaitValue())
        // as movie list will not get set in this case, according to out implementation of liveData
        // Utils, it will throw TimeOutException
        assertThrows<TimeoutException> { viewModel.moviesList.getOrAwaitValue() }
    }

    @Test
    fun `getImdbRatedMovies success`() = runTest {

        Mockito.`when`(repository.getImdbRatedMovies(initialPage))
            .thenReturn(Result.Success(mockMoviesResponse))

        viewModel.processMoviesType(imdbRatedMovies, initialPage)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.loadingState.getOrAwaitValue())
        assertEquals(viewModel.moviesList.getOrAwaitValue(), mockMoviesResponse)
    }

    @Test
    fun `getImdbRatedMovies error`() = runTest {
        Mockito.`when`(repository.getImdbRatedMovies(initialPage))
            .thenReturn(Result.Error(CustomApiFailedException("sdf")))

        viewModel.processMoviesType(imdbRatedMovies, initialPage)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.loadingState.getOrAwaitValue())
        assertTrue(viewModel.errorState.getOrAwaitValue())
        // as movie list will not get set in this case, according to out implementation of liveData
        // Utils, it will throw TimeOutException
        assertThrows<TimeoutException> { viewModel.moviesList.getOrAwaitValue() }
    }

    @Test
    fun `getUpcomingMovies success`() = runTest {

        Mockito.`when`(repository.getUpcomingMovies(initialPage))
            .thenReturn(Result.Success(mockMoviesResponse))

        viewModel.processMoviesType(upComingMovies, initialPage)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.loadingState.getOrAwaitValue())
        assertEquals(viewModel.moviesList.getOrAwaitValue(), mockMoviesResponse)
    }

    @Test
    fun `getUpcomingMovie error`() = runTest {
        Mockito.`when`(repository.getUpcomingMovies(initialPage))
            .thenReturn(Result.Error(CustomApiFailedException("sdf")))

        viewModel.processMoviesType(upComingMovies, initialPage)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.loadingState.getOrAwaitValue())
        assertTrue(viewModel.errorState.getOrAwaitValue())
        // as movie list will not get set in this case, according to out implementation of liveData
        // Utils, it will throw TimeOutException
        assertThrows<TimeoutException> { viewModel.moviesList.getOrAwaitValue() }
    }

    @Test
    fun startLoadingTest() {
        // to access class private methods use this
        val method = BaseMovieListViewModel::class.java.getDeclaredMethod("startLoading")
        method.isAccessible = true
        method.invoke(viewModel)
        assertTrue(viewModel.loadingState.getOrAwaitValue())
    }

    @Test
    fun allIndexToInitial(){
        viewModel.allIndexToInitial()
        assertEquals(viewModel.currentPage, 1)
        assertEquals(viewModel.listInitialIndex, 0)
        assertEquals(viewModel.listLastIndex, 0)
    }
}
package com.example.movieapp.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.model.movies.MoviesItemListResponse
import com.example.movieapp.repository.movie.MovieDataRepository
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Result
import kotlinx.coroutines.launch

class BaseMovieListViewModel(private val repository: MovieDataRepository) : ViewModel() {

    private var _moviesList = MutableLiveData<MoviesItemListResponse>()
    val moviesList: LiveData<MoviesItemListResponse>
        get() = _moviesList

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String>
        get() = _errorState

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    var currentPage = 1
    var listInitialIndex = 0
    var listLastIndex = 0

    fun allIndexToInitial(){
        currentPage = 1
        listLastIndex = 0
        listInitialIndex = 0
    }

    fun processMoviesType(moviesType: String, page: Int) {
        when (moviesType) {
            Constants.MoviesType.POPULAR_MOVIES.value -> {
                getPopularMovies(page)
            }
            Constants.MoviesType.IMDB_RATED_MOVIES.value -> {
                getImdbRatedMovies(page)
            }
            Constants.MoviesType.UPCOMING_MOVIES.value -> {
                getUpcomingMovies(page)
            }
        }
    }

   private fun getPopularMovies(page: Int) = viewModelScope.launch {
        when (val result = repository.getPopularMovies(page)) {
            is Result.Success -> {
                _moviesList.value = result.data
                _loadingState.value = false
            }
            is Result.Error -> {
                _errorState.value = result.exception?.message
                Log.i("Abhi", "errpr")
                // ui change according to error state
            }
        }
    }

    private fun getImdbRatedMovies(page: Int) = viewModelScope.launch {
        when (val result = repository.getImdbRatedMovies(page)) {
            is Result.Success -> {
                _moviesList.value = result.data
                _loadingState.value = false
            }
            is Result.Error -> {
                _errorState.value = result.exception?.message
                Log.i("Abhi", "errpr")
                // ui change according to error state
            }
        }
    }

    private fun getUpcomingMovies(page: Int) = viewModelScope.launch {
        when (val result = repository.getUpcomingMovies(page)) {
            is Result.Success -> {
                _moviesList.value = result.data
                _loadingState.value = false
            }
            is Result.Error -> {
                _errorState.value = result.exception?.message
                Log.i("Abhi", "errpr")
                // ui change according to error state
            }
        }
    }



}

class BaseMovieListViewModeFactory(private val repository: MovieDataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BaseMovieListViewModel::class.java)) { //If the modelClass is not compatible with HomePageViewModel, it throws an IllegalArgumentException with the message "Unknown ViewModel class." This is a safety check to ensure that you only create instances of the expected ViewModel class.
            return BaseMovieListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.example.movieapp.viewModel.movie

import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.data.remote.model.movies.MoviesItemListResponse
import com.example.movieapp.data.repository.movie.MovieDataRepository
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Result
import com.example.movieapp.utils.getClassTag
import kotlinx.coroutines.launch

class BaseMovieListViewModel(private val repository: MovieDataRepository) : ViewModel() {

    private var _moviesList = MutableLiveData<MoviesItemListResponse>()
    val moviesList: LiveData<MoviesItemListResponse>
        get() = _moviesList

    private val _errorState = MutableLiveData<Boolean>()
    val errorState: LiveData<Boolean>
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

    private fun startLoading(){
        _loadingState.value = true
    }

    fun processMoviesType(moviesType: String, page: Int) {
        startLoading()
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
                _loadingState.value = false
                _errorState.value = true
            }
        }
    }

    private fun getImdbRatedMovies(page: Int) = viewModelScope.launch {
        when (val result = repository.getImdbRatedMovies(page)) {
            is Result.Success -> {
                _moviesList.value = result.data!!
                _loadingState.value = false
            }
            is Result.Error -> {
                _loadingState.value = false
                _errorState.value = true
//                Log.i(getClassTag(), result.exception.toString())
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
                _loadingState.value = false
                _errorState.value = true
//                Log.i(getClassTag(), result.exception.toString())
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

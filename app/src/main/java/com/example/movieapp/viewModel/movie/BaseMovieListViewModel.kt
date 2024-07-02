package com.example.movieapp.viewModel.movie

import androidx.lifecycle.*
import com.example.movieapp.data.remote.model.movies.MoviesItemListResponse
import com.example.movieapp.data.repository.movie.MovieDataRepository
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseMovieListViewModel @Inject constructor(private val repository: MovieDataRepository) : ViewModel() {

    private var _moviesList = MutableLiveData<MoviesItemListResponse>()
    val moviesList: LiveData<MoviesItemListResponse>
        get() = _moviesList

    private val _errorState = MutableLiveData<Boolean>()
    val errorState: LiveData<Boolean>
        get() = _errorState

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _currentPageLiveValue = MutableLiveData<Int>()
    val currentPageLiveValue : LiveData<Int> = _currentPageLiveValue

    var currentPage = 1
    var listInitialIndex = 0
    var listLastIndex = 0

    fun allIndexToInitial(){
//        currentPage = 1
        listLastIndex = 0
        listInitialIndex = 0
    }

    fun incrementCurrentPage(value: Int){
        currentPage = value
        _currentPageLiveValue.postValue(value)
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
                _moviesList.value = result.data!!
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
                _moviesList.value = result.data!!
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

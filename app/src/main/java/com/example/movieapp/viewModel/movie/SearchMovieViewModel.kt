package com.example.movieapp.viewModel.movie

import androidx.lifecycle.*
import com.example.movieapp.data.remote.model.movies.MoviesItemListResponse
import com.example.movieapp.data.remote.model.tvSeries.SeriesItemListResponse
import com.example.movieapp.data.repository.common.SearchRepository
import com.example.movieapp.utils.Result
import kotlinx.coroutines.launch

class SearchMovieViewModel(private val repository: SearchRepository) : ViewModel() {

    private var _moviesList = MutableLiveData<MoviesItemListResponse>()
    val moviesList: LiveData<MoviesItemListResponse>
        get() = _moviesList

    private var _seriesList = MutableLiveData<SeriesItemListResponse>()
    val seriesList: LiveData<SeriesItemListResponse>
        get() = _seriesList


    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String>
        get() = _errorState

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    var currentMoviePage = 1
    var movieListInitialIndex = 0
    var movieListLastIndex = 0

    var currentSeriesPage = 1
    var seriesListInitialIndex = 0
    var seriesListLastIndex = 0

    fun allMovieIndexToInitial() {
        currentMoviePage = 1
        movieListLastIndex = 0
        movieListInitialIndex = 0
    }

    fun allSeriesIndexToInitial() {
        currentSeriesPage = 1
        seriesListLastIndex = 0
        seriesListInitialIndex = 0
    }



    fun searchMovies(query: String, page: Int) = viewModelScope.launch {
        when (val result = repository.searchMovie(query, page)) {
            is Result.Success -> {
                _moviesList.value = result.data!!
                _loadingState.value = false
            }

            is Result.Error -> {
                _errorState.value = result.exception?.message
                // ui change according to error state
            }
        }
    }

    fun searchSeries(query: String, page: Int) = viewModelScope.launch {
        when (val result = repository.searchSeries(query, page)) {
            is Result.Success -> {
                _seriesList.value = result.data!!
                _loadingState.value = false
            }

            is Result.Error -> {
                _errorState.value = result.exception?.message
                // ui change according to error state
            }
        }
    }
}

class SearchMovieViewModelFactory(private val repository: SearchRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchMovieViewModel::class.java)) { //If the modelClass is not compatible with HomePageViewModel, it throws an IllegalArgumentException with the message "Unknown ViewModel class." This is a safety check to ensure that you only create instances of the expected ViewModel class.
            return SearchMovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
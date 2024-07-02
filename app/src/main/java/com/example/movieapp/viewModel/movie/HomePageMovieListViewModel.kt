package com.example.movieapp.viewModel.movie

import androidx.lifecycle.*
import com.example.movieapp.data.remote.model.movies.MoviesItemListResponse
import com.example.movieapp.data.repository.movie.MovieDataRepository
import com.example.movieapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Simple, easiest & easy to main things is 1 view (Activity) -> 1 view model
// or 1 to many ( 1 view - many view model)
@HiltViewModel
class HomePageMovieListViewModel @Inject constructor(private val repository: MovieDataRepository) : ViewModel() {

    private var _trendingMovies = MutableLiveData<MoviesItemListResponse>()
    val trendingMovies: LiveData<MoviesItemListResponse>
        get() = _trendingMovies

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String>
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
        listInitialIndex = 0
        listLastIndex = 0
        _currentPageLiveValue.postValue(value)
    }

    private fun startLoading(){
        _loadingState.value = true
    }

    fun getTrendingMovies(page: Int) = viewModelScope.launch {
        startLoading()
        when (val result = repository.getTrendingMoviesInWeek(page)) {
            is Result.Success -> {
                _trendingMovies.value = result.data!!
                _loadingState.value = false
            }
            is Result.Error -> {
                _errorState.value = result.exception?.message
                // ui change according to error state
            }
        }
    }

}
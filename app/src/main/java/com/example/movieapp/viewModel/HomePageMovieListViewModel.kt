package com.example.movieapp.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.model.movies.MoviesItemListResponse
import com.example.movieapp.repository.movie.MovieDataRepository
import com.example.movieapp.utils.Result
import kotlinx.coroutines.launch

// Simple, easiest & easy to main things is 1 view (Activity) -> 1 view model
// or 1 to many ( 1 view - many view model)
class HomePageMovieListViewModel(private val repository: MovieDataRepository) : ViewModel() {

    private var _trendingMovies = MutableLiveData<MoviesItemListResponse>()
    val trendingMovies: LiveData<MoviesItemListResponse>
        get() = _trendingMovies

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String>
        get() = _errorState

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState


    var initialPage = 1
    var listInitialIndex = 0
    var listLastIndex = 0

    fun allIndexToInitial(){
        initialPage = 1
        listLastIndex = 0
        listInitialIndex = 0
    }

    fun getTrendingMovies(page: Int) = viewModelScope.launch {
        when (val result = repository.getTrendingMoviesInWeek(page)) {
            is Result.Success -> {
                _trendingMovies.value = result.data
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

class HomePageMovieListViewModelFactory(private val repository: MovieDataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomePageMovieListViewModel::class.java)) { //If the modelClass is not compatible with HomePageViewModel, it throws an IllegalArgumentException with the message "Unknown ViewModel class." This is a safety check to ensure that you only create instances of the expected ViewModel class.
            return HomePageMovieListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
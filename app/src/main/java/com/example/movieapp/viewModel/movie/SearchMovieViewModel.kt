package com.example.movieapp.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.model.movies.MoviesItemListResponse
import com.example.movieapp.repository.movie.MovieDataRepository
import com.example.movieapp.utils.Result
import kotlinx.coroutines.launch

class SearchMovieViewModel(private val repository: MovieDataRepository): ViewModel() {

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

    fun getSearchedMovies(query: String, page: Int) = viewModelScope.launch {
        when (val result = repository.searchMovie(query, page)) {
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
class SearchMovieViewModelFactory(private val repository: MovieDataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchMovieViewModel::class.java)) { //If the modelClass is not compatible with HomePageViewModel, it throws an IllegalArgumentException with the message "Unknown ViewModel class." This is a safety check to ensure that you only create instances of the expected ViewModel class.
            return SearchMovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
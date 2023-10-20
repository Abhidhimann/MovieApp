package com.example.movieapp.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.model.movies.MovieDetails
import com.example.movieapp.repository.movie.MovieDataRepository
import com.example.movieapp.utils.Result
import kotlinx.coroutines.launch

class MovieDetailsViewModel(private val repository: MovieDataRepository): ViewModel() {

    private var _movieDetails = MutableLiveData<MovieDetails>()
    val movieDetails: LiveData<MovieDetails>
        get() = _movieDetails

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String>
        get() = _errorState

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    var listInitialIndex = 0
    var listLastIndex = 0

    fun getMovieDetails(movieId: Long) = viewModelScope.launch {
        when (val result = repository.getMovieDetails(movieId)) {
            is Result.Success -> {
                _movieDetails.value = result.data
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
class MovieDetailsViewModelFactory(private val repository: MovieDataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDetailsViewModel::class.java)) { //If the modelClass is not compatible with HomePageViewModel, it throws an IllegalArgumentException with the message "Unknown ViewModel class." This is a safety check to ensure that you only create instances of the expected ViewModel class.
            return MovieDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
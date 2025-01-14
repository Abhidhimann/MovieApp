package com.example.movieapp.viewModel.movie

import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.data.remote.model.movies.MovieDetails
import com.example.movieapp.data.repository.movie.MovieDataRepository
import com.example.movieapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(private val repository: MovieDataRepository) : ViewModel() {

    private var _movieDetails = MutableLiveData<MovieDetails>()
    val movieDetails: LiveData<MovieDetails>
        get() = _movieDetails

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String>
        get() = _errorState

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _isMovieSaved = MutableLiveData<Boolean>()
    val isMovieSaved: LiveData<Boolean> = _isMovieSaved

    var listInitialIndex = 0
    var listLastIndex = 0

    fun allIndexToInitial(){
        listLastIndex = 0
        listInitialIndex = 0
    }

    fun getMovieDetails(movieId: Long) = viewModelScope.launch {
        when (val result = repository.getMovieDetails(movieId)) {
            is Result.Success -> {
                _movieDetails.value = result.data!!
                _loadingState.value = false
            }

            is Result.Error -> {
                _errorState.value = result.exception?.message
                Log.i("Abhi", "error message ${_errorState.value}")
                // ui change according to error state
            }
        }
    }

    fun saveMovie(movieDetails: MovieDetails) = viewModelScope.launch {
        repository.saveMovie(movieDetails)
        isMovieSaved(movieDetails.id)
    }

    fun deleteMovie(movieDetails: MovieDetails) = viewModelScope.launch {
        repository.deleteMovie(movieDetails)
        isMovieSaved(movieDetails.id)
    }

    fun isMovieSaved(movieId: Long) = viewModelScope.launch {
        _isMovieSaved.postValue(repository.isMovieSaved(movieId))
    }
}

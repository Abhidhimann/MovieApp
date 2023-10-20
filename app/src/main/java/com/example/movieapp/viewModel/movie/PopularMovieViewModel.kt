package com.example.movieapp.viewModel


import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.model.movies.MovieDetails
import com.example.movieapp.model.movies.MoviesItemListResponse
import com.example.movieapp.repository.movie.MovieDataRepository
import com.example.movieapp.utils.Result
import kotlinx.coroutines.launch

class PopularMovieViewModel(private val repository: MovieDataRepository): ViewModel() {

    companion object{
        private val TAG = PopularMovieViewModel::class.java.name
    }

    private var _popularMovies = MutableLiveData<MoviesItemListResponse>()
    val popularMovies:  LiveData<MoviesItemListResponse>
        get() = _popularMovies

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String>
        get() = _errorState

    private var _movieDetails = MutableLiveData<MovieDetails>()
    val movieDetails:  LiveData<MovieDetails>
        get() = _movieDetails

    fun getPopularMovies(page: Int) = viewModelScope.launch {
        when (val result = repository.getPopularMovies(page)){
          is Result.Success -> {
              _popularMovies.value = result.data
          }
            is Result.Error -> {
                _errorState.value = result.exception?.message
                Log.i("Abhi","errpr")
                // ui change according to error state
            }
        }
    }

    fun getMovieDetails(movieId: Long)  = viewModelScope.launch {
        when (val result = repository.getMovieDetails(movieId)){
            is Result.Success -> {
                _movieDetails.value = result.data
            }
            is Result.Error -> {
                _errorState.value = result.exception?.message
                Log.i("Abhi",result.exception.toString())
                // ui change according to error state
            }
        }
    }

}
class PopularMovieViewModelFactory(private val repository: MovieDataRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PopularMovieViewModel::class.java)) { //If the modelClass is not compatible with HomePageViewModel, it throws an IllegalArgumentException with the message "Unknown ViewModel class." This is a safety check to ensure that you only create instances of the expected ViewModel class.
            return PopularMovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
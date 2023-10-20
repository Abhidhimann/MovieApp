package com.example.movieapp.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.model.RecommendationResponse
import com.example.movieapp.repository.movie.MovieDataRepository
import com.example.movieapp.utils.Result
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repository: MovieDataRepository): ViewModel() {

    private var _trending = MutableLiveData<RecommendationResponse>()
    val trending: LiveData<RecommendationResponse>
        get() = _trending

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String>
        get() = _errorState

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    fun getTrendingRecommendation() = viewModelScope.launch {
        when (val result = repository.getTrendingRecommendation()) {
            is Result.Success -> {
                _trending.value = result.data
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
class MainActivityViewModelFactory(private val repository: MovieDataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) { //If the modelClass is not compatible with HomePageViewModel, it throws an IllegalArgumentException with the message "Unknown ViewModel class." This is a safety check to ensure that you only create instances of the expected ViewModel class.
            return MainActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
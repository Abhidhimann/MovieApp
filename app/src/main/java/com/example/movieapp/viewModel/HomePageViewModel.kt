package com.example.movieapp.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.data.remote.model.common.RecommendationResponse
import com.example.movieapp.data.repository.movie.MovieDataRepository
import com.example.movieapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(private val repository: MovieDataRepository): ViewModel() {

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
                // really don't know why
                result.data?.let {
                    _trending.value = it
                }
                _loadingState.value = false
            }
            is Result.Error -> {
                _errorState.value = result.exception.message
                Log.i("Abhi", "errpr")
                // ui change according to error state
            }
        }
    }
}

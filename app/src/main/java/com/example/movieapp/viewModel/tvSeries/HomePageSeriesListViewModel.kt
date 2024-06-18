package com.example.movieapp.viewModel.tvSeries

import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.model.tvSeries.SeriesItemListResponse
import com.example.movieapp.repository.series.SeriesDataRepository
import com.example.movieapp.utils.Result
import kotlinx.coroutines.launch

class HomePageSeriesListViewModel(private val repository: SeriesDataRepository) : ViewModel() {
    private var _trendingSeries = MutableLiveData<SeriesItemListResponse>()

    val trendingSeries: LiveData<SeriesItemListResponse>
        get() = _trendingSeries

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String>
        get() = _errorState

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState


    var currentPage = 1
    var listInitialIndex = 0
    var listLastIndex = 0

    fun allIndexToInitial() {
        currentPage = 1
        listLastIndex = 0
        listInitialIndex = 0
    }

    private fun startLoading(){
        _loadingState.value = true
    }

    fun getTrendingMovies(page: Int) = viewModelScope.launch {
        startLoading()
        when (val result = repository.getTrendingMoviesInWeek(page)) {
            is Result.Success -> {
                _trendingSeries.value = result.data
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

class HomePageSeriesListViewModelFactory(private val repository: SeriesDataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomePageSeriesListViewModel::class.java)) { //If the modelClass is not compatible with HomePageViewModel, it throws an IllegalArgumentException with the message "Unknown ViewModel class." This is a safety check to ensure that you only create instances of the expected ViewModel class.
            return HomePageSeriesListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
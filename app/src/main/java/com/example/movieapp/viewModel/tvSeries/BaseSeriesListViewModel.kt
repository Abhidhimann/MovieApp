package com.example.movieapp.viewModel.tvSeries


import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.model.tvSeries.SeriesItemListResponse
import com.example.movieapp.repository.series.SeriesDataRepository
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Result
import kotlinx.coroutines.launch

class BaseSeriesListViewModel(private val repository: SeriesDataRepository): ViewModel() {

    private var _seriesList = MutableLiveData<SeriesItemListResponse>()
    val seriesList: LiveData<SeriesItemListResponse>
        get() = _seriesList

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

    fun processSeriesType(seriesType: String, page: Int) {
        when (seriesType) {
            Constants.SeriesType.POPULAR_SERIES.value -> {
                getPopularSeries(page)
            }
            Constants.SeriesType.IMDB_RATED_SERIES.value -> {
                getImdbRatedSeries(page)
            }
            Constants.SeriesType.AIRING_SERIES.value -> {
                getAiringSeries(page)
            }
        }
    }

    private fun getPopularSeries(page: Int) = viewModelScope.launch {
        when (val result = repository.getPopularSeries(page)) {
            is Result.Success -> {
                _seriesList.value = result.data
                _loadingState.value = false
            }
            is Result.Error -> {
                _errorState.value = result.exception?.message
                Log.i("Abhi", "errpr")
                // ui change according to error state
            }
        }
    }

    private fun getImdbRatedSeries(page: Int) = viewModelScope.launch {
        when (val result = repository.getImdbRatedSeries(page)) {
            is Result.Success -> {
                _seriesList.value = result.data
                _loadingState.value = false
            }
            is Result.Error -> {
                _errorState.value = result.exception?.message
                Log.i("Abhi", "errpr")
                // ui change according to error state
            }
        }
    }

    private fun getAiringSeries(page: Int) = viewModelScope.launch {
        when (val result = repository.getAiringSeries(page)) {
            is Result.Success -> {
                _seriesList.value = result.data
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

class BaseSeriesListViewModeFactory(private val repository: SeriesDataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BaseSeriesListViewModel::class.java)) { //If the modelClass is not compatible with HomePageViewModel, it throws an IllegalArgumentException with the message "Unknown ViewModel class." This is a safety check to ensure that you only create instances of the expected ViewModel class.
            return BaseSeriesListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
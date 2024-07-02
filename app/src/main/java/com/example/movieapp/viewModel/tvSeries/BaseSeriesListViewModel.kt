package com.example.movieapp.viewModel.tvSeries

import androidx.lifecycle.*
import com.example.movieapp.data.remote.model.tvSeries.SeriesItemListResponse
import com.example.movieapp.data.repository.series.SeriesDataRepository
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseSeriesListViewModel @Inject constructor(private val repository: SeriesDataRepository): ViewModel() {

    private var _seriesList = MutableLiveData<SeriesItemListResponse>()
    val seriesList: LiveData<SeriesItemListResponse>
        get() = _seriesList

    private val _errorState = MutableLiveData<Boolean>()
    val errorState: LiveData<Boolean>
        get() = _errorState

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _currentPageLiveValue = MutableLiveData<Int>()
    val currentPageLiveValue : LiveData<Int> = _currentPageLiveValue

    var currentPage = 1
    var listInitialIndex = 0
    var listLastIndex = 0

    private fun startLoading(){
        _loadingState.value = true
    }

    fun incrementCurrentPage(value: Int){
        currentPage = value
        _currentPageLiveValue.postValue(value)
    }

    fun allIndexToInitial(){
//        currentPage = 1
        listLastIndex = 0
        listInitialIndex = 0
    }

    fun processSeriesType(seriesType: String, page: Int) {
        startLoading()
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
                _seriesList.value = result.data!!
                _loadingState.value = false
            }
            is Result.Error -> {
                _errorState.value = true
                // ui change according to error state
            }
        }
    }

    private fun getImdbRatedSeries(page: Int) = viewModelScope.launch {
        when (val result = repository.getImdbRatedSeries(page)) {
            is Result.Success -> {
                _seriesList.value = result.data!!
                _loadingState.value = false
            }
            is Result.Error -> {
                _errorState.value = true
                // ui change according to error state
            }
        }
    }

    private fun getAiringSeries(page: Int) = viewModelScope.launch {
        when (val result = repository.getAiringSeries(page)) {
            is Result.Success -> {
                _seriesList.value = result.data!!
                _loadingState.value = false
            }
            is Result.Error -> {
                _errorState.value = true
                // ui change according to error state
            }
        }
    }
}

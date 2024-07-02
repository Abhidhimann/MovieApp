package com.example.movieapp.viewModel.tvSeries

import androidx.lifecycle.*
import com.example.movieapp.data.remote.model.tvSeries.SeriesItemListResponse
import com.example.movieapp.data.repository.series.SeriesDataRepository
import com.example.movieapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageSeriesListViewModel @Inject constructor(private val repository: SeriesDataRepository) : ViewModel() {
    private var _trendingSeries = MutableLiveData<SeriesItemListResponse>()

    val trendingSeries: LiveData<SeriesItemListResponse>
        get() = _trendingSeries

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String>
        get() = _errorState

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _currentPageLiveValue = MutableLiveData<Int>()
    val currentPageLiveValue : LiveData<Int> = _currentPageLiveValue

    var currentPage = 1
    var listInitialIndex = 0
    var listLastIndex = 0

    fun allIndexToInitial() {
//        currentPage = 1
        listLastIndex = 0
        listInitialIndex = 0
    }

    fun incrementCurrentPage(value: Int){
        currentPage = value
        listInitialIndex = 0
        listLastIndex = 0
        _currentPageLiveValue.postValue(value)
    }

    private fun startLoading(){
        _loadingState.value = true
    }

    fun getTrendingSeries(page: Int) = viewModelScope.launch {
        startLoading()
        when (val result = repository.getTrendingSeriesInWeek(page)) {
            is Result.Success -> {
                _trendingSeries.value = result.data!!
                _loadingState.value = false
            }
            is Result.Error -> {
                _errorState.value = result.exception?.message
                // ui change according to error state
            }
        }
    }

}

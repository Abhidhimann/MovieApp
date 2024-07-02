package com.example.movieapp.viewModel.tvSeries

import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.data.remote.model.tvSeries.SeriesDetails
import com.example.movieapp.data.repository.series.SeriesDataRepository
import com.example.movieapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesDetailsViewModel @Inject constructor(private val repository: SeriesDataRepository): ViewModel() {
    private var _seriesDetails = MutableLiveData<SeriesDetails>()
    val seriesDetails: LiveData<SeriesDetails>
        get() = _seriesDetails

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String>
        get() = _errorState

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _isSeriesSaved = MutableLiveData<Boolean>()
    val isSeriesSaved: LiveData<Boolean> = _isSeriesSaved

    var listInitialIndex = 0
    var listLastIndex = 0

    fun allIndexToInitial() {
        listLastIndex = 0
        listInitialIndex = 0
    }

    fun saveSeries(seriesDetails: SeriesDetails) = viewModelScope.launch {
        repository.saveSeries(seriesDetails)
        isSeriesSaved(seriesDetails.id)
    }

    fun deleteSeries(seriesDetails: SeriesDetails) = viewModelScope.launch {
        repository.deleteSeries(seriesDetails)
        isSeriesSaved(seriesDetails.id)
    }

    fun isSeriesSaved(seriesId: Long) = viewModelScope.launch {
        _isSeriesSaved.postValue(repository.isSeriesSaved(seriesId))
    }

    fun getSeriesDetails(movieId: Long) = viewModelScope.launch {
        when (val result = repository.getSeriesDetails(movieId)) {
            is Result.Success -> {
                _seriesDetails.value = result.data!!
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

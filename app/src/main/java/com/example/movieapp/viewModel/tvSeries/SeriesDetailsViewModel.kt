package com.example.movieapp.viewModel.tvSeries

import android.util.Log
import androidx.lifecycle.*
import com.example.movieapp.data.remote.model.tvSeries.SeriesDetails
import com.example.movieapp.data.repository.series.SeriesDataRepository
import com.example.movieapp.utils.Result
import com.example.movieapp.utils.tempTag
import kotlinx.coroutines.launch

class SeriesDetailsViewModel(private val repository: SeriesDataRepository): ViewModel() {
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

    fun saveSeries(seriesDetails: SeriesDetails) = viewModelScope.launch {
        Log.i(tempTag(), "saving movie item")
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
class SeriesDetailsViewModelFactory(private val repository: SeriesDataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeriesDetailsViewModel::class.java)) { //If the modelClass is not compatible with HomePageViewModel, it throws an IllegalArgumentException with the message "Unknown ViewModel class." This is a safety check to ensure that you only create instances of the expected ViewModel class.
            return SeriesDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
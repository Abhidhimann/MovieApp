package com.example.movieapp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.local.entity.SavedItemEntity
import com.example.movieapp.data.remote.model.common.RecommendationItem
import com.example.movieapp.data.repository.common.SavedItemRepository
import com.example.movieapp.utils.tempTag
import kotlinx.coroutines.launch

class SavedItemsViewModel(private val repository: SavedItemRepository) : ViewModel() {


    fun getAllItems() = repository.getAllItemList().asLiveData()

    fun deleteSavedItem(item: SavedItemEntity) = viewModelScope.launch {
        repository.deleteSavedItem(item)
    }
}

class SavedItemsViewModelFactory(private val repository: SavedItemRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedItemsViewModel::class.java)) { //If the modelClass is not compatible with HomePageViewModel, it throws an IllegalArgumentException with the message "Unknown ViewModel class." This is a safety check to ensure that you only create instances of the expected ViewModel class.
            return SavedItemsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

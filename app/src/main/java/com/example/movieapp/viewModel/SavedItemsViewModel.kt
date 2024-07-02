package com.example.movieapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.local.entity.SavedItemEntity
import com.example.movieapp.data.repository.common.SavedItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedItemsViewModel @Inject constructor(private val repository: SavedItemRepository) : ViewModel() {

    fun getAllItems() = repository.getAllItemList().asLiveData()

}

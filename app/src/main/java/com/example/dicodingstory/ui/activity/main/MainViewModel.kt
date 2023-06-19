package com.example.dicodingstory.ui.activity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dicodingstory.data.remote.response.ListStoryItem
import com.example.dicodingstory.repository.MainRepository

class MainViewModel(private val mainRepository: MainRepository): ViewModel() {
    fun getStories(token: String) = mainRepository.getStories(token)

    fun getStoriesPaging(token: String): LiveData<PagingData<ListStoryItem>> =
        mainRepository.getStoriesPaging(token).cachedIn(viewModelScope)
}
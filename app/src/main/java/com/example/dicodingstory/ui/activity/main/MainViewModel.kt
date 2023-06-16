package com.example.dicodingstory.ui.activity.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.remote.response.StoriesResponse
import com.example.dicodingstory.data.remote.network.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel: ViewModel() {
    private val _stories = MutableLiveData<StoriesResponse>()
    val stories: LiveData<StoriesResponse> = _stories

    private val _isRefresh = MutableLiveData<Boolean>()
    val isRefresh: LiveData<Boolean> = _isRefresh

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _onFailure = MutableLiveData<String>()
    val onFailure: LiveData<String> = _onFailure

    fun getStories(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getStories("Bearer $token", 1)
                withContext(Dispatchers.Main) {
                    _isRefresh.value = false
                    _isLoading.value = false
                    _stories.value = response
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isRefresh.value = false
                    _isLoading.value = false
                    _onFailure.value = e.message
                    Log.e(TAG, "onFailure: ${e.message.toString()}")
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
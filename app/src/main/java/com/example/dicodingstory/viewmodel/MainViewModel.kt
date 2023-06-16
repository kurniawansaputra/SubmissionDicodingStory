package com.example.dicodingstory.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingstory.model.StoriesResponse
import com.example.dicodingstory.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val client = ApiConfig.getApiService().getStories("Bearer $token", 1)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(call: Call<StoriesResponse>, response: Response<StoriesResponse>) {
                _isRefresh.value = false
                _isLoading.value = false
                if (response.isSuccessful) {
                    _stories.value = response.body() as StoriesResponse
                } else {
                    _onFailure.value = response.message()
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _isRefresh.value = false
                _isLoading.value = false
                _onFailure.value = t.message
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
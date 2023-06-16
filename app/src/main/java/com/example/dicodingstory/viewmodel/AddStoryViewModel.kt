package com.example.dicodingstory.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.model.NewStoryResponse
import com.example.dicodingstory.network.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryViewModel : ViewModel() {
    private val _add = MutableLiveData<NewStoryResponse>()
    val add: LiveData<NewStoryResponse> = _add

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _onFailure = MutableLiveData<String>()
    val onFailure: LiveData<String> = _onFailure

    fun addStories(token: String, description: String, file: File, lat: Double, lon: Double) {
        val requestDescription = description.toRequestBody("text/plain".toMediaType())
        val requestLat = lat.toString().toRequestBody("text/plain".toMediaType())
        val requestLon = lon.toString().toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().newStory("Bearer $token", imageMultipart, requestDescription, requestLat, requestLon)
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _add.value = response
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _onFailure.value = e.message
                    Log.e(TAG, "onFailure: ${e.message.toString()}")
                }
            }
        }
    }

    companion object {
        private const val TAG = "AddStoryViewModel"
    }
}
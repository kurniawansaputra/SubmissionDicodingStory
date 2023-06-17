package com.example.dicodingstory.ui.activity.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.remote.network.ApiConfig
import com.example.dicodingstory.data.remote.response.NewStoryResponse
import kotlinx.coroutines.launch
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
                _add.value = response
            } catch (e: Exception) {
                _onFailure.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
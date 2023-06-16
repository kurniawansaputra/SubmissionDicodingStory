package com.example.dicodingstory.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingstory.model.NewStoryResponse
import com.example.dicodingstory.network.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel : ViewModel() {
    private val _add = MutableLiveData<NewStoryResponse>()
    val add: LiveData<NewStoryResponse> = _add

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _onFailure = MutableLiveData<String>()
    val onFailure: LiveData<String> = _onFailure

    fun addStories(token: String, description: String, file: File) {
        val requestDescription = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        _isLoading.value = true
        val client = ApiConfig.getApiService().newStory("Bearer $token", imageMultipart, requestDescription)
        client.enqueue(object : Callback<NewStoryResponse> {
            override fun onResponse(call: Call<NewStoryResponse>, response: Response<NewStoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _add.value = response.body() as NewStoryResponse
                } else {
                    _onFailure.value = response.message()
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<NewStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _onFailure.value = t.message
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "AddStoryViewModel"
    }
}
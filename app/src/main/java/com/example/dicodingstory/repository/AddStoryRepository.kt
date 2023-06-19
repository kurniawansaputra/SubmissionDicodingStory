package com.example.dicodingstory.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.dicodingstory.data.remote.Result
import com.example.dicodingstory.data.remote.network.ApiService
import com.example.dicodingstory.data.remote.response.NewStoryResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryRepository private constructor(private val apiService: ApiService){
    fun addStories(token: String, description: String, file: File, lat: Double, lon: Double) : LiveData<Result<NewStoryResponse>> = liveData {
        val requestDescription = description.toRequestBody("text/plain".toMediaType())
        val requestLat = lat.toString().toRequestBody("text/plain".toMediaType())
        val requestLon = lon.toString().toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        emit(Result.Loading)
        try {
            val response = apiService.newStory("Bearer $token", imageMultipart, requestDescription, requestLat, requestLon)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: AddStoryRepository? = null
        fun getInstance(
            apiService: ApiService
        ): AddStoryRepository =
            instance ?: synchronized(this) {
                instance ?: AddStoryRepository(apiService)
            }.also { instance = it }
    }
}
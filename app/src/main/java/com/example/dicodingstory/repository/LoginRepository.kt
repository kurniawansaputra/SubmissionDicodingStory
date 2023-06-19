package com.example.dicodingstory.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.dicodingstory.data.remote.Result
import com.example.dicodingstory.data.remote.network.ApiService
import com.example.dicodingstory.data.remote.response.UserResponse

class LoginRepository private constructor(private val apiService: ApiService){
    fun login(email: String, password: String): LiveData<Result<UserResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: LoginRepository? = null
        fun getInstance(
            apiService: ApiService
        ): LoginRepository =
            instance ?: synchronized(this) {
                instance ?: LoginRepository(apiService)
            }.also { instance = it }
    }
}
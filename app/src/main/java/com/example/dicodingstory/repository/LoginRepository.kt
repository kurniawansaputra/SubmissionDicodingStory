package com.example.dicodingstory.repository

import com.example.dicodingstory.data.remote.network.ApiService
import com.example.dicodingstory.data.remote.response.UserResponse

class LoginRepository private constructor(
    private val apiService: ApiService,
){
    suspend fun login(email: String, password: String): UserResponse {
        return apiService.login(email, password)
    }

    companion object {
        @Volatile
        private var instance: LoginRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): LoginRepository =
            instance ?: synchronized(this) {
                instance ?: LoginRepository(apiService)
            }.also { instance = it }
    }
}
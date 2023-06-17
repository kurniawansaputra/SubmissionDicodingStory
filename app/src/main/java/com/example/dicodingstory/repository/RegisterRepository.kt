package com.example.dicodingstory.repository

import com.example.dicodingstory.data.remote.network.ApiService
import com.example.dicodingstory.data.remote.response.RegisterResponse

class RegisterRepository private constructor(
    private val apiService: ApiService,
) {
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    companion object {
        @Volatile
        private var instance: RegisterRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): RegisterRepository =
            instance ?: synchronized(this) {
                instance ?: RegisterRepository(apiService)
            }.also { instance = it }
    }
}
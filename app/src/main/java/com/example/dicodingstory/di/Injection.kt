package com.example.dicodingstory.di

import com.example.dicodingstory.data.remote.network.ApiConfig
import com.example.dicodingstory.repository.AddStoryRepository
import com.example.dicodingstory.repository.LoginRepository
import com.example.dicodingstory.repository.MainRepository
import com.example.dicodingstory.repository.RegisterRepository

object Injection {
    fun provideLoginRepository(): LoginRepository {
        val apiService = ApiConfig.getApiService()
        return LoginRepository.getInstance(apiService)
    }

    fun provideRegisterRepository(): RegisterRepository {
        val apiService = ApiConfig.getApiService()
        return RegisterRepository.getInstance(apiService)
    }

    fun provideMainRepository(): MainRepository {
        val apiService = ApiConfig.getApiService()
        return MainRepository.getInstance(apiService)
    }

    fun provideAddStoryRepository(): AddStoryRepository {
        val apiService = ApiConfig.getApiService()
        return AddStoryRepository.getInstance(apiService)
    }
}
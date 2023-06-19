package com.example.dicodingstory.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.dicodingstory.data.StoryPagingSource
import com.example.dicodingstory.data.remote.Result
import com.example.dicodingstory.data.remote.network.ApiService
import com.example.dicodingstory.data.remote.response.ListStoryItem
import com.example.dicodingstory.data.remote.response.StoriesResponse

class MainRepository private constructor(private val apiService: ApiService){
    fun getStories(token: String) : LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories("Bearer $token", 1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStoriesPaging(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: MainRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): MainRepository =
            instance ?: synchronized(this) {
                instance ?: MainRepository(apiService)
            }.also { instance = it }
    }
}
package com.example.dicodingstory.network

import com.example.dicodingstory.model.RegisterResponse
import com.example.dicodingstory.model.StoriesResponse
import com.example.dicodingstory.model.UserResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<UserResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
    ): Call<StoriesResponse>
}
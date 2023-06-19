package com.example.dicodingstory.ui.activity.main

import androidx.lifecycle.ViewModel
import com.example.dicodingstory.repository.MainRepository

class MainViewModel(private val mainRepository: MainRepository): ViewModel() {
    fun getStories(token: String) = mainRepository.getStories(token)
}
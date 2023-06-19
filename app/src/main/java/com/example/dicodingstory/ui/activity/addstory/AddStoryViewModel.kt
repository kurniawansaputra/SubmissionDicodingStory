package com.example.dicodingstory.ui.activity.addstory

import androidx.lifecycle.ViewModel
import com.example.dicodingstory.repository.AddStoryRepository
import java.io.File

class AddStoryViewModel(private val addStoryRepository: AddStoryRepository) : ViewModel() {
    fun addStories(token: String, description: String, file: File, lat: Double, lon: Double) = addStoryRepository.addStories(token, description, file, lat, lon)
}
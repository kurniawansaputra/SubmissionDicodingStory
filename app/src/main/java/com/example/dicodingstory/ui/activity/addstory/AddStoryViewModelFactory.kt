package com.example.dicodingstory.ui.activity.addstory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.di.Injection
import com.example.dicodingstory.repository.AddStoryRepository

class AddStoryViewModelFactory private constructor(private val addStoryRepository: AddStoryRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(addStoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: AddStoryViewModelFactory? = null
        fun getInstance(): AddStoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AddStoryViewModelFactory(Injection.provideAddStoryRepository())
            }.also { instance = it }
    }
}
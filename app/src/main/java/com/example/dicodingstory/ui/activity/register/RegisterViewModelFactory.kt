package com.example.dicodingstory.ui.activity.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.di.Injection
import com.example.dicodingstory.repository.RegisterRepository

class RegisterViewModelFactory private constructor(private val registerRepository: RegisterRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(registerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: RegisterViewModelFactory? = null
        fun getInstance(): RegisterViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: RegisterViewModelFactory(Injection.provideRegisterRepository())
            }.also { instance = it }
    }
}
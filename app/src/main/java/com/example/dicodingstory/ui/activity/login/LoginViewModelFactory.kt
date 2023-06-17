package com.example.dicodingstory.ui.activity.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.di.Injection
import com.example.dicodingstory.repository.LoginRepository

class LoginViewModelFactory private constructor(private val loginRepository: LoginRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(loginRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: LoginViewModelFactory? = null
        fun getInstance(): LoginViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: LoginViewModelFactory(Injection.provideLoginRepository())
            }.also { instance = it }
    }
}

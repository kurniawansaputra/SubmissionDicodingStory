package com.example.dicodingstory.ui.activity.login

import androidx.lifecycle.ViewModel
import com.example.dicodingstory.repository.LoginRepository

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    fun login(email: String, password: String) = loginRepository.login(email, password)
}
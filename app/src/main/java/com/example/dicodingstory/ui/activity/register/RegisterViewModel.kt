package com.example.dicodingstory.ui.activity.register

import androidx.lifecycle.ViewModel
import com.example.dicodingstory.repository.RegisterRepository

class RegisterViewModel(private val registerRepository: RegisterRepository): ViewModel() {
    fun register(name: String, email: String, password: String) = registerRepository.register(name, email, password)
}
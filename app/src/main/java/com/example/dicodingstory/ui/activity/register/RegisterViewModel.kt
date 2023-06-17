package com.example.dicodingstory.ui.activity.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.remote.response.RegisterResponse
import com.example.dicodingstory.repository.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerRepository: RegisterRepository): ViewModel() {
    private val _register = MutableLiveData<RegisterResponse>()
    val register: LiveData<RegisterResponse> = _register

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _onFailure = MutableLiveData<String>()
    val onFailure: LiveData<String> = _onFailure

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = registerRepository.register(name, email, password)
                _register.value = response
            } catch (e: Exception) {
                _onFailure.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
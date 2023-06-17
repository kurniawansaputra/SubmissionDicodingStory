package com.example.dicodingstory.ui.activity.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.remote.network.ApiConfig
import com.example.dicodingstory.data.remote.response.UserResponse
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _login = MutableLiveData<UserResponse>()
    val login: LiveData<UserResponse> = _login

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _onFailure = MutableLiveData<String>()
    val onFailure: LiveData<String> = _onFailure

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().login(email, password)
                _login.value = response
            } catch (e: Exception) {
                _onFailure.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
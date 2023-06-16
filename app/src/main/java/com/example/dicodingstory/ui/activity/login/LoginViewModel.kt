package com.example.dicodingstory.ui.activity.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.remote.response.UserResponse
import com.example.dicodingstory.data.remote.network.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _login.value = response
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _onFailure.value = e.message
                    Log.e(TAG, "onFailure: ${e.message.toString()}")
                }
            }
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}
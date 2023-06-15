package com.example.dicodingstory.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.databinding.ActivityLoginBinding
import com.example.dicodingstory.hawkstorage.HawkStorage
import com.example.dicodingstory.util.hideLoading
import com.example.dicodingstory.util.showLoading
import com.example.dicodingstory.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var loginViewModel: LoginViewModel

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        loginViewModel.isLoading.observe(this) {
            setLoading(it)
        }
        loginViewModel.onFailure.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        setListener()
        checkIsLogin()
    }

    private fun setListener() {
        binding.apply {
            buttonLogin.setOnClickListener {
                email = binding.etEmail.text.toString().trim()
                password = binding.etPassword.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Column should not be empty", Toast.LENGTH_SHORT).show()
                } else {
                    loginViewModel.login(email, password)
                    loginViewModel.login.observe(this@LoginActivity) {
                        val user = it
                        val error = it.error

                        if (error == false) {
                            if (user != null) {
                                HawkStorage.instance(this@LoginActivity).setUser(user)
                                goToMain()
                            }
                        }
                    }
                }
            }

            labelRegister.setOnClickListener {
                val  intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun checkIsLogin() {
        val isLogin = HawkStorage.instance(this).isLogin()
        if (isLogin) {
           goToMain()
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            showLoading(this)
        } else {
            hideLoading()
        }
    }
}
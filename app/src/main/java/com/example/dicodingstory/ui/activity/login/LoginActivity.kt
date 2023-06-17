package com.example.dicodingstory.ui.activity.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingstory.databinding.ActivityLoginBinding
import com.example.dicodingstory.hawkstorage.HawkStorage
import com.example.dicodingstory.ui.activity.main.MainActivity
import com.example.dicodingstory.ui.activity.register.RegisterActivity
import com.example.dicodingstory.utils.hideLoading
import com.example.dicodingstory.utils.showLoading

class LoginActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var password: String

    private val factory: LoginViewModelFactory = LoginViewModelFactory.getInstance()
    private val loginViewModel: LoginViewModel by viewModels {
        factory
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {

        setObsLogin()
        textWatcher()
        setListener()
        checkIsLogin()
    }

    private fun setListener() {
        binding.apply {
            buttonLogin.setOnClickListener {
                loginViewModel.login(email, password)
            }

            labelRegister.setOnClickListener {
                val  intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun textWatcher() {
        binding.apply {
            etEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    validateInput()
                }

                override fun afterTextChanged(s: Editable) {

                }
            })

            etPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    validateInput()
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
        }
    }

    private fun validateInput() {
        binding.apply {
            email = etEmail.text.toString()
            password = etPassword.text.toString()

            val isEmailValidated = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val isPasswordValidated = password.isNotEmpty() && password.length >= 8

            buttonLogin.isEnabled = isEmailValidated && isPasswordValidated
        }
    }

    private fun setObsLogin() {
        loginViewModel.isLoading.observe(this) {
            setLoading(it)
        }
        loginViewModel.onFailure.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
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
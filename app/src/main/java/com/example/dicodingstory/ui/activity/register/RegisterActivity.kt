package com.example.dicodingstory.ui.activity.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingstory.databinding.ActivityRegisterBinding
import com.example.dicodingstory.ui.activity.login.LoginActivity
import com.example.dicodingstory.utils.hideLoading
import com.example.dicodingstory.utils.showLoading

class RegisterActivity : AppCompatActivity() {
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    private val factory: RegisterViewModelFactory = RegisterViewModelFactory.getInstance()
    private val registerViewModel: RegisterViewModel by viewModels {
        factory
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        setObsRegister()
        setToolbar()
        textWatcher()
        setListener()
    }

    private fun setToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setListener() {
        binding.apply {
            buttonRegister.setOnClickListener {
                registerViewModel.register(name, email, password)
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

            etName.addTextChangedListener(object : TextWatcher {
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
            name = etName.text.toString()
            email = etEmail.text.toString()
            password = etPassword.text.toString()

            val isNameValidated = name.isNotEmpty()
            val isEmailValidated = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val isPasswordValidated = password.isNotEmpty() && password.length >= 8

            buttonRegister.isEnabled = isEmailValidated && isPasswordValidated && isNameValidated
        }
    }

    private fun setObsRegister() {
        registerViewModel.isLoading.observe(this) {
            setLoading(it)
        }
        registerViewModel.onFailure.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        registerViewModel.register.observe(this@RegisterActivity) {
            val error = it.error
            val message = it.message

            if (error == false) {
                goToLogin()
                Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
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
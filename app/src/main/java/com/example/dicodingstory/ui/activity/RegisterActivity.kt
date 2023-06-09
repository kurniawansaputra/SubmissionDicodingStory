package com.example.dicodingstory.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.databinding.ActivityRegisterBinding
import com.example.dicodingstory.util.hideLoading
import com.example.dicodingstory.util.showLoading
import com.example.dicodingstory.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var registerViewModel: RegisterViewModel

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        setListener()
    }

    private fun setListener() {
        binding.apply {
            buttonRegister.setOnClickListener {
                name = binding.etName.text.toString()
                email = binding.etEmail.text.toString().trim()
                password = binding.etPassword.text.toString().trim()

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this@RegisterActivity, "Column should not be empty", Toast.LENGTH_SHORT).show()
                } else {
                    register()
                }
            }

            labelLogin.setOnClickListener {
                val  intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun register() {
        registerViewModel.isLoading.observe(this) {
            setLoading(it)
        }

        registerViewModel.register(name, email, password)
        registerViewModel.register.observe(this) {
            val error = it.error
            val message = it.message

            if (error == false) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        registerViewModel.onFailure.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            showLoading(this)
        } else {
            hideLoading()
        }
    }
}
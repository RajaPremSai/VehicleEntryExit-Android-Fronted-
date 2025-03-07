package com.example.vehicleentryexit

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vehicleentryexit.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Sign In button click listener
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                // Determine which activity to navigate to based on email
                when (email) {
                    "sg@cutmap.ac.in" -> {
                        val intent = Intent(this, SGHomeActivity::class.java)
                        startActivity(intent)
                    }
                    "admin@cutmap.ac.in" -> {
                        val intent = Intent(this, AdminHomeActivity::class.java)
                        startActivity(intent)
                    }
                    "user@cutmap.ac.in" -> {
                        val intent = Intent(this, UserHomeActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {
                        Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
                    }
                }

                // Finish the current activity to prevent back navigation
                finish()
            }
        }

        // Sign Up button click listener - Navigates to Register.kt
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Forgot Password click listener
        binding.tvForgotPassword.setOnClickListener {
            // Navigate to forgot password activity
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        // Validate email
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Please enter a valid email address"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        // Validate password
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }
}

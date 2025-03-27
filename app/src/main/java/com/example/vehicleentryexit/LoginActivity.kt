package com.example.vehicleentryexit

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowInsetsAnimation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.vehicleentryexit.databinding.ActivityLoginBinding
import com.example.vehicleentryexit.models.auth.LoginRequest
import com.example.vehicleentryexit.models.auth.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        setupListeners()
//        setupListeners()
    }

    private fun showTermsAndConditionsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.terms_and_conditions_dialog, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val dialog = dialogBuilder.create()
        dialog.show()

        val agreeButton = dialogView.findViewById<Button>(R.id.agreeButton)
        val termsTextView = dialogView.findViewById<TextView>(R.id.termsTextView)

        // Set your terms and conditions here
        termsTextView.text = """
            Terms and Conditions:
            1. Rule 1: ...
            2. Rule 2: ...
            3. Rule 3: ...
            ...
        """.trimIndent()

        agreeButton.setOnClickListener {
            binding.agreeBtn.isChecked = true
            dialog.dismiss()
        }
    }

    private fun setupListeners() {
        // Sign In button click listener
        binding.btnSignIn.setOnClickListener {
            if (binding.agreeBtn.isChecked) {
            val email = binding.etEmail.text.toString().trim() // Get email from EditText
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                val request = LoginRequest(email, password)
                RetrofitClient.apiService.login(request).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            val token = loginResponse?.token
                            val role = loginResponse?.role
                            val empNumber = loginResponse?.empNumber

                            if (token != null && role != null) {
                                sharedPreferences.edit()
                                    .putString("token", token)
                                    .putString("role", role)
                                    .putString("email", email) // Store email from EditText
                                    .putString("empNumber",empNumber)
                                    .apply()
//                                Toast.makeText(this@LoginActivity,"$empNumber", Toast.LENGTH_SHORT).show()
                                navigateToRoleSpecificActivity(role)
                            } else {
                                Toast.makeText(this@LoginActivity, "Login failed: Invalid token or role", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            Toast.makeText(this@LoginActivity, "Please agree to Terms and Conditions", Toast.LENGTH_SHORT).show()
        }
        }

//        binding.btnSignIn.setOnClickListener {
//            val email = binding.etEmail.text.securityGuardId().trim()
//            val password = binding.etPassword.text.securityGuardId().trim()
//
//            if (validateInputs(email, password)) {
//                val request = LoginRequest(email, password)
//                RetrofitClient.apiService.login(request).enqueue(object : Callback<LoginResponse> {
//                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                        if (response.isSuccessful) {
//                            val loginResponse = response.body()
//                            val token = loginResponse?.token
//                            val role = loginResponse?.role
//                            val userEmail = loginResponse?.email
//
//                            if (token != null && role != null) {
//                                sharedPreferences.edit().putString("token", token).putString("role", role).putString("email", userEmail).apply()
//                                Toast.makeText(this@LoginActivity, "${userEmail}-szbfksf", Toast.LENGTH_SHORT).show()
//                                navigateToRoleSpecificActivity(role)
//                            } else {
//                                Toast.makeText(this@LoginActivity, "Login failed: Invalid token or role", Toast.LENGTH_SHORT).show()
//                            }
//
//                        } else {
//                            Toast.makeText(this@LoginActivity, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                        Toast.makeText(this@LoginActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
//                    }
//                })
//            }
//
//
//        }

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

        binding.agreeBtn.setOnClickListener {
            showTermsAndConditionsDialog()
        }
    }

    private fun navigateToRoleSpecificActivity(role: String) {
        when (role) {
            "SECURITY_GUARD" -> startActivity(Intent(this, SGHomeActivity::class.java))
            "MANAGER" -> startActivity(Intent(this, AdminHomeActivity::class.java))
            "USER" -> startActivity(Intent(this, UserHomeActivity::class.java))
            else -> Toast.makeText(this, "Invalid role", Toast.LENGTH_SHORT).show()
        }
        finish()
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



//        binding.btnSignIn.setOnClickListener {
//            val email = binding.etEmail.text.securityGuardId().trim()
//            val password = binding.etPassword.text.securityGuardId().trim()
//
//            if (validateInputs(email, password)) {
//                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
//
//                // Determine which activity to navigate to based on email
//                when (email) {
//                    "sg@cutmap.ac.in" -> {
//                        val intent = Intent(this, SGHomeActivity::class.java)
//                        startActivity(intent)
//                    }
//                    "admin@cutmap.ac.in" -> {
//                        val intent = Intent(this, AdminHomeActivity::class.java)
//                        startActivity(intent)
//                    }
//                    "user@cutmap.ac.in" -> {
//                        val intent = Intent(this, UserHomeActivity::class.java)
//                        startActivity(intent)
//                    }
//                    else -> {
//                        Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                // Finish the current activity to prevent back navigation
//                finish()
//            }
//        }
package com.example.vehicleentryexit

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        // Move to LoginActivity
//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
//        finish() // Close MainActivity so the user can't go back
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val token = sharedPreferences.getString("token", null)
        val role = sharedPreferences.getString("role", null)

        if (token != null && role != null) {
            navigateToRoleSpecificActivity(role)
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun navigateToRoleSpecificActivity(role: String) {
        when (role) {
            "SECURITY_GUARD" -> startActivity(Intent(this, SGHomeActivity::class.java))
            "MANAGER" -> startActivity(Intent(this, AdminHomeActivity::class.java))
            "USER" -> startActivity(Intent(this, UserHomeActivity::class.java))
            else -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        finish()
    }
}
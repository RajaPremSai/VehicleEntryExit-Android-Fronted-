package com.example.vehicleentryexit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vehicleentryexit.RetrofitClient.getAuthenticatedApiService
import com.example.vehicleentryexit.models.UserDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileActivity : AppCompatActivity() {
    private lateinit var etName: TextView
    private lateinit var etIDEmployee: TextView
    private lateinit var etUserType: TextView
    private lateinit var etEmailId: TextView
    private lateinit var etPhoneNumber: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, UserHomeActivity::class.java)
            startActivity(intent)
        }
        etName = findViewById(R.id.etName)
        etIDEmployee = findViewById(R.id.etIDEmployee)
        etUserType = findViewById(R.id.etUserType)
        etEmailId = findViewById(R.id.etEmailId)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        fetchUserProfile()

    }
    private fun fetchUserProfile() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)

        if (email != null) {
            val apiService = getAuthenticatedApiService(this)
            val call = apiService.getUserByEmail(email)

            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        val fullName = user?.let { listOfNotNull(it.firstName, it.middleName, it.lastName).joinToString(" ") }
                        if (user != null) {
                            etName.text = fullName
                            etIDEmployee.text = user.empNumber
                            etUserType.text = user.userType
                            etEmailId.text = user.email
                            etPhoneNumber.text = user.contactNumber
                        } else {
                            Toast.makeText(this@UserProfileActivity, "User data is null", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@UserProfileActivity, "Failed to fetch user data: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                    Toast.makeText(this@UserProfileActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Email not found in shared preferences", Toast.LENGTH_SHORT).show()
        }
    }
}
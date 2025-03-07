package com.example.vehicleentryexit

//import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat

//class Register : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_register)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}


import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class RegisterActivity : AppCompatActivity() {

    // UI components
    private lateinit var btnSignUp: CardView
    private lateinit var tvLogin: TextView
    private lateinit var btnNext: Button
    private lateinit var etIdNumber: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etMiddleName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etContactNumber: EditText
    private lateinit var tvEmployeeLabel: TextView
    private lateinit var tvStudentLabel: TextView

    // User type selection (default to EMPLOYEE)
    private var userType = UserType.EMPLOYEE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize UI components
        initViews()
        setupListeners()
    }

    private fun initViews() {
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLogin = findViewById(R.id.tvLogin)
        btnNext = findViewById(R.id.btnNext)
        etIdNumber = findViewById(R.id.etIdNumber)
        etFirstName = findViewById(R.id.etFirstName)
        etMiddleName = findViewById(R.id.etMiddleName)
        etLastName = findViewById(R.id.etLastName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etContactNumber = findViewById(R.id.etContactNumber)
        tvEmployeeLabel = findViewById(R.id.tvEmployeeLabel)
        tvStudentLabel = findViewById(R.id.tvStudentLabel)

        // Set default values
        etIdNumber.setText("mrboston")
        etEmail.setText("example@gmail.com")
        etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        etConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        etContactNumber.setText("09123456789")
    }

    private fun setupListeners() {
        // Login text click listener
        tvLogin.setOnClickListener {
            // Navigate to login screen
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Employee label click listener
        tvEmployeeLabel.setOnClickListener {
            setUserType(UserType.EMPLOYEE)
        }

        // Student label click listener
        tvStudentLabel.setOnClickListener {
            setUserType(UserType.STUDENT)
        }

        // Next button click listener
        btnNext.setOnClickListener {
            if (validateInputs()) {
                // Proceed to next step
                proceedToNextStep()
            }
        }
    }

    private fun setUserType(type: UserType) {
        userType = type
        when (type) {
            UserType.EMPLOYEE -> {
                tvEmployeeLabel.setTextColor(getColor(R.color.orange))
                tvStudentLabel.setTextColor(getColor(R.color.black))
            }
            UserType.STUDENT -> {
                tvEmployeeLabel.setTextColor(getColor(R.color.black))
                tvStudentLabel.setTextColor(getColor(R.color.orange))
            }
        }
    }

    private fun validateInputs(): Boolean {
        // Check if ID number is empty
        if (etIdNumber.text.toString().trim().isEmpty()) {
            showToast("Please enter ID number")
            return false
        }

        // Check if first name is empty
        if (etFirstName.text.toString().trim().isEmpty()) {
            showToast("Please enter first name")
            return false
        }

        // Check if last name is empty
        if (etLastName.text.toString().trim().isEmpty()) {
            showToast("Please enter last name")
            return false
        }

        // Check if email is empty or invalid
        val email = etEmail.text.toString().trim()
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address")
            return false
        }

        // Check if password is empty or too short
        val password = etPassword.text.toString()
        if (password.isEmpty() || password.length < 6) {
            showToast("Password must be at least 6 characters")
            return false
        }

        // Check if confirm password matches
        if (password != etConfirmPassword.text.toString()) {
            showToast("Passwords do not match")
            return false
        }

        // Check if contact number is empty or invalid
        val contactNumber = etContactNumber.text.toString().trim()
        if (contactNumber.isEmpty() || !contactNumber.matches(Regex("^09\\d{9}$"))) {
            showToast("Please enter a valid contact number (format: 09XXXXXXXXX)")
            return false
        }

        return true
    }

    private fun proceedToNextStep() {
        // Create user data object
        val userData = UserData(
            idNumber = etIdNumber.text.toString().trim(),
            firstName = etFirstName.text.toString().trim(),
            middleName = etMiddleName.text.toString().trim(),
            lastName = etLastName.text.toString().trim(),
            email = etEmail.text.toString().trim(),
            password = etPassword.text.toString(),
            contactNumber = etContactNumber.text.toString().trim(),
            userType = userType
        )

        // Navigate to next registration step
        val intent = Intent(this, RegisterStep2Activity::class.java)
        intent.putExtra("USER_DATA", userData)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Enum for user types
    enum class UserType {
        EMPLOYEE,
        STUDENT
    }

    // Data class for user data
    data class UserData(
        val idNumber: String,
        val firstName: String,
        val middleName: String,
        val lastName: String,
        val email: String,
        val password: String,
        val contactNumber: String,
        val userType: UserType
    ) : java.io.Serializable
}
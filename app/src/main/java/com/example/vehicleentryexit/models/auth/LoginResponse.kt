package com.example.vehicleentryexit.models.auth

data class LoginResponse(
    val message: String,
    val role: String,
    val token: String,
    val email: String,
    val empNumber: String
)

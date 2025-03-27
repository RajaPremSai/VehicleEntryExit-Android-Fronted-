package com.example.vehicleentryexit.models

data class SecurityGuard(
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val empNumber: String,
    val email: String,
    val contactNumber: String,
    val securityGuardId: String // This is the ID from the database
)

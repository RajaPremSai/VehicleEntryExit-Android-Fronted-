package com.example.vehicleentryexit.models

data class LogDTO(
    val vehicleNumber: String,
    val gateNumber: String,
    val securityGuardId: String,
    val timeIn: String?,
    val timeOut: String?
)

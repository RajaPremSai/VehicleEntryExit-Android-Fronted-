package com.example.vehicleentryexit.models

data class Log(
    val id: Long,
    val vehicleNumber: String,
    val gateNumber: String,
    val securityGuardId: String,
    val timeIn: String?,
    val timeOut: String?
)

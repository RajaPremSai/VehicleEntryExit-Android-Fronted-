package com.example.vehicleentryexit.models

data class UniversityVehicle(
    val vehicleNumber: String,
    val vehicleType: String,
    val vehicleModelName: String,
    val vehicleImages: List<String>,
    val driverName: String,
    val driverMobileNumber: String
)

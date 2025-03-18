package com.example.vehicleentryexit.models

data class VehicleDTO(
    val vehicleNumber: String,
    val vehicleType: String,
    val vehicleModelName: String,
    val vehicleImages: List<String>,
    val vehicleOwner: String,
    val driverName: String,
    val driverMobileNumber: String,
    val empNumber: String
)

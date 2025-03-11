package com.example.vehicleentryexit

import com.example.vehicleentryexit.models.auth.LoginRequest
import com.example.vehicleentryexit.models.auth.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
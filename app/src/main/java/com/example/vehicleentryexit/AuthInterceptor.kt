package com.example.vehicleentryexit

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import android.util.Log

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: okhttp3.Interceptor.Chain): Response {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        Log.d("AUTH_INTERCEPTOR", "Token retrieved: $token") // Debugging log

        val requestBuilder = chain.request().newBuilder()
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Log.d("AUTH_INTERCEPTOR", "Authorization header added: Bearer $token") // Debugging log
        }
        return chain.proceed(requestBuilder.build())
    }
}
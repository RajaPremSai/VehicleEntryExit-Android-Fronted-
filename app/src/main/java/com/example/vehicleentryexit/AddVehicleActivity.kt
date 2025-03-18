package com.example.vehicleentryexit

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vehicleentryexit.models.Vehicle
import com.example.vehicleentryexit.models.VehicleDTO
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddVehicleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val etVehicleNumber = findViewById<EditText>(R.id.etVehicleNumber)
        val etVehicleType = findViewById<EditText>(R.id.etVehicleType)
        val etVehicleModel = findViewById<EditText>(R.id.etVehicleModel)
        val etVehicleOwner = findViewById<EditText>(R.id.etVehicleOwner)
        val etDriverName = findViewById<EditText>(R.id.etDriverName)
        val etDriverMobile = findViewById<EditText>(R.id.etDriverMobile)

        val btnAddVehicle = findViewById<Button>(R.id.btnAddVehicle)
        btnAddVehicle.setOnClickListener {
            val vehicleNumber = etVehicleNumber.text.toString()
            val vehicleType = etVehicleType.text.toString()
            val vehicleModel = etVehicleModel.text.toString()
            val vehicleOwner = etVehicleOwner.text.toString()
            val driverName = etDriverName.text.toString()
            val driverMobile = etDriverMobile.text.toString()

            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val empNumber = sharedPreferences.getString("empNumber", null) ?: return@setOnClickListener

            val vehicleDTO = VehicleDTO(
                vehicleNumber,
                vehicleType,
                vehicleModel,
                emptyList(), // Images are not handled in this example
                vehicleOwner,
                driverName,
                driverMobile,
                empNumber
            )

            val apiService = RetrofitClient.getAuthenticatedApiService(this)
            apiService.addVehicle(vehicleDTO).enqueue(object : Callback<Vehicle> {
                override fun onResponse(call: Call<Vehicle>, response: Response<Vehicle>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddVehicleActivity, "Vehicle added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddVehicleActivity, "Failed to add vehicle", Toast.LENGTH_SHORT).show()
                        Log.e("AddVehicleActivity", "API request failed: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Vehicle>, t: Throwable) {
                    Log.e("AddVehicleActivity", "Network error: ${t.message}")
                    Toast.makeText(this@AddVehicleActivity, "Error adding vehicle", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
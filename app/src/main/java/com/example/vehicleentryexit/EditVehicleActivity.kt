package com.example.vehicleentryexit

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.vehicleentryexit.models.Vehicle
import com.example.vehicleentryexit.models.VehicleDTO

class EditVehicleActivity : AppCompatActivity() {

    private lateinit var vehicleNumber: String
    private lateinit var vehicleType: String
    private lateinit var vehicleModel: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_vehicle)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        vehicleNumber = intent.getStringExtra("vehicleNumber") ?: return
        vehicleType = intent.getStringExtra("vehicleType") ?: "" // Retrieve vehicleType
        vehicleModel = intent.getStringExtra("vehicleModel") ?: "" // Retrieve vehicleModel


        val etVehicleOwner = findViewById<EditText>(R.id.etVehicleOwner)
        val etDriverName = findViewById<EditText>(R.id.etDriverName)
        val etDriverMobile = findViewById<EditText>(R.id.etDriverMobile)

        etVehicleOwner.setText(intent.getStringExtra("vehicleOwner"))
        etDriverName.setText(intent.getStringExtra("driverName"))
        etDriverMobile.setText(intent.getStringExtra("driverMobile"))

        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            val vehicleOwner = etVehicleOwner.text.toString()
            val driverName = etDriverName.text.toString()
            val driverMobile = etDriverMobile.text.toString()

            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val empNumber = sharedPreferences.getString("empNumber", null) ?: return@setOnClickListener

            val vehicleDTO = VehicleDTO(
                vehicleNumber,
                vehicleType, // Use retrieved vehicleType
                vehicleModel, // Use retrieved vehicleModel
                emptyList(),
                vehicleOwner,
                driverName,
                driverMobile,
                empNumber
            )

            val apiService = RetrofitClient.getAuthenticatedApiService(this)
            apiService.editVehicle(vehicleNumber, vehicleDTO).enqueue(object : Callback<Vehicle> {
                override fun onResponse(call: Call<Vehicle>, response: Response<Vehicle>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditVehicleActivity, "Vehicle updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditVehicleActivity, "Failed to update vehicle", Toast.LENGTH_SHORT).show()
                        Log.e("EditVehicleActivity", "API request failed: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Vehicle>, t: Throwable) {
                    Log.e("EditVehicleActivity", "Network error: ${t.message}")
                    Toast.makeText(this@EditVehicleActivity, "Error updating vehicle", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
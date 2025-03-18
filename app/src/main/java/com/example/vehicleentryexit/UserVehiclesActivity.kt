package com.example.vehicleentryexit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.vehicleentryexit.models.Vehicle

class UserVehiclesActivity : AppCompatActivity() {
    private lateinit var recyclerViewVehicles: RecyclerView
    private lateinit var adapter: VehiclesAdapter
    private val vehiclesList = mutableListOf<Vehicle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_vehicles)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, UserHomeActivity::class.java))
            finish()
        }

        recyclerViewVehicles = findViewById(R.id.recyclerViewVehicles)
        recyclerViewVehicles.layoutManager = LinearLayoutManager(this)
        adapter = VehiclesAdapter(vehiclesList) { vehicle ->
            // Handle item click (go to individual vehicle activity)
            val intent = Intent(this, IndividualVehicleActivity::class.java)
            intent.putExtra("vehicleNumber", vehicle.vehicleNumber)
            startActivity(intent)
        }
        recyclerViewVehicles.adapter = adapter

        val fabAddVehicle = findViewById<FloatingActionButton>(R.id.fabAddVehicle)
        fabAddVehicle.setOnClickListener {
            startActivity(Intent(this, AddVehicleActivity::class.java))
        }

        fetchUserVehicles()
    }

    private fun fetchUserVehicles() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val empNumber = sharedPreferences.getString("empNumber", null) ?: return

        val apiService = RetrofitClient.getAuthenticatedApiService(this)
        apiService.getUserVehicles(empNumber).enqueue(object : Callback<List<Vehicle>> {
            override fun onResponse(call: Call<List<Vehicle>>, response: Response<List<Vehicle>>) {
                if (response.isSuccessful) {
                    vehiclesList.clear()
                    vehiclesList.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@UserVehiclesActivity, "Failed to fetch vehicles", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Vehicle>>, t: Throwable) {
                Log.e("UserVehiclesActivity", "Network error: ${t.message}")
                Toast.makeText(this@UserVehiclesActivity, "Error fetching vehicles", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
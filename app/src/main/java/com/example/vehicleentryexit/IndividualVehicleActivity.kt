package com.example.vehicleentryexit

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.example.vehicleentryexit.models.Vehicle

class IndividualVehicleActivity : AppCompatActivity() {

    private lateinit var vehicle: Vehicle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_vehicle)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val vehicleNumber = intent.getStringExtra("vehicleNumber") ?: return

        fetchVehicleDetails(vehicleNumber)

        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val btnEdit = findViewById<Button>(R.id.btnEdit)
        val btnDownloadQr = findViewById<Button>(R.id.btnDownloadQr)

        btnDelete.setOnClickListener { showDeleteConfirmationDialog() }
        btnEdit.setOnClickListener { startEditVehicleActivity() }
        btnDownloadQr.setOnClickListener { downloadQrCode() }
    }

    private fun fetchVehicleDetails(vehicleNumber: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val empNumber = sharedPreferences.getString("empNumber", null) ?: return

        val apiService = RetrofitClient.getAuthenticatedApiService(this)
        apiService.getUserVehicles(empNumber).enqueue(object : Callback<List<Vehicle>> {
            override fun onResponse(call: Call<List<Vehicle>>, response: Response<List<Vehicle>>) {
                if (response.isSuccessful) {
                    val vehicles = response.body() ?: emptyList()
                    vehicle = vehicles.find { it.vehicleNumber == vehicleNumber } ?: run {
                        Toast.makeText(this@IndividualVehicleActivity, "Vehicle not found", Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                    displayVehicleDetails(vehicle)
                    generateQrCode(vehicle.vehicleNumber)
                } else {
                    Toast.makeText(this@IndividualVehicleActivity, "Failed to fetch vehicle details", Toast.LENGTH_SHORT).show()
                    Log.e("IndividualVehicleActivity", "API request failed: ${response.code()}")
                    finish()
                }
            }

            override fun onFailure(call: Call<List<Vehicle>>, t: Throwable) {
                Log.e("IndividualVehicleActivity", "Network error: ${t.message}")
                Toast.makeText(this@IndividualVehicleActivity, "Error fetching vehicle details", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun displayVehicleDetails(vehicle: Vehicle) {
        findViewById<TextView>(R.id.tvVehicleNumber).text = "Vehicle Number: ${vehicle.vehicleNumber}"
        findViewById<TextView>(R.id.tvVehicleType).text = "Vehicle Type: ${vehicle.vehicleType}"
        findViewById<TextView>(R.id.tvVehicleModel).text = "Vehicle Model: ${vehicle.vehicleModelName}"
        findViewById<TextView>(R.id.tvVehicleOwner).text = "Vehicle Owner: ${vehicle.vehicleOwner}"
        findViewById<TextView>(R.id.tvDriverName).text = "Driver Name: ${vehicle.driverName}"
        findViewById<TextView>(R.id.tvDriverMobile).text = "Driver Mobile: ${vehicle.driverMobileNumber}"
    }

    private fun generateQrCode(vehicleNumber: String) {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(vehicleNumber, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            findViewById<ImageView>(R.id.ivQrCode).setImageBitmap(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun downloadQrCode() {
        val imageView = findViewById<ImageView>(R.id.ivQrCode)
        val drawable = imageView.drawable as BitmapDrawable
        val bitmap = drawable.bitmap

        val fileName = "QR_${vehicle.vehicleNumber}.png"
        val imagesFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "VehicleQRCodes")
        imagesFolder.mkdirs()
        val file = File(imagesFolder, fileName)

        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            Toast.makeText(this, "QR code saved to Pictures/VehicleQRCodes", Toast.LENGTH_SHORT).show()

            // Notify the system that the file has been created
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = Uri.fromFile(file)
            sendBroadcast(mediaScanIntent)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save QR code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Vehicle")
            .setMessage("Are you sure you want to delete this vehicle?")
            .setPositiveButton("Delete") { _, _ -> deleteVehicle() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteVehicle() {
        val apiService = RetrofitClient.getAuthenticatedApiService(this)
        apiService.deleteVehicle(vehicle.vehicleNumber).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@IndividualVehicleActivity, "Vehicle deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@IndividualVehicleActivity, "Failed to delete vehicle", Toast.LENGTH_SHORT).show()
                    Log.e("IndividualVehicleActivity", "API request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("IndividualVehicleActivity", "Network error: ${t.message}")
                Toast.makeText(this@IndividualVehicleActivity, "Error deleting vehicle", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startEditVehicleActivity() {
        val intent = Intent(this, EditVehicleActivity::class.java)
        intent.putExtra("vehicleNumber", vehicle.vehicleNumber)
        intent.putExtra("vehicleOwner", vehicle.vehicleOwner)
        intent.putExtra("driverName", vehicle.driverName)
        intent.putExtra("driverMobile", vehicle.driverMobileNumber)
        intent.putExtra("vehicleType", vehicle.vehicleType) // Add this line
        intent.putExtra("vehicleModel", vehicle.vehicleModelName) // Add this line
        startActivity(intent)
    }
}
package com.example.vehicleentryexit.manager

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
import android.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.vehicleentryexit.ApiService
import com.example.vehicleentryexit.R
import com.example.vehicleentryexit.RetrofitClient
import com.example.vehicleentryexit.models.UniversityVehicle
import com.example.vehicleentryexit.models.UniversityVehicleDTO
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VehicleDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
//class VehicleDetailsFragment : Fragment() {
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_vehicle_details2, container, false)
//    }
//
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment VehicleDetailsFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            VehicleDetailsFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}

class VehicleDetailsFragment : Fragment() {

    private lateinit var apiService: ApiService
    private lateinit var vehicleNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vehicle_details2, container, false)
        apiService = RetrofitClient.getAuthenticatedApiService(requireContext())
        vehicleNumber = arguments?.getString("vehicleNumber") ?: ""
        fetchVehicleDetails()
        return view
    }

    private fun fetchVehicleDetails() {
        apiService.getUniversityVehicles().enqueue(object : Callback<List<UniversityVehicle>> {
            override fun onResponse(call: Call<List<UniversityVehicle>>, response: Response<List<UniversityVehicle>>) {
                if (response.isSuccessful) {
                    val vehicle = response.body()?.find { it.vehicleNumber == vehicleNumber }
                    if (vehicle != null) {
                        displayVehicleDetails(vehicle)
                    } else {
                        Toast.makeText(context, "Vehicle not found", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch vehicles", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }

            override fun onFailure(call: Call<List<UniversityVehicle>>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        })
    }

    private fun displayVehicleDetails(vehicle: UniversityVehicle) {
        view?.findViewById<TextView>(R.id.vehicleNumberTextView)?.text = "Vehicle Number: ${vehicle.vehicleNumber}"
        view?.findViewById<TextView>(R.id.vehicleTypeTextView)?.text = "Vehicle Type: ${vehicle.vehicleType}"
        view?.findViewById<TextView>(R.id.vehicleModelNameTextView)?.text = "Model Name: ${vehicle.vehicleModelName}"
        view?.findViewById<TextView>(R.id.driverNameTextView)?.text = "Driver Name: ${vehicle.driverName}"
        view?.findViewById<TextView>(R.id.driverMobileNumberTextView)?.text = "Driver Mobile: ${vehicle.driverMobileNumber}"

        val qrCodeImageView = view?.findViewById<ImageView>(R.id.qrCodeImageView)
        val qrCodeBitmap = generateQRCode(vehicle.vehicleNumber)
        qrCodeImageView?.setImageBitmap(qrCodeBitmap)

        view?.findViewById<View>(R.id.deleteButton)?.setOnClickListener {
            showDeleteConfirmationDialog(vehicle.vehicleNumber)
        }

        view?.findViewById<View>(R.id.editButton)?.setOnClickListener {
            showEditVehicleDialog(vehicle)
        }

        view?.findViewById<View>(R.id.downloadQrCodeButton)?.setOnClickListener {
            context?.let { it1 -> saveQRCode(qrCodeBitmap, vehicle.vehicleNumber , it1) }
        }
    }

    private fun generateQRCode(data: String): Bitmap? {
        val writer = QRCodeWriter()
        return try {
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bmp
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

//    private fun saveQRCode(bitmap: Bitmap?, vehicleNumber: String) {
//        if (bitmap == null) return
//
//        val filename = "QRCode_$vehicleNumber.png"
//        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "QRCodes")
//        if (!directory.exists()) {
//            directory.mkdirs()
//        }
//        val file = File(directory, filename)
//
//        try {
//            val fileOutputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
//            fileOutputStream.close()
//            Toast.makeText(context, "QR code saved to Pictures/QRCodes/$filename", Toast.LENGTH_SHORT).show()
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Toast.makeText(context, "Failed to save QR code", Toast.LENGTH_SHORT).show()
//        }
//    }
    private fun saveQRCode(bitmap: Bitmap?, vehicleNumber: String, context: Context) {
        if (bitmap == null) return

        val filename = "QRCode_$vehicleNumber.png"
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "UVQRCodes")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, filename)

        try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush() // Add flush
            fileOutputStream.close()
            Toast.makeText(context, "QR code saved to Pictures/QRCodes/$filename", Toast.LENGTH_SHORT).show()

            // Notify the system that the file has been created
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = Uri.fromFile(file)
            context.sendBroadcast(mediaScanIntent) // Use the context to send the broadcast.
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save QR code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog(vehicleNumber: String) {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Delete Vehicle")
                .setMessage("Are you sure you want to delete this vehicle?")
                .setPositiveButton("Yes") { _, _ ->
                    deleteVehicle(vehicleNumber)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun deleteVehicle(vehicleNumber: String) {
        apiService.deleteUniversityVehicle(vehicleNumber).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Vehicle deleted", Toast.LENGTH_SHORT).show()
//                    findNavController().popBackStack()
                    fetchVehicleDetails()
                } else {
                    Toast.makeText(context, "Failed to delete vehicle", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEditVehicleDialog(vehicle: UniversityVehicle) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_university_vehicle, null)
//        val dialog = context?.let { AlertDialog.Builder(it).setView(dialogView).create() }
        val dialog = android.app.AlertDialog.Builder(context).setView(dialogView).create()

        val vehicleNumberEditText = dialogView.findViewById<EditText>(R.id.vehicleNumberEditText)
        val vehicleTypeEditText = dialogView.findViewById<EditText>(R.id.vehicleTypeEditText)
        val vehicleModelNameEditText = dialogView.findViewById<EditText>(R.id.vehicleModelNameEditText)
        val driverNameEditText = dialogView.findViewById<EditText>(R.id.driverNameEditText)
        val driverMobileNumberEditText = dialogView.findViewById<EditText>(R.id.driverMobileNumberEditText)

        vehicleNumberEditText.setText(vehicle.vehicleNumber)
        vehicleTypeEditText.setText(vehicle.vehicleType)
        vehicleModelNameEditText.setText(vehicle.vehicleModelName)
        driverNameEditText.setText(vehicle.driverName)
        driverMobileNumberEditText.setText(vehicle.driverMobileNumber)

        dialogView.findViewById<View>(R.id.saveButton).setOnClickListener {
            val updatedVehicle = UniversityVehicleDTO(
                vehicleNumberEditText.text.toString(),
                vehicleTypeEditText.text.toString(),
                vehicleModelNameEditText.text.toString(),
                driverNameEditText.text.toString(),
                driverMobileNumberEditText.text.toString()
            )

            editVehicle(vehicle.vehicleNumber, updatedVehicle)
            dialog?.dismiss()
        }

        dialogView.findViewById<View>(R.id.cancelButton).setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.show()
    }

    private fun editVehicle(vehicleNumber: String, vehicleDTO: UniversityVehicleDTO) {
        apiService.editUniversityVehicle(vehicleNumber, vehicleDTO).enqueue(object : Callback<UniversityVehicle> {
            override fun onResponse(call: Call<UniversityVehicle>, response: Response<UniversityVehicle>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Vehicle updated", Toast.LENGTH_SHORT).show()
//                    findNavController().popBackStack()
                    fetchVehicleDetails()
                } else {
                    Toast.makeText(context, "Failed to update vehicle", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UniversityVehicle>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
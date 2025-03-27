package com.example.vehicleentryexit.manager

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.R
import com.example.vehicleentryexit.ApiService
import com.example.vehicleentryexit.RetrofitClient
import com.example.vehicleentryexit.models.UniversityVehicle
import com.example.vehicleentryexit.models.UniversityVehicleDTO
import com.example.vehicleentryexit.manager.VehicleDetailsFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.fragment.app.FragmentManager

class ManagerUniversityVehicles : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UniversityVehiclesAdapter
    private lateinit var apiService: ApiService
    private lateinit var fabAddVehicle: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manager_university_vehicles, container, false)
        recyclerView = view.findViewById(R.id.universityVehiclesRecyclerView)
        fabAddVehicle = view.findViewById(R.id.fabAddUniversityVehicle)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = UniversityVehiclesAdapter(emptyList(), this::navigateToVehicleDetails)
        recyclerView.adapter = adapter

        apiService = RetrofitClient.getAuthenticatedApiService(requireContext())

        fetchUniversityVehicles()

        fabAddVehicle.setOnClickListener {
            showAddVehicleDialog()
        }

        return view
    }

    private fun fetchUniversityVehicles() {
        apiService.getUniversityVehicles().enqueue(object : Callback<List<UniversityVehicle>> {
            override fun onResponse(call: Call<List<UniversityVehicle>>, response: Response<List<UniversityVehicle>>) {
                if (response.isSuccessful) {
                    val vehicles = response.body() ?: emptyList()
                    adapter.updateVehicles(vehicles)
                } else {
                    Toast.makeText(context, "Failed to fetch vehicles", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UniversityVehicle>>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun navigateToVehicleDetails(vehicle: UniversityVehicle) {
        val bundle = Bundle()
        bundle.putString("vehicleNumber", vehicle.vehicleNumber)
//        findNavController().navigate(R.id.action_managerUniversityVehicles_to_vehicleDetailsFragment, bundle)
        val vehicleDetailsFragment = VehicleDetailsFragment().apply {
            arguments = bundle
        }
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager // Get FragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, vehicleDetailsFragment)
            .addToBackStack(null) // Optional: for back navigation
            .commit()
    }

    private fun showAddVehicleDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_university_vehicle, null)
        val dialog = context?.let { AlertDialog.Builder(it).setView(dialogView).create() }

        dialogView.findViewById<View>(R.id.saveButton).setOnClickListener {
            val vehicleNumberEditText = dialogView.findViewById<EditText>(R.id.vehicleNumberEditText)
            val vehicleTypeEditText = dialogView.findViewById<EditText>(R.id.vehicleTypeEditText)
            val vehicleModelNameEditText = dialogView.findViewById<EditText>(R.id.vehicleModelNameEditText)
            val driverNameEditText = dialogView.findViewById<EditText>(R.id.driverNameEditText)
            val driverMobileNumberEditText = dialogView.findViewById<EditText>(R.id.driverMobileNumberEditText)

            val newVehicle = UniversityVehicleDTO(
                vehicleNumberEditText.text.toString(),
                vehicleTypeEditText.text.toString(),
                vehicleModelNameEditText.text.toString(),
                driverNameEditText.text.toString(),
                driverMobileNumberEditText.text.toString()
            )

            addVehicle(newVehicle)
            if (dialog != null) {
                dialog.dismiss()
            }
        }

        dialogView.findViewById<View>(R.id.cancelButton).setOnClickListener {
            if (dialog != null) {
                dialog.dismiss()
            }
        }

        if (dialog != null) {
            dialog.show()
        }
    }

    private fun addVehicle(vehicleDTO: UniversityVehicleDTO) {
        apiService.addUniversityVehicle(vehicleDTO).enqueue(object : Callback<UniversityVehicle> {
            override fun onResponse(call: Call<UniversityVehicle>, response: Response<UniversityVehicle>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Vehicle added", Toast.LENGTH_SHORT).show()
                    fetchUniversityVehicles()
                } else {
                    Toast.makeText(context, "Failed to add vehicle", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UniversityVehicle>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private class UniversityVehiclesAdapter(
        private var vehicles: List<UniversityVehicle>,
        private val onItemClick: (UniversityVehicle) -> Unit
    ) : RecyclerView.Adapter<UniversityVehiclesAdapter.VehicleViewHolder>() {

        class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val vehicleNumberTextView: TextView = itemView.findViewById(R.id.vehicleNumberTextView)
            val vehicleTypeTextView: TextView = itemView.findViewById(R.id.vehicleTypeTextView)
            val cardView: MaterialCardView = itemView.findViewById(R.id.vehicleCardView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_university_vehicle, parent, false)
            return VehicleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
            val vehicle = vehicles[position]
            holder.vehicleNumberTextView.text = "Vehicle #: ${vehicle.vehicleNumber}"
            holder.vehicleTypeTextView.text = "Type: ${vehicle.vehicleType}"
            holder.cardView.setOnClickListener {
                onItemClick(vehicle)
            }
        }

        override fun getItemCount() = vehicles.size

        fun updateVehicles(newVehicles: List<UniversityVehicle>) {
            vehicles = newVehicles
            notifyDataSetChanged()
        }
    }
}
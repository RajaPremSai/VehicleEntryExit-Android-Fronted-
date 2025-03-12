package com.example.vehicleentryexit.sg

import android.os.Bundle
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.vehicleentryexit.ApiService
import com.example.vehicleentryexit.R
import com.example.vehicleentryexit.RetrofitClient
import com.example.vehicleentryexit.models.Log
import com.example.vehicleentryexit.models.LogDTO
import com.example.vehicleentryexit.models.VehicleDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VehicleDetailsFragment : Fragment() {

    private lateinit var vehicleNoText: TextView
    private lateinit var driverNameText: TextView
    private lateinit var vehicleTypeText: TextView
    private lateinit var spinnerGate: Spinner
    private lateinit var btnSubmit: Button
    private lateinit var radioGroup: RadioGroup

    private var vehicleNumber: String? = null
    private var selectedGate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vehicle_details, container, false)

        vehicleNoText = view.findViewById(R.id.vehicleNoText)
        driverNameText = view.findViewById(R.id.driverNameText)
        vehicleTypeText = view.findViewById(R.id.vehicleTypeText)
        spinnerGate = view.findViewById(R.id.spinnerGate)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        radioGroup = view.findViewById(R.id.radioGroup)

        vehicleNumber = arguments?.getString("vehicleNumber")
        vehicleNumber?.let {
            fetchVehicleDetails(it)
            fetchGateNumbers()
        }
        // Handle Back Button Click
        val backButton = view.findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            // Replace the current fragment with SGScanBarcodeFragment
            requireActivity().supportFragmentManager.commit {
                replace(R.id.fragmentContainerView, SGScanBarcodeFragment())
                addToBackStack(null) // Optional: Add to back stack if you want to navigate back
            }
        }
        btnSubmit.setOnClickListener { submitLog() }
        return view
    }
    private fun fetchVehicleDetails(vehicleNumber: String) {
        RetrofitClient.getAuthenticatedApiService(requireContext()).getVehicleDetails(vehicleNumber)
//        RetrofitClient.apiService.getVehicleDetails(vehicleNumber)
            .enqueue(object : Callback<VehicleDetails> {
                override fun onResponse(call: Call<VehicleDetails>, response: Response<VehicleDetails>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            vehicleNoText.text = it.vehicleNumber
                            driverNameText.text = it.driverName
                            vehicleTypeText.text = it.vehicleType
                        }
                    } else {
                        Toast.makeText(context, "Failed to fetch vehicle details", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<VehicleDetails>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

        private fun fetchGateNumbers() {
            RetrofitClient.getAuthenticatedApiService(requireContext()).getGateNumbers()
                .enqueue(object : Callback<List<String>> {
                    override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                        if (response.isSuccessful) {
                            val gates = response.body() ?: emptyList()
                            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, gates)
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerGate.adapter = adapter
                        } else {
                            Toast.makeText(context, "Failed to fetch gates", Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.commit {
                                replace(R.id.fragmentContainerView, SGScanBarcodeFragment())
                                addToBackStack(null)
                            }
                        }
                    }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.commit {
                    replace(R.id.fragmentContainerView, SGScanBarcodeFragment())
                    addToBackStack(null)
                }
            }
        })
}

    private fun submitLog() {
        selectedGate = spinnerGate.selectedItem.toString()
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId

        if (selectedRadioButtonId == -1) {
            Toast.makeText(context, "Please select ENTRY or EXIT", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedStatus = view?.findViewById<RadioButton>(selectedRadioButtonId)?.text.toString()

        val currentTime = getCurrentTime()
        val timeIn = if (selectedStatus == "ENTRY") currentTime else ""
        val timeOut = if (selectedStatus == "EXIT") currentTime else ""

        // Toast for Gate and Entry/Exit selection
        Toast.makeText(context, "Gate: $selectedGate, Status: $selectedStatus", Toast.LENGTH_SHORT).show()

        val logDTO = LogDTO(
            vehicleNumber = vehicleNumber ?: "",
            gateNumber = selectedGate ?: "",
            securityGuardId = "SG123", // Replace with actual ID
            timeIn = timeIn,
            timeOut = timeOut
        )
        RetrofitClient.getAuthenticatedApiService(requireContext()).postLog(logDTO)
            .enqueue(object : Callback<Log> {
                override fun onResponse(call: Call<Log>, response: Response<Log>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Log submitted successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.commit {
                            replace(R.id.fragmentContainerView, SGScanBarcodeFragment())
                            addToBackStack(null)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(context, "Failed to submit log: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Log>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
    }
}
package com.example.vehicleentryexit.sg

import android.os.Bundle
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.vehicleentryexit.R

class VehicleDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vehicle_details, container, false)

        // Handle Back Button Click
        val backButton = view.findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            // Replace the current fragment with SGScanBarcodeFragment
            requireActivity().supportFragmentManager.commit {
                replace(R.id.fragmentContainerView, SGScanBarcodeFragment())
                addToBackStack(null) // Optional: Add to back stack if you want to navigate back
            }
        }

        return view
    }
}

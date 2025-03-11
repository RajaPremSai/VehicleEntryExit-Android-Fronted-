package com.example.vehicleentryexit.sg

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.vehicleentryexit.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SGScanBarcodeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_s_g_scan_barcode, container, false)

        // Initialize barcode scan button
        val barcodeScannerButton: Button = view.findViewById(R.id.barcodeScannerButton)

        // When barcode scan button is clicked, navigate to vehicle details fragment
//        barcodeScannerButton.setOnClickListener {
//            findNavController().navigate(R.id.action_SGScanBarcodeFragment_to_vehicleDetailsFragment)
//        }
//        val scanCardView: CardView = view.findViewById(R.id.scanCard) // Replace R.id.scanCard with the actual ID of your CardView

        barcodeScannerButton.setOnClickListener {
            // Replace the current fragment with VehicleDetailsFragment
            requireActivity().supportFragmentManager.commit {
                replace(R.id.fragmentContainerView, VehicleDetailsFragment())
                addToBackStack(null) // Optional: Add to back stack if you want to navigate back
            }
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SGScanBarcodeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

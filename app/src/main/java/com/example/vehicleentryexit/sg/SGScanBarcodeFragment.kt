package com.example.vehicleentryexit.sg

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.camera.lifecycle.ProcessCameraProvider
import com.example.vehicleentryexit.R
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import android.Manifest

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SGScanBarcodeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val CAMERA_PERMISSION_REQUEST = 101
    private lateinit var previewView: PreviewView
    private lateinit var scannedResult: TextView
    private lateinit var qrCodeImage: ImageView

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
        previewView = view.findViewById(R.id.previewView)
        scannedResult = view.findViewById(R.id.scannedResult)
        qrCodeImage = view.findViewById(R.id.qrCodeImage)
        previewView.visibility = View.INVISIBLE

        barcodeScannerButton.setOnClickListener {
            // Replace the current fragment with VehicleDetailsFragment
//            requireActivity().supportFragmentManager.commit {
//                replace(R.id.fragmentContainerView, VehicleDetailsFragment())
//                addToBackStack(null) // Optional: Add to back stack if you want to navigate back
            //            }
            if (previewView.visibility == android.view.View.INVISIBLE) {
                qrCodeImage.visibility = View.GONE
                previewView.visibility = android.view.View.VISIBLE
                requestCameraPermission()
            } else {
                previewView.visibility = android.view.View.INVISIBLE
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

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val imageAnalysis = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(Executors.newSingleThreadExecutor(), BarcodeAnalyzer(scannedResult, requireActivity().supportFragmentManager, R.id.fragmentContainerView))
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    class BarcodeAnalyzer(private val resultTextView: TextView, private val fragmentManager: androidx.fragment.app.FragmentManager, private val containerId: Int) : ImageAnalysis.Analyzer {
        @androidx.annotation.OptIn(ExperimentalGetImage::class)
        @OptIn(ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val scanner = BarcodeScanning.getClient()

                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            val rawValue = barcode.rawValue
                            resultTextView.text = rawValue

                            // Pass data using Bundle
                            val bundle = Bundle().apply {
                                putString("vehicleNumber", rawValue)
                            }

                            // Replace current fragment with VehicleDetailsFragment
                            val vehicleDetailsFragment = VehicleDetailsFragment().apply {
                                arguments = bundle
                            }

                            fragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView, vehicleDetailsFragment)
                                .addToBackStack(null) // Optional: for back navigation
                                .commit()
                        }
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }
        }
    }
}

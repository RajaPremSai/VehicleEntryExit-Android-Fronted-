package com.example.vehicleentryexit.sg

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.vehicleentryexit.R
import com.example.vehicleentryexit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SGProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var etName: TextView
    private lateinit var etIDEmployee: TextView
    private lateinit var etIdSG: TextView
    private lateinit var etUserType: TextView
    private lateinit var etEmailId: TextView
    private lateinit var etPhoneNumber: TextView

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
        val view = inflater.inflate(R.layout.fragment_s_g_profile, container, false)

        // Initialize UI elements
        etName = view.findViewById(R.id.etName)
        etIDEmployee = view.findViewById(R.id.etIDEmployee)
        etIdSG = view.findViewById(R.id.etIdSG)
        etUserType = view.findViewById(R.id.etUserType)
        etEmailId = view.findViewById(R.id.etEmailId)
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber)

        // Find the back button and set its click listener
//        val backButton: Button = view.findViewById(R.id.backButton)
//        backButton.setOnClickListener {
//            requireActivity().onBackPressedDispatcher.onBackPressed()
//        }

        fetchSGDetails()

        return view
    }

    private fun fetchSGDetails() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)

        if (email != null) {
            val apiService = RetrofitClient.getAuthenticatedApiService(requireContext())
            apiService.getSecurityGuardByEmail(email).enqueue(object : Callback<SecurityGuard> {
//                override fun onResponse(call: Call<SecurityGuard>, response: Response<SecurityGuard>) {
//                    if (response.isSuccessful && response.body() != null) {
//                        updateUI(response.body()!!)
//                    } else {
//                        Toast.makeText(requireContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
//                    }
//                }

                override fun onResponse(call: Call<SecurityGuard>, response: Response<SecurityGuard>) {
                    Log.d("SGDetailsFragment", "Response code: ${response.code()}") // Log response code
                    if (response.isSuccessful && response.body() != null) {
                        updateUI(response.body()!!)
                    } else {
                        Toast.makeText(requireContext(), "Failed to retrieve data. Code: ${response.code()}", Toast.LENGTH_SHORT).show() // Include code in toast
                    }
                }

                override fun onFailure(call: Call<SecurityGuard>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Email not found in SharedPreferences", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(sg: SecurityGuard) {
        val fullName = listOfNotNull(sg.firstName, sg.middleName, sg.lastName).joinToString(" ")

        etName.text = fullName
        etIDEmployee.text = sg.empNumber
        etIdSG.text = sg.securityGuardId
        etUserType.text = "Security Guard"  // Static text as per requirement
        etEmailId.text = sg.email
        etPhoneNumber.text = sg.contactNumber
    }

    // Data Class for API Response
    data class SecurityGuard(
        val id: String,
        val firstName: String,
        val middleName: String?,  // Nullable to avoid issues if middle name is missing
        val lastName: String,
        val empNumber: String,
        val email: String,
        val contactNumber: String,
        val securityGuardId: String
    )

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SGProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

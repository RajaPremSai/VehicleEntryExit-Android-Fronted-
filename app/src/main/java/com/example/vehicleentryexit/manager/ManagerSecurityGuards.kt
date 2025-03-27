package com.example.vehicleentryexit.manager

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.R
import com.example.vehicleentryexit.ApiService
import com.example.vehicleentryexit.RetrofitClient
import com.example.vehicleentryexit.models.SecurityGuard
import com.example.vehicleentryexit.models.SecurityGuardDTO
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagerSecurityGuards : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SecurityGuardsAdapter
    private lateinit var apiService: ApiService
    private lateinit var fabAddGuard: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manager_security_guards, container, false)
        recyclerView = view.findViewById(R.id.securityGuardsRecyclerView)
        fabAddGuard = view.findViewById(R.id.fabAddGuard)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = SecurityGuardsAdapter(emptyList(), this::showGuardDetails)
        recyclerView.adapter = adapter

        apiService = RetrofitClient.getAuthenticatedApiService(requireContext())

        fetchSecurityGuards()

        fabAddGuard.setOnClickListener {
            showAddGuardDialog()
        }

        return view
    }

    private fun fetchSecurityGuards() {
        apiService.getGuards().enqueue(object : Callback<List<SecurityGuard>> {
            override fun onResponse(call: Call<List<SecurityGuard>>, response: Response<List<SecurityGuard>>) {
                if (response.isSuccessful) {
                    val guards = response.body() ?: emptyList()
                    adapter.updateGuards(guards)
                } else {
                    Toast.makeText(context, "Failed to fetch guards", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<SecurityGuard>>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showGuardDetails(guard: SecurityGuard) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_security_guard_details, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()

        dialogView.findViewById<TextView>(R.id.firstNameTextView).text = "First Name: ${guard.firstName}"
        dialogView.findViewById<TextView>(R.id.middleNameTextView).text = "Middle Name: ${guard.middleName}"
        dialogView.findViewById<TextView>(R.id.lastNameTextView).text = "Last Name: ${guard.lastName}"
        dialogView.findViewById<TextView>(R.id.empNumberTextView).text = "Employee Number: ${guard.empNumber}"
        dialogView.findViewById<TextView>(R.id.emailTextView).text = "Email: ${guard.email}"
        dialogView.findViewById<TextView>(R.id.contactNumberTextView).text = "Contact Number: ${guard.contactNumber}"
        dialogView.findViewById<TextView>(R.id.sgidTextView).text = "Security Guard ID: ${guard.securityGuardId}"


        dialogView.findViewById<View>(R.id.deleteButton).setOnClickListener {
            showDeleteConfirmationDialog(guard.securityGuardId)
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.editButton).setOnClickListener {
            showEditGuardDialog(guard)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(guardId: String) {
        AlertDialog.Builder(context)
            .setTitle("Delete Security Guard")
            .setMessage("Are you sure you want to delete this guard?")
            .setPositiveButton("Yes") { _, _ ->
                deleteGuard(guardId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteGuard(guardId: String) {
        apiService.deleteSecurityGuard(guardId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Guard deleted", Toast.LENGTH_SHORT).show()
                    fetchSecurityGuards()
                } else {
                    Toast.makeText(context, "Failed to delete guard", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEditGuardDialog(guard: SecurityGuard) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_guard, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()

        val firstNameEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.firstNameEditText)
        val middleNameEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.middleNameEditText)
        val lastNameEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.lastNameEditText)
        val empNumberEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.empNumberEditText)
        val emailEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.emailEditText)
        val contactNumberEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.contactNumberEditText)
        val sgidEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.sgidEditText)
        val passwordEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.passwordEditText)


        firstNameEditText.setText(guard.firstName)
        middleNameEditText.setText(guard.middleName)
        lastNameEditText.setText(guard.lastName)
        empNumberEditText.setText(guard.empNumber)
        emailEditText.setText(guard.email)
        contactNumberEditText.setText(guard.contactNumber)
        sgidEditText.setText(guard.securityGuardId)
        passwordEditText.setText("")

        dialogView.findViewById<View>(R.id.saveButton).setOnClickListener {
            val updatedGuard = SecurityGuardDTO(
                firstNameEditText.text.toString(),
                middleNameEditText.text.toString(),
                lastNameEditText.text.toString(),
                empNumberEditText.text.toString(),
                emailEditText.text.toString(),
                contactNumberEditText.text.toString(),
                sgidEditText.text.toString(),
                passwordEditText.text.toString()
            )
            updateGuard(guard.securityGuardId, updatedGuard)
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateGuard(guardId: String, guardDTO: SecurityGuardDTO) {
        apiService.updateSecurityGuard(guardId, guardDTO).enqueue(object : Callback<SecurityGuard> {
            override fun onResponse(call: Call<SecurityGuard>, response: Response<SecurityGuard>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Guard updated", Toast.LENGTH_SHORT).show()
                    fetchSecurityGuards()
                } else {
                    Toast.makeText(context, "Failed to update guard", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SecurityGuard>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddGuardDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_guard, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()
        dialogView.findViewById<View>(R.id.saveButton).setOnClickListener {
            val firstNameEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.firstNameEditText)
            val middleNameEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.middleNameEditText)
            val lastNameEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.lastNameEditText)
            val empNumberEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.empNumberEditText)
            val emailEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.emailEditText)
            val contactNumberEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.contactNumberEditText)
            val sgidEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.sgidEditText)
            val passwordEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.passwordEditText)

            val newGuard = SecurityGuardDTO(
                firstNameEditText.text.toString(),
                middleNameEditText.text.toString(),
                lastNameEditText.text.toString(),
                empNumberEditText.text.toString(),
                emailEditText.text.toString(),
                contactNumberEditText.text.toString(),
                sgidEditText.text.toString(),
                passwordEditText.text.toString()
            )
            addGuard(newGuard)
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addGuard(guardDTO: SecurityGuardDTO) {
        apiService.addSecurityGuard(guardDTO).enqueue(object : Callback<SecurityGuard> {
            override fun onResponse(call: Call<SecurityGuard>, response: Response<SecurityGuard>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Guard added", Toast.LENGTH_SHORT).show()
                    fetchSecurityGuards()
                } else {
                    Toast.makeText(context, "Failed to add guard", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SecurityGuard>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private class SecurityGuardsAdapter(
        private var guards: List<SecurityGuard>,
        private val onItemClick: (SecurityGuard) -> Unit
    ) : RecyclerView.Adapter<SecurityGuardsAdapter.GuardViewHolder>() {

        class GuardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: android.widget.TextView = itemView.findViewById(R.id.guardNameTextView)
            val empNumberTextView: android.widget.TextView = itemView.findViewById(R.id.guardEmpNumberTextView)
            val cardView: MaterialCardView = itemView.findViewById(R.id.guardCardView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuardViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_security_guard, parent, false)
            return GuardViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: GuardViewHolder, position: Int) {
            val guard = guards[position]
            holder.nameTextView.text = "${guard.firstName} ${guard.lastName}"
            holder.empNumberTextView.text = "Emp #: ${guard.empNumber}"
            holder.cardView.setOnClickListener {
                onItemClick(guard)
            }
        }

        override fun getItemCount() = guards.size

        fun updateGuards(newGuards: List<SecurityGuard>) {
            guards = newGuards
            notifyDataSetChanged()
        }
    }
}
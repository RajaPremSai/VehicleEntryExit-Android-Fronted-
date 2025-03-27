package com.example.vehicleentryexit.manager

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.R
import com.example.vehicleentryexit.ApiService
import com.example.vehicleentryexit.RetrofitClient
import com.example.vehicleentryexit.models.Gate
import com.example.vehicleentryexit.models.GateDTO
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagerGates : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GatesAdapter
    private lateinit var apiService: ApiService
    private lateinit var fabAddGate: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manager_gates, container, false)
        recyclerView = view.findViewById(R.id.gatesRecyclerView)
        fabAddGate = view.findViewById(R.id.fabAddGate)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = GatesAdapter(emptyList(), this::showGateDetails)
        recyclerView.adapter = adapter

        apiService = RetrofitClient.getAuthenticatedApiService(requireContext())

        fetchGates()

        fabAddGate.setOnClickListener {
            showAddGateDialog()
        }

        return view
    }

    private fun fetchGates() {
        apiService.getGates().enqueue(object : Callback<List<Gate>> {
            override fun onResponse(call: Call<List<Gate>>, response: Response<List<Gate>>) {
                if (response.isSuccessful) {
                    val gates = response.body() ?: emptyList()
                    adapter.updateGates(gates)
                } else {
                    Toast.makeText(context, "Failed to fetch gates", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Gate>>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showGateDetails(gate: Gate) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_gate_details, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()

        dialogView.findViewById<TextView>(R.id.gateNameTextView).text = "Gate Name: ${gate.gateName}"
        dialogView.findViewById<TextView>(R.id.gateNumberTextView).text = "Gate Number: ${gate.gateNumber}"

        dialogView.findViewById<View>(R.id.deleteButton).setOnClickListener {
            showDeleteConfirmationDialog(gate.gateNumber)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(gateNumber: String) {
        AlertDialog.Builder(context)
            .setTitle("Delete Gate")
            .setMessage("Are you sure you want to delete this gate?")
            .setPositiveButton("Yes") { _, _ ->
                deleteGate(gateNumber)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteGate(gateNumber: String) {
        apiService.deleteGates(gateNumber).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Gate deleted", Toast.LENGTH_SHORT).show()
                    fetchGates()
                } else {
                    Toast.makeText(context, "Failed to delete gate", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddGateDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_gate, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()

        dialogView.findViewById<View>(R.id.saveButton).setOnClickListener {
            val gateNameEditText = dialogView.findViewById<EditText>(R.id.gateNameEditText)
            val gateNumberEditText = dialogView.findViewById<EditText>(R.id.gateNumberEditText)

            val newGate = GateDTO(
                gateNumberEditText.text.toString(),
                gateNameEditText.text.toString()
            )

            addGate(newGate)
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addGate(gateDTO: GateDTO) {
        apiService.addGates(gateDTO).enqueue(object : Callback<Gate> {
            override fun onResponse(call: Call<Gate>, response: Response<Gate>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Gate added", Toast.LENGTH_SHORT).show()
                    fetchGates()
                } else {
                    Toast.makeText(context, "Failed to add gate", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Gate>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private class GatesAdapter(
        private var gates: List<Gate>,
        private val onItemClick: (Gate) -> Unit
    ) : RecyclerView.Adapter<GatesAdapter.GateViewHolder>() {

        class GateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val gateNameTextView: TextView = itemView.findViewById(R.id.gateNameTextView)
            val gateNumberTextView: TextView = itemView.findViewById(R.id.gateNumberTextView)
            val cardView: MaterialCardView = itemView.findViewById(R.id.gateCardView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GateViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_gate, parent, false)
            return GateViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: GateViewHolder, position: Int) {
            val gate = gates[position]
            holder.gateNameTextView.text = gate.gateName
            holder.gateNumberTextView.text = "Gate #: ${gate.gateNumber}"
            holder.cardView.setOnClickListener {
                onItemClick(gate)
            }
        }

        override fun getItemCount() = gates.size

        fun updateGates(newGates: List<Gate>) {
            gates = newGates
            notifyDataSetChanged()
        }
    }
}
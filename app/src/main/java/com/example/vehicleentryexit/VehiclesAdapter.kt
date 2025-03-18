package com.example.vehicleentryexit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.models.Vehicle

class VehiclesAdapter(
    private val vehicles: List<Vehicle>,
    private val onItemClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<VehiclesAdapter.VehicleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicles[position]
        holder.bind(vehicle)
        holder.itemView.setOnClickListener { onItemClick(vehicle) }
    }

    override fun getItemCount() = vehicles.size

    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvVehicleNumber: TextView = itemView.findViewById(R.id.tvVehicleNumber)
        private val tvVehicleModel: TextView = itemView.findViewById(R.id.tvVehicleModel)

        fun bind(vehicle: Vehicle) {
            tvVehicleNumber.text = vehicle.vehicleNumber
            tvVehicleModel.text = vehicle.vehicleModelName
        }
    }
}
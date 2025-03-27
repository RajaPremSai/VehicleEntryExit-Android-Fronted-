package com.example.vehicleentryexit.manager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.R
import com.example.vehicleentryexit.ApiService
import com.example.vehicleentryexit.models.LogDTO
import com.example.vehicleentryexit.RetrofitClient
import com.example.vehicleentryexit.RetrofitClient.getAuthenticatedApiService
//import com.example.vehicleentryexit.models.LogDTO
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ManagerLogs : Fragment() {

    private lateinit var recyclerViewLogs: RecyclerView
    private lateinit var logsAdapter: LogsAdapter
    private val logsList = mutableListOf<LogEntry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_manager_logs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLogs = view.findViewById(R.id.logsRecyclerView)
        recyclerViewLogs.layoutManager = LinearLayoutManager(requireContext())

        fetchLogs()

        logsAdapter = LogsAdapter(logsList) {} // Empty lambda, no delete functionality for now
        recyclerViewLogs.adapter = logsAdapter
    }

    private fun fetchLogs() {
//        val apiService = getAuthenticatedApiService(requireContext())
//        val call = apiService.getLogs() // Ensure this is not null
//        call.enqueue(object : Callback<List<LogDTO>>
        val apiService = getAuthenticatedApiService(requireContext())
        apiService.getLogs1().enqueue(object : Callback<List<LogDTO>>{
            override fun onResponse(
                call: Call<List<LogDTO>>,
                response: Response<List<LogDTO>>
            ) {
                if (response.isSuccessful) {
                    val logDTOs = response.body() ?: emptyList()
                    logsList.clear() // Clear existing data
                    logsList.addAll(mapLogDTOsToLogEntries(logDTOs))
                    logsAdapter.notifyDataSetChanged()
                } else {
                    Log.e("ManagerLogs", "API request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<LogDTO>>, t: Throwable) {
                Log.e("ManagerLogs", "Network error: ${t.message}")
            }
        })
    }

    private fun mapLogDTOsToLogEntries(logDTOs: List<LogDTO>): List<LogEntry> {
        val logEntries = mutableListOf<LogEntry>()
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a")

        logDTOs.forEach { logDTO ->
            val date = logDTO.timeIn?.format(dateFormatter) ?: logDTO.timeOut?.format(dateFormatter) ?: "Unknown Date"
            if (logDTO.timeIn != null) {
                val timeIn = TimeLog(
                    "TIME IN",
                    "${logDTO.vehicleNumber}, ${logDTO.gateNumber}",
                    logDTO.timeIn.format(timeFormatter),
                    true
                )
                logEntries.add(LogEntry(date, timeIn, TimeLog("","", "", false)))
            }

            if (logDTO.timeOut != null) {
                val timeOut = TimeLog(
                    "TIME OUT",
                    "${logDTO.vehicleNumber}, ${logDTO.gateNumber}",
                    logDTO.timeOut.format(timeFormatter),
                    false
                )
                logEntries.add(LogEntry(date,TimeLog("","", "", true), timeOut))
            }

        }
        return logEntries
    }
}

data class LogDTO(
    val logId: String,
    val securityGuardId: String,
    val vehicleNumber: String,
    val timeIn: LocalDateTime?,
    val timeOut: LocalDateTime?,
    val gateNumber: String
)

data class LogEntry(
    val date: String,
    val timeIn: TimeLog,
    val timeOut: TimeLog
)

data class TimeLog(
    val type: String,
    val details: String,
    val time: String,
    val isTimeIn: Boolean
)

class LogsAdapter(
    private val logEntries: List<LogEntry>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<LogsAdapter.LogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_log, parent, false)
        return LogViewHolder(view)
    }

//    override fun onBindViewHolder(
//        holder: com.example.vehicleentryexit.sg.LogsAdapter.LogViewHolder,
//        position: Int
//    ) {
//        TODO("Not yet implemented")
//    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val logEntry = logEntries[position]
        holder.bind(logEntry)
    }

    override fun getItemCount() = logEntries.size

    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val ivTimeInIcon: ImageView = itemView.findViewById(R.id.ivTimeInIcon)
        private val tvTimeInDetails: TextView = itemView.findViewById(R.id.tvTimeInDetails)
        private val tvTimeInTime: TextView = itemView.findViewById(R.id.tvTimeInTime)
        private val ivTimeOutIcon: ImageView = itemView.findViewById(R.id.ivTimeOutIcon)
        private val tvTimeOutDetails: TextView = itemView.findViewById(R.id.tvTimeOutDetails)
        private val tvTimeOutTime: TextView = itemView.findViewById(R.id.tvTimeOutTime)
        private val timeInLayout: LinearLayout = itemView.findViewById(R.id.timeInLayout)
        private val timeOutLayout: LinearLayout = itemView.findViewById(R.id.timeOutLayout)

        fun bind(logEntry: LogEntry) {
            tvDate.text = logEntry.date

            if (logEntry.timeIn.type.isNotEmpty()) {
                timeInLayout.visibility = View.VISIBLE;
                tvTimeInDetails.text = "${logEntry.timeIn.type} - ${logEntry.timeIn.details}"
                tvTimeInTime.text = logEntry.timeIn.time
                ivTimeInIcon.setColorFilter(
                    ContextCompat.getColor(
                        itemView.context,
                        if (logEntry.timeIn.isTimeIn) R.color.green else R.color.red
                    )
                )
            } else {
                timeInLayout.visibility = View.GONE;
            }

            if (logEntry.timeOut.type.isNotEmpty()) {
                timeOutLayout.visibility = View.VISIBLE;
                tvTimeOutDetails.text = "${logEntry.timeOut.type} - ${logEntry.timeOut.details}"
                tvTimeOutTime.text = logEntry.timeOut.time
                ivTimeOutIcon.setColorFilter(
                    ContextCompat.getColor(
                        itemView.context,
                        if (!logEntry.timeOut.isTimeIn) R.color.red else R.color.green
                    )
                )
            } else {
                timeOutLayout.visibility = View.GONE;
            }
        }
    }
}

//class ManagerLogs : Fragment() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: LogsAdapter
//    private lateinit var apiService: ApiService
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_manager_logs, container, false)
//        recyclerView = view.findViewById(R.id.logsRecyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        adapter = LogsAdapter(emptyList())
//        recyclerView.adapter = adapter
//
//        apiService = RetrofitClient.getAuthenticatedApiService(requireContext())
//        fetchLogs()
//
//        return view
//    }
//
//    private fun fetchLogs() {
//        apiService.getLogs1().enqueue(object : Callback<List<LogDTO>> {
//            override fun onResponse(call: Call<List<LogDTO>>, response: Response<List<LogDTO>>) {
//                if (response.isSuccessful) {
//                    val logs = response.body() ?: emptyList()
//                    adapter.updateLogs(logs)
//                } else {
//                    Toast.makeText(context, "Failed to fetch logs", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<List<LogDTO>>, t: Throwable) {
//                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private class LogsAdapter(private var logs: List<LogDTO>) : RecyclerView.Adapter<LogsAdapter.LogViewHolder>() {
//
//        class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val tvDate: TextView = itemView.findViewById(R.id.tvDate)
//            val timeInLayout: View = itemView.findViewById(R.id.timeInLayout)
//            val tvTimeInDetails: TextView = itemView.findViewById(R.id.tvTimeInDetails)
//            val tvTimeInTime: TextView = itemView.findViewById(R.id.tvTimeInTime)
//            val timeOutLayout: View = itemView.findViewById(R.id.timeOutLayout)
//            val tvTimeOutDetails: TextView = itemView.findViewById(R.id.tvTimeOutDetails)
//            val tvTimeOutTime: TextView = itemView.findViewById(R.id.tvTimeOutTime)
//            val cardView : MaterialCardView = itemView.findViewById(R.id.cardView)
//            val timeInIcon : ImageView = itemView.findViewById(R.id.ivTimeInIcon)
//            val timeOutIcon : ImageView = itemView.findViewById(R.id.ivTimeOutIcon)
//
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
//            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user_log, parent, false)
//            return LogViewHolder(itemView)
//        }
//
//        override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
//            val log = logs[position]
//            val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
//            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a")
//
//            holder.tvDate.text = try {
//                log.timeIn?.format(dateFormatter) ?: log.timeOut?.format(dateFormatter) ?: "Date N/A"
//            } catch (e: DateTimeParseException) {
//                "Date N/A"
//            }
//
//            if (log.timeIn != null) {
//                holder.timeInLayout.visibility = View.VISIBLE
//                holder.tvTimeInDetails.text = "TIME IN - ${log.vehicleNumber}, Gate: ${log.gateNumber}"
//                holder.tvTimeInTime.text = try {
//                    log.timeIn?.format(timeFormatter) ?: "Time N/A"
//                } catch (e: DateTimeParseException) {
//                    "Time N/A"
//                }
//            } else {
//                holder.timeInLayout.visibility = View.GONE
//            }
//
//            if (log.timeOut != null) {
//                holder.timeOutLayout.visibility = View.VISIBLE
//                holder.tvTimeOutDetails.text = "TIME OUT - ${log.vehicleNumber}, Gate: ${log.gateNumber}"
//                holder.tvTimeOutTime.text = try {
//                    log.timeOut?.format(timeFormatter) ?: "Time N/A"
//                } catch (e: DateTimeParseException) {
//                    "Time N/A"
//                }
//            } else {
//                holder.timeOutLayout.visibility = View.GONE
//            }
//        }
//
//        override fun getItemCount() = logs.size
//
//        fun updateLogs(newLogs: List<LogDTO>) {
//            logs = newLogs
//            notifyDataSetChanged()
//        }
//    }
//}
package com.example.vehicleentryexit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.RetrofitClient.getAuthenticatedApiService
import com.example.vehicleentryexit.sg.LogEntry
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView

class UserLogsActivity : AppCompatActivity() {
    private lateinit var recyclerViewLogs: RecyclerView
    private lateinit var logsAdapter: LogsAdapter
    private val logsList = mutableListOf<LogEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_logs)

        // Initialize RecyclerView
        recyclerViewLogs = findViewById(R.id.recyclerViewLogs)
        recyclerViewLogs.layoutManager = LinearLayoutManager(this)

        // Setup adapter
        logsAdapter = LogsAdapter(logsList) { }
        recyclerViewLogs.adapter = logsAdapter

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, UserHomeActivity::class.java)
            startActivity(intent)
        }
        fetchUserLogs()
    }

    private fun fetchUserLogs() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val empNumber = sharedPreferences.getString("empNumber", null)
        val token = sharedPreferences.getString("token", null)
        Log.d("UserLogsActivity", "EmpNumber: $empNumber, Token: $token")

        if (empNumber.isNullOrEmpty()) {
            Toast.makeText(this, "Employee number not found", Toast.LENGTH_SHORT).show()
            return
        }
        val apiService = getAuthenticatedApiService(this)
        apiService.getUserVehicleLogs(empNumber)
            .enqueue(object :Callback<List<com.example.vehicleentryexit.models.Log>> {
            override fun onResponse(
                call: Call<List<com.example.vehicleentryexit.models.Log>>,
                response: Response<List<com.example.vehicleentryexit.models.Log>>
            ) {
                if (response.isSuccessful) {
                    val logDTOs = response.body() ?: emptyList()
                    logsList.clear() // Clear existing data
                    logsList.addAll(mapLogDTOsToLogEntries(logDTOs))
                    logsAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@UserLogsActivity, "Failed to fetch logs", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<com.example.vehicleentryexit.models.Log>>, t: Throwable) {
                Log.e("UserLogsActivity", "Network error: ${t.message}")
                Toast.makeText(this@UserLogsActivity, "Error fetching logs", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mapLogDTOsToLogEntries(logDTOs: List<com.example.vehicleentryexit.models.Log>): List<LogEntry> {
        val logEntries = mutableListOf<LogEntry>()
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a")

        logDTOs.forEach { logDTO ->
            val date = logDTO.timeIn?.format(dateFormatter) ?: logDTO.timeOut?.format(dateFormatter) ?: "Unknown Date"
            if (logDTO.timeIn != null) {
                val timeIn = com.example.vehicleentryexit.sg.TimeLog(
                    "TIME IN",
                    "${logDTO.vehicleNumber}, ${logDTO.gateNumber}",
                    logDTO.timeIn.format(timeFormatter),
                    true
                )
                logEntries.add(LogEntry(date, timeIn, com.example.vehicleentryexit.sg.TimeLog("","", "", false)))
            }

            if (logDTO.timeOut != null) {
                val timeOut = com.example.vehicleentryexit.sg.TimeLog(
                    "TIME OUT",
                    "${logDTO.vehicleNumber}, ${logDTO.gateNumber}",
                    logDTO.timeOut.format(timeFormatter),
                    false
                )
                logEntries.add(
                    LogEntry(date,
                        com.example.vehicleentryexit.sg.TimeLog("","", "", true), timeOut)
                )
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
) : RecyclerView.Adapter<LogsAdapter.ViewHolder>() { // Renamed ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { // Renamed ViewHolder
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_log, parent, false)
        return ViewHolder(view) // Renamed ViewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { // Renamed ViewHolder
        val logEntry = logEntries[position]
        holder.bind(logEntry)
    }

    override fun getItemCount() = logEntries.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { // Renamed ViewHolder
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
                timeInLayout.visibility = View.VISIBLE
                tvTimeInDetails.text = "${logEntry.timeIn.type} - ${logEntry.timeIn.details}"
                tvTimeInTime.text = logEntry.timeIn.time
                ivTimeInIcon.setColorFilter(
                    ContextCompat.getColor(itemView.context,
                        if (logEntry.timeIn.isTimeIn) R.color.green else R.color.red)
                )
            } else {
                timeInLayout.visibility = View.GONE
            }

            if (logEntry.timeOut.type.isNotEmpty()) {
                timeOutLayout.visibility = View.VISIBLE
                tvTimeOutDetails.text = "${logEntry.timeOut.type} - ${logEntry.timeOut.details}"
                tvTimeOutTime.text = logEntry.timeOut.time
                ivTimeOutIcon.setColorFilter(
                    ContextCompat.getColor(itemView.context,
                        if (!logEntry.timeOut.isTimeIn) R.color.red else R.color.green)
                )
            } else {
                timeOutLayout.visibility = View.GONE
            }
        }
    }
}

//class LogsAdapter(
//    private val logEntries: List<LogEntry>,
//    private val onDeleteClick: (Int) -> Unit
//) : RecyclerView.Adapter<com.example.vehicleentryexit.sg.LogsAdapter.LogViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_user_log, parent, false)
//        return LogViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
//        val logEntry = logEntries[position]
//        holder.bind(logEntry)
//    }
//
//    override fun getItemCount() = logEntries.size
//
//    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
//        private val ivTimeInIcon: ImageView = itemView.findViewById(R.id.ivTimeInIcon)
//        private val tvTimeInDetails: TextView = itemView.findViewById(R.id.tvTimeInDetails)
//        private val tvTimeInTime: TextView = itemView.findViewById(R.id.tvTimeInTime)
//        private val ivTimeOutIcon: ImageView = itemView.findViewById(R.id.ivTimeOutIcon)
//        private val tvTimeOutDetails: TextView = itemView.findViewById(R.id.tvTimeOutDetails)
//        private val tvTimeOutTime: TextView = itemView.findViewById(R.id.tvTimeOutTime)
//        private val timeInLayout: LinearLayout = itemView.findViewById(R.id.timeInLayout)
//        private val timeOutLayout: LinearLayout = itemView.findViewById(R.id.timeOutLayout)
//
//        fun bind(logEntry: LogEntry) {
//            tvDate.text = logEntry.date
//
//            if(logEntry.timeIn.type.isNotEmpty()){
//                timeInLayout.visibility = View.VISIBLE;
//                tvTimeInDetails.text = "${logEntry.timeIn.type} - ${logEntry.timeIn.details}"
//                tvTimeInTime.text = logEntry.timeIn.time
//                ivTimeInIcon.setColorFilter(
//                    ContextCompat.getColor(itemView.context,
//                        if (logEntry.timeIn.isTimeIn) R.color.green else R.color.red)
//                )
//            } else {
//                timeInLayout.visibility = View.GONE;
//            }
//
//            if(logEntry.timeOut.type.isNotEmpty()){
//                timeOutLayout.visibility = View.VISIBLE;
//                tvTimeOutDetails.text = "${logEntry.timeOut.type} - ${logEntry.timeOut.details}"
//                tvTimeOutTime.text = logEntry.timeOut.time
//                ivTimeOutIcon.setColorFilter(
//                    ContextCompat.getColor(itemView.context,
//                        if (!logEntry.timeOut.isTimeIn) R.color.red else R.color.green)
//                )
//            } else {
//                timeOutLayout.visibility = View.GONE;
//            }
//        }
//    }
//}

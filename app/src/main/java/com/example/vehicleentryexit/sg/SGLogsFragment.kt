package com.example.vehicleentryexit.sg

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
//import androidx.core.i18n.DateTimeFormatter
import java.time.LocalDateTime
import com.example.vehicleentryexit.models.LogDTO
import java.time.format.DateTimeFormatter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.R
import com.example.vehicleentryexit.RetrofitClient.getAuthenticatedApiService
import com.example.vehicleentryexit.models.AnnouncementDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
//import java.time.LocalDateTime

//class SGLogsFragment : Fragment() {
//
//    private lateinit var recyclerViewLogs: RecyclerView
//    private lateinit var logsAdapter: LogsAdapter
//    private val logsList = mutableListOf<LogEntry>()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return inflater.inflate(R.layout.fragment_s_g_logs, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Initialize RecyclerView
//        recyclerViewLogs = view.findViewById(R.id.recyclerViewLogs)
//        recyclerViewLogs.layoutManager = LinearLayoutManager(requireContext())
//
//        // Populate sample data
//        populateSampleData()
//
//        // Setup adapter
//        logsAdapter = LogsAdapter(logsList) { position ->
//            logsList.removeAt(position)
//            logsAdapter.notifyItemRemoved(position)
//        }
//        recyclerViewLogs.adapter = logsAdapter
//    }
//
//    private fun populateSampleData() {
//        logsList.addAll(
//            listOf(
//                LogEntry(
//                    "05/21/2022",
//                    TimeLog("TIME IN", "ABC 126, Honda Civic 2022, C", "02:53:21 PM", true),
//                    TimeLog("TIME OUT", "ABC 126, Honda Civic 2022, C", "02:53:21 PM", false)
//                ),
//                LogEntry(
//                    "05/23/2022",
//                    TimeLog("TIME IN", "XYZ 456, Toyota Innova, C", "10:21:15 AM", true),
//                    TimeLog("TIME OUT", "XYZ 125, Toyota Innova, C", "04:01:55 PM", false)
//                )
//            )
//        )
//    }
//}

class SGLogsFragment : Fragment() {

    private lateinit var recyclerViewLogs: RecyclerView
    private lateinit var logsAdapter: LogsAdapter
    private val logsList = mutableListOf<LogEntry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_s_g_logs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLogs = view.findViewById(R.id.recyclerViewLogs)
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
        apiService.getLogs().enqueue(object : Callback<List<LogDTO>>{
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
                    Log.e("SGLogsFragment", "API request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<LogDTO>>, t: Throwable) {
                Log.e("SGLogsFragment", "Network error: ${t.message}")
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

            if(logEntry.timeIn.type.isNotEmpty()){
                timeInLayout.visibility = View.VISIBLE;
                tvTimeInDetails.text = "${logEntry.timeIn.type} - ${logEntry.timeIn.details}"
                tvTimeInTime.text = logEntry.timeIn.time
                ivTimeInIcon.setColorFilter(
                    ContextCompat.getColor(itemView.context,
                        if (logEntry.timeIn.isTimeIn) R.color.green else R.color.red)
                )
            } else {
                timeInLayout.visibility = View.GONE;
            }

            if(logEntry.timeOut.type.isNotEmpty()){
                timeOutLayout.visibility = View.VISIBLE;
                tvTimeOutDetails.text = "${logEntry.timeOut.type} - ${logEntry.timeOut.details}"
                tvTimeOutTime.text = logEntry.timeOut.time
                ivTimeOutIcon.setColorFilter(
                    ContextCompat.getColor(itemView.context,
                        if (!logEntry.timeOut.isTimeIn) R.color.red else R.color.green)
                )
            } else {
                timeOutLayout.visibility = View.GONE;
            }
        }
    }

//    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
//        private val ivTimeInIcon: ImageView = itemView.findViewById(R.id.ivTimeInIcon)
//        private val tvTimeInDetails: TextView = itemView.findViewById(R.id.tvTimeInDetails)
//        private val tvTimeInTime: TextView = itemView.findViewById(R.id.tvTimeInTime)
//        private val ivTimeOutIcon: ImageView = itemView.findViewById(R.id.ivTimeOutIcon)
//        private val tvTimeOutDetails: TextView = itemView.findViewById(R.id.tvTimeOutDetails)
//        private val tvTimeOutTime: TextView = itemView.findViewById(R.id.tvTimeOutTime)
//
//        fun bind(logEntry: LogEntry) {
//            tvDate.text = logEntry.date
//            tvTimeInDetails.text = "${logEntry.timeIn.type} - ${logEntry.timeIn.details}"
//            tvTimeInTime.text = logEntry.timeIn.time
//            ivTimeInIcon.setColorFilter(
//                ContextCompat.getColor(itemView.context,
//                    if (logEntry.timeIn.isTimeIn) R.color.green else R.color.red)
//            )
//
//            tvTimeOutDetails.text = "${logEntry.timeOut.type} - ${logEntry.timeOut.details}"
//            tvTimeOutTime.text = logEntry.timeOut.time
//            ivTimeOutIcon.setColorFilter(
//                ContextCompat.getColor(itemView.context,
//                    if (!logEntry.timeOut.isTimeIn) R.color.red else R.color.green)
//            )
//        }
//    }
}
//}
//
//// Data classes for logs
//data class LogEntry(
//    val date: String,
//    val timeIn: TimeLog,
//    val timeOut: TimeLog
//)
//
//data class TimeLog(
//    val type: String,
//    val details: String,
//    val time: String,
//    val isTimeIn: Boolean
//)
//
//// RecyclerView Adapter
//class LogsAdapter(
//    private val logEntries: List<LogEntry>,
//    private val onDeleteClick: (Int) -> Unit
//) : RecyclerView.Adapter<LogsAdapter.LogViewHolder>() {
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
//
//        fun bind(logEntry: LogEntry) {
//            tvDate.text = logEntry.date
//            tvTimeInDetails.text = "${logEntry.timeIn.type} - ${logEntry.timeIn.details}"
//            tvTimeInTime.text = logEntry.timeIn.time
//            ivTimeInIcon.setColorFilter(
//                ContextCompat.getColor(itemView.context,
//                    if (logEntry.timeIn.isTimeIn) R.color.green else R.color.red)
//            )
//
//            tvTimeOutDetails.text = "${logEntry.timeOut.type} - ${logEntry.timeOut.details}"
//            tvTimeOutTime.text = logEntry.timeOut.time
//            ivTimeOutIcon.setColorFilter(
//                ContextCompat.getColor(itemView.context,
//                    if (!logEntry.timeOut.isTimeIn) R.color.red else R.color.green)
//            )
//        }
//    }
//}

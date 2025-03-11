package com.example.vehicleentryexit.sg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.R

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

        // Initialize RecyclerView
        recyclerViewLogs = view.findViewById(R.id.recyclerViewLogs)
        recyclerViewLogs.layoutManager = LinearLayoutManager(requireContext())

        // Populate sample data
        populateSampleData()

        // Setup adapter
        logsAdapter = LogsAdapter(logsList) { position ->
            logsList.removeAt(position)
            logsAdapter.notifyItemRemoved(position)
        }
        recyclerViewLogs.adapter = logsAdapter
    }

    private fun populateSampleData() {
        logsList.addAll(
            listOf(
                LogEntry(
                    "05/21/2022",
                    TimeLog("TIME IN", "ABC 126, Honda Civic 2022, C", "02:53:21 PM", true),
                    TimeLog("TIME OUT", "ABC 126, Honda Civic 2022, C", "02:53:21 PM", false)
                ),
                LogEntry(
                    "05/23/2022",
                    TimeLog("TIME IN", "XYZ 456, Toyota Innova, C", "10:21:15 AM", true),
                    TimeLog("TIME OUT", "XYZ 125, Toyota Innova, C", "04:01:55 PM", false)
                )
            )
        )
    }
}

// Data classes for logs
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

// RecyclerView Adapter
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

        fun bind(logEntry: LogEntry) {
            tvDate.text = logEntry.date
            tvTimeInDetails.text = "${logEntry.timeIn.type} - ${logEntry.timeIn.details}"
            tvTimeInTime.text = logEntry.timeIn.time
            ivTimeInIcon.setColorFilter(
                ContextCompat.getColor(itemView.context,
                    if (logEntry.timeIn.isTimeIn) R.color.green else R.color.red)
            )

            tvTimeOutDetails.text = "${logEntry.timeOut.type} - ${logEntry.timeOut.details}"
            tvTimeOutTime.text = logEntry.timeOut.time
            ivTimeOutIcon.setColorFilter(
                ContextCompat.getColor(itemView.context,
                    if (!logEntry.timeOut.isTimeIn) R.color.red else R.color.green)
            )
        }
    }
}

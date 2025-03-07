package com.example.vehicleentryexit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        // Populate sample data
        populateSampleData()

        // Setup adapter
        logsAdapter = LogsAdapter(logsList) { position ->
            // Handle delete click
            logsList.removeAt(position)
            logsAdapter.notifyItemRemoved(position)
        }
        recyclerViewLogs.adapter = logsAdapter

        var backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, UserHomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun populateSampleData() {
        logsList.addAll(listOf(
            LogEntry("05/21/2022",
                TimeLog("TIME IN", "ABC 126, Honda Civic 2022, C", "02:53:21 PM", true),
                TimeLog("TIME OUT", "ABC 126, Honda Civic 2022, C", "02:53:21 PM", false)
            ),
            LogEntry("05/23/2022",
                TimeLog("TIME IN", "XYZ 456, Toyota Innova, C", "10:21:15 AM", true),
                TimeLog("TIME OUT", "XYZ 125, Toyota Innova, C", "04:01:55 PM", false)
            )
        ))
    }
}

// Data classes to represent log entries
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

        // Delete button click listener
//        holder.itemView.findViewById<ImageButton>(R.id.btnDelete)
//            .setOnClickListener { onDeleteClick(position) }
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
            // Set date
            tvDate.text = logEntry.date

            // Set Time In details
            tvTimeInDetails.text = "${logEntry.timeIn.type} - ${logEntry.timeIn.details}"
            tvTimeInTime.text = logEntry.timeIn.time
            ivTimeInIcon.setColorFilter(
                ContextCompat.getColor(itemView.context,
                    if (logEntry.timeIn.isTimeIn) R.color.green else R.color.red)
            )

            // Set Time Out details
            tvTimeOutDetails.text = "${logEntry.timeOut.type} - ${logEntry.timeOut.details}"
            tvTimeOutTime.text = logEntry.timeOut.time
            ivTimeOutIcon.setColorFilter(
                ContextCompat.getColor(itemView.context,
                    if (!logEntry.timeOut.isTimeIn) R.color.red else R.color.green)
            )
        }
    }
}
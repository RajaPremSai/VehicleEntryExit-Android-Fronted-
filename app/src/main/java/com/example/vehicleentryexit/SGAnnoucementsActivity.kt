package com.example.vehicleentryexit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SGAnnoucementsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val announcements = mutableListOf(
        SGAnnouncement("New Event", "Join us for the upcoming hackathon!", "March 5, 2025"),
        SGAnnouncement("Maintenance", "Scheduled server downtime at midnight.", "March 10, 2025"),
        SGAnnouncement("Cybersecurity Alert", "Beware of phishing emails.", "March 15, 2025")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sgannoucements)

        recyclerView = findViewById(R.id.recyclerViewAnnouncements)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SGAnnoucementsAdapter(announcements)

        var backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, UserHomeActivity::class.java)
            startActivity(intent)
        }
    }
}

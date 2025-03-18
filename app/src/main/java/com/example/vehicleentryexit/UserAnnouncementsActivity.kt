package com.example.vehicleentryexit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.RetrofitClient.getAuthenticatedApiService
import com.example.vehicleentryexit.models.AnnouncementDTO
import com.example.vehicleentryexit.sg.SGAnnoucementsAdapter
import com.example.vehicleentryexit.sg.SGAnnouncement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserAnnouncementsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_announcements)

        recyclerView = findViewById(R.id.recyclerViewAnnouncements)

        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, UserHomeActivity::class.java)
            startActivity(intent)
        }
        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        val apiService = getAuthenticatedApiService(this)
        apiService.getAllAnnouncements1().enqueue(object : Callback<List<AnnouncementDTO>> {
            override fun onResponse(
                call: Call<List<AnnouncementDTO>>,
                response: Response<List<AnnouncementDTO>>
            ) {
                if (response.isSuccessful) {
                    val announcements = response.body() ?: emptyList()
                    recyclerView.adapter = SGAnnoucementsAdapter(announcements.map {
                        SGAnnouncement(it.title, it.description, it.date)
                    })
                } else {
                    // Handle error (e.g., show a toast or log the error)
                    Log.e("UserAnnouncementsActivity", "API request failed: ${response.code()}")
                    // Optionally show a user-friendly message
                }
            }

            override fun onFailure(call: Call<List<AnnouncementDTO>>, t: Throwable) {
                // Handle network errors (e.g., show a toast or log the error)
                Log.e("UserAnnouncementsActivity", "Network error: ${t.message}")
                // Optionally show a user-friendly message
            }
        })
    }
}

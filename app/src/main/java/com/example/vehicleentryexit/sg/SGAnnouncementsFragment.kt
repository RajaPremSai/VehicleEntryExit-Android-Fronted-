package com.example.vehicleentryexit.sg

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.R
import com.example.vehicleentryexit.RetrofitClient.getAuthenticatedApiService
import com.example.vehicleentryexit.models.AnnouncementDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SGAnnouncementsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_s_g_announcements, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAnnouncements)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchAnnouncements()

        return view
    }

    private fun fetchAnnouncements() {
        val apiService = getAuthenticatedApiService(requireContext())
        apiService.getAllAnnouncements().enqueue(object : Callback<List<AnnouncementDTO>> {
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
                    Log.e("SGAnnouncementsFragment", "API request failed: ${response.code()}")
                    // Optionally show a user-friendly message
                }
            }

            override fun onFailure(call: Call<List<AnnouncementDTO>>, t: Throwable) {
                // Handle network errors (e.g., show a toast or log the error)
                Log.e("SGAnnouncementsFragment", "Network error: ${t.message}")
                // Optionally show a user-friendly message
            }
        })
    }

    companion object {
        fun newInstance(): SGAnnouncementsFragment {
            return SGAnnouncementsFragment()
        }
    }
}

//class SGAnnouncementsFragment : Fragment() {
//
//    private lateinit var recyclerView: RecyclerView
//    private val announcements = mutableListOf(
//        SGAnnouncement("New Event", "Join us for the upcoming hackathon!", "March 5, 2025"),
//        SGAnnouncement("Maintenance", "Scheduled server downtime at midnight.", "March 10, 2025"),
//        SGAnnouncement("Cybersecurity Alert", "Beware of phishing emails.", "March 15, 2025")
//    )
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_s_g_announcements, container, false)
//
//        recyclerView = view.findViewById(R.id.recyclerViewAnnouncements) // Ensure this ID matches your layout
//
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = SGAnnoucementsAdapter(announcements) // Assuming you have an adapter
//
//        return view
//    }
//
//    companion object {
//        fun newInstance(): SGAnnouncementsFragment {
//            return SGAnnouncementsFragment()
//        }
//    }
//}
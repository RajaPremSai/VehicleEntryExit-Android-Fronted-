package com.example.vehicleentryexit.sg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.R

class SGAnnouncementsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val announcements = mutableListOf(
        SGAnnouncement("New Event", "Join us for the upcoming hackathon!", "March 5, 2025"),
        SGAnnouncement("Maintenance", "Scheduled server downtime at midnight.", "March 10, 2025"),
        SGAnnouncement("Cybersecurity Alert", "Beware of phishing emails.", "March 15, 2025")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_s_g_announcements, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAnnouncements) // Ensure this ID matches your layout

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = SGAnnoucementsAdapter(announcements) // Assuming you have an adapter

        return view
    }

    companion object {
        fun newInstance(): SGAnnouncementsFragment {
            return SGAnnouncementsFragment()
        }
    }
}
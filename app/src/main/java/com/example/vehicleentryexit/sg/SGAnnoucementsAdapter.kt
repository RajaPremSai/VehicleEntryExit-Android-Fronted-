package com.example.vehicleentryexit.sg


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.R

class SGAnnoucementsAdapter(private val announcements: List<SGAnnouncement>) :
    RecyclerView.Adapter<SGAnnoucementsAdapter.AnnouncementViewHolder>() {

    class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.announcementTitle)
        val description: TextView = itemView.findViewById(R.id.announcementDescription)
        val date: TextView = itemView.findViewById(R.id.announcementDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.title.text = announcement.title
        holder.description.text = announcement.description
        holder.date.text = announcement.date
    }

    override fun getItemCount(): Int = announcements.size
}

package com.example.vehicleentryexit.manager

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vehicleentryexit.R
import com.example.vehicleentryexit.ApiService
import com.example.vehicleentryexit.RetrofitClient
import com.example.vehicleentryexit.models.Announcement
import com.example.vehicleentryexit.models.AnnouncementDTO
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ManagerAnnouncementsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnnouncementsAdapter
    private lateinit var apiService: ApiService
    private lateinit var fabAddAnnouncement: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manager_announcements_fragement, container, false)
        recyclerView = view.findViewById(R.id.announcementsRecyclerView)
        fabAddAnnouncement = view.findViewById(R.id.fabAddAnnouncement)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AnnouncementsAdapter(emptyList(), this::showAnnouncementDetails)
        recyclerView.adapter = adapter

        apiService = RetrofitClient.getAuthenticatedApiService(requireContext())

        fetchAnnouncements()

        fabAddAnnouncement.setOnClickListener {
            showAddAnnouncementDialog()
        }

        return view
    }

    private fun fetchAnnouncements() {
        apiService.getAnnouncements().enqueue(object : Callback<List<Announcement>> {
            override fun onResponse(call: Call<List<Announcement>>, response: Response<List<Announcement>>) {
                if (response.isSuccessful) {
                    val announcements = response.body() ?: emptyList()
                    adapter.updateAnnouncements(announcements)
                } else {
                    Toast.makeText(context, "Failed to fetch announcements", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Announcement>>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAnnouncementDetails(announcement: Announcement) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_announcement_details, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()

        dialogView.findViewById<TextView>(R.id.titleTextView).text = "Title: ${announcement.title}"
        dialogView.findViewById<TextView>(R.id.descriptionTextView).text = "Description: ${announcement.description}"
        dialogView.findViewById<TextView>(R.id.dateTextView).text = "Date: ${announcement.date}"

        dialogView.findViewById<View>(R.id.backButton).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.deleteButton).setOnClickListener {
            showDeleteConfirmationDialog(announcement.id)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(announcementId: String) {
        AlertDialog.Builder(context)
            .setTitle("Delete Announcement")
            .setMessage("Are you sure you want to delete this announcement?")
            .setPositiveButton("Yes") { _, _ ->
                deleteAnnouncement(announcementId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteAnnouncement(announcementId: String) {
        apiService.deleteAnnouncement(announcementId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Announcement deleted", Toast.LENGTH_SHORT).show()
                    fetchAnnouncements()
                } else {
                    Toast.makeText(context, "Failed to delete announcement", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddAnnouncementDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_announcement, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()

        dialogView.findViewById<View>(R.id.saveButton).setOnClickListener {
            val titleEditText = dialogView.findViewById<EditText>(R.id.titleEditText)
            val descriptionEditText = dialogView.findViewById<EditText>(R.id.descriptionEditText)

            val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()).toString()

            val newAnnouncement = AnnouncementDTO(
                null.toString(), // id is auto generated by backend
                titleEditText.text.toString(),
                descriptionEditText.text.toString(),
                currentDate
            )
//            Log.d("Date sent", currentDate) // add this log.
            addAnnouncement(newAnnouncement)
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addAnnouncement(announcementDTO: AnnouncementDTO) {
        apiService.addAnnouncement(announcementDTO).enqueue(object : Callback<Announcement> {
            override fun onResponse(call: Call<Announcement>, response: Response<Announcement>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Announcement added", Toast.LENGTH_SHORT).show()
                    fetchAnnouncements()
                } else {
                    Toast.makeText(context, "Failed to add announcement", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Announcement>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private class AnnouncementsAdapter(
        private var announcements: List<Announcement>,
        private val onItemClick: (Announcement) -> Unit
    ) : RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementViewHolder>() {

        class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.announcementTitleTextView)
            val dateTextView: TextView = itemView.findViewById(R.id.announcementDateTextView)
            val cardView: MaterialCardView = itemView.findViewById(R.id.announcementCardView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_announcement1, parent, false)
            return AnnouncementViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
            val announcement = announcements[position]
            holder.titleTextView.text = announcement.title
            holder.dateTextView.text = announcement.date
            holder.cardView.setOnClickListener {
                onItemClick(announcement)
            }
        }

        override fun getItemCount() = announcements.size

        fun updateAnnouncements(newAnnouncements: List<Announcement>) {
            announcements = newAnnouncements
            notifyDataSetChanged()
        }
    }
}
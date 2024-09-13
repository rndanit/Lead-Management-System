package com.rndtechnosoft.lms.Activity.Adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.NotificationResponseItem
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationAdapter(
    private val context: Context,
    var notificationList: MutableList<NotificationResponseItem>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    private val token: String? = sharedPreferences.getString("token", null)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.bind(notification)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }


    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val dateTextView: TextView = itemView.findViewById(R.id.date)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_loader)
        private val description: TextView = itemView.findViewById(R.id.description)


        fun bind(notification: NotificationResponseItem) {
            // Common fields
            nameTextView.text = notification.title
            description.text = notification.body

            // Extracting only the date part from the createdAt field
            val fullDate = notification.createdAt
            val dateOnly =
                fullDate.split("T")[0] // This will give you the date part (before the 'T')
            dateTextView.text = dateOnly
        }


    }
}

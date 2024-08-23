package com.rndtechnosoft.lms.Activity.Adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.NotificationResponse
import com.rndtechnosoft.lms.Activity.LeadNotificationActivity
import com.rndtechnosoft.lms.R
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationAdapter(
    private val context: Context,
    var notification: MutableList<NotificationResponse>
) : RecyclerView.Adapter<NotificationAdapter.LeadViewHolder>() {

    fun removeNotification(position: Int) {
        if (position >= 0 && position < notification.size) {
            notification.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, notification.size)
        }
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    private val token: String? = sharedPreferences.getString("token", null)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return LeadViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeadViewHolder, position: Int) {
        val notification = notification[position]
        holder.bind(notification)
    }

    override fun getItemCount(): Int {
        return notification.size
    }

    inner class LeadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val emailTextView: TextView = itemView.findViewById(R.id.email)
        private val mobileNumberTextView: TextView = itemView.findViewById(R.id.mobile_number)
        private val companyTextView: TextView = itemView.findViewById(R.id.CompanyNames)
        private val leadInfoTextView: TextView = itemView.findViewById(R.id.LeadSources)
        private val leadDetailTextView: TextView = itemView.findViewById(R.id.lead_detail)
        private val statusTextView: TextView = itemView.findViewById(R.id.statusText)
        private val addLeadButton: Button = itemView.findViewById(R.id.add_notification)
        private val cancelButton: TextView = itemView.findViewById(R.id.cancel_notification)

        fun bind(notification: NotificationResponse) {
            nameTextView.text = " ${notification.firstname} ${notification.lastname}"
            emailTextView.text = " ${notification.email}"
            mobileNumberTextView.text = "${notification.mobile}"
            companyTextView.text = "${notification.companyname}"
            leadInfoTextView.text = "${notification.leadInfo}"
            leadDetailTextView.text = " ${notification.leadsDetails}"
            statusTextView.text = "${notification.status}"

            addLeadButton.setOnClickListener {
                if (token != null) {
                    performApiAction(notification._id, true)
                } else {
                    Toast.makeText(context, "Authentication token is missing", Toast.LENGTH_SHORT).show()
                }
            }

            cancelButton.setOnClickListener {
                if (token != null) {
                    performApiAction(notification._id, false)
                } else {
                    Toast.makeText(context, "Authentication token is missing", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun performApiAction(id: String, isExcept: Boolean) {
            RetrofitInstance.apiInterface.notificationOperations("Bearer $token", id, isExcept)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                // Remove notification from the list
                                removeNotification(adapterPosition)
                                // Notify user
                                Toast.makeText(context, "Action successful", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.e("API Error", "Response Code: ${response.code()}, Message: ${response.message()}"
                            )
                            Toast.makeText(context, "Failed to perform action: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("API Error", "Error: ${t.message}", t)
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

    }

}

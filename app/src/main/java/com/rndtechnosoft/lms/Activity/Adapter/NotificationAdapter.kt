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

    fun removeNotification(position: Int) {
        if (position >= 0 && position < notificationList.size) {
            notificationList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, notificationList.size)
        }
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val emailTextView: TextView = itemView.findViewById(R.id.email)
        private val mobileNumberTextView: TextView = itemView.findViewById(R.id.mobile_number)
        private val companyTextView: TextView = itemView.findViewById(R.id.CompanyNames)
        private val leadInfoTextView: TextView = itemView.findViewById(R.id.LeadSources)
        private val leadDetailTextView: TextView = itemView.findViewById(R.id.lead_detail)
        private val statusTextView: TextView = itemView.findViewById(R.id.statusText)
        private val addLeadButton: Button = itemView.findViewById(R.id.add_notification)
        private val cancelButton: TextView = itemView.findViewById(R.id.cancel_notification)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_loader)
        private val leaddetailnew: TextView = itemView.findViewById(R.id.leadDetail)
        private val companynamenew: CardView = itemView.findViewById(R.id.comapny_name)

        fun bind(notification: NotificationResponseItem) {
            if (notification.firstname != "" && notification.lastname != "" && notification.mobile != "") {
                // For Type 1 notifications
                nameTextView.text = "${notification.firstname} ${notification.lastname}"
                mobileNumberTextView.text = notification.mobile
                leadInfoTextView.text = notification.leadInfo
                leadDetailTextView.text = notification.leadsDetails
                companyTextView.text = notification.companyname
            } else {
                // For Type 2 notifications
                nameTextView.text = notification.name
                mobileNumberTextView.text = notification.phone
                leadInfoTextView.text = "Own Website"
                leadDetailTextView.text = notification.message
                leaddetailnew.text = "Message:"

                // Hide companynamenew CardView for Type 2 notifications
                companynamenew.visibility = View.GONE
            }
            // Common fields
            emailTextView.text = notification.email ?: "No Email"
            statusTextView.text = notification.status ?: "No Status"

            addLeadButton.setOnClickListener {
                if (token != null) {
                    progressBar.visibility = View.VISIBLE
                    performApiAction(notification._id, true)


                } else {
                    Toast.makeText(context, "Authentication token is missing", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            cancelButton.setOnClickListener {
                if (token != null) {
                    progressBar.visibility = View.VISIBLE
                    performApiAction(notification._id, false)
                } else {
                    Toast.makeText(context, "Authentication token is missing", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        private fun performApiAction(id: String?, isAccept: Boolean) {
            if (id == null) {
                Toast.makeText(context, "Invalid notification ID", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return
            }

            val call =
                RetrofitInstance.apiInterface.notificationOperations("Bearer $token", id, isAccept)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        removeNotification(adapterPosition)
                        showSuccessDialogSafely()
                    } else {
                        Log.e(
                            "API Error",
                            "Response Code: ${response.code()}, Message: ${response.message()}"
                        )
                        Toast.makeText(
                            context,
                            "Failed to perform action: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e("API Error", "Error: ${t.message}", t)
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        private fun showSuccessDialogSafely() {
            val context = itemView.context

            // Ensure the context is an activity and it's not finishing
            if (context is Activity && !context.isFinishing) {
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.alert_box)
                val okButton: Button = dialog.findViewById(R.id.ok_button)
                okButton.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }
}

package com.rndtechnosoft.lms.Activity.Adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.DataX
import com.rndtechnosoft.lms.Activity.DataModel.UpdaredleadRequest
import com.rndtechnosoft.lms.Activity.DataModel.UpdatedLeadResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatusAdapter(
    private val context: Context,
    private val leads: List<DataX>,
    private val cardColor: Int,
    private val textColor: Int
) : RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    private val token: String? = sharedPreferences.getString("token", null)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lead, parent, false)
        return StatusViewHolder(view, ::statusUpdate, token)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val lead = leads[position]
        holder.bind(lead, position)

        holder.statusText.text = lead.status

        // Apply the passed colors
        holder.statusCardView.setCardBackgroundColor(cardColor)
        holder.statusText.setTextColor(textColor)
    }

    override fun getItemCount(): Int {
        return leads.size
    }

    private fun statusUpdate(statusText: TextView, token: String?, notificationId: String?, newStatus: String) {
        if (token != null && notificationId != null) {

            val request=UpdaredleadRequest(status = newStatus)
            RetrofitInstance.apiInterface.statusUpdate("Bearer $token", notificationId,request).enqueue(object : Callback<UpdatedLeadResponse> {
                override fun onResponse(call: Call<UpdatedLeadResponse>, response: Response<UpdatedLeadResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                        statusText.text = newStatus // Confirm the update on a successful API call
                    } else {
                        val errorMessage = "Failed to update status: ${response.code()} ${response.message()}"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.e("StatusUpdate", errorMessage)
                        response.errorBody()?.let { errorBody ->
                            Log.e("StatusUpdate", "Error body: ${errorBody.string()}")
                        }
                    }
                }

                override fun onFailure(call: Call<UpdatedLeadResponse>, t: Throwable) {
                    val errorMessage = "Error updating status: ${t.message}"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e("StatusUpdate", errorMessage, t)
                }
            })
        } else {
            Toast.makeText(context, "Token or Notification ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    class StatusViewHolder(
        itemView: View,
        private val statusUpdateCallback: (TextView, String?, String?, String) -> Unit,
        private val token: String?
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val emailTextView: TextView = itemView.findViewById(R.id.email)
        private val mobileNumberTextView: TextView = itemView.findViewById(R.id.mobile_number)
        private val companyTextView: TextView = itemView.findViewById(R.id.CompanyNames)
        private val leadInfoTextView: TextView = itemView.findViewById(R.id.LeadSources)
        private val leadDetailTextView: TextView = itemView.findViewById(R.id.lead_detail)
        val statusCardView: CardView = itemView.findViewById(R.id.statuscardview)
        val statusText: TextView = itemView.findViewById(R.id.statusTextview)
        private val leaddetailnew: TextView = itemView.findViewById(R.id.leadDetail)
        private val companynamenew: CardView = itemView.findViewById(R.id.comapny_name)

        fun bind(lead: DataX, position: Int) {
            if (lead.firstname.isNotEmpty() && lead.lastname.isNotEmpty() && lead.mobile.isNotEmpty()) {
                // For Type 1 notifications
                nameTextView.text = "${lead.firstname} ${lead.lastname}"
                mobileNumberTextView.text = lead.mobile
                leadInfoTextView.text = lead.leadInfo
                leadDetailTextView.text = lead.leadsDetails
                companyTextView.text = lead.companyname
            } else {
                // For Type 2 notifications
                nameTextView.text = lead.name
                mobileNumberTextView.text = lead.phone
                leadInfoTextView.text = lead.subject
                leadDetailTextView.text = lead.message
                leaddetailnew.text = "Message:"
                companynamenew.visibility = View.GONE
            }

            // Common fields
            emailTextView.text = lead.email ?: "No Email"
            statusText.text = lead.status

            // Set up the statusCardView click listener to show a popup menu
            statusCardView.setOnClickListener { view ->
                showStatusPopupMenu(view, statusText, lead._id, position)
            }
        }

        private fun showStatusPopupMenu(view: View, statusText: TextView, notificationId: String, position: Int) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.status_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                val newStatus = when (menuItem.itemId) {
                    R.id.option_new -> "New"
                    R.id.option_in_progress -> "In Progress"
                    R.id.option_completed -> "Completed"
                    else -> return@setOnMenuItemClickListener false
                }
                statusText.text = newStatus // Immediately update the TextView
                statusUpdateCallback(statusText, token, notificationId, newStatus)
                true
            }

            popupMenu.show()
        }
    }
}

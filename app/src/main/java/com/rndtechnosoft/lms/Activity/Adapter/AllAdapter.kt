package com.rndtechnosoft.lms.Activity.Adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.*
import com.rndtechnosoft.lms.Activity.WhatsappTemplateActivity
import com.rndtechnosoft.lms.R
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllAdapter(
    private val context: Context,
    private val leads: MutableList<Lead>
) : RecyclerView.Adapter<AllAdapter.StatusViewHolder>() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    private val token: String? = sharedPreferences.getString("token", null)
    private val userId: String? = sharedPreferences.getString("userId", null)
    val managerId = sharedPreferences.getString("managerId", null)
    val role = sharedPreferences.getString("role", null)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_all_fragment, parent, false)
        return StatusViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val lead = leads[position]
        holder.bind(lead, position)

        holder.statusText.text = lead.status

        holder.statusCardView.setOnClickListener {
            if (userId != null) {
                fetchStatusTypes(holder.statusText, token, role, userId, managerId, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return leads.size
    }

    private fun fetchStatusTypes(
        statusText: TextView,
        token: String?,
        role: String?,
        userId: String?,
        managerId: String?,
        position: Int
    ) {
        if (token != null && role != null) {
            // Determine the correct ID based on the role
            val id = when (role) {
                "user" -> userId
                "Manager" -> managerId
                else -> null // Handle invalid roles or roles that are not "User" or "Manager"
            }

            if (id != null) {
                RetrofitInstance.apiInterface.statusCard("Bearer $token", id)
                    .enqueue(object : Callback<StatusTypeResponse> {
                        override fun onResponse(
                            call: Call<StatusTypeResponse>,
                            response: Response<StatusTypeResponse>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.data?.let { statusTypes ->
                                    showPopupMenu(
                                        statusText,
                                        statusTypes,
                                        token,
                                        id,
                                        position,
                                        this@AllAdapter,
                                        leads
                                    )
                                    Log.d("StatusCardResponse", "onResponse: {${response.body()}}")
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to fetch status types",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<StatusTypeResponse>, t: Throwable) {
                            val errorMessage = "Error fetching status types: ${t.message}"
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            Log.e("StatusUpdate", errorMessage, t)
                        }
                    })
            } else {
                Toast.makeText(context, "Invalid role or ID not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Token or role not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPopupMenu(
        statusText: TextView,
        statusTypes: List<DataXXX>,
        token: String?,
        userId: String,
        position: Int,
        adapter: AllAdapter, // Pass the adapter to update the UI
        leads: MutableList<Lead>
    ) {
        val popupMenu = PopupMenu(context, statusText)
        statusTypes.forEach { statusType ->
            popupMenu.menu.add(statusType.status_type)
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val newStatus = menuItem.title.toString()
            statusUpdate(statusText, token, position, newStatus, this@AllAdapter, leads)
            true
        }
        popupMenu.show()
    }

    private fun statusUpdate(
        statusText: TextView,
        token: String?,
        position: Int,
        newStatus: String,
        adapter: AllAdapter, // Pass the adapter to update the UI
        leads: MutableList<Lead> // Pass the data source
    ) {
        val lead = leads[position]
        val statusId = lead._id // Assuming '_id' is the correct field for status ID
        if (token != null && statusId != null) {
            val request = UpdaredleadRequest(status = newStatus)

            RetrofitInstance.apiInterface.statusUpdate("Bearer $token", id = statusId, request)
                .enqueue(object : Callback<UpdatedLeadResponse> {
                    override fun onResponse(
                        call: Call<UpdatedLeadResponse>,
                        response: Response<UpdatedLeadResponse>
                    ) {
                        if (response.isSuccessful) {
                            //Toast.makeText( context, "Status updated to $newStatus", Toast.LENGTH_SHORT ).show()
                            statusText.text = newStatus


                        } else {
                            val errorMessage =
                                "Failed to update status: ${response.code()} ${response.message()}"
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
            Toast.makeText(context, "Token or Status ID not found", Toast.LENGTH_SHORT).show()
        }
    }


    class StatusViewHolder(
        itemView: View,
        private val context: Context
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
        private val price: TextView = itemView.findViewById(R.id.lead_price)
        private val path: TextView = itemView.findViewById(R.id.lead_path)
        private val leadPath: TextView = itemView.findViewById(R.id.leadPath)


        // WhatsApp image view
        private val whatsappImageView: ImageView = itemView.findViewById(R.id.imgWhatsapp)

        // Call image view
        private val imgcall: ImageView = itemView.findViewById(R.id.imgCall)

        fun bind(lead: Lead, position: Int) {
            if (lead.firstname.isNotEmpty() && lead.lastname.isNotEmpty() && lead.mobile.isNotEmpty()) {
                // For Type 1 notifications
                nameTextView.text = "${lead.firstname} ${lead.lastname}"
                mobileNumberTextView.text = lead.mobile
                leadInfoTextView.text = lead.leadInfo
                leadDetailTextView.text = lead.leadsDetails
                companyTextView.text = lead.companyname
                emailTextView.text = lead.email

                // Set price, default to "0" if null
                price.text = lead.price ?: "0"

                // Set path visibility based on whether it's null
                if (lead.path.isNullOrEmpty()) {
                    leadPath.visibility = View.GONE
                    path.visibility = View.GONE
                } else {
                    leadPath.visibility = View.VISIBLE
                    path.visibility = View.VISIBLE
                    path.text = lead.path
                }
            } else {
                // For Type 2 notifications
                nameTextView.text = lead.name
                mobileNumberTextView.text = lead.phone
                leadInfoTextView.text = "Own Website"
                leadDetailTextView.text = lead.message
                leaddetailnew.text = "Message:"
                companynamenew.visibility = View.GONE
                emailTextView.text = lead.email

                // Set price, default to "0" if null
                price.text = lead.price ?: "0"

                // Set path visibility based on whether it's null
                if (lead.path.isNullOrEmpty()) {
                    leadPath.visibility = View.GONE
                    path.visibility = View.GONE
                } else {
                    leadPath.visibility = View.VISIBLE
                    path.visibility = View.VISIBLE
                    path.text = lead.path
                }
            }

            // WhatsApp click listener
            whatsappImageView.setOnClickListener {
                val mobileNumber = lead.mobile ?: lead.phone // Use lead.mobile or lead.phone

                if (mobileNumber.isNotEmpty()) {
                    // Store mobile number in SharedPreferences
                    val sharedPreferences =
                        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("mobile_number", mobileNumber)
                    editor.apply() // Commit the changes

                    // Start the WhatsappTemplateActivity
                    val intent = Intent(context, WhatsappTemplateActivity::class.java).apply {
                        putExtra(
                            "mobile_number",
                            mobileNumber
                        ) // Pass the mobile number to the activity
                    }
                    Toast.makeText(context, "Mobile number: $mobileNumber", Toast.LENGTH_SHORT)
                        .show()
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Mobile number not available", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            // Call click listener
            imgcall.setOnClickListener {
                val phoneNumber = lead.mobile ?: lead.phone
                if (phoneNumber.isNotEmpty()) {
                    // Initiate the call using ACTION_DIAL intent
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$phoneNumber")
                    }
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Phone number not available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}



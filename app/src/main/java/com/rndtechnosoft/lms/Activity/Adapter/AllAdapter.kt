package com.rndtechnosoft.lms.Activity.Adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rndtechnosoft.lms.Activity.DataModel.*
import com.rndtechnosoft.lms.R

class AllAdapter(
    private val context: Context,
    private val leads: List<Lead>
) : RecyclerView.Adapter<AllAdapter.StatusViewHolder>() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    private val token: String? = sharedPreferences.getString("token", null)
    private val userId: String? = sharedPreferences.getString("userId", null)
    val managerId = sharedPreferences.getString("managerId", null)
    val role = sharedPreferences.getString("role", null)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_all_fragment, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val lead = leads[position]
        holder.bind(lead, position)

        holder.statusText.text = lead.status


    }

    override fun getItemCount(): Int {
        return leads.size
    }



    class StatusViewHolder(
        itemView: View
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

        fun bind(lead: Lead, position: Int) {
            if (lead.firstname.isNotEmpty() && lead.lastname.isNotEmpty() && lead.mobile.isNotEmpty()) {
                // For Type 1 notifications
                nameTextView.text = "${lead.firstname} ${lead.lastname}"
                mobileNumberTextView.text = lead.mobile
                leadInfoTextView.text = lead.leadInfo
                leadDetailTextView.text = lead.leadsDetails
                companyTextView.text = lead.companyname
                emailTextView.text=lead.email
            } else {
                // For Type 2 notifications
                nameTextView.text = lead.name
                mobileNumberTextView.text = lead.phone
                leadInfoTextView.text = "Own Website"
                leadDetailTextView.text = lead.message
                leaddetailnew.text = "Message:"
                companynamenew.visibility = View.GONE
                emailTextView.text=lead.email
            }
        }
    }
}

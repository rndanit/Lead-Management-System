package com.rndtechnosoft.lms.Activity.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rndtechnosoft.lms.Activity.DataModel.DataX
import com.rndtechnosoft.lms.R

class StatusAdapter(private val leads: List<DataX>,private val cardColor:Int,private val textColor:Int) :
    RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lead, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val lead = leads[position]
        holder.bind(lead)

        holder.statusText.text = lead.status

        // Apply the passed colors
        holder.statusCardView.setCardBackgroundColor(cardColor)
        holder.statusText.setTextColor(textColor)
    }

    override fun getItemCount(): Int {
        return leads.size
    }

    class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val emailTextView: TextView = itemView.findViewById(R.id.email)
        private val mobileNumberTextView:TextView=itemView.findViewById(R.id.mobile_number)
        private val companyTextView: TextView = itemView.findViewById(R.id.CompanyNames)
        private val leadInfoTextView: TextView = itemView.findViewById(R.id.LeadSources)
        private val leadDetailTextView: TextView = itemView.findViewById(R.id.lead_detail)
        val statusCardView: CardView = itemView.findViewById(R.id.statuscardview)
        val statusText: TextView = itemView.findViewById(R.id.statusTextview)

        fun bind(lead: DataX) {
            nameTextView.text =  "${lead.firstname} ${lead.lastname}"
            emailTextView.text = "${lead.email}"
            mobileNumberTextView.text= "${lead.mobile}"
            companyTextView.text = "${lead.companyname}"
            leadInfoTextView.text= "${lead.leadInfo}"
            leadDetailTextView.text=" ${lead.leadsDetails}"
            statusText.text="${lead.status}"
        }
    }
}

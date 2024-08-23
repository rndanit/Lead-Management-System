package com.rndtechnosoft.lms.Activity.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rndtechnosoft.lms.Activity.DataModel.ShowLeadResponseItem
import com.rndtechnosoft.lms.R

class ShowLeadAdapter(private val leads: List<ShowLeadResponseItem>) :
    RecyclerView.Adapter<ShowLeadAdapter.LeadViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lead, parent, false)
        return LeadViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeadViewHolder, position: Int) {
        val lead = leads[position]
        holder.bind(lead)
    }

    override fun getItemCount(): Int {
        return leads.size
    }

    class LeadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val emailTextView: TextView = itemView.findViewById(R.id.email)
        private val mobileNumberTextView:TextView=itemView.findViewById(R.id.mobile_number)
        private val companyTextView: TextView = itemView.findViewById(R.id.CompanyNames)
        private val leadInfoTextView: TextView = itemView.findViewById(R.id.LeadSources)
        private val leadDetailTextView: TextView = itemView.findViewById(R.id.lead_detail)
        private val statusTextView:TextView=itemView.findViewById(R.id.statusText)

        fun bind(lead: ShowLeadResponseItem) {
            nameTextView.text =  "${lead.firstname} ${lead.lastname}"
            emailTextView.text = "${lead.email}"
            mobileNumberTextView.text= "${lead.mobile}"
            companyTextView.text = "${lead.companyname}"
            leadInfoTextView.text= "${lead.leadInfo}"
            leadDetailTextView.text=" ${lead.leadsDetails}"
            statusTextView.text="${lead.status}"
        }
    }
}

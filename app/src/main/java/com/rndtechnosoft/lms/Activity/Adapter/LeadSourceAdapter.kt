package com.rndtechnosoft.lms.Activity.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.DataXXX
import com.rndtechnosoft.lms.Activity.DataModel.DataXXXXX
import com.rndtechnosoft.lms.Activity.DataModel.DeleteLeadResponse
import com.rndtechnosoft.lms.Activity.DataModel.EditStatusRequest
import com.rndtechnosoft.lms.Activity.DataModel.EditStatusResponse
import com.rndtechnosoft.lms.Activity.DataModel.LeadEditRequest
import com.rndtechnosoft.lms.Activity.DataModel.UpdatedLeadSource
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeadSourceAdapter(
    private var LeadList: List<DataXXXXX>,  // Change from List to MutableList
    private val context: Context
) : RecyclerView.Adapter<LeadSourceAdapter.StatusViewHolder>() {

    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val LeadText: TextView = itemView.findViewById(R.id.LeadSourceText)
        val editButton: ImageView = itemView.findViewById(R.id.editbutton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lead_source, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val Leads = LeadList[position]
        holder.LeadText.text = Leads.status_type

        // Check if the status_type is "Website" and hide the edit and delete buttons if true
        if (Leads.status_type == "Website") {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        } else {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
        }

        // Handle edit button click
        holder.editButton.setOnClickListener {
            showEditDialog(Leads.id, position, Leads.status_type)
        }

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(Leads.id, position)
        }
    }

    // Show confirmation dialog for deletion
    private fun showDeleteConfirmationDialog(LeadId: String?, position: Int) {
        if (LeadId == null) {
            Toast.makeText(context, "Invalid lead ID", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle("Delete Lead")
            .setMessage("Are you sure you want to delete this lead?")
            .setPositiveButton("OK") { _, _ ->
                deleteLead(LeadId, position)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun deleteLead(LeadId: String, position: Int) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            RetrofitInstance.apiInterface.DeleteLeads("Bearer $token", LeadId)
                .enqueue(object : Callback<DeleteLeadResponse> {
                    override fun onResponse(call: Call<DeleteLeadResponse>, response: Response<DeleteLeadResponse>) {
                        if (response.isSuccessful) {
                            // Show success message after deletion
                            showSuccessDialog()

                            // Create a new list excluding the deleted item
                            LeadList = LeadList.filter { it.id != LeadId }  // Use filter to exclude the deleted item

                            // Notify the adapter about the change
                            notifyDataSetChanged()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(context, "Failed to delete lead: $errorBody", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<DeleteLeadResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(context, "Token is missing", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showSuccessDialog() {
        val successDialog = AlertDialog.Builder(context)
            .setTitle("Success")
            .setMessage("Lead deleted successfully")
            .setPositiveButton("OK", null)
            .create()

        successDialog.show()
    }

    private fun showEditDialog(LeadsId: String?, position: Int, LeadType: String) {
        if (LeadsId == null) {
            Toast.makeText(context, "Invalid status ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dailog_edit_status, null)
        val statusEditText: EditText = dialogView.findViewById(R.id.editTextStatus)

        statusEditText.setText(LeadType)

        // Create an AlertDialog
        val dialog = AlertDialog.Builder(context)
            .setTitle("Edit Status")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val newStatus = statusEditText.text.toString()
                if (newStatus.isNotEmpty()) {
                    updateStatus(LeadsId, newStatus, position)
                } else {
                    Toast.makeText(context, "Status cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun updateStatus(LeadId: String, newStatus: String, position: Int) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        val request=LeadEditRequest(leadSources = newStatus)
        if (token != null) {
            RetrofitInstance.apiInterface.LeadSourcesEdit("Bearer $token", LeadId, request)
                .enqueue(object : Callback<UpdatedLeadSource> {
                    override fun onResponse(
                        call: Call<UpdatedLeadSource>,
                        response: Response<UpdatedLeadSource>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                LeadList[position].status_type = newStatus
                                notifyItemChanged(position)
                            } ?: run {
                                Toast.makeText(
                                    context,
                                    "Failed to update status: Empty response",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(
                                context,
                                "Failed to update status: $errorBody",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<UpdatedLeadSource>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(context, "Token is missing", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return LeadList.size
    }
}

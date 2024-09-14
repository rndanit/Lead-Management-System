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
import com.rndtechnosoft.lms.Activity.DataModel.EditStatusRequest
import com.rndtechnosoft.lms.Activity.DataModel.EditStatusResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatusShowAdapter(
    private val statusList: List<DataXXX>,
    private val context: Context // Pass context to show the dialog
) : RecyclerView.Adapter<StatusShowAdapter.StatusViewHolder>() {

    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusText: TextView = itemView.findViewById(R.id.statustext)
        val editButton: ImageView = itemView.findViewById(R.id.editbutton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_showstatus, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val status = statusList[position]
        holder.statusText.text = status.status_type

        // Check if the status is "Spam", if yes hide the edit button
        if (status.status_type.equals("Spam", ignoreCase = true)) {
            holder.editButton.visibility = View.GONE
        } else {
            holder.editButton.visibility = View.VISIBLE
        }

        // Handle edit button click
        holder.editButton.setOnClickListener {
            // Show edit dialog
            showEditDialog(status.id, position, status.status_type)
        }
    }

    private fun showEditDialog(statusId: String?, position: Int, statusType: String) {
        if (statusId == null) {
            Toast.makeText(context, "Invalid status ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dailog_edit_status, null)
        val statusEditText: EditText = dialogView.findViewById(R.id.editTextStatus)

        statusEditText.setText(statusType)

        // Create an AlertDialog
        val dialog = AlertDialog.Builder(context)
            .setTitle("Edit Status")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val newStatus = statusEditText.text.toString()
                if (newStatus.isNotEmpty()) {
                    updateStatus(statusId, newStatus, position)
                } else {
                    Toast.makeText(context, "Status cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun updateStatus(statusId: String, newStatus: String, position: Int) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        val request = EditStatusRequest(status_type = newStatus)
        if (token != null) {
            RetrofitInstance.apiInterface.editStatus("Bearer $token", statusId, request)
                .enqueue(object : Callback<EditStatusResponse> {
                    override fun onResponse(
                        call: Call<EditStatusResponse>,
                        response: Response<EditStatusResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                statusList[position].status_type = newStatus
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

                    override fun onFailure(call: Call<EditStatusResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(context, "Token is missing", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return statusList.size
    }
}

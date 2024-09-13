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
import com.google.firebase.messaging.RemoteMessage
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.DataXXXXXXX
import com.rndtechnosoft.lms.Activity.DataModel.DeleteTemplateResponse
import com.rndtechnosoft.lms.Activity.DataModel.GetTemplateResponse
import com.rndtechnosoft.lms.Activity.DataModel.LeadEditRequest
import com.rndtechnosoft.lms.Activity.DataModel.TemplateEditRequest
import com.rndtechnosoft.lms.Activity.DataModel.UpdatedLeadSource
import com.rndtechnosoft.lms.R
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TemplateAdapter(
    private var TemplateList: List<DataXXXXXXX>,  // Change from List to MutableList
    private val context: Context
) : RecyclerView.Adapter<TemplateAdapter.StatusViewHolder>() {

    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val MessageText: TextView = itemView.findViewById(R.id.message)
        val TitleText: TextView = itemView.findViewById(R.id.title)
        val editButton: ImageView = itemView.findViewById(R.id.editbutton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_template, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val Template = TemplateList[position]
        holder.MessageText.text =Template.message
        holder.TitleText.text =Template.title

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(Template._id, position)
        }

        // Handle edit button click
        holder.editButton.setOnClickListener {
            showEditDialog(Template._id, position, Template.message,Template.title)
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
            RetrofitInstance.apiInterface.DeleteTemplate("Bearer $token", LeadId)
                .enqueue(object : Callback<DeleteTemplateResponse> {
                    override fun onResponse(call: Call<DeleteTemplateResponse>, response: Response<DeleteTemplateResponse>) {
                        if (response.isSuccessful) {
                            // Show success message after deletion
                            showSuccessDialog()

                            // Create a new list excluding the deleted item
                            TemplateList = TemplateList.filter { it._id != LeadId }  // Use filter to exclude the deleted item

                            // Notify the adapter about the change
                            notifyDataSetChanged()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(context, "Failed to delete lead: $errorBody", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<DeleteTemplateResponse>, t: Throwable) {
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

    private fun showEditDialog(LeadsId: String?, position: Int, message: String, title: String) {
        if (LeadsId == null) {
            Toast.makeText(context, "Invalid status ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dailog_template, null)
        val TitileEditText: EditText = dialogView.findViewById(R.id.titleedit)
        val MessageEditText: EditText = dialogView.findViewById(R.id.messageedit)

        TitileEditText.setText(title)
        MessageEditText.setText(message)

        // Create an AlertDialog
        val dialog = AlertDialog.Builder(context)
            .setTitle("Edit Status")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val newTitle = TitileEditText.text.toString()
                val newMessage =MessageEditText.text.toString()
                if (newTitle.isNotEmpty()) {
                    updateStatus(LeadsId, newTitle,newMessage, position)
                } else {
                    Toast.makeText(context, "Status cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun updateStatus(LeadId: String, newTitle: String,newMessage:String, position: Int) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        val request=TemplateEditRequest(title = newTitle, message = newMessage)
        if (token != null) {
            RetrofitInstance.apiInterface.TemplateEdit("Bearer $token", LeadId, request)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                TemplateList[position].title = newTitle
                                TemplateList[position].message = newMessage

                                notifyItemChanged(position)

                                Toast.makeText(
                                    context,
                                    "Template Updated Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
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

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(context, "Token is missing", Toast.LENGTH_SHORT).show()
        }
    }


    override fun getItemCount(): Int {
        return TemplateList.size
    }
}

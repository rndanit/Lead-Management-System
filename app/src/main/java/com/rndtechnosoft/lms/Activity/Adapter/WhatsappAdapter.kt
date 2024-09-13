package com.rndtechnosoft.lms.Activity.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
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

class WhatsappAdapter(
    private var TemplateList: List<DataXXXXXXX>,  // Change from List to MutableList
    private val context: Context
) : RecyclerView.Adapter<WhatsappAdapter.StatusViewHolder>() {

    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val MessageText: TextView = itemView.findViewById(R.id.message)
        val TitleText: TextView = itemView.findViewById(R.id.title)
        val whatsappImage: ImageView = itemView.findViewById(R.id.whatsapp)

        fun bind(Template: DataXXXXXXX) {
            // Set text for message and title
            MessageText.text = Template.message
            TitleText.text = Template.title

            // OnClickListener for WhatsApp image
            whatsappImage.setOnClickListener {
                // Retrieve mobile number from SharedPreferences
                val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val mobileNumber = sharedPreferences.getString("mobile_number", null)

                Toast.makeText(context, "Mobile number:-${mobileNumber}", Toast.LENGTH_SHORT).show()

                if (mobileNumber != null) {
                    sendWhatsAppMessage(mobileNumber, Template.message)
                } else {
                    Toast.makeText(context, "Mobile number not available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendWhatsAppMessage(mobileNumber: String, message: String) {
        try {
            // Format mobile number for WhatsApp (must include country code, e.g., "91" for India)
            val formattedNumber = mobileNumber.replace(" ", "").replace("+91", "")

            // Create WhatsApp intent
            val whatsappIntent = Intent(Intent.ACTION_VIEW)
            whatsappIntent.setPackage("com.whatsapp")
            whatsappIntent.data = Uri.parse("https://api.whatsapp.com/send?phone=$formattedNumber&text=$message")

            // Check if WhatsApp is installed
            if (whatsappIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(whatsappIntent)
            } else {
                Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_whatsapp, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val template = TemplateList[position]
        holder.bind(template)
    }

    override fun getItemCount(): Int {
        return TemplateList.size
    }
}

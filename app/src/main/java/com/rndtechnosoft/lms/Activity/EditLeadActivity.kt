package com.rndtechnosoft.lms.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.AddNewLeadSourceRequest
import com.rndtechnosoft.lms.Activity.DataModel.AddNewLeadSourceResponse
import com.rndtechnosoft.lms.Activity.DataModel.AddStatusRequest
import com.rndtechnosoft.lms.Activity.DataModel.AddStatusResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditLeadActivity : AppCompatActivity() {

    private lateinit var addButton: AppCompatButton
    private lateinit var editLeadSourceEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_lead)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Initialize your views
        addButton = findViewById(R.id.add_button)
        editLeadSourceEditText = findViewById(R.id.EditLeadSourceEditText)

        // Handle the back button click
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        addButton.setOnClickListener {
            addFunction()
        }

    }

    private fun addFunction() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getString("userId", null)
        val managerId = sharedPreferences.getString("managerId", null)
        val role = sharedPreferences.getString("role", null)
        val lead_type = editLeadSourceEditText.text.toString().trim()

        val request = AddNewLeadSourceRequest(leadSources = lead_type)

        // Log request details for debugging
        Log.d("EditLeadSourceActivity", "Token: $token")
        Log.d("EditLeadSourceActivity", "UserId: $userId")
        Log.d("EditLeadSourceActivity", "LeadSourcesType: $lead_type")
        Log.d("EditLeadSourceActivity", "Request: $request")

        if (token != null) {
            val idToPass: String? = when (role) {
                "user" -> userId
                "Manager" -> managerId
                else -> null
            }

            if (idToPass != null) {
                RetrofitInstance.apiInterface.AddNewLeadSource("Bearer $token", user = idToPass, request).enqueue(object :
                    Callback<AddNewLeadSourceResponse> {
                    override fun onResponse(call: Call<AddNewLeadSourceResponse>, response: Response<AddNewLeadSourceResponse>) {
                        if (response.isSuccessful) {
                            // Show success dialog
                            showSuccessDialog(response.body()?.msg ?: "Lead Source updated successfully")
                        } else {
                            // Handle failure response
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(this@EditLeadActivity, "Failed to add status: $errorBody", Toast.LENGTH_LONG).show()
                            Log.e("EditLeadSourceActivity", "Error: $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<AddNewLeadSourceResponse>, t: Throwable) {
                        // Handle error
                        Toast.makeText(this@EditLeadActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("EditLeadSourceActivity", "Failure: ${t.message}")
                    }
                })
            }
        }
    }

    // Function to show success dialog
    private fun showSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Success")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialogInterface, _ ->
            dialogInterface.dismiss()
            // Navigate to LoginActivity after dismissing the dialog
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.show()
    }
}
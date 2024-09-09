package com.rndtechnosoft.lms.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.app.AlertDialog
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.AddStatusRequest
import com.rndtechnosoft.lms.Activity.DataModel.AddStatusResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditStatusActivity : AppCompatActivity() {

    private lateinit var addButton: AppCompatButton
    private lateinit var editStatusEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_status)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Initialize your views
        addButton = findViewById(R.id.add_button)
        editStatusEditText = findViewById(R.id.EditStatusEditText)

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
        val status_type = editStatusEditText.text.toString().trim()

        val request = AddStatusRequest(status_type = status_type)

        // Log request details for debugging
        Log.d("EditStatusActivity", "Token: $token")
        Log.d("EditStatusActivity", "UserId: $userId")
        Log.d("EditStatusActivity", "Status Type: $status_type")
        Log.d("EditStatusActivity", "Request: $request")

        if (token != null) {
            val idToPass: String? = when (role) {
                "user" -> userId
                "Manager" -> managerId
                else -> null
            }

        if (idToPass != null) {
            RetrofitInstance.apiInterface.addFunction("Bearer $token", user = idToPass, request).enqueue(object :
                Callback<AddStatusResponse> {
                override fun onResponse(call: Call<AddStatusResponse>, response: Response<AddStatusResponse>) {
                    if (response.isSuccessful) {
                        // Show success dialog
                        showSuccessDialog(response.body()?.msg ?: "Status updated successfully")
                    } else {
                        // Handle failure response
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@EditStatusActivity, "Failed to add status: $errorBody", Toast.LENGTH_LONG).show()
                        Log.e("EditStatusActivity", "Error: $errorBody")
                    }
                }

                override fun onFailure(call: Call<AddStatusResponse>, t: Throwable) {
                    // Handle error
                    Toast.makeText(this@EditStatusActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("EditStatusActivity", "Failure: ${t.message}")
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

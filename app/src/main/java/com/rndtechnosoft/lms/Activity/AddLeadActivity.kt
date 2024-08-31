package com.rndtechnosoft.lms.Activity

import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.AddLeadRequest
import com.rndtechnosoft.lms.Activity.DataModel.AddLeadResponse
import com.rndtechnosoft.lms.R
import com.google.gson.JsonParser
import com.rndtechnosoft.lms.Activity.DataModel.leadSourceResponseItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLeadActivity : AppCompatActivity() {

    private lateinit var firstnameEditText: EditText
    private lateinit var lastnameEditText: EditText
    private lateinit var mobileEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var companynameEditText: EditText
    private lateinit var leadSourceEditText: AutoCompleteTextView
    private lateinit var leadDetailEditText: EditText
    private lateinit var addLeadButton: AppCompatButton
    private lateinit var mProgress: ProgressDialog

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lead)

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setDisplayShowTitleEnabled(false)


        // Handle the back button click
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Set the toolbar background color (optional)
        //toolbar.setBackgroundColor(getColor(R.color.red))

        //Inilized the UI Component.
        addLeadButton = findViewById(R.id.addLead_button)
        firstnameEditText = findViewById(R.id.FirstNameEditText)
        lastnameEditText = findViewById(R.id.LastNameEditText)
        mobileEditText = findViewById(R.id.MobileNumberEditText)
        emailEditText = findViewById(R.id.EmailaddressEditText)
        companynameEditText = findViewById(R.id.CompanyNameEditText)
        leadSourceEditText = findViewById(R.id.LeadInfoEditText)
        leadDetailEditText = findViewById(R.id.LeadDetailEditText)

        // Retrieve the token from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)


        //Functionality of a ProgressBar.
        mProgress = ProgressDialog(this).apply {
            setTitle("Loading Data....")
            setMessage("Please wait...")
            setCancelable(false)
            setIndeterminate(true)
        }

        // Set up click listener for leadSourceEditText
        leadSourceEditText.setOnClickListener {
            token?.let { fetchLeadSources(it) }
        }


        addLeadButton.setOnClickListener {
            if (token != null) {
                // Use the token for making API requests
                addLead(token)

            } else {
                Toast.makeText(this, "Token not found", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun fetchLeadSources(token: String) {
        RetrofitInstance.apiInterface.leadSource("Bearer $token")
            .enqueue(object : Callback<MutableList<leadSourceResponseItem>> {
                override fun onResponse(
                    call: Call<MutableList<leadSourceResponseItem>>,
                    response: Response<MutableList<leadSourceResponseItem>>
                ) {

                    if (response.isSuccessful) {
                        val leadSources = response.body()
                        if (!leadSources.isNullOrEmpty()) {
                            val leadSourceNames = leadSources.map { it.leadSources }
                            val adapter = ArrayAdapter(
                                this@AddLeadActivity,
                                android.R.layout.simple_dropdown_item_1line,
                                leadSourceNames
                            )
                            leadSourceEditText.setAdapter(adapter)
                            leadSourceEditText.showDropDown()
                        }
                    } else {
                        Toast.makeText(
                            this@AddLeadActivity,
                            "Failed to fetch lead sources",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<MutableList<leadSourceResponseItem>>,
                    t: Throwable
                ) {
                    mProgress.dismiss()
                    Toast.makeText(
                        this@AddLeadActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private fun addLead(token: String) {

        // Retrieve the token from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("userId", null)

        val firstname = firstnameEditText.text.toString().trim()
        val lastname = lastnameEditText.text.toString().trim()
        val mobile = mobileEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val companyname = companynameEditText.text.toString().trim()
        val leadInfo = leadSourceEditText.text.toString().trim()
        val leadDetail = leadDetailEditText.text.toString().trim()

        if (firstname.isEmpty() || lastname.isEmpty() || mobile.isEmpty() || email.isEmpty() || companyname.isEmpty() || leadInfo.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()

            return
        }

        val request =
            AddLeadRequest(
                firstname = firstname,
                lastname = lastname,
                mobile = mobile,
                email = email,
                companyname = companyname,
                leadInfo = leadInfo,
                token = token,
                leadsDetails = leadDetail
            )

        Log.d("AddLead", "Request Value:-$request")

        if (id != null) {

            mProgress.show()
            RetrofitInstance.apiInterface.addLead("Bearer $token", id = id, request)
                .enqueue(object :
                    Callback<AddLeadResponse?> {
                    override fun onResponse(
                        call: Call<AddLeadResponse?>,
                        response: Response<AddLeadResponse?>
                    ) {

                        mProgress.dismiss()
                        if (response.isSuccessful) {
                            val addLeadResponse = response.body()
                            if (addLeadResponse != null) {
                                Toast.makeText(
                                    this@AddLeadActivity,
                                    "Lead added successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = getErrorMessageFromResponse(errorBody)
                            Toast.makeText(
                                this@AddLeadActivity,
                                "Failed to add lead: $errorMessage",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("AddLead", "Failed to add lead: $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<AddLeadResponse?>, t: Throwable) {
                        Toast.makeText(
                            this@AddLeadActivity,
                            "Failed to add lead: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        mProgress.dismiss()

                    }
                })
        }
    }

    private fun getErrorMessageFromResponse(errorBody: String?): String {
        return if (errorBody.isNullOrEmpty()) {
            "Unknown error"
        } else {
            try {
                val jsonObject = JsonParser.parseString(errorBody).asJsonObject
                jsonObject.get("message").asString
            } catch (e: Exception) {
                "Unknown error"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
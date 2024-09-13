package com.rndtechnosoft.lms.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndtechnosoft.lms.Activity.Adapter.LeadSourceAdapter
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.LeadSourcesResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeadSourceActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeadSourceAdapter
    private lateinit var updateLeadButton: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_source)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Find the Id of a UI Components.
        updateLeadButton = findViewById(R.id.leadSources_button)


        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Handle the back button click
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.showRecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch Lead Source data
        fetchLeadSourceData()

        updateLeadButton.setOnClickListener {
            val intent = Intent(this@LeadSourceActivity, EditLeadActivity::class.java)
            startActivity(intent)
        }

    }

    private fun fetchLeadSourceData() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getString("userId", null)
        val managerId = sharedPreferences.getString("managerId", null)
        val role = sharedPreferences.getString("role", null)

        if (token != null) {
            val idToPass: String? = when (role) {
                "user" -> userId
                "Manager" -> managerId
                else -> null
            }

            if (idToPass != null) {
                RetrofitInstance.apiInterface.LeadSourceFunction("Bearer $token", idToPass)
                    .enqueue(object : Callback<LeadSourcesResponse> {
                        override fun onResponse(
                            call: Call<LeadSourcesResponse>,
                            response: Response<LeadSourcesResponse>
                        ) {
                            if (response.isSuccessful) {
                                val LeadList = response.body()?.data ?: emptyList()
                                adapter = LeadSourceAdapter(LeadList, this@LeadSourceActivity)
                                recyclerView.adapter = adapter
                                Log.d("LeadNames", "onResponse:${LeadList} ")
                            } else {
                                Toast.makeText(
                                    this@LeadSourceActivity,
                                    "Failed to fetch status",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<LeadSourcesResponse>, t: Throwable) {
                            Toast.makeText(
                                this@LeadSourceActivity,
                                "Error: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }

    }
}

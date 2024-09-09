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
import com.rndtechnosoft.lms.Activity.Adapter.StatusShowAdapter
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.StatusTypeResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateStatusActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatusShowAdapter
    private lateinit var updateStatusButton:AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_status)

        //Find the Id of a UI Components.
        updateStatusButton=findViewById(R.id.addStatus_button)

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

        recyclerView = findViewById(R.id.showRecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch status data
        fetchStatusData()

        updateStatusButton.setOnClickListener {
            val intent=Intent(this@UpdateStatusActivity,EditStatusActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchStatusData() {
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
                RetrofitInstance.apiInterface.statusCard("Bearer $token", idToPass)
                    .enqueue(object : Callback<StatusTypeResponse> {
                        override fun onResponse(
                            call: Call<StatusTypeResponse>,
                            response: Response<StatusTypeResponse>
                        ) {
                            if (response.isSuccessful) {
                                val statusList = response.body()?.data ?: emptyList()
                                adapter = StatusShowAdapter(statusList, this@UpdateStatusActivity)
                                recyclerView.adapter = adapter
                                Log.d("StatusNames", "onResponse:${statusList} ")
                            } else {
                                Toast.makeText(
                                    this@UpdateStatusActivity,
                                    "Failed to fetch status",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<StatusTypeResponse>, t: Throwable) {
                            Toast.makeText(
                                this@UpdateStatusActivity,
                                "Error: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }

    }}

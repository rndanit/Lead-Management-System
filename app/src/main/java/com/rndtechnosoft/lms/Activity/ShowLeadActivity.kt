package com.rndtechnosoft.lms.Activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rndtechnosoft.lms.Activity.Adapter.ShowLeadAdapter
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.ShowLeadResponseItem
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowLeadActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShowLeadAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarTextView: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var noLeadsTextView: TextView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_lead)

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Find the Id of a Variables.
        progressBar = findViewById(R.id.progressBar)
        progressBarTextView = findViewById(R.id.progressTextview)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        noLeadsTextView = findViewById(R.id.noLeadsTextView)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Show Lead"
        toolbar.setTitleTextColor(getColor(R.color.white))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Show progress bar and text
        progressBarTextView.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        recyclerView = findViewById(R.id.ShowLeadRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        swipeRefreshLayout.setOnRefreshListener {
            fetchLeads()
        }

        fetchLeads()
    }

    private fun fetchLeads() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getString("userId", null)
        Log.d("Token", "fetchLeads: $token")
        Log.d("User Id", "fetchLeads: $userId")

        if (token != null && userId != null) {
            RetrofitInstance.apiInterface.showLead("Bearer $token", user = userId).enqueue(object : Callback<List<ShowLeadResponseItem>> {
                override fun onResponse(call: Call<List<ShowLeadResponseItem>>, response: Response<List<ShowLeadResponseItem>>) {
                    Log.d("API Response", "Response code: ${response.code()}")
                    if (response.isSuccessful && response.body() != null) {
                        val leads = response.body()!!
                        Log.d("ShowLeadActivity", "Fetched leads: $leads")

                        // Hide the progress bar and text
                        progressBarTextView.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false

                        if (leads.isEmpty()) {
                            // Show "No Leads Found" message if the list is empty
                            noLeadsTextView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            // Hide "No Leads Found" message and show the list
                            noLeadsTextView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            adapter = ShowLeadAdapter(leads)
                            recyclerView.adapter = adapter
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("ShowLeadActivity", "Response failed or empty: ${response.code()} - ${response.message()} - $errorBody")
                        Log.e("ShowLeadActivity", "Raw JSON response: ${response.errorBody()?.string()}")

                        // Handle failure
                        progressBarTextView.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                        noLeadsTextView.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<List<ShowLeadResponseItem>>, t: Throwable) {
                    Log.e("ShowLeadActivity", "API call failed", t)

                    // Handle failure
                    progressBarTextView.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    swipeRefreshLayout.isRefreshing = false
                    noLeadsTextView.visibility = View.VISIBLE
                }
            })
        } else {
            Log.e("ShowLeadActivity", "Token or User ID is null")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

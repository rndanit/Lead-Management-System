package com.rndtechnosoft.lms.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rndtechnosoft.lms.Activity.Adapter.NotificationAdapter
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.NotificationResponseItem
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeadNotificationActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var noNotificationsTextView: TextView
    private lateinit var noNotificationsImageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarTextView: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var ids: String? = null

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_notification)

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Find the Id of UI Components.
        progressBar = findViewById(R.id.progressBar)
        progressBarTextView = findViewById(R.id.progressTextview)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Lead Notification"
        toolbar.setTitleTextColor(getColor(R.color.white))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Handle the back button click
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Show progress bar and text
        progressBarTextView.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        recyclerView = findViewById(R.id.notificationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Find the TextView and ImageView for "No Notifications Available"
        noNotificationsTextView = findViewById(R.id.notificationTextView)
        noNotificationsImageView = findViewById(R.id.notificationImage)

        swipeRefreshLayout.setOnRefreshListener {
            notificationLeads()
        }

        notificationLeads()
    }

    // Notification Function
    private fun notificationLeads() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getString("userId", null)
        val managerId = sharedPreferences.getString("managerId", null)
        val role = sharedPreferences.getString("role", null)

        Log.d("Token", "fetchLeads: $token")
        Log.d("User Id", "fetchLeads: $userId")
        Log.d("Manager Id", "fetchLeads: $managerId")
        Log.d("Role", "fetchLeads: $role")

        if (token != null) {
            val idToPass: String? = when (role) {
                "user" -> userId
                "Manager" -> managerId
                else -> null
            }

            if (idToPass != null) {
                Log.d("NotificationActivity", "Manager id: $idToPass")

                RetrofitInstance.apiInterface.notification("Bearer $token", id = idToPass)
                    .enqueue(object : Callback<MutableList<NotificationResponseItem>> {
                        override fun onResponse(
                            call: Call<MutableList<NotificationResponseItem>>,
                            response: Response<MutableList<NotificationResponseItem>>
                        ) {
                            Log.d("API Response", "Response code: ${response.code()}")

                            // Hide the progress bar and swipe refresh loader
                            progressBarTextView.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            swipeRefreshLayout.isRefreshing = false

                            if (response.isSuccessful && response.body() != null) {
                                val notifications = response.body()!!
                                Log.d(
                                    "NotificationActivity",
                                    "Fetched Notifications: $notifications"
                                )
                                Log.d("NotificationActivity", "Status id: $idToPass")

                                // Store _id values of Notification Id.
                                ids = notifications.map { it._id }.toString()
                                Log.d("NotificationActivity", "Notification IDs: $ids")

                                // Sort the notifications by `createdAt` in descending order
                                notifications.sortByDescending { it.createdAt }

                                if (notifications.isEmpty()) {
                                    // Show "No Notifications Available" and bell icon
                                    noNotificationsTextView.visibility = View.VISIBLE
                                    noNotificationsImageView.visibility = View.VISIBLE
                                    recyclerView.visibility = View.GONE
                                } else {
                                    // Load the notifications into the RecyclerView
                                    adapter = NotificationAdapter(applicationContext, notifications)
                                    recyclerView.adapter = adapter

                                    // Hide the "No Notifications Available" and bell icon
                                    noNotificationsTextView.visibility = View.GONE
                                    noNotificationsImageView.visibility = View.GONE
                                    recyclerView.visibility = View.VISIBLE
                                }

                            } else {
                                val errorBody = response.errorBody()?.string()
                                Log.e(
                                    "NotificationActivity",
                                    "Response failed: ${response.code()} - ${response.message()} - $errorBody"
                                )

                                // Handle empty or failed responses
                                noNotificationsTextView.visibility = View.VISIBLE
                                noNotificationsImageView.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                            }
                        }

                        override fun onFailure(
                            call: Call<MutableList<NotificationResponseItem>>,
                            t: Throwable
                        ) {
                            Log.e("NotificationActivity", "API call failed", t)

                            // Hide progress bar and show error state
                            progressBarTextView.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            swipeRefreshLayout.isRefreshing = false

                            // Handle failure by showing error or retry options
                            noNotificationsTextView.visibility = View.VISIBLE
                            noNotificationsImageView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                    })
            } else {
                Log.e("NotificationActivity", "Role is invalid or ID is null")
                // Handle case where ID is null
                noNotificationsTextView.visibility = View.VISIBLE
                noNotificationsImageView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        } else {
            Log.e("NotificationActivity", "Token or Role is null")
            // Handle case where token or role is null
            noNotificationsTextView.visibility = View.VISIBLE
            noNotificationsImageView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

package com.rndtechnosoft.lms.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rndtechnosoft.lms.Activity.Adapter.StatusAdapter
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.StatusResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_STATUS_TYPE = "status_type"

class DynamicFragment : Fragment() {
    private var statusType: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatusAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarTextView: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var noLeadsTextView: TextView
    private var ids: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            statusType = it.getString(ARG_STATUS_TYPE)
        }
    }

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dynamic, container, false)

        // Find the Id of a Variables.
        progressBar = view.findViewById(R.id.progressBar)
        progressBarTextView = view.findViewById(R.id.progressTextview)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        noLeadsTextView = view.findViewById(R.id.noLeadsTextView)

        // Show progress bar and text
        progressBarTextView.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        recyclerView = view.findViewById(R.id.NewStatusRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        swipeRefreshLayout.setOnRefreshListener {
            fetchLeads()
        }

        fetchLeads()

        return view
    }

    override fun onResume() {
        fetchLeads()
        super.onResume()
    }

    private fun fetchLeads() {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getString("userId", null)
        val managerId = sharedPreferences.getString("managerId", null)
        val role = sharedPreferences.getString("role", null)

        Log.d("Token", "fetchLeads: $token")
        Log.d("User Id", "fetchLeads: $userId")

        if (token != null) {
            val idToPass: String? = when (role) {
                "user" -> userId
                "Manager" -> managerId
                else -> null
            }


        if (token != null && idToPass != null) {
            RetrofitInstance.apiInterface.statusOperation("Bearer $token", user=idToPass, status = statusType ?: "New").enqueue(object : Callback<StatusResponse> {
                override fun onResponse(call: Call<StatusResponse>, response: Response<StatusResponse>) {
                    // Check if the fragment is still attached to its activity
                    if (isAdded && !isDetached) {
                        Log.d("API Response", "Response code: ${response.code()}")
                        if (response.isSuccessful && response.body() != null) {
                            val leads = response.body()!!.data

                            Log.d("ShowLeadFragment", "Fetched leads: $leads")
                            // Store _id values of Notification Ids
                            val idsList = leads.map { it._id }
                            ids = idsList.joinToString(",")  // Convert the list to a comma-separated string
                            Log.d("NotificationActivity", "Notification IDs: $ids")

                            // Save the ids string in SharedPreferences
                            sharedPreferences.edit().putString("notification_ids", ids).apply()

                            // Define the colors for the fragment
                            val cardColor = ContextCompat.getColor(requireContext(), R.color.buleindigo)
                            val textColor = ContextCompat.getColor(requireContext(), R.color.blue)

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
                                adapter = StatusAdapter(requireContext(), leads,cardColor,textColor)
                                recyclerView.adapter = adapter
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("ShowLeadFragment", "Response failed or empty: ${response.code()} - ${response.message()} - $errorBody")
                            Log.e("ShowLeadFragment", "Raw JSON response: ${response.errorBody()?.string()}")

                            // Handle failure
                            progressBarTextView.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            swipeRefreshLayout.isRefreshing = false
                            noLeadsTextView.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                    if (isAdded && !isDetached) {
                        Log.e("ShowLeadFragment", "API call failed", t)

                        // Handle failure
                        progressBarTextView.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                        noLeadsTextView.visibility = View.VISIBLE
                    }
                }
            })
        } else {
            Log.e("ShowLeadFragment", "Token or User ID is null")
        }
    }
    }
    companion object {
        @JvmStatic
        fun newInstance(statusType: String) =
            DynamicFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_STATUS_TYPE, statusType)
                }
            }
    }
}

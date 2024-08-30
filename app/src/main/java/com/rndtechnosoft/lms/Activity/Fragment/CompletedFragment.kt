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
import com.rndtechnosoft.lms.Activity.Adapter.ShowLeadAdapter
import com.rndtechnosoft.lms.Activity.Adapter.StatusAdapter
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.DataX
import com.rndtechnosoft.lms.Activity.DataModel.ShowLeadResponseItem
import com.rndtechnosoft.lms.Activity.DataModel.StatusResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompletedFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatusAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarTextView: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var noLeadsTextView: TextView

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_lead, container, false)

        // Find the Id of a Variables.
        progressBar = view.findViewById(R.id.progressBar)
        progressBarTextView = view.findViewById(R.id.progressTextview)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        noLeadsTextView = view.findViewById(R.id.noLeadsTextView)

        // Show progress bar and text
        progressBarTextView.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        recyclerView = view.findViewById(R.id. NewStatusRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        swipeRefreshLayout.setOnRefreshListener {
            fetchLeads()
        }

        fetchLeads()

        return view
    }

    override fun onResume() {
        super.onResume()
        fetchLeads()
    }


    private fun fetchLeads() {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getString("userId", null)
        Log.d("Token", "fetchLeads: $token")
        Log.d("User Id", "fetchLeads: $userId")

        if (token != null && userId != null) {
            RetrofitInstance.apiInterface.statusOperation("Bearer $token", user=userId, status = "Completed").enqueue(object : Callback<StatusResponse> {
                override fun onResponse(call: Call<StatusResponse>, response: Response<StatusResponse>) {
                    Log.d("API Response", "Response code: ${response.code()}")
                    if (response.isSuccessful && response.body() != null) {
                        val leads = response.body()!!.data
                        Log.d("ShowLeadFragment", "Fetched leads: $leads")

                        // Define the colors for CompletedFragment
                        val cardColor = ContextCompat.getColor(requireContext(), R.color.greenLight)
                        val textColor = ContextCompat.getColor(requireContext(), R.color.green)

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
                            adapter = context?.let { StatusAdapter(it,leads,cardColor,textColor) }!!
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

                override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                    Log.e("ShowLeadFragment", "API call failed", t)

                    // Handle failure
                    progressBarTextView.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    swipeRefreshLayout.isRefreshing = false
                    noLeadsTextView.visibility = View.VISIBLE
                }
            })
        } else {
            Log.e("ShowLeadFragment", "Token or User ID is null")
        }
    }
}

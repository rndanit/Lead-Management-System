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
import com.rndtechnosoft.lms.Activity.Adapter.TemplateAdapter
import com.rndtechnosoft.lms.Activity.Adapter.WhatsappAdapter
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.GetTemplateResponse
import com.rndtechnosoft.lms.Activity.DataModel.LeadSourcesResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WhatsappTemplateActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WhatsappAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whatsapp_template)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Find the Id of a UI Components.
       // AddTemplateButton= findViewById(R.id.addTemplate_button)


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
                RetrofitInstance.apiInterface.GetTemplate("Bearer $token", idToPass)
                    .enqueue(object : Callback<GetTemplateResponse> {
                        override fun onResponse(
                            call: Call<GetTemplateResponse>,
                            response: Response<GetTemplateResponse>
                        ) {
                            if (response.isSuccessful) {
                                val LeadList = response.body()?.data ?: emptyList()
                                adapter = WhatsappAdapter(LeadList, this@WhatsappTemplateActivity)
                                recyclerView.adapter = adapter
                                Log.d("LeadNames", "onResponse:${LeadList} ")
                            } else {
                                Toast.makeText(
                                    this@WhatsappTemplateActivity,
                                    "Failed to fetch status",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<GetTemplateResponse>, t: Throwable) {
                            Toast.makeText(
                                this@WhatsappTemplateActivity,
                                "Error: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }

    }
}

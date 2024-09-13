package com.rndtechnosoft.lms.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.GetUserResponse
import com.rndtechnosoft.lms.Activity.DataModel.ManagerResponse
import com.rndtechnosoft.lms.Activity.DataModel.UpdatedFields
import com.rndtechnosoft.lms.Activity.DataModel.emailRequest
import com.rndtechnosoft.lms.Activity.DataModel.nameRequest
import com.rndtechnosoft.lms.Activity.DataModel.numberRequest
import com.rndtechnosoft.lms.R
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileActivity : AppCompatActivity() {

    private lateinit var profileUserName: TextView
    private lateinit var profileUserEmail: TextView
    private lateinit var profileUserMobile: TextView
    private lateinit var profileCompanyname:TextView
    private lateinit var profileWebsite:TextView
    private lateinit var editProfile:ImageView
    private lateinit var logoutButton:CardView
    private lateinit var profileImage:ImageView

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        profileUserName=findViewById(R.id.profileUserName)
        profileUserEmail=findViewById(R.id.profileEmail)
        profileUserMobile=findViewById(R.id.profileNumber)
        profileCompanyname=findViewById(R.id.companyName)
        profileWebsite=findViewById(R.id.profileWebsite)
        editProfile=findViewById(R.id.editProfileButton)
        logoutButton=findViewById(R.id.logoutAccount)
        profileImage=findViewById(R.id.mainProfileImage)

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            // Refresh logic here
            // Once the refresh is complete, call setRefreshing(false) to stop the animation
            swipeRefreshLayout.isRefreshing = false
            fetchUserDetails()
        }


        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)



        // Customize the toolbar
        //supportActionBar?.title = "Profile" // Set the toolbar title
        //toolbar.setTitleTextColor(getColor(R.color.white)) // Set the title color

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Handle the back button click
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }




        //Set the click on listner to edit profile button.
        editProfile.setOnClickListener {
            val intent= Intent(this@UserProfileActivity,ManageProfileActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            //logoutAccount()
            showLogoutConfirmationDialog()
        }
        fetchUserDetails()

    }


    // FetchUserDetails Function
    private fun fetchUserDetails() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getString("userId", null)
        val role = sharedPreferences.getString("role", null)

        if (userId != null && token != null) {
            when (role) {
                "user" -> {
                    // Call getUser function
                    RetrofitInstance.apiInterface.getUser("Bearer $token", id = userId)
                        .enqueue(object : Callback<GetUserResponse> {
                            override fun onResponse(
                                call: Call<GetUserResponse>,
                                response: Response<GetUserResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val user = response.body()
                                    if (user != null) {
                                        profileUserName.text = user.name
                                        profileUserEmail.text = user.email
                                        profileUserMobile.text = user.mobile
                                        profileCompanyname.text = user.companyname

                                        // Check if the website is null or empty
                                        if (user.website.isNullOrEmpty()) {
                                            profileWebsite.visibility = View.GONE // Hide if null or empty
                                        } else {
                                            profileWebsite.text = user.website
                                            profileWebsite.visibility = View.VISIBLE // Show if not null
                                        }

                                        // Base URL
                                        val baseUrl = "https://leads.rndtechnosoft.com/api/image/download/"
                                        // Complete photo URL
                                        val photoUrl: String = baseUrl + user.photo.firstOrNull()

                                        Log.d("Photo URL", "onResponse: $photoUrl")

                                        // Load the profile image using Glide
                                        Glide.with(this@UserProfileActivity)
                                            .load(photoUrl)
                                            .placeholder(R.drawable.user) // Show default image while loading
                                            .error(R.drawable.user)
                                            .into(profileImage)

                                    } else {
                                        Log.e("API Error", "Empty response body")
                                    }
                                } else {
                                    Log.e(
                                        "API Error",
                                        "Failed to get user detail: ${response.message()} - ${
                                            response.errorBody()?.string()
                                        }"
                                    )
                                }
                            }

                            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                                Log.e("API Error", "Failed to get user details: ${t.message}")
                            }
                        })
                }
                "Manager" -> {
                    // Call getManager function
                    RetrofitInstance.apiInterface.getManager("Bearer $token", id = userId)
                        .enqueue(object : Callback<ManagerResponse> {
                            override fun onResponse(
                                call: Call<ManagerResponse>,
                                response: Response<ManagerResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val manager = response.body()
                                    if (manager != null) {
                                        profileUserName.text = manager.name
                                        profileUserEmail.text = manager.email
                                        profileUserMobile.text = manager.mobile
                                        profileCompanyname.text = manager.companyname

                                        // Check if the website is null or empty
                                        if (manager.website.isNullOrEmpty()) {
                                            profileWebsite.visibility = View.GONE // Hide if null or empty
                                        } else {
                                            profileWebsite.text = manager.website
                                            profileWebsite.visibility = View.VISIBLE // Show if not null
                                        }

                                        // Base URL
                                        val baseUrl = "https://leads.rndtechnosoft.com/api/image/download/"
                                        // Complete photo URL
                                        val photoUrl: String = baseUrl + manager.photo.firstOrNull()

                                        Log.d("Photo URL", "onResponse: $photoUrl")

                                        // Load the profile image using Glide
                                        Glide.with(this@UserProfileActivity)
                                            .load(photoUrl)
                                            .placeholder(R.drawable.user) // Show default image while loading
                                            .error(R.drawable.user)
                                            .into(profileImage)

                                    } else {
                                        Log.e("API Error", "Empty response body")
                                    }
                                } else {
                                    Log.e(
                                        "API Error",
                                        "Failed to get manager detail: ${response.message()} - ${
                                            response.errorBody()?.string()
                                        }"
                                    )
                                }
                            }

                            override fun onFailure(call: Call<ManagerResponse>, t: Throwable) {
                                Log.e("API Error", "Failed to get manager details: ${t.message}")
                            }
                        })
                }
                else -> {
                    Log.e("API Error", "Unknown role: $role")
                }
            }
        } else {
            Log.e("API Error", "User ID or token is null")
        }
    }

    // Alert Box Functionality
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("Do you want to logout?")
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            performLogout()
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.show()
    }

    // Perform Logout Functionality
    private fun performLogout() {
        // Clear user session data
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Show logout success dialog and redirect to login
        showLogoutSuccessDialog()
    }

    // Alert Box Functionality
    private fun showLogoutSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Success")
        builder.setMessage("Logged out successfully")
        builder.setPositiveButton("OK") { dialogInterface, _ ->
            dialogInterface.dismiss()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        fetchUserDetails()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

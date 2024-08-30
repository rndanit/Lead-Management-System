package com.rndtechnosoft.lms.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.GetUserResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileActivity : AppCompatActivity() {

    private lateinit var profileUserName: TextView
    private lateinit var profileUserEmail: TextView
    private lateinit var profileUserMobile: TextView
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
        editProfile=findViewById(R.id.editProfileButton)
        logoutButton=findViewById(R.id.logoutAccount)
        profileImage=findViewById(R.id.mainProfileImage)

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


        // Set the toolbar background color (optional)
        //toolbar.setBackgroundColor(getColor(R.drawable.gradientcolor))

        // Retrieve user details from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "Default Name")
        val email = sharedPreferences.getString("email", "Default Email")
        val mobile = sharedPreferences.getString("mobile", "Default Mobile")
        val photoUrl=sharedPreferences.getString("photo","Default Photo")

        Log.d("PhotoURL", "onCreate: PhotoUrl:-${photoUrl}")

        // Set the user details in the UI
        profileUserName.text = name
        profileUserEmail.text = email
        profileUserMobile.text = mobile

        // Load the profile image using Glide
        Glide.with(this)
            .load(photoUrl)
            .into(profileImage)


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
        if (userId != null) {
            RetrofitInstance.apiInterface.getUser("Bearer $token", userId)
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
                                profileUserMobile.text=user.mobile



                                // Base URL
                                val baseUrl = "https://leads.rndtechnosoft.com/api/image/download/"

                                // Complete photo URL
                                val photoUrl: String = baseUrl + user.photo.firstOrNull()

                                Glide.with(applicationContext).load(photoUrl).into(profileImage)

                                Log.d("Photo URL", "onResponse: $photoUrl")

                                // Store user details in SharedPreferences
                                val editor = sharedPreferences.edit()
                                editor.putString("name", user.name)
                                editor.putString("email", user.email)
                                editor.putString("mobile", user.mobile)
                                editor.putString("photo", photoUrl)
                                editor.apply()

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

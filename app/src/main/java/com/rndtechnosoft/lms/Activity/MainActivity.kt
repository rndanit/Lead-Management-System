package com.rndtechnosoft.lms.Activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.GetUserResponse
import com.rndtechnosoft.lms.R
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.animation.ObjectAnimator
import android.widget.TableLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.rndtechnosoft.lms.Activity.Adapter.LeadsPagerAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var addLead: AppCompatButton
   // private lateinit var showLead: AppCompatButton
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private var notificationBell: MenuItem? = null
    private lateinit var tabLayout:TabLayout
    private lateinit var viewPager:ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        tabLayout=findViewById(R.id.tab)
        viewPager=findViewById(R.id.viewPager)

        // Setup ViewPager2 with the adapter
        val pagerAdapter = LeadsPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Setup TabLayout with ViewPager2
        tabLayout.addTab(tabLayout.newTab().setText("New Lead"))
        tabLayout.addTab(tabLayout.newTab().setText("In Progress"))
        tabLayout.addTab(tabLayout.newTab().setText("Completed"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab?.position ?: 0
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.setScrollPosition(position, 0f, true)
            }
        })


        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        // Find the Id of UI Components.
       // showLead = findViewById(R.id.show_leads)
        addLead = findViewById(R.id.add_leads)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(Color.BLACK)
        toolbar.setSubtitleTextColor(Color.BLACK)
        setSupportActionBar(toolbar)


        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        // Set your custom toggle icon

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set the toggle icon color to gray
        toggle.drawerArrowDrawable.color = Color.GRAY

        // AddLead Set on Click Listener Functionality
        addLead.setOnClickListener {
            val intent = Intent(this@MainActivity, AddLeadActivity::class.java)
            startActivity(intent)
        }

        // ShowLead Set on Click Listener Functionality
        /*
        showLead.setOnClickListener {
            val intent = Intent(this@MainActivity, ShowLeadActivity::class.java)
            startActivity(intent)
        }

         */

        // Navigation Set on Click Listener Functionality
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    // Handle Home click
                }

                R.id.nav_profile -> {
                    profile()
                }

                R.id.nav_lead -> {
                    //lead()
                }

                R.id.nav_logout -> {
                    showLogoutConfirmationDialog()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Retrieve user data from SharedPreferences
        val name = sharedPreferences.getString("name", "Default Name")
        val email = sharedPreferences.getString("email", "Default Email")
        val mobilenumber = sharedPreferences.getString("number", "Default Email")

        // Set the name and email in the UI
        val headerView = navigationView.getHeaderView(0)
        userName = headerView.findViewById(R.id.user_name)
        userEmail = headerView.findViewById(R.id.user_email)
       // userName.text = name
       // userEmail.text = email

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
                                userName.text = user.name
                                userEmail.text = user.email

                                // Base URL
                                val baseUrl = "https://leads.rndtechnosoft.com/api/image/download/"

                                // Complete photo URL
                                val photoUrl: String = baseUrl + user.photo.firstOrNull()

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

    // Lead Function
    private fun lead() {
        val intent = Intent(this@MainActivity, ShowLeadActivity::class.java)
        startActivity(intent)
    }

    // Profile Function
    private fun profile() {
        val intent = Intent(this@MainActivity, UserProfileActivity::class.java)
        startActivity(intent)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        notificationBell = menu?.findItem(R.id.action_notification)

        // Set up custom view click listener if needed
        notificationBell?.actionView?.setOnClickListener {
            onOptionsItemSelected(notificationBell!!)
        }

        return true
    }


    override fun onResume() {
        super.onResume()
        fetchUserDetails()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notification -> {
                val intent = Intent(this, LeadNotificationActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

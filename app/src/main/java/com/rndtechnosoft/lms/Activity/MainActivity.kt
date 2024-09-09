package com.rndtechnosoft.lms.Activity

import android.content.Context
import android.content.Intent
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
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.rndtechnosoft.lms.Activity.Adapter.LeadsPagerAdapter
import com.rndtechnosoft.lms.Activity.DataModel.DataXXX
import com.rndtechnosoft.lms.Activity.DataModel.StatusTypeResponse

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var addLead: AppCompatButton

    // private lateinit var showLead: AppCompatButton
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userImage:ImageView
    private var notificationBell: MenuItem? = null
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1

    // Declare the variable to track initialization
    private var isViewPagerInitialized: Boolean = false
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            // Save the currently selected tab index before refreshing
            val currentTabIndex = tabLayout.selectedTabPosition

            // Refresh logic
            fetchUserDetails()
            setupTabsAndViewPager()

            // Restore the selected tab after refresh
            tabLayout.getTabAt(currentTabIndex)?.select()

            // Stop the loading spinner
            swipeRefreshLayout.isRefreshing = false
        }


        // Check for notification permission
        // Check for notification permission and show dialog if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isNotificationPermissionGranted()) {
            showNotificationPermissionDialog()
        }

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        tabLayout = findViewById(R.id.tab)
        viewPager = findViewById(R.id.viewPager)

        setupTabsAndViewPager()


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
                    updateStatus()
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
        userImage=headerView.findViewById(R.id.user_image)
        // userName.text = name
        // userEmail.text = email
        if (!isNotificationPermissionGranted()) {
            showNotificationPermissionDialog()
        }
        fetchUserDetails()
    }


    private fun isNotificationPermissionGranted(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission is not required for API levels below Tiramisu
        }
    }

    private fun showNotificationPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission Required")
            .setMessage("We need notification permission to keep you updated. Please allow it in the app settings.")
            .setPositiveButton("Allow") { _, _ ->
                requestNotificationPermission()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                // Optionally, you can handle the case where the user cancels
            }
            .setCancelable(false)
            .show()
    }

    private fun requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Handle the case for versions below Tiramisu
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                // Optionally, you can redirect the user to app settings to manually enable the permission
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${packageName}")
                startActivity(intent)
            }
        }
    }

    private fun setupTabsAndViewPager() {
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
                            val statusTypes = response.body()?.data ?: emptyList()
                            setupViewPagerWithTabs(statusTypes)




                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Failed to load status types",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<StatusTypeResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "API call failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }
    }
    }

    private fun setupViewPagerWithTabs(statusTypes: List<DataXXX>) {
        val pagerAdapter = LeadsPagerAdapter(this, statusTypes)
        viewPager.adapter = pagerAdapter

        tabLayout.removeAllTabs()
        for (status in statusTypes) {
            tabLayout.addTab(tabLayout.newTab().setText(status.status_type))
        }

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
                                        userName.text = user.name
                                        userEmail.text = user.email

                                        // Base URL
                                        val baseUrl = "https://leads.rndtechnosoft.com/api/image/download/"

                                        // Complete photo URL
                                        val photoUrl: String = baseUrl + user.photo.firstOrNull()

                                        // Load the profile image using Glide
                                        Glide.with(this@MainActivity)
                                            .load(photoUrl)
                                            .placeholder(R.drawable.user) // Show default image while loading
                                            .error(R.drawable.user)
                                            .into(userImage)


                                        Log.d("Photo URL", "onResponse: $photoUrl")

                                        // Store user details in SharedPreferences
                                        val editor = sharedPreferences.edit()
                                        editor.putString("name", user.name)
                                        editor.putString("email", user.email)
                                        editor.putString("mobile", user.mobile)
                                        editor.putString("company",user.companyname)
                                        editor.putString("website",user.website.firstOrNull())
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
                "Manager" -> {
                    // Call getManager function
                    RetrofitInstance.apiInterface.getManager("Bearer $token", id = userId)
                        .enqueue(object : Callback<GetUserResponse> {
                            override fun onResponse(
                                call: Call<GetUserResponse>,
                                response: Response<GetUserResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val manager = response.body()
                                    if (manager != null) {
                                        userName.text = manager.name
                                        userEmail.text = manager.email

                                        // Base URL
                                        val baseUrl = "https://leads.rndtechnosoft.com/api/image/download/"

                                        // Complete photo URL
                                        val photoUrl: String = baseUrl + manager.photo.firstOrNull()

                                        Log.d("Photo URL", "onResponse: $photoUrl")

                                        // Load the profile image using Glide
                                        Glide.with(this@MainActivity)
                                            .load(photoUrl)
                                            .placeholder(R.drawable.user) // Show default image while loading
                                            .error(R.drawable.user)
                                            .into(userImage)

                                        // Store user details in SharedPreferences
                                        val editor = sharedPreferences.edit()
                                        editor.putString("name", manager.name)
                                        editor.putString("email", manager.email)
                                        editor.putString("mobile", manager.mobile)
                                        editor.putString("company",manager.companyname)
                                        editor.putString("website",manager.website.firstOrNull())
                                        editor.putString("photo", photoUrl)
                                        editor.apply()

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

                            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
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

    // Lead Function
    private fun updateStatus() {
        val intent = Intent(this@MainActivity, UpdateStatusActivity::class.java)
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

        //recreate()
        // setupTabsAndViewPager()
        // Re-fetch status data when the activity resumes
        // Safeguard against re-initialization or potential issues

    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Handle your refresh logic here
        fetchUserDetails()
        setupTabsAndViewPager()
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

package com.rndtechnosoft.lms.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.UserLoginRequest
import com.rndtechnosoft.lms.Activity.DataModel.UserLoginResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var LoginButton: AppCompatButton
    private lateinit var DontButton: TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var progressBar: ProgressBar
    private var isPasswordVisible: Boolean = false
    private var deviceTokens: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Check if the user is already logged in
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            navigateToMainActivity()
        }

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Find the Id of a UI Components.
        emailEditText = findViewById(R.id.EmailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        progressBar = findViewById(R.id.progressBar) // Find ProgressBar


        //Device Token Functionality(Generate a Device Token).
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val devicetoken = task.result
                    deviceTokens = devicetoken
                    Log.d("Mamta", "FCM Token: $devicetoken")
                } else {
                    Log.w("Mamta", "Fetching FCM token failed", task.exception)
                }
            }

        // Set click listener on the eye icon
        passwordEditText.setOnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.rawX >= (passwordEditText.right - passwordEditText.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                togglePasswordVisibility()
                return@setOnTouchListener true
            }
            false
        }


        //Find the id of Login Button
        LoginButton = findViewById(R.id.login_button)

        LoginButton.setOnClickListener {

            // Show ProgressBar
            progressBar.visibility = View.VISIBLE
            //Call the function.
            loginUser()
        }

        //Find the id of Don't Button
        DontButton = findViewById(R.id.DontButton)

        DontButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    // togglePasswordVisibility function.
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.password_scaled,
                0,
                R.drawable.eye,
                0
            )
        } else {
            passwordEditText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.password_scaled,
                0,
                R.drawable.closed_eye,
                0
            )
        }
        passwordEditText.setSelection(passwordEditText.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    //LoginUser Function.
    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this@LoginActivity,
                "Please enter email and password",
                Toast.LENGTH_SHORT
            ).show()
            progressBar.visibility = View.GONE
            LoginButton.isEnabled = true
            return
        }

        val request = UserLoginRequest(email, password, deviceTokens!!)

        RetrofitInstance.apiInterface.loginUser(request)
            .enqueue(object : Callback<UserLoginResponse?> {
                override fun onResponse(
                    call: Call<UserLoginResponse?>,
                    response: Response<UserLoginResponse?>
                ) {


                    // Hide ProgressBar when response is received
                    progressBar.visibility = View.GONE
                    LoginButton.isEnabled = true
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            // showCustomToast()
                            //Toast.makeText(this@LoginActivity, "Login ID: ${loginResponse.id}", Toast.LENGTH_SHORT).show()
                            Log.d("LoginResponse", "onResponse: $loginResponse")
                            //saveUserData(loginResponse)

                            navigateToMainActivity()

                            // Store the token in SharedPreferences
                            val sharedPreferences =
                                getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("token", loginResponse.token)
                            editor.putString("userId", loginResponse.id)
                            editor.putBoolean("isLoggedIn", true)  // User logged in
                            editor.apply()

                            // Navigate to MainActivity
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login failed: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<UserLoginResponse?>, t: Throwable) {

                    // Hide ProgressBar if login fails
                    progressBar.visibility = View.GONE
                    LoginButton.isEnabled = true
                    Toast.makeText(
                        this@LoginActivity, "Login failed: ${t.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    /*
    private fun saveUserData(loginResponse: UserLoginResponse) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("token", loginResponse.token)
            putString("userId", loginResponse.id)
            putBoolean("isLoggedIn", true)
            apply()
        }
    }

     */

    private fun navigateToMainActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun showCustomToast() {
        val layoutInflater = layoutInflater
        val view =
            layoutInflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_root))
        // Create and show the custom toast
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }

}


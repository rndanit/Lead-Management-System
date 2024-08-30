package com.rndtechnosoft.lms.Activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.UserSignupRequest
import com.rndtechnosoft.lms.Activity.DataModel.UserSignupResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var companyNameEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var numberEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmEditText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var alreadyButton: TextView
    private lateinit var signUpButton: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        companyNameEditText = findViewById(R.id.comapanyNameEditText)
        nameEditText = findViewById(R.id.NameEditText)
        numberEditText = findViewById(R.id.NumberEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.PasswordEditText)
        confirmEditText = findViewById(R.id.ConfirmEditText)
        progressBar=findViewById(R.id.progressBar)

        //Find the id of SignUp Button
        signUpButton = findViewById(R.id.signUp_button)

        signUpButton.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            signupUser()
        }

        //Find the id of Already Button

        alreadyButton = findViewById(R.id.alreadyButton)

        alreadyButton.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    //Signup User Function.
    private fun signupUser() {

        val companyname = companyNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val mobile = numberEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || companyname.isEmpty() || mobile.isEmpty() || name.isEmpty()) {
            Toast.makeText(
                this@SignUpActivity,
                "Please enter email and password",
                Toast.LENGTH_SHORT
            ).show()
            progressBar.visibility = View.GONE
            signUpButton.isEnabled = true
            return
        }

        val request = UserSignupRequest(companyname, email, mobile, name, password)

        RetrofitInstance.apiInterface.signupUser(request)
            .enqueue(object : Callback<UserSignupResponse?> {
                override fun onResponse(
                    call: Call<UserSignupResponse?>,
                    response: Response<UserSignupResponse?>
                ) {

                    progressBar.visibility = View.GONE
                    signUpButton.isEnabled = true
                    if (response.isSuccessful) {
                        val signupResponse = response.body()
                        if (signupResponse != null) {

                            //showToast()
                             Toast.makeText(this@SignUpActivity ,"Signup Successfully", Toast.LENGTH_SHORT).show()
                            Log.d("SignUpResponse", "onResponse: $signupResponse")

                            // Store user data in SharedPreferences
                            val sharedPreferences =
                                getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("name", name)
                            editor.putString("email", email)
                            editor.putString("mobile", mobile)
                            editor.putString("id", signupResponse.user._id) // Store the id
                            editor.apply()

                            // Navigate to MainActivity
                            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Signup failed: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<UserSignupResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Signup failed: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility = View.GONE
                    signUpButton.isEnabled = true
                }
            })
    }

    //showToast Function(Custom Toast ko Show karaata hai)
    private fun showToast() {
        val layoutInflater = layoutInflater
        val view =
            layoutInflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_root))
        // Create and show the custom toast

        // Customize the TextView or ImageView in the custom toast layout
        val textView = view.findViewById<TextView>(R.id.toast_text)

        // Set custom text and emoji if needed (optional)
        textView.text = "SignUp Successful"

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }


}


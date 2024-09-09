package com.rndtechnosoft.lms.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.ForgetRequest
import com.rndtechnosoft.lms.Activity.DataModel.ForgetResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var continueButton:AppCompatButton
    private lateinit var email:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        //Find the UI variables.
        continueButton=findViewById(R.id.continue_button)
        email=findViewById(R.id.EmailEditText)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setDisplayShowTitleEnabled(false)


        // Handle the back button click
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        //Functionality of Continue Button.

        continueButton.setOnClickListener {

            forgetFunction()
        }
        // Handle the back button click

    }

    private fun forgetFunction() {
        val emailText = email.text.toString().trim()

        // Check if email is not empty
        if (emailText.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the request object
        val request = ForgetRequest(email = emailText)

        // Make the API call
        RetrofitInstance.apiInterface.forgetFunction(request).enqueue(object :
            Callback<ForgetResponse> {
            override fun onResponse(call: Call<ForgetResponse>, response: Response<ForgetResponse>) {
                if (response.isSuccessful) {
                    // Handle successful response
                    Toast.makeText(this@ForgetPasswordActivity, response.body()?.message, Toast.LENGTH_SHORT).show()

                    val intent=Intent(this@ForgetPasswordActivity,RestPasswordActivity::class.java)
                    startActivity(intent)

                    finish()
                } else {
                    // Handle failure response
                    Toast.makeText(this@ForgetPasswordActivity,"User not Found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForgetResponse>, t: Throwable) {
                // Handle error
                Toast.makeText(this@ForgetPasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
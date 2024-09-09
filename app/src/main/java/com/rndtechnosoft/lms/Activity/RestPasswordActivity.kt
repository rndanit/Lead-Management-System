package com.rndtechnosoft.lms.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.ForgetRequest
import com.rndtechnosoft.lms.Activity.DataModel.ForgetResponse
import com.rndtechnosoft.lms.Activity.DataModel.RestRequest
import com.rndtechnosoft.lms.Activity.DataModel.RestResponse
import com.rndtechnosoft.lms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestPasswordActivity : AppCompatActivity() {

    private lateinit var OtpEditText:EditText
    private lateinit var NewPassword:EditText
    private lateinit var RestPassword:AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rest_password)

        //Find the Id od a UI Components.

        OtpEditText=findViewById(R.id.OtpEditText)
        NewPassword=findViewById(R.id.NewPasswordEditText)
        RestPassword=findViewById(R.id.rest_button)

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


        RestPassword.setOnClickListener {
            RestFunction()
        }
    }

    private fun RestFunction(){

        val otp=OtpEditText.text.toString().trim()
        val newpassword=NewPassword.text.toString().trim()
        if (otp.isEmpty() || newpassword.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the request object
        val request =RestRequest(otp=otp, newPassword = newpassword)

        RetrofitInstance.apiInterface.restFunction(request).enqueue(object :
            Callback<RestResponse> {
            override fun onResponse(call: Call<RestResponse>, response: Response<RestResponse>) {
                if (response.isSuccessful) {
                    // Handle successful response
                    Toast.makeText(this@RestPasswordActivity, response.body()?.message, Toast.LENGTH_SHORT).show()

                    val intent= Intent(this@RestPasswordActivity,LoginActivity::class.java)
                    startActivity(intent)

                    finish()
                } else {
                    // Handle failure response
                    Toast.makeText(this@RestPasswordActivity, "Failed to rest password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RestResponse>, t: Throwable) {
                // Handle error
                Toast.makeText(this@RestPasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

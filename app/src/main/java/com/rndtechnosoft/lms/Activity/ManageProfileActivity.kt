package com.rndtechnosoft.lms.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rndtechnosoft.lms.Activity.Api.RetrofitInstance
import com.rndtechnosoft.lms.Activity.DataModel.UpdatedFields
import com.rndtechnosoft.lms.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ManageProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var mobileNo: EditText
    private lateinit var userName: EditText
    private lateinit var email: EditText
    private lateinit var saveButton: Button
    private val REQUEST_IMAGE_PICK = 1
    private var selectedImageUri: Uri? = null
    private lateinit var mProgress: ProgressDialog
    private lateinit var CancelButton:TextView

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(R.layout.activity_manage_profile)

        //Find the Id of a Variables.
        CancelButton=findViewById(R.id.CancelButton)
        profileImageView = findViewById(R.id.profileImageView)
        saveButton = findViewById(R.id.saveButton)
        userName = findViewById(R.id.userName)
        email = findViewById(R.id.email)
        mobileNo=findViewById(R.id.mobile)
        val btnPickImage = findViewById<FloatingActionButton>(R.id.cameraButton)


        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val names = sharedPreferences.getString("name", "Default Name")
        val emails = sharedPreferences.getString("email", "Default Email")
        val mobiles = sharedPreferences.getString("mobile", "Default Mobile")

        userName.setText(names)
        email.setText(emails)
        mobileNo.setText(mobiles)

        //Find the Id of a Toolbar.
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Customize the toolbar
        supportActionBar?.title = "Edit Profile" // Set the toolbar title
        toolbar.setTitleTextColor(getColor(R.color.white)) // Set the title color

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Set the toolbar background color (optional)
        //toolbar.setBackgroundColor(getColor(R.color.colorPrimary))


        //Functionality of a ProgressBar.
        mProgress = ProgressDialog(this).apply {
            setTitle("Loading Data....")
            setMessage("Please wait...")
            setCancelable(false)
            setIndeterminate(true)
        }

        //Camera Button setOnClickListener to open the Image Chooser.
        btnPickImage.setOnClickListener {
            openImagePicker()
        }

        CancelButton.setOnClickListener {
            userName.text.clear()
            email.text.clear()
            mobileNo.text.clear()
        }

        saveButton.setOnClickListener {
            val name = userName.text.toString().trim()
            val emailText = email.text.toString().trim()
            val mobile=mobileNo.text.toString().trim()

            // Check if the name or email is empty
            if (name.isEmpty() || emailText.isEmpty() || mobile.isEmpty()) {
                Toast.makeText(this@ManageProfileActivity, "Please fill in your name and email,mobileNumber.", Toast.LENGTH_SHORT).show()
            } else {
                updateProfile()
            }
        }
    }

    private fun updateProfile() {
        val name = userName.text.toString()
        val emailText = email.text.toString()
        val mobile=mobileNo.text.toString()

        // Retrieve the token from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        Toast.makeText(this@ManageProfileActivity, "Token Number:${token}", Toast.LENGTH_SHORT).show()


        // Convert to RequestBody
        val nameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val emailRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), emailText)
        val mobileNumberRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), mobile)
        val tokenRequestBody = token?.let {
            RequestBody.create("text/plain".toMediaTypeOrNull(), it)
        }

        var photoPart: MultipartBody.Part? = null

        selectedImageUri?.let {
            val file = getFileFromUri(it)
            file?.let {
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), it)
                photoPart = MultipartBody.Part.createFormData("photo", it.name, requestFile)
            }
        }

        if (tokenRequestBody != null) {
            mProgress.show()

            photoPart?.let {
                RetrofitInstance.apiInterface.updateProfile("Bearer $token",nameRequestBody, emailRequestBody,mobileNumberRequestBody,
                    it
                ).enqueue(object : Callback<UpdatedFields?> {
                    override fun onResponse(call: Call<UpdatedFields?>, response: Response<UpdatedFields?>) {
                        mProgress.dismiss()
                        if (response.isSuccessful) {
                            val profileResponse = response.body()
                            Toast.makeText(this@ManageProfileActivity, "Response:-${profileResponse}", Toast.LENGTH_SHORT).show()
                            profileResponse?.let {
                                userName.setText(it.name)
                                email.setText(it.email)
                                mobileNo.setText(it.mobile)

                                Toast.makeText(this@ManageProfileActivity, "Successfully Profile Updated", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } else {
                            // Handle the error
                            Toast.makeText(this@ManageProfileActivity, "Failed to update profile. Error code: ${response.code()}", Toast.LENGTH_SHORT).show()

                            Log.d("Update Error", "onResponse: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<UpdatedFields?>, t: Throwable) {
                        mProgress.dismiss()
                        Toast.makeText(this@ManageProfileActivity, "Failed to update profile: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("Profile Update", "onFailure: ${t.message}", t)
                    }
                })
            }
        } else {
            Toast.makeText(this, "Token is missing.", Toast.LENGTH_SHORT).show()
        }
    }

    // Utility function to get the actual file from a Uri
    private fun getFileFromUri(uri: Uri): File? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = cursor?.getString(columnIndex ?: 0)
        cursor?.close()
        return filePath?.let { File(it) }
    }


    //openImagePicker function.
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    // onActivityResult Function.
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            profileImageView.setImageURI(selectedImageUri)
        }
    }

    //OnBackPressed Function.
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
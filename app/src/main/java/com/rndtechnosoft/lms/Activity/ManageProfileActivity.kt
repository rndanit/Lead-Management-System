package com.rndtechnosoft.lms.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
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
    private lateinit var password:EditText
    private lateinit var companyName:EditText
    private lateinit var websiteName:EditText
    private lateinit var saveButton: Button
    private lateinit var photo:ImageView
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
        password=findViewById(R.id.password)
        companyName=findViewById(R.id.company)
        websiteName=findViewById(R.id.website)
        val btnPickImage = findViewById<FloatingActionButton>(R.id.cameraButton)


        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val names = sharedPreferences.getString("name", "Default Name")
        val emails = sharedPreferences.getString("email", "Default Email")
        val mobiles = sharedPreferences.getString("mobile", "Default Mobile")
        val company=sharedPreferences.getString("company","Default Email")
        val website=sharedPreferences.getString("website","Default Email")
        val userpassword=sharedPreferences.getString("userPassword","Default Password")


        userName.setText(names)
        email.setText(emails)
        mobileNo.setText(mobiles)
        password.setText(userpassword)
        companyName.setText(company)
        websiteName.setText(website)

        //Find the Id of a Toolbar.
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

        //Functionality of a ProgressBar.
        mProgress = ProgressDialog(this).apply {
            setTitle("Saving Data....")
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
            password.text.clear()
            companyName.text.clear()
            websiteName.text.clear()
        }

        saveButton.setOnClickListener {
            updateProfile()

        }
    }

    private fun updateProfile() {
        val name = userName.text.toString()
        val emailText = email.text.toString()
        val mobile = mobileNo.text.toString()
        val password = password.text.toString()
        val company=companyName.text.toString()
        val website=websiteName.text.toString()

        // Retrieve the token, role, and other user data from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val role = sharedPreferences.getString("role", null)
        val userId = sharedPreferences.getString("userId", null)

        // Retrieve existing values from SharedPreferences in case fields are empty
        val currentName = sharedPreferences.getString("name", "")
        val currentEmail = sharedPreferences.getString("email", "")
        val currentMobile = sharedPreferences.getString("mobile", "")
        val currentPassword = sharedPreferences.getString("password", "")
        val currentCompanyName = sharedPreferences.getString("company", "")
        val currentWebsite = sharedPreferences.getString("website", "")
        val originalPhoto = sharedPreferences.getString("photo", "Default Photo")

        // Extract the filename after the last '/'
        val photoFileName = originalPhoto?.substringAfterLast("/")

        Log.d("Image URL", "Photo File Name: $originalPhoto")

        // Create RequestBody instances
        val nameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), name.ifEmpty { currentName ?: "" })
        val emailRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), emailText.ifEmpty { currentEmail ?: "" })
        val mobileNumberRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), mobile.ifEmpty { currentMobile ?: "" })
        val passwordRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), password.ifEmpty { currentPassword ?: "" })
        val companyRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), company.ifEmpty { currentCompanyName ?: "" })
        val websiteRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), website.ifEmpty { currentWebsite ?: "" })

        val tokenRequestBody = token?.let {
            RequestBody.create("text/plain".toMediaTypeOrNull(), it)
        }

        var photoPart: MultipartBody.Part? = null
        var isPhotoUpdated = false

        if (selectedImageUri != null) {
            // If a new photo is selected, get the file from URI
            val file = getFileFromUri(selectedImageUri!!)
            file?.let {
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), it)
                photoPart = MultipartBody.Part.createFormData("photo", it.name, requestFile)
                isPhotoUpdated = true // Mark that the photo is updated
            }
        }

        // Check if any other field (except photo) has been updated
        val isOtherFieldUpdated = (name != currentName || emailText != currentEmail || mobile != currentMobile || password != currentPassword || company!=currentCompanyName || website!=currentWebsite)

        if (tokenRequestBody != null) {
            mProgress.show()

            when (role) {
                "user" -> {
                    if (userId != null) {
                        if (isPhotoUpdated && isOtherFieldUpdated) {
                            // Call updateProfile() if both photo and other fields are updated
                            photoPart?.let {
                                RetrofitInstance.apiInterface.updateProfile(
                                    "Bearer $token",
                                    nameRequestBody,
                                    emailRequestBody,
                                    mobileNumberRequestBody,
                                    passwordRequestBody,
                                    companyRequestBody,
                                    websiteRequestBody,
                                    it,
                                    id = userId
                                ).enqueue(profileUpdateCallback)
                            }
                        } else if (isOtherFieldUpdated) {
                            // Call updateProfileData() if only other fields are updated
                            RetrofitInstance.apiInterface.updateProfileData(
                                "Bearer $token",
                                nameRequestBody,
                                emailRequestBody,
                                mobileNumberRequestBody,
                                passwordRequestBody,
                                companyRequestBody,
                                websiteRequestBody,
                                id = userId
                            ).enqueue(profileUpdateCallback)
                        }
                    }
                }
                "Manager" -> {
                    if (userId != null) {
                        if (isPhotoUpdated && isOtherFieldUpdated) {
                            // Call updateProfileManager() if both photo and other fields are updated
                            photoPart?.let {
                                RetrofitInstance.apiInterface.updateProfileManager(
                                    "Bearer $token",
                                    nameRequestBody,
                                    emailRequestBody,
                                    mobileNumberRequestBody,
                                    passwordRequestBody,
                                    companyRequestBody,
                                    websiteRequestBody,
                                    it,
                                    id = userId
                                ).enqueue(profileUpdateCallback)
                            }
                        } else if (isOtherFieldUpdated) {
                            // Call updateProfileManagerData() if only other fields are updated
                            RetrofitInstance.apiInterface.updateProfileManagerData(
                                "Bearer $token",
                                nameRequestBody,
                                emailRequestBody,
                                mobileNumberRequestBody,
                                passwordRequestBody,
                                companyRequestBody,
                                websiteRequestBody,
                                id = userId
                            ).enqueue(profileUpdateCallback)
                        }
                    }
                }
                else -> {
                    Toast.makeText(this, "Unknown role: $role", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Token is missing.", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to get file from URI (make sure to implement this correctly)

    // Common callback for profile update
    private val profileUpdateCallback = object : Callback<UpdatedFields?> {
        override fun onResponse(call: Call<UpdatedFields?>, response: Response<UpdatedFields?>) {
            mProgress.dismiss()
            if (response.isSuccessful) {
                val profileResponse = response.body()
                profileResponse?.let {
                    userName.setText(it.name)
                    email.setText(it.email)
                    mobileNo.setText(it.mobile)
                    companyName.setText(it.companyname)

                    // Check if the website is null or empty and hide the EditText accordingly
                    if (it.website.isNullOrEmpty()) {
                        websiteName.visibility = View.GONE // Hide the website EditText
                    } else {
                        websiteName.visibility = View.VISIBLE // Show the website EditText if not null or empty
                        websiteName.setText(it.website.firstOrNull()) // Set the first website if available
                    }

                    // Update SharedPreferences with the new data
                    val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().apply {
                        putString("name", it.name)
                        putString("email", it.email)
                        putString("mobile", it.mobile)
                        putString("company", it.companyname)
                        putString("website", it.website.firstOrNull())
                        apply()
                    }

                    // Show success dialog
                    showSuccessDialog()
                    finish()
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Toast.makeText(this@ManageProfileActivity, "Failed to update profile. Error code: ${response.code()}", Toast.LENGTH_SHORT).show()
                Log.d("Update Error", "onResponse: ${response.code()} - $errorBody")
            }
        }

        override fun onFailure(call: Call<UpdatedFields?>, t: Throwable) {
            mProgress.dismiss()
            Toast.makeText(this@ManageProfileActivity, "Failed to update profile: ${t.message}", Toast.LENGTH_SHORT).show()
            Log.e("Profile Update", "onFailure: ${t.message}", t)
        }
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Success")
        builder.setMessage("Profile updated successfully!")
        builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.create().show()
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
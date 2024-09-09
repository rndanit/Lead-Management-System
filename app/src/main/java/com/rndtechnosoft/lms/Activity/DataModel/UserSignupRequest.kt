package com.rndtechnosoft.lms.Activity.DataModel

data class UserSignupRequest(
    val companyname: String,
    val email: String,
    val mobile: String,
    val name: String,
    val password: String,
    val website:String
)
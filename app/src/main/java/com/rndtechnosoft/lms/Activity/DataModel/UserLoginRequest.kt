package com.rndtechnosoft.lms.Activity.DataModel

data class UserLoginRequest(
    val email: String,
    val password: String,
    val deviceTokens:String
)
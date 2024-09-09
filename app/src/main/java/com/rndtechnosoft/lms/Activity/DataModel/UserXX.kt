package com.rndtechnosoft.lms.Activity.DataModel

data class UserXX(
    val associatedUserId: String,
    val deviceTokens: List<String>,
    val email: String,
    val id: String,
    val role: String
)
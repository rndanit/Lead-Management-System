package com.rndtechnosoft.lms.Activity.DataModel

data class GetUserResponse(
    val API_KEY: String,
    val __v: Int,
    val _id: String,
    val byLead: String,
    val companyname: String,
    val email: String,
    val mobile: String,
    val name: String,
    val password: String,
    val photo: List<String>,
    val role: String,
    val status: String
)
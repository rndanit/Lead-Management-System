package com.rndtechnosoft.lms.Activity.DataModel

data class User(
    val API_KEY: String,
    val __v: Int,
    val _id: String,
    val byLead: String,
    val companyname: String,
    val email: String,
    val mobile: String,
    val name: String,
    val password: String,
    val photo: List<Any>,
    val role: String,
    val status: String,
    val website:String
)
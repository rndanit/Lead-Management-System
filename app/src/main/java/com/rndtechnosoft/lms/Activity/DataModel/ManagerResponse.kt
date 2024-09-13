package com.rndtechnosoft.lms.Activity.DataModel

data class ManagerResponse(
    val __v: Int,
    val _id: String,
    val byLead: String,
    val companyname: String,
    val createdAt: String,
    val deviceTokens: List<String>,
    val email: String,
    val mobile: String,
    val name: String,
    val password: String,
    val photo: List<Any>,
    val role: String,
    val status: String,
    val updatedAt: String,
    val user: String,
    val website: String
)
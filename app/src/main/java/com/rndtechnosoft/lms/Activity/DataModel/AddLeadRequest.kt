package com.rndtechnosoft.lms.Activity.DataModel

data class AddLeadRequest(
    val token:String,
    val firstname:String,
    val lastname:String,
    val mobile:String,
    val email:String,
    val companyname:String,
    val leadInfo:String,
    val leadsDetails:String
)
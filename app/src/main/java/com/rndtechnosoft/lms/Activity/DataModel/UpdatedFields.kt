package com.rndtechnosoft.lms.Activity.DataModel

import com.google.gson.annotations.SerializedName

data class UpdatedFields(
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("photo") val photo: List<String?>,
    @SerializedName("status") val status: String?,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("companyname") val companyname: String?,
    @SerializedName("website") val website:String?,
)

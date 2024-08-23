package com.rndtechnosoft.lms.Activity.DataModel

import com.google.gson.annotations.SerializedName

data class UpdatedFields(
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("photo") val photo: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

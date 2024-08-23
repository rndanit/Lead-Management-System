package com.rndtechnosoft.lms.Activity.DataModel

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(

    val id: String,
    val updatedFields: UpdatedFields
)
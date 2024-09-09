package com.rndtechnosoft.lms.Activity.DataModel

data class Leads(
    val acknowledged: Boolean,
    val matchedCount: Int,
    val modifiedCount: Int,
    val upsertedCount: Int,
    val upsertedId: Any
)
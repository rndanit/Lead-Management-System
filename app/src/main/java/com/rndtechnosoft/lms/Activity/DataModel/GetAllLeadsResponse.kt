package com.rndtechnosoft.lms.Activity.DataModel

data class GetAllLeadsResponse(
    val currentPage: Int,
    val leads: MutableList<Lead>,
    val total: Int,
    val totalPages: Int
)
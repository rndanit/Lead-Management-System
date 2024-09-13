package com.rndtechnosoft.lms.Activity.DataModel

data class GetAllLeadsResponse(
    val currentPage: Int,
    val leads: List<Lead>,
    val total: Int,
    val totalPages: Int
)
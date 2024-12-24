package com.example.splitmategamma.dashboard.principalTenant.utility.model

data class UtilityByIdResponse(
    val _id: String,
    val name: String,
    val type: String,
    val cost: Int,
    val houseId: Any
)
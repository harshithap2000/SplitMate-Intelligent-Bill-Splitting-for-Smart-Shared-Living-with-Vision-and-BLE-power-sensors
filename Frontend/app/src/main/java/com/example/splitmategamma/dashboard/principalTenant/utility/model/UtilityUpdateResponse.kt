package com.example.splitmategamma.dashboard.principalTenant.utility.model

data class UtilityUpdateResponse(
    val message: String,
    val utility: UtilityDetails
)

data class UtilityDetails(
    val _id: String,
    val name: String,
    val type: String,
    val sensor: String
)
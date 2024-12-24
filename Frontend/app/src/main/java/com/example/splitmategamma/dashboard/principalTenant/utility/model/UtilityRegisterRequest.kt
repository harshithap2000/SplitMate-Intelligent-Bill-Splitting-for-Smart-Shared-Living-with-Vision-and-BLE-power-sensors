package com.example.splitmategamma.dashboard.principalTenant.utility.model

data class UtilityRegisterRequest(
    val name: String,
    val type: String,
    val sensor: String,
    val houseId: String
)
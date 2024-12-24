package com.example.splitmategamma.dashboard.principalTenant.utility.model

data class UtilitiesListResponse(
    val utilities: List<Utilities>
)

data class Utilities(
    val _id: String,
    var name: String,
    var type: String,
    var sensor: String,
)
package com.example.splitmategamma.dashboard.principalTenant.tenantManagement.model

data class Tenant(
    val _id: String,
    val name: String,
    val email: String,
    val role: String,
    val photo: String,
    val houseId: Any
)
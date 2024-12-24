package com.example.splitmategamma.auth.model

data class LoginResponse(
    val token: String,
    val user: AuthUser
)

data class AuthUser(
    val _id: String,
    val role: String,
    val houseId: Any
)
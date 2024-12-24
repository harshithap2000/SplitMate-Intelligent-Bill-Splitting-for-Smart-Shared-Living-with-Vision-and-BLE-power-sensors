package com.example.splitmategamma.auth.model

data class RegisterUserResponse(
    val _id: String,
    val name: String,
    val email: String,
    val role: String,
    val photo: String,
    val houseId: Any
)
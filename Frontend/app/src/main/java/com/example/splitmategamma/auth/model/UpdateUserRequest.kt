package com.example.splitmategamma.auth.model

data class UpdateUserRequest(
    val name: String,
    val photo: String? // Base64 encoded image data or URI
)
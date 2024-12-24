package com.example.splitmategamma.auth.model

sealed class RegisterUserRequest {
    data class PrincipalUser(
        val name: String,
        val email: String,
        val password: String,
        val role: String,
        val houseAddress: String,
        val houseName: String,
        val image: String? = null // Base64 encoded image data or URI
    ) : RegisterUserRequest()

    data class NormalUser(
        val name: String,
        val email: String,
        val password: String,
        val role: String,
        val houseId: String,
        val image: String? = null // Base64 encoded image data or URI
    ) : RegisterUserRequest()
}
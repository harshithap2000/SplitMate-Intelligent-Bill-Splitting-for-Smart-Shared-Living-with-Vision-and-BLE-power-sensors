package com.example.splitmategamma.auth.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmategamma.auth.model.AuthUser
import com.example.splitmategamma.auth.model.House
import com.example.splitmategamma.auth.model.RegisterUserResponse
import com.example.splitmategamma.auth.repository.AuthRepository
import com.example.splitmategamma.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    var email: String = Constants.DEFAULT_EMAIL
    var password: String = Constants.DEFAULT_PASSWORD
    var name: String = Constants.DEFAULT_NAME
    var role: String = Constants.DEFAULT_ROLE
    var selectedImageUri: String? = Constants.DEFAULT_SELECTED_IMAGE_URI
    var houseId: String = Constants.DEFAULT_HOUSE_ID

    private val _houses = MutableStateFlow<List<House>>(emptyList())
    val houses: StateFlow<List<House>> = _houses

    fun fetchHouses(onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.fetchHouses()
                if (response.isSuccessful) {
                    _houses.value = response.body() ?: emptyList()
                    Log.d("FetchHouses", "Fetched houses: ${_houses.value}")
                } else {
                    onError("Failed to fetch houses: ${response.message()}")
                    Log.e("FetchHouses", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
                Log.e("FetchHouses", "Exception: ${e.message}", e)
            }
        }
    }

    fun signupUser(
        name: String,
        email: String,
        password: String,
        role: String,
        houseName: String?,
        houseAddress: String?,
        houseId: String?,
        imageUri: Uri?,
        context: Context,
        onSuccess: (RegisterUserResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = authRepository.registerUser(
                    name = name,
                    email = email,
                    password = password,
                    role = role,
                    houseName = houseName,
                    houseAddress = houseAddress,
                    houseId = houseId,
                    photoUri = imageUri,
                    context = context
                )
                if (response.isSuccessful) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Signup failed: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            }
        }
    }

    fun loginUser(onSuccess: (String, AuthUser) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.loginUser(
                    mapOf(
                        Constants.PREFS_USER_EMAIL to email,
                        Constants.PREFS_USER_PASSWORD to password
                    )
                )
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        onSuccess(loginResponse.token, loginResponse.user)
                    } else {
                        onError(Constants.ERROR_LOGIN_FAILED)
                    }
                } else {
                    onError("${Constants.ERROR_LOGIN_FAILED}: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("NetworkError", "Error occurred: ${e.message}", e)
                onError("Network error: ${e.message}")
            }
        }
    }
}
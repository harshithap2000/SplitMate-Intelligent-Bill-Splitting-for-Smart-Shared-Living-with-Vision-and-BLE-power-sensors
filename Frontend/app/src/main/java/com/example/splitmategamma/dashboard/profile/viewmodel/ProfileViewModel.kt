package com.example.splitmategamma.dashboard.profile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmategamma.auth.model.House
import com.example.splitmategamma.auth.model.User
import com.example.splitmategamma.dashboard.profile.repository.ProfileRepository
import com.example.splitmategamma.utils.Constants
import kotlinx.coroutines.launch

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    fun fetchUserProfile(onSuccess: (User) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = profileRepository.getUserProfile()
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onError(Constants.ERROR_FETCH_USER)
                    }
                } else {
                    onError("${Constants.ERROR_FETCH_USER}: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("NetworkError", "Error occurred: ${e.message}", e)
                onError("Network error: ${e.message}")
            }
        }
    }

    fun fetchHouses(onSuccess: (List<House>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = profileRepository.getHouses()
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Failed to fetch house details")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")
            }
        }
    }

    fun fetchAllHouses(onSuccess: (List<House>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = profileRepository.fetchHouses()
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Failed to fetch house details")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")
            }
        }
    }

}
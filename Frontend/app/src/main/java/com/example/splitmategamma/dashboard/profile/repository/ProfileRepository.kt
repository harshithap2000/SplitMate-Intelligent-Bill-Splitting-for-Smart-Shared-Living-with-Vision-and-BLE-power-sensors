package com.example.splitmategamma.dashboard.profile.repository

import android.util.Log
import com.example.splitmategamma.auth.model.House
import com.example.splitmategamma.auth.model.User
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager
import com.example.splitmategamma.utils.Constants
import retrofit2.Response

class ProfileRepository(private val apiService: ApiService) {

    suspend fun getUserProfile(): Response<User> {
        val token = PreferenceManager.getUserToken()
        Log.d("Profile", "Fetching user profile with token: $token")
        return if (token != null) {
            apiService.getUserProfile("Bearer $token")
        } else {
            throw IllegalStateException("Token is null")
        }
    }

    suspend fun getHouses(): Response<List<House>> {
        val token = PreferenceManager.getUserToken()
        return if (token != null) {
            apiService.getHouses("Bearer $token")
        } else {
            throw IllegalStateException("Token is null")
        }
    }

    suspend fun fetchHouses(): Response<List<House>> {
        val token = PreferenceManager.getUserToken()
        return if (token != null) {
            apiService.fetchHouses()
        } else {
            throw IllegalStateException("Token is null")
        }
    }


}
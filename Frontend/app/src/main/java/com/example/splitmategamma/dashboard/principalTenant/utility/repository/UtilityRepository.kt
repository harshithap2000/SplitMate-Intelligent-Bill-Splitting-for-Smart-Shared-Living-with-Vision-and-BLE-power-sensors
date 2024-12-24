package com.example.splitmategamma.dashboard.principalTenant.utility.repository

import android.util.Log
import com.example.splitmategamma.dashboard.principalTenant.utility.model.HouseRequest
import com.example.splitmategamma.dashboard.principalTenant.utility.model.UtilityRegisterRequest
import com.example.splitmategamma.dashboard.principalTenant.utility.model.UtilityRegisterResponse
import com.example.splitmategamma.dashboard.principalTenant.utility.model.UtilitiesListResponse
import com.example.splitmategamma.dashboard.principalTenant.utility.model.Utility
import com.example.splitmategamma.dashboard.principalTenant.utility.model.UtilityUpdateResponse
import com.example.splitmategamma.dashboard.principalTenant.utility.model.UtilityDeleteResponse
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager
import retrofit2.Response

class UtilityRepository(private val apiService: ApiService) {

    // Get all utilities
    suspend fun getUtilities(token: String, houseId: String): Response<UtilitiesListResponse> {
        return apiService.getUtilities(token, HouseRequest(houseId))
    }

    // Register a utility
    suspend fun registerUtility(token: String, utility: UtilityRegisterRequest): Response<UtilityRegisterResponse> {
        Log.d("UtilityRepo", "Registering utility with token: $token and data: $utility")
        return apiService.registerUtility(token, utility)
    }

    // Update a utility
    suspend fun updateUtility(token: String, id: String, utility: Utility): Response<UtilityUpdateResponse> {
        return apiService.updateUtility(token, id, utility)
    }

    // Delete a utility
    suspend fun deleteUtility(token: String, id: String): Response<UtilityDeleteResponse> {
        return apiService.deleteUtility(token, id)
    }
}

package com.example.splitmategamma.dashboard.principalTenant.utility.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmategamma.dashboard.principalTenant.utility.model.*
import com.example.splitmategamma.dashboard.principalTenant.utility.repository.UtilityRepository
import com.example.splitmategamma.network.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UtilityViewModel(private val repository: UtilityRepository) : ViewModel() {
    private val _utilities = MutableStateFlow<List<Utilities>>(emptyList())
    val utilities: StateFlow<List<Utilities>> = _utilities

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Fetch all utilities
    fun fetchUtilities() {
        viewModelScope.launch {
            val token = PreferenceManager.getUserToken()
            val houseId = PreferenceManager.getHouseId()
            if (houseId != null) {
                val response = repository.getUtilities("Bearer $token", houseId)
                if (response.isSuccessful) {
                    _utilities.value = response.body()?.utilities ?: emptyList()
                } else {
                    _error.value = "Failed to fetch utilities: ${response.message()}"
                }
            } else {
                _error.value = "Token or House ID is missing"
            }
        }
    }


    // Register Utility
    fun registerUtility(name: String, type: String, sensor: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val token = PreferenceManager.getUserToken()
            val houseId = PreferenceManager.getHouseId()

            if (houseId != null) {
                val utility = UtilityRegisterRequest(name = name, type = type, sensor = sensor, houseId = houseId)
                val response = repository.registerUtility("Bearer $token", utility)
                if (response.isSuccessful) {
                    fetchUtilities()  // Fetch the updated list of utilities
                    onSuccess()
                } else {
                    onError("Failed to register utility: ${response.message()}")
                }
            } else {
                onError("Token or House ID is missing")
            }
        }
    }



    // Update an existing utility
    fun updateUtility(id: String, name: String, type: String, sensor: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val token = PreferenceManager.getUserToken()
            val utility = Utility(name = name, type = type, sensor = sensor)
            val response = repository.updateUtility("Bearer $token", id, utility)
            if (response.isSuccessful) {
                fetchUtilities()
                onSuccess()
            } else {
                onError("Failed to update utility: ${response.message()}")
            }
        }
    }

    // Delete a utility
    fun deleteUtility(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val token = PreferenceManager.getUserToken()
            val response = repository.deleteUtility("Bearer $token", id)
            if (response.isSuccessful) {
                fetchUtilities()
                onSuccess()
            } else {
                onError("Failed to delete utility: ${response.message()}")
            }
        }
    }
}
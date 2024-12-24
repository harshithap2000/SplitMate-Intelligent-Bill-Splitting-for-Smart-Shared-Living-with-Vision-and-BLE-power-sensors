package com.example.splitmategamma.dashboard.principalTenant.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmategamma.auth.model.House
import com.example.splitmategamma.dashboard.principalTenant.home.repository.PrincipalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PrincipalViewModel(private val principalRepository: PrincipalRepository) : ViewModel() {
    // State for houses using MutableStateFlow
    private val _houses = MutableStateFlow<List<House>>(emptyList())
    val houses: StateFlow<List<House>> = _houses

    fun fetchHouses(token: String) {
        viewModelScope.launch {
            val fetchedHouses = principalRepository.getHousesForPrincipalTenant("Bearer $token")
            if (fetchedHouses != null) {
                _houses.value = fetchedHouses
                println("Houses fetched successfully: $fetchedHouses")
            } else {
                println("Failed to fetch houses.")
                // Handle error, e.g., show a message to the user
            }
        }
    }


    fun addHouse(token: String, houseName: String, houseAddress: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                println("Token = $token");
                val response = principalRepository.addHouse("Bearer $token", houseName, houseAddress)
                if (response.isSuccessful) {
                    fetchHouses(token) // Refresh the list of houses after adding
                    onSuccess()
                } else {
                    onError("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
}

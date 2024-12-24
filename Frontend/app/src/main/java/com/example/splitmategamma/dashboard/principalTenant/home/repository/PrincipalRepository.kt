package com.example.splitmategamma.dashboard.principalTenant.home.repository

import com.example.splitmategamma.auth.model.AddHouse
import com.example.splitmategamma.auth.model.House
import com.example.splitmategamma.network.ApiService
import retrofit2.Response

class PrincipalRepository(private val apiService: ApiService) {

    suspend fun getHousesForPrincipalTenant(token: String): List<House>? {
        return try {
            val response = apiService.getHouses(token)
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error fetching houses: ${response.code()} - ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            println("Exception while fetching houses: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun addHouse(token: String, houseName: String, houseAddress: String): Response<House> {
        return apiService.addHouse(token, AddHouse(houseName = houseName, houseAddress = houseAddress))
    }
}
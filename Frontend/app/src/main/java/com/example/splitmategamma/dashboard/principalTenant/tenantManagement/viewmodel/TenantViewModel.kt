package com.example.splitmategamma.dashboard.principalTenant.tenantManagement.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.model.RemoveTenantRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.model.Tenant
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.repository.TenantRepository
import com.example.splitmategamma.network.PreferenceManager

class TenantManagementViewModel(
    private val tenantManagementRepository: TenantRepository
) : ViewModel() {

    private val _tenants = MutableStateFlow<List<Tenant>>(emptyList())
    val tenants: StateFlow<List<Tenant>> = _tenants

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchTenants() {
        viewModelScope.launch {
            try {
                val token = PreferenceManager.getUserToken()
                val houseId = PreferenceManager.getHouseId()
                if (houseId != null) {
                    val response = tenantManagementRepository.getTenants("Bearer $token", houseId)
                    if (response.isSuccessful) {
                        _tenants.value = response.body() ?: emptyList()
                    } else {
                        _error.value = "Failed to fetch tenants: ${response.message()}"
                    }
                } else {
                    _error.value = "Token or House ID is missing."
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun removeTenant(tenantId: String) {
        viewModelScope.launch {
            val token = PreferenceManager.getUserToken()
            val houseId = PreferenceManager.getHouseId()

            if (houseId != null) {
                val request = RemoveTenantRequest(tenantId = tenantId, houseId = houseId)
                val response = tenantManagementRepository.removeTenant("Bearer $token", request)

                if (response.isSuccessful) {
                    fetchTenants()
                } else {
                    _error.value = "Failed to remove tenant: ${response.message()}"
                }
            } else {
                _error.value = "Token or House ID is missing."
            }
        }
    }
}
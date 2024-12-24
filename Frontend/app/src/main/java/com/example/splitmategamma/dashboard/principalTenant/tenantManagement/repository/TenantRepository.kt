package com.example.splitmategamma.dashboard.principalTenant.tenantManagement.repository

import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.model.RemoveTenantRequest
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.model.Tenant
import com.example.splitmategamma.network.ApiService
import retrofit2.Response

class TenantRepository(private val apiService: ApiService) {

    suspend fun getTenants(token: String, houseId: String): Response<List<Tenant>> {
        val requestBody = mapOf("houseId" to houseId)
        return apiService.getTenants(token, requestBody)
    }

    suspend fun removeTenant(token: String, request: RemoveTenantRequest): Response<Void> {
        return apiService.removeTenant(token, request)
    }
}

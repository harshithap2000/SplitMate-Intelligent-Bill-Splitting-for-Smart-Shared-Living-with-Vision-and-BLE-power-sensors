package com.example.splitmategamma.dashboard.principalTenant.tenantManagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.repository.TenantRepository

class TenantManagementViewModelFactory(
    private val tenantManagementRepository: TenantRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TenantManagementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TenantManagementViewModel(tenantManagementRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
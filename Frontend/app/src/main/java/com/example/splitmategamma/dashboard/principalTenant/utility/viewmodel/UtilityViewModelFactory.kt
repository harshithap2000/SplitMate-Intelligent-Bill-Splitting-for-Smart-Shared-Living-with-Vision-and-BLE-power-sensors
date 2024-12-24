package com.example.splitmategamma.dashboard.principalTenant.utility.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitmategamma.dashboard.principalTenant.utility.repository.UtilityRepository

class UtilityViewModelFactory(
    private val repository: UtilityRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UtilityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UtilityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

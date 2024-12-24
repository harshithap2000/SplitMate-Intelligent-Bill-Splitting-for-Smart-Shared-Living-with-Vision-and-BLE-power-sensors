package com.example.splitmategamma.dashboard.principalTenant.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitmategamma.dashboard.principalTenant.home.repository.PrincipalRepository

class PrincipalViewModelFactory(private val principalRepository: PrincipalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrincipalViewModel::class.java)) {
            return PrincipalViewModel(principalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

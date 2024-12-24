package com.example.splitmategamma.dashboard.principalTenant.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitmategamma.dashboard.principalTenant.notiification.viewmodel.NotificationViewModel
import com.example.splitmategamma.network.ApiService

class NotificationViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
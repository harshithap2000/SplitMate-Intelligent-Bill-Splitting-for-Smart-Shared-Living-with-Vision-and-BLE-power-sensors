package com.example.splitmategamma.dashboard.principalTenant.notiification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmategamma.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.splitmategamma.dashboard.principalTenant.notiification.model.NotificationResponse

class NotificationViewModel(private val apiService: ApiService) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationResponse>>(emptyList())
    val notifications: StateFlow<List<NotificationResponse>> = _notifications

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchNotifications(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getNotifications("Bearer $token")
                if (response.isSuccessful) {
                    _notifications.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to fetch notifications: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun markNotificationAsRead(token: String, notificationId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.markNotificationAsRead("Bearer $token", notificationId)
                if (response.isSuccessful) {
                    _notifications.value = _notifications.value.map {
                        if (it._id == notificationId) it.copy(status = "read") else it
                    }
                } else {
                    _error.value = "Failed to mark notification as read: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun dismissNotification(token: String, notificationId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.dismissNotification("Bearer $token", notificationId)
                if (response.isSuccessful) {
                    _notifications.value = _notifications.value.filter { it._id != notificationId }
                } else {
                    _error.value = "Failed to dismiss notification: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}

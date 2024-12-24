package com.example.splitmategamma.dashboard.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitmategamma.auth.model.House
import com.example.splitmategamma.dashboard.profile.repository.ProfileRepository

class ProfileViewModelFactory(private val profileRepository: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(profileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
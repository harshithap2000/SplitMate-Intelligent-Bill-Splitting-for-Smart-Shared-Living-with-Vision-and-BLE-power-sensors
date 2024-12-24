package com.example.splitmategamma.dashboard.bill.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitmategamma.dashboard.bill.repository.BillRepository

class BillViewModelFactory(private val repository: BillRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BillViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.splitmategamma.dashboard.bill.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmategamma.dashboard.bill.model.BillResponse
import com.example.splitmategamma.dashboard.bill.repository.BillRepository
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.model.Tenant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class BillViewModel(private val repository: BillRepository) : ViewModel() {

    private val _billResponse = MutableStateFlow<BillResponse?>(null)
    val billResponse: StateFlow<BillResponse?> = _billResponse

    private val _tenants = MutableStateFlow<List<Tenant>>(emptyList())
    val tenants: StateFlow<List<Tenant>> = _tenants

    private val _uploadSuccess = MutableStateFlow<Boolean?>(null)
    val uploadSuccess: StateFlow<Boolean?> = _uploadSuccess

    private val _payBillStatus = MutableStateFlow<String?>(null)
    val payBillStatus: StateFlow<String?> = _payBillStatus

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Fetch the bill
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchBill(token: String, houseId: String, chosenDate: String) {
        viewModelScope.launch {
            if (token.isNotBlank()) {
                try {
                    val response = repository.getBill("Bearer $token", houseId, chosenDate)
                    if (response.isSuccessful) {
                        _billResponse.value = response.body()
                    } else {
                        _error.value = "Failed to fetch bill: ${response.message()}"
                    }
                } catch (e: Exception) {
                    _error.value = "An error occurred: ${e.localizedMessage}"
                }
            } else {
                _error.value = "House ID or token is missing."
            }
        }
    }

    // Fetch tenants for the given house
    fun fetchTenants(token: String, houseId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getTenants("Bearer $token", houseId)
                if (response.isSuccessful) {
                    _tenants.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to fetch tenants: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "An error occurred: ${e.localizedMessage}"
            }
        }
    }

    fun uploadBill(
        token: String,
        houseId: String,
        totalAmount: String,
        chosenDate: String,
        pdfFile: File
    ) {
        viewModelScope.launch {
            try {
                val response = repository.uploadBill(token, houseId, totalAmount, chosenDate, pdfFile)
                if (response.isSuccessful) {
                    _uploadSuccess.value = true
                    Log.d("BillViewModel", "Upload successful: ${response.body()}")
                } else {
                    _uploadSuccess.value = false
                    _error.value = when (response.code()) {
                        400 -> "Invalid input provided (400)."
                        401 -> "Unauthorized, token is invalid or missing (401)."
                        403 -> "Forbidden, user is not a principal tenant (403)."
                        404 -> "User not found or does not belong to this house (404)."
                        500 -> "Internal server error (500)."
                        else -> "Unknown error occurred."
                    }
                    Log.e("BillViewModel", "Upload failed: ${_error.value}")
                }
            } catch (e: Exception) {
                _uploadSuccess.value = false
                _error.value = "An error occurred: ${e.localizedMessage}"
                Log.e("BillViewModel", "Upload exception: ${e.localizedMessage}")
            }
        }
    }

    suspend fun downloadBill(token: String, houseId: String, billingDate: String): File? {
        return withContext(Dispatchers.IO) {
            repository.downloadBill(token, houseId, billingDate)
        }
    }

    fun openPdf(context: Context, pdfUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(pdfUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "No application found to open PDF file", Toast.LENGTH_SHORT).show()
        }
    }

    fun payBill(token: String, houseId: String, chosenDate: String) {
        viewModelScope.launch {
            try {
                val requestBody = mapOf("chosenDate" to chosenDate, "houseId" to houseId)
                val response = repository.payBill(token, requestBody)
                if (response.isSuccessful) {
                    _billResponse.value?.tenantBill?.status = "paid"
                    _payBillStatus.value = "Bill paid successfully!"
                    Log.d("BillViewModel", "Bill paid: ${response.body()}")
                } else {
                    _error.value = when (response.code()) {
                        400 -> "Invalid request."
                        401 -> "Unauthorized, token is invalid or missing."
                        403 -> "Forbidden, you do not have permission to perform this action."
                        404 -> "Bill not found."
                        else -> "Server error. Please try again."
                    }
                    Log.e("BillViewModel", "Failed to pay bill: ${_error.value}")
                }
            } catch (e: Exception) {
                _error.value = "An error occurred: ${e.localizedMessage}"
                Log.e("BillViewModel", "Pay bill exception: ${e.localizedMessage}")
            }
        }
    }

    // Send a manual notification
    fun sendManualNotification(token: String, request: Map<String, String>, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.sendManualNotification("Bearer $token", request)
                if (response.isSuccessful) {
                    onSuccess() // Call the success callback
                } else {
                    onFailure("Failed to send notification: ${response.message()}") // Call the failure callback with an error message
                }
            } catch (e: Exception) {
                onFailure("Error: ${e.localizedMessage}") // Handle any exceptions
            }
        }
    }


    // Clear pay bill status
    fun clearPayBillStatus() {
        _payBillStatus.value = null
    }

    // Clear error
    fun clearError() {
        _error.value = null
    }

    // Clear upload success state
    fun clearUploadSuccess() {
        _uploadSuccess.value = null
    }
}

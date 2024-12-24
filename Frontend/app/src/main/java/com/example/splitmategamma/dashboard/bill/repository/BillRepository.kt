package com.example.splitmategamma.dashboard.bill.repository

import android.os.Environment
import android.util.Log
import com.example.splitmategamma.dashboard.bill.model.BillResponse
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.model.Tenant
import com.example.splitmategamma.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class BillRepository(private val apiService: ApiService) {

    // Function to get the bill
    suspend fun getBill(token: String, houseId: String, chosenDate: String): Response<BillResponse> {
        val requestBody = mapOf(
            "chosenDate" to chosenDate,
            "houseId" to houseId
        )
        Log.d("BillRepository", "Request Payload: $requestBody")
        val response = apiService.getBill(token, requestBody)
        if (response.isSuccessful) {
            Log.d("BillRepository", "Bill data fetched successfully: ${response.body()}")
        } else {
            Log.e("BillRepository", "Failed to fetch bill data: ${response.code()} - ${response.message()}")
        }
        return response
    }

    // Function to pay the bill
    suspend fun payBill(token: String, requestBody: Map<String, String>): Response<Map<String, String>> {
        Log.d("BillRepository", "Pay Bill Request: $requestBody")
        return try {
            val response = apiService.payBill("Bearer $token", requestBody)
            if (response.isSuccessful) {
                Log.d("BillRepository", "Bill status: ${response.body()?.get("status")}")
            } else {
                Log.e("BillRepository", "Failed to pay bill: ${response.message()}")
            }
            response
        } catch (e: Exception) {
            Log.e("BillRepository", "Exception occurred while paying bill: ${e.localizedMessage}")
            throw e
        }
    }

    // Function to upload the bill
    suspend fun uploadBill(
        token: String,
        houseId: String,
        totalAmount: String,
        chosenDate: String,
        pdfFile: File
    ): Response<Map<String, String>> {
        if (houseId.length != 24) {
            Log.e("BillRepository", "Invalid houseId: $houseId. Must be a 24-character hex string.")
            return Response.error(400, okhttp3.ResponseBody.create(null, "Invalid houseId format"))
        }

        val houseIdRequestBody = houseId.toRequestBody("text/plain".toMediaTypeOrNull())
        val totalAmountRequestBody = totalAmount.toRequestBody("text/plain".toMediaTypeOrNull())
        val chosenDateRequestBody = chosenDate.toRequestBody("text/plain".toMediaTypeOrNull())
        val pdfRequestBody = pdfFile.asRequestBody("application/pdf".toMediaTypeOrNull())
        val pdfPart = MultipartBody.Part.createFormData("pdf", pdfFile.name, pdfRequestBody)

        Log.d("BillRepository", "Token: $token")
        Log.d("BillRepository", "House ID: $houseId")
        Log.d("BillRepository", "Total Amount: $totalAmount")
        Log.d("BillRepository", "Chosen Date: $chosenDate")
        Log.d("BillRepository", "PDF File Name: ${pdfFile.name}")

        return apiService.uploadBill(
            token = "Bearer $token",
            houseId = houseIdRequestBody,
            totalAmount = totalAmountRequestBody,
            chosenDate = chosenDateRequestBody,
            pdf = pdfPart
        )
    }

    // Function to download the bill
    suspend fun downloadBill(token: String, houseId: String, billingDate: String): File? {
        return try {
            val response = apiService.downloadBill("Bearer $token", houseId, billingDate)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    saveFileToDownloads(body, "bill_${billingDate}.pdf")
                }
            } else {
                Log.e("BillRepository", "Failed to download bill: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("BillRepository", "Download exception: ${e.localizedMessage}")
            e.printStackTrace()
            null
        }
    }

    private fun saveFileToDownloads(body: ResponseBody, fileName: String): File? {
        val sanitizedFileName = fileName.replace(":", "_")
        return try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                sanitizedFileName
            )
            val inputStream: InputStream = body.byteStream()
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("BillRepository", "File saved to: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e("BillRepository", "Error saving file: ${e.localizedMessage}")
            e.printStackTrace()
            null
        }
    }

    // Function to fetch tenant details for a house
    suspend fun getTenants(token: String, houseId: String): Response<List<Tenant>> {
        val requestBody = mapOf("houseId" to houseId)
        return apiService.getTenants(token, requestBody)
    }

    // Function to send manual notification
    suspend fun sendManualNotification(token: String, request: Map<String, String>): Response<Map<String, String>> {
        return try {
            val response = apiService.sendManualNotification("Bearer $token", request)
            if (response.isSuccessful) {
                Log.d("BillRepository", "Notification sent successfully: ${response.body()}")
            } else {
                //Log.e("BillRepository", "Failed to send notification: ${response.message()}")
            }
            response
        } catch (e: Exception) {
            Log.e("BillRepository", "Exception occurred while sending notification: ${e.localizedMessage}")
            throw e
        }
    }
}

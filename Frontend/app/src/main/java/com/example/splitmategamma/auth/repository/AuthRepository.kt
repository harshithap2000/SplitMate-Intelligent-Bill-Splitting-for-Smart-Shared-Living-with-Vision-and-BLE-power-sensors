package com.example.splitmategamma.auth.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.splitmategamma.auth.model.House
import com.example.splitmategamma.auth.model.LoginResponse
import com.example.splitmategamma.auth.model.RegisterUserResponse
import com.example.splitmategamma.auth.model.UpdateUserRequest
import com.example.splitmategamma.auth.model.User
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager
import com.example.splitmategamma.utils.Constants
import com.example.splitmategamma.utils.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.io.InputStream

class AuthRepository(private val apiService: ApiService, private val preferences: SharedPreferences) {

    suspend fun loginUser(credentials: Map<String, String>): Response<LoginResponse> {
        val response = apiService.loginUser(credentials)
        if (response.isSuccessful) {
            val loginResponse = response.body()
            if (loginResponse != null) {
                PreferenceManager.saveUserToken(loginResponse.token) // Save the token
                return Response.success(loginResponse)
            } else {
                Log.e("AuthRepository", "Login response is null")
            }
        } else {
            Log.e("AuthRepository", "${Constants.ERROR_LOGIN_FAILED}: ${response.message()}")
        }
        return response
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        role: String,
        houseName: String?,
        houseAddress: String?,
        houseId: String?,
        photoUri: Uri?,
        context: Context
    ): Response<RegisterUserResponse> {
        val namePart = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val emailPart = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val passwordPart = RequestBody.create("text/plain".toMediaTypeOrNull(), password)
        val rolePart = RequestBody.create("text/plain".toMediaTypeOrNull(), role)

        val photoPart: MultipartBody.Part? = photoUri?.let { uri ->
            val file = File(FileUtils.getPath(context, uri))
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        val houseNamePart = houseName?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
        val houseAddressPart = houseAddress?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
        val houseIdPart = houseId?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it) }

        return apiService.registerUser(namePart, emailPart, passwordPart, rolePart, houseNamePart, houseAddressPart, houseIdPart, photoPart)
    }

    suspend fun fetchHouses(): Response<List<House>> {
        val response = apiService.fetchHouses()

        // Log the response body for debugging
        if (response.isSuccessful) {
            Log.d("FetchHouses", "Fetched Houses: ${response.body()}")
        } else {
            Log.e("FetchHouses", "Error: ${response.message()}")
        }

        return response
    }

    private fun convertImageToBase64(context: Context, uri: Uri): String? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val byteArray = inputStream?.readBytes()
        return byteArray?.let { Base64.encodeToString(it, Base64.DEFAULT) }
    }
}
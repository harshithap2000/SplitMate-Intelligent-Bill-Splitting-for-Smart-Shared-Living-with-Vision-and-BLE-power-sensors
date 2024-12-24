package com.example.splitmategamma.network

import com.example.splitmategamma.auth.model.AddHouse
import com.example.splitmategamma.auth.model.House
import com.example.splitmategamma.auth.model.LoginResponse
import com.example.splitmategamma.auth.model.RegisterUserResponse
import com.example.splitmategamma.auth.model.UpdateUserRequest
import com.example.splitmategamma.auth.model.User
import com.example.splitmategamma.dashboard.bill.model.BillResponse
import com.example.splitmategamma.dashboard.principalTenant.notiification.model.NotificationResponse
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.model.RemoveTenantRequest
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.model.Tenant
import com.example.splitmategamma.dashboard.principalTenant.utility.model.HouseRequest
import com.example.splitmategamma.dashboard.principalTenant.utility.model.UtilitiesListResponse
import com.example.splitmategamma.dashboard.principalTenant.utility.model.Utility
import com.example.splitmategamma.dashboard.principalTenant.utility.model.UtilityDeleteResponse
import com.example.splitmategamma.dashboard.principalTenant.utility.model.UtilityRegisterRequest
import com.example.splitmategamma.dashboard.principalTenant.utility.model.UtilityRegisterResponse
import com.example.splitmategamma.dashboard.principalTenant.utility.model.UtilityUpdateResponse
import com.example.splitmategamma.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.DELETE
import retrofit2.http.PATCH

interface ApiService {

    //Auth
    @Multipart
    @POST(Constants.API_REGISTER)
    suspend fun registerUser(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("role") role: RequestBody,
        @Part("houseName") houseName: RequestBody?,
        @Part("houseAddress") houseAddress: RequestBody?,
        @Part("houseId") houseId: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<RegisterUserResponse>

    @POST(Constants.API_LOGIN)
    suspend fun loginUser(
        @Body credentials: Map<String, String>
    ): Response<LoginResponse>

    //Users
    @GET(Constants.API_GET_USER)
    suspend fun getUserById(
        @Path("id") userId: String,
        @Header("Authorization") token: String
    ): Response<User>

    @PUT(Constants.API_UPDATE_USER)
    suspend fun updateUserProfile(
        @Path("id") userId: String,
        @Header("Authorization") token: String,
        @Body request: UpdateUserRequest
    ): Response<Void>

    @GET(Constants.API_GET_USER_PROFILE)
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<User>

    @POST(Constants.API_GET_TENANTS)
    suspend fun getTenants(
        @Header("Authorization") token: String,
        @Body houseId: Map<String, String>
    ): Response<List<Tenant>>

    @PUT(Constants.API_REMOVE_TENANT)
    suspend fun removeTenant(
        @Header("Authorization") token: String,
        @Body request: RemoveTenantRequest
    ): Response<Void>

    //Utilities
    @POST(Constants.API_GET_UTILITIES)
    suspend fun getUtilities(
        @Header("Authorization") token: String,
        @Body request: HouseRequest
    ): Response<UtilitiesListResponse>

    @POST(Constants.API_REGISTER_UTILITY)
    suspend fun registerUtility(
        @Header("Authorization") token: String,
        @Body utility: UtilityRegisterRequest
    ): Response<UtilityRegisterResponse>

    @PUT(Constants.API_UPDATE_UTILITY)
    suspend fun updateUtility(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body utility: Utility
    ): Response<UtilityUpdateResponse>

    @DELETE(Constants.API_DELETE_UTILITY)
    suspend fun deleteUtility(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<UtilityDeleteResponse>

    @POST(Constants.API_BILLS)
    suspend fun getBill(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, String>
    ): Response<BillResponse>

    @POST(Constants.API_PAY)
    suspend fun payBill(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, String>
    ): Response<Map<String, String>>


    @Multipart
    @POST(Constants.API_UPLOAD_BILL)
    suspend fun uploadBill(
        @Header("Authorization") token: String,
        @Part("houseId") houseId: RequestBody,
        @Part("totalAmount") totalAmount: RequestBody,
        @Part("chosenDate") chosenDate: RequestBody,
        @Part pdf: MultipartBody.Part
    ): Response<Map<String, String>>

    @GET(Constants.API_DOWNLOAD_BILL)
    suspend fun downloadBill(
        @Header("Authorization") token: String,
        @Path("houseId") houseId: String,
        @Path("billingDate") billingDate: String
    ): Response<ResponseBody>

    //House
    @GET(Constants.API_HOUSES)
    suspend fun fetchHouses(): Response<List<House>>

    @GET(Constants.API_GET_HOUSES)
    suspend fun getHouses(
        @Header("Authorization") token: String
    ): Response<List<House>>

    @POST(Constants.API_ADD_HOUSE)
    suspend fun addHouse(
        @Header("Authorization") token: String,
        @Body house: AddHouse
    ): Response<House>

    // Notifications
    @POST(Constants.API_NOTIFICATIONS_MANUAL)
    suspend fun sendManualNotification(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<Map<String, String>>

    @GET(Constants.API_NOTIFICATIONS)
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): Response<List<NotificationResponse>>

    @PATCH(Constants.API_NOTIFICATIONS_READ)
    suspend fun markNotificationAsRead(
        @Header("Authorization") token: String,
        @Path("notificationId") notificationId: String
    ): Response<Map<String, String>>

    @PATCH(Constants.API_NOTIFICATIONS_DISMISS)
    suspend fun dismissNotification(
        @Header("Authorization") token: String,
        @Path("notificationId") notificationId: String
    ): Response<Map<String, String>>

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
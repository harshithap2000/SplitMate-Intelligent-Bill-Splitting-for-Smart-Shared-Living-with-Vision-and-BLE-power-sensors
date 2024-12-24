package com.example.splitmategamma.utils

object Constants {
    const val BASE_URL = "http://54.162.214.191:5000/"
    const val ROLE_PRINCIPAL = "p"
    const val ROLE_REGULAR = "n"

    // API Endpoints
    const val API_LOGIN = "api/users/login"
    const val API_REGISTER = "api/users/register"

    const val API_HOUSES = "api/housing/houses"
    const val API_GET_HOUSES = "api/housing/getHouses"

    const val API_GET_USER = "api/users/{id}"
    const val API_GET_TENANTS = "api/users/tenants"
    const val API_REMOVE_TENANT = "api/users/removeTenant"
    const val API_UPDATE_USER = "api/users/{id}"
    const val API_GET_USER_PROFILE = "api/users/me"
    const val API_ADD_HOUSE = "api/users/addHouse"

    const val API_REGISTER_UTILITY = "api/utilities/register"
    const val API_GET_UTILITIES = "api/utilities/all"
    const val API_UPDATE_UTILITY = "api/utilities/update/{id}"
    const val API_DELETE_UTILITY = "api/utilities/delete/{id}"
    const val API_BILLS = "api/bills"
    const val API_UPLOAD_BILL = "api/bills/upload"
    const val API_DOWNLOAD_BILL = "api/bills/download/{houseId}/{billingDate}"
    const val API_PAY = "api/bills/pay"

    const val API_NOTIFICATIONS_MANUAL = "api/notifications/manual"
    const val API_NOTIFICATIONS = "api/notifications"
    const val API_NOTIFICATIONS_READ = "api/notifications/read/{notificationId}"
    const val API_NOTIFICATIONS_DISMISS = "api/notifications/dismiss/{notificationId}"

    // Error Messages
    const val ERROR_LOGIN_FAILED = "Login failed"
    const val ERROR_FETCH_USER = "Failed to fetch user details"

    // Default Values
    const val DEFAULT_EMAIL = ""
    const val DEFAULT_PASSWORD = ""
    const val DEFAULT_NAME = ""
    const val DEFAULT_ROLE = ""
    const val DEFAULT_HOUSE_ID = ""
    val DEFAULT_SELECTED_IMAGE_URI = null

    // Preferences Keys
    const val PREFS_USER_EMAIL = "email"
    const val PREFS_USER_PASSWORD = "password"
}
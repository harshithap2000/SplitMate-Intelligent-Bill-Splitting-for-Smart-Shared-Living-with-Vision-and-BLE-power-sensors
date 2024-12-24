package com.example.splitmategamma.network

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREFS_NAME = "AppPreferences"
    private const val USER_TOKEN_KEY = "user_token"
    private const val USER_ID_KEY = "user_id"
    private const val USER_ROLE_KEY = "user_role"
    private const val HOUSE_ID_KEY = "house_id" // For registration
    private const val SELECTED_HOUSE_ID_KEY = "selected_house_id" // For selected house by principal tenant

    private lateinit var preferences: SharedPreferences

    fun initialize(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Existing methods...

    // For saving house during registration
    fun saveHouseId(houseId: String) {
        val editor = preferences.edit()
        editor.putString(HOUSE_ID_KEY, houseId)
        editor.apply()
    }

    fun getHouseId(): String? {
        return preferences.getString(HOUSE_ID_KEY, null)
    }

    // For storing selected house by principal tenant
    fun saveSelectedHouseId(selectedHouseId: String) {
        val editor = preferences.edit()
        editor.putString(SELECTED_HOUSE_ID_KEY, selectedHouseId)
        editor.apply()
    }

    fun getSelectedHouseId(): String? {
        return preferences.getString(SELECTED_HOUSE_ID_KEY, null)
    }

    fun getUserToken(): String {
        return preferences.getString(USER_TOKEN_KEY, null) ?: ""
    }

    fun saveUserToken(token: String) {
        val editor = preferences.edit()
        editor.putString(USER_TOKEN_KEY, token)
        editor.apply()
    }

    fun getPreferences(): SharedPreferences {
        return preferences
    }

    fun saveUserId(_id: String) {
        val editor = preferences.edit()
        editor.putString(USER_ID_KEY, _id)
        editor.apply()
    }

    fun saveUserRole(role: String) {
        val editor = preferences.edit()
        editor.putString(USER_ROLE_KEY, role)
        editor.apply()
    }

    fun getUserRole(context: Context): String {
        return preferences.getString(USER_ROLE_KEY, null) ?: ""
    }
}

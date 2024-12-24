package com.example.splitmategamma.core

import android.app.Application
import com.example.splitmategamma.network.PreferenceManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize PreferenceManager with the application context
        PreferenceManager.initialize(applicationContext)
    }
}
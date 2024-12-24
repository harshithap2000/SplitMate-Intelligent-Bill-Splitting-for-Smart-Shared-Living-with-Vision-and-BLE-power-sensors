package com.example.splitmategamma.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    // Format Date to UTC string to send to the backend
    fun getUtcFormattedDate(date: Date): String {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormatter.format(date)
    }

    // Convert UTC string to Local Time for displaying to users
    fun convertUtcToLocal(utcTime: String): String {
        val utcFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        utcFormatter.timeZone = TimeZone.getTimeZone("UTC")

        val date = utcFormatter.parse(utcTime)
        val localFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        localFormatter.timeZone = TimeZone.getDefault() // Converts to the local timezone

        return localFormatter.format(date!!)
    }
}

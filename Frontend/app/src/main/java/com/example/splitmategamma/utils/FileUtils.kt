package com.example.splitmategamma.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import java.io.File

object FileUtils {
    // Button Colors
    val buttonColor = Color(0xFF002021)

    // Text Color
    val textColorWhite = Color.White

    fun getPath(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(MediaStore.Images.Media.DATA)
                filePath = if (index != -1) {
                    it.getString(index)
                } else {
                    null
                }
            }
        }
        return filePath
    }

}

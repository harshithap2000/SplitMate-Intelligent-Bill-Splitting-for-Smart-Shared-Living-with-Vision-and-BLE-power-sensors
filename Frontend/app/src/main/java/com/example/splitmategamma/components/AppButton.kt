package com.example.splitmategamma.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitmategamma.utils.FileUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = FileUtils.buttonColor,
    contentColor: Color = FileUtils.textColorWhite,
    height: Int = 55,
    fontSize: Int = 18
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            color = contentColor,
            style = TextStyle(fontSize = fontSize.sp)
        )
    }
}
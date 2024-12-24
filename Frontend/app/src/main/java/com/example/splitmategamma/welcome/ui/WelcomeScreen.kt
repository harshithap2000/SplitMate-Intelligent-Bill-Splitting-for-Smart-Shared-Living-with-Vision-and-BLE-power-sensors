package com.example.splitmategamma.welcome.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.splitmategamma.ui.components.AppButton
import com.example.splitmategamma.components.AppSpacer
import com.example.splitmategamma.ui.components.AppText

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = buildAnnotatedString {
                append("SplitMa")
                withStyle(style = SpanStyle(fontSize = 50.sp, color = Color(0xFF002021), fontWeight = FontWeight.W300)) {
                    append("Γ")
                }
                withStyle(style = SpanStyle(letterSpacing = (-0.05).em)) {
                    append("e")
                }
            },
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Thin,
                textAlign = TextAlign.Center,
                fontSize = 36.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )

        AppSpacer(height = 32)

        AppButton(
            text = "Create Account",
            onClick = { navController.navigate("signup") }
        )

        AppSpacer(height = 16)

        AppText(
            text = "Already have an account?",
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        AppSpacer(height = 16)

        AppButton(
            text = "Log in",
            onClick = { navController.navigate("login") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(navController = rememberNavController())
}
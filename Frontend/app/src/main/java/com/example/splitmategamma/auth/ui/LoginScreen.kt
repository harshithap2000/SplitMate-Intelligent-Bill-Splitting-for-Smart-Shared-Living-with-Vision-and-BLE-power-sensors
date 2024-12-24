package com.example.splitmategamma.auth.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.splitmategamma.auth.repository.AuthRepository
import com.example.splitmategamma.auth.viewmodel.AuthViewModel
import com.example.splitmategamma.auth.viewmodel.AuthViewModelFactory
import com.example.splitmategamma.core.Routes
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager
import com.example.splitmategamma.ui.components.AppButton
import com.example.splitmategamma.ui.components.AppTextField
import com.example.splitmategamma.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    PreferenceManager.initialize(context)
    val preferences = PreferenceManager.getPreferences()

    val authRepository = AuthRepository(ApiService.create(), preferences)
    val viewModelFactory = AuthViewModelFactory(authRepository)
    val viewModel: AuthViewModel = viewModel(factory = viewModelFactory)

    var email by remember { mutableStateOf(Constants.DEFAULT_EMAIL) }
    var password by remember { mutableStateOf(Constants.DEFAULT_PASSWORD) }
    var showPassword by remember { mutableStateOf(false) }

    // Update the navigation from LoginScreen
    fun handleLogin() {
        viewModel.email = email
        viewModel.password = password
        viewModel.loginUser(
            onSuccess = { token, authUser ->
                // Save token and relevant details
                PreferenceManager.saveUserToken(token)
                PreferenceManager.saveUserId(authUser._id)
                PreferenceManager.saveUserRole(authUser.role)

                // Handle houseId based on tenant type (List for principal, String for normal tenant)
                when (val houseId = authUser.houseId) {
                    is List<*> -> {
                        if (authUser.role == Constants.ROLE_PRINCIPAL && houseId.isNotEmpty()) {
                            PreferenceManager.saveHouseId(houseId.first().toString()) // Save the first house in the list by default
                        }
                    }
                    is String -> PreferenceManager.saveHouseId(houseId) // Save for normal tenant
                    else -> Toast.makeText(context, "Unknown houseId format", Toast.LENGTH_SHORT).show()
                }

                // Navigate based on role
                when (authUser.role) {
                    Constants.ROLE_PRINCIPAL -> {
                        // Pass null for houseId when logging in
                        navController.navigate(Routes.PRINCIPAL_DASHBOARD + "/null")
                    }
                    Constants.ROLE_REGULAR -> navController.navigate(Routes.REGULAR_DASHBOARD)
                    else -> Toast.makeText(context, "Unknown role: ${authUser.role}", Toast.LENGTH_SHORT).show()
                }
            },
            onError = { message ->
                Toast.makeText(context, "${Constants.ERROR_LOGIN_FAILED}: $message", Toast.LENGTH_SHORT).show()
            }
        )
    }


    // UI for login screen
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(imageVector = image, contentDescription = if (showPassword) "Hide password" else "Show password")
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            AppButton(
                text = "Log in",
                onClick = { handleLogin() }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = NavController(LocalContext.current))
}
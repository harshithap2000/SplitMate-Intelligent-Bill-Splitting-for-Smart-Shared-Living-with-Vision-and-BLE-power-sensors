package com.example.splitmategamma.auth.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.splitmategamma.auth.viewmodel.AuthViewModel
import com.example.splitmategamma.auth.viewmodel.AuthViewModelFactory
import com.example.splitmategamma.auth.repository.AuthRepository
import com.example.splitmategamma.core.Routes
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.ui.components.AppButton
import com.example.splitmategamma.components.AppSpacer
import com.example.splitmategamma.ui.components.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    val authRepository = AuthRepository(ApiService.create(), sharedPreferences)
    val viewModelFactory = AuthViewModelFactory(authRepository)
    val viewModel: AuthViewModel = viewModel(factory = viewModelFactory)

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var houseName by remember { mutableStateOf("") }
    var houseAddress by remember { mutableStateOf("") }
    var houseId by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val houses by viewModel.houses.collectAsState()
    val roles = listOf("Principal Tenant" to "p", "Normal Tenant" to "n")
    var roleDropdownExpanded by remember { mutableStateOf(false) }
    var houseDropdownExpanded by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Fetch houses if the user selects "Normal Tenant"
    LaunchedEffect(role) {
        if (role == "n") {
            viewModel.fetchHouses(onError = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            })
            Log.d("SignupScreen", "Houses fetched: ${viewModel.houses.value}")
        }
    }

    // Function to handle signup based on the role
    fun handleSignup() {
        viewModel.signupUser(
            name = name,
            email = email,
            password = password,
            role = role,
            houseName = houseName.takeIf { role == "p" }, // For Principal Tenant
            houseAddress = houseAddress.takeIf { role == "p" }, // For Principal Tenant
            houseId = houseId.takeIf { role == "n" }, // For Normal Tenant
            imageUri = selectedImageUri,
            context = context,
            onSuccess = { response ->
                Toast.makeText(context, "Signup successful: ${response.name}", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.LOGIN)
            },
            onError = { errorMsg ->
                Toast.makeText(context, "Signup failed: $errorMsg", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // TopAppBar with back button
        TopAppBar(
            title = { Text("") }, // Empty title
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
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Name"
            )
            AppSpacer(height = 16)
            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                keyboardType = KeyboardType.Email
            )
            AppSpacer(height = 16)
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
            AppSpacer(height = 16)

            // Role Dropdown
            ExposedDropdownMenuBox(
                expanded = roleDropdownExpanded,
                onExpandedChange = { roleDropdownExpanded = it }
            ) {
                TextField(
                    value = roles.find { it.second == role }?.first ?: "Select Role",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Role") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .clickable { roleDropdownExpanded = !roleDropdownExpanded }
                )
                ExposedDropdownMenu(
                    expanded = roleDropdownExpanded,
                    onDismissRequest = { roleDropdownExpanded = false }
                ) {
                    roles.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.first) },
                            onClick = {
                                role = item.second
                                roleDropdownExpanded = false
                                Log.d("SignupScreen", "Role selected: ${item.first}")
                            }
                        )
                    }
                }
            }

            if (role == "p") {
                // House Name and Address for Principal Tenant
                AppSpacer(height = 16)
                AppTextField(
                    value = houseName,
                    onValueChange = { houseName = it },
                    label = "House Name"
                )
                AppSpacer(height = 16)
                AppTextField(
                    value = houseAddress,
                    onValueChange = { houseAddress = it },
                    label = "House Address"
                )
            } else if (role == "n") {
                // Dropdown for Normal Tenant to select a house
                AppSpacer(height = 16)
                ExposedDropdownMenuBox(
                    expanded = houseDropdownExpanded,
                    onExpandedChange = { houseDropdownExpanded = it }
                ) {
                    TextField(
                        value = houses.find { it._id == houseId }?.name ?: "Select House",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select House") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = houseDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clickable { houseDropdownExpanded = !houseDropdownExpanded }
                    )
                    ExposedDropdownMenu(
                        expanded = houseDropdownExpanded,
                        onDismissRequest = { houseDropdownExpanded = false }
                    ) {
                        houses.forEach { house ->
                            DropdownMenuItem(
                                text = { Text(house.name) },
                                onClick = {
                                    houseId = house._id
                                    houseDropdownExpanded = false
                                    Log.d("SignupScreen", "Selected house ID: $houseId")
                                }
                            )
                        }
                    }
                }
            }

            AppSpacer(height = 16)
            AppButton(
                text = "Upload Photo ID",
                onClick = { galleryLauncher.launch(PickVisualMediaRequest()) }
            )

            AppSpacer(height = 16)

            selectedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .padding(16.dp)
                )
            }

            AppButton(
                text = "Sign Up",
                onClick = { handleSignup() }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SignupScreenPreview() {
    SignupScreen(navController = NavController(LocalContext.current))
}
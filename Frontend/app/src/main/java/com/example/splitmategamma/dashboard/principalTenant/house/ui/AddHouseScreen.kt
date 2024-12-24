package com.example.splitmategamma.dashboard.principalTenant.house.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import com.example.splitmategamma.dashboard.principalTenant.home.repository.PrincipalRepository
import com.example.splitmategamma.dashboard.principalTenant.home.viewmodel.PrincipalViewModel
import com.example.splitmategamma.dashboard.principalTenant.home.viewmodel.PrincipalViewModelFactory
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHouseScreen(navController: NavController) {
    val context = LocalContext.current
    PreferenceManager.initialize(context)
    val token = PreferenceManager.getUserToken()

    val principalRepository = PrincipalRepository(ApiService.create())
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    val viewModel: PrincipalViewModel = ViewModelProvider(
        viewModelStoreOwner!!,
        PrincipalViewModelFactory(principalRepository)
    )[PrincipalViewModel::class.java]

    var houseName by remember { mutableStateOf(TextFieldValue("")) }
    var houseAddress by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add House") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = houseName,
                    onValueChange = { houseName = it },
                    label = { Text("House Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = houseAddress,
                    onValueChange = { houseAddress = it },
                    label = { Text("House Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        // Validate fields before calling addHouse
                        if (houseName.text.isBlank() || houseAddress.text.isBlank()) {
                            Toast.makeText(context, "House name and address are required", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addHouse(
                                token = token,
                                houseName = houseName.text,
                                houseAddress = houseAddress.text,
                                onSuccess = {
                                    // Navigate back to the house list after adding
                                    navController.popBackStack()
                                },
                                onError = { errorMessage ->
                                    // Show an error message if adding fails
                                    Toast.makeText(context, "Failed to add house: $errorMessage", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add House")
                }
            }
        }
    )
}
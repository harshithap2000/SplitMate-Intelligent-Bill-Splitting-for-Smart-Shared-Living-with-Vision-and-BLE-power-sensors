package com.example.splitmategamma.dashboard.principalTenant.utility.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.splitmategamma.dashboard.principalTenant.common.PrincipalBottomNavigationBar
import com.example.splitmategamma.dashboard.principalTenant.utility.model.Utilities
import com.example.splitmategamma.dashboard.principalTenant.utility.repository.UtilityRepository
import com.example.splitmategamma.dashboard.principalTenant.utility.viewmodel.UtilityViewModel
import com.example.splitmategamma.dashboard.principalTenant.utility.viewmodel.UtilityViewModelFactory
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityManagementScreen(navController: NavController) {
    val context = LocalContext.current
    PreferenceManager.initialize(context)

    val utilityRepository = UtilityRepository(ApiService.create())
    val viewModel: UtilityViewModel = viewModel(factory = UtilityViewModelFactory(utilityRepository))

    val utilities by viewModel.utilities.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUtilities()
    }

    if (error != null) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Utility Management", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("register_utility") }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Utility",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            PrincipalBottomNavigationBar(navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (utilities.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No utilities available. Please add a new utility.")
                    }
                }
            } else {
                items(utilities) { utility ->
                    UtilityItem(
                        utility = utility,
                        onUpdate = { newName, newType, newSensor ->
                            viewModel.updateUtility(
                                id = utility._id,
                                name = newName,
                                type = newType,
                                sensor = newSensor,
                                onSuccess = { Toast.makeText(context, "Utility updated", Toast.LENGTH_SHORT).show() },
                                onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                            )
                        },
                        onDelete = {
                            viewModel.deleteUtility(
                                id = utility._id,
                                onSuccess = { Toast.makeText(context, "Utility deleted", Toast.LENGTH_SHORT).show() },
                                onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityItem(
    utility: Utilities,
    onUpdate: (String, String, String) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var updatedName by remember { mutableStateOf(utility.name) }
    var updatedType by remember { mutableStateOf(utility.type) }
    var updatedSensor by remember { mutableStateOf(utility.sensor) }
    var expanded by remember { mutableStateOf(false) }

    val options = listOf("electric", "gas", "water")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = updatedName,
                    onValueChange = { updatedName = it },
                    label = { Text("Utility Name") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = updatedType,
                        onValueChange = {},
                        label = { Text("Utility Type") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clickable { expanded = !expanded },
                        textStyle = MaterialTheme.typography.bodyLarge
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                                onClick = {
                                    updatedType = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = updatedSensor,
                    onValueChange = { updatedSensor = it },
                    label = { Text("Utility Sensor") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save and Delete buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onUpdate(updatedName, updatedType, updatedSensor)
                            isEditing = false
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save", style = MaterialTheme.typography.bodyLarge)
                    }

                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                Column(
                    Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = utility.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Type: ${utility.type} | Sensor: ${utility.sensor}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { isEditing = true },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Edit", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Preview (showSystemUi = true)
@Composable
fun UtilityManagementScreenPreview() {
    UtilityManagementScreen(navController = rememberNavController())
}
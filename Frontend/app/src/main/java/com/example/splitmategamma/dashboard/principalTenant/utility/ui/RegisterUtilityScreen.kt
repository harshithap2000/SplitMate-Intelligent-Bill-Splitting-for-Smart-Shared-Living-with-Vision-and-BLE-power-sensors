package com.example.splitmategamma.dashboard.principalTenant.utility.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.splitmategamma.dashboard.principalTenant.common.PrincipalBottomNavigationBar
import com.example.splitmategamma.dashboard.principalTenant.utility.repository.UtilityRepository
import com.example.splitmategamma.dashboard.principalTenant.utility.viewmodel.UtilityViewModel
import com.example.splitmategamma.dashboard.principalTenant.utility.viewmodel.UtilityViewModelFactory
import com.example.splitmategamma.network.ApiService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUtilityScreen(navController: NavController) {
    val context = LocalContext.current
    val utilityRepository = UtilityRepository(ApiService.create())
    val viewModel: UtilityViewModel = viewModel(factory = UtilityViewModelFactory(utilityRepository))

    var isScanning by remember { mutableStateOf(false) }
    var utilityName by remember { mutableStateOf(TextFieldValue("")) }
    var utilityType by remember { mutableStateOf("") }
    var selectedSensor by remember { mutableStateOf<BluetoothDevice?>(null) }
    val bleAdapter = BluetoothAdapter.getDefaultAdapter()

    val options = listOf("electric", "gas", "water")
    var expanded by remember { mutableStateOf(false) }

    // State to keep track of available devices (Classic and BLE)
    val availableDevices = remember { mutableStateListOf<BluetoothDevice>() }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            if (!it.value) {
                Toast.makeText(context, "Permission ${it.key} is required for Bluetooth operations", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun hasBluetoothPermissions(context: Context): Boolean {
        val bluetoothPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
        val locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

        return bluetoothPermission == PackageManager.PERMISSION_GRANTED &&
                locationPermission == PackageManager.PERMISSION_GRANTED
    }

    fun scanForDevices(context: Context) {
        if (hasBluetoothPermissions(context)) {
            try {
                if (bleAdapter?.isDiscovering == false) {
                    isScanning = true  // Set scanning state to true
                    bleAdapter?.startDiscovery()
                    Log.d("RegisterUtility", "Started Classic Bluetooth device discovery")
                }
            } catch (e: SecurityException) {
                Log.e("RegisterUtility", "Bluetooth operation failed: ${e.message}")
                Toast.makeText(context, "Permission not granted for Bluetooth operation", Toast.LENGTH_SHORT).show()
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    // BroadcastReceiver for Bluetooth Classic device discovery
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action ?: return
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        try {
                            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            device?.let {
                                Log.d("RegisterUtility", "Discovered Classic Bluetooth device: ${it.name ?: "Unnamed Device"} (${it.address})")
                                if (it !in availableDevices) {
                                    availableDevices.add(it)
                                }
                            }
                        } catch (e: SecurityException) {
                            Log.e("RegisterUtility", "Bluetooth operation failed: ${e.message}")
                            Toast.makeText(context, "Permission not granted for Bluetooth operation", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Bluetooth permission not granted.", Toast.LENGTH_SHORT).show()
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    isScanning = false // Reset scanning state when discovery finishes
                }
            }
        }
    }

    DisposableEffect(Unit) {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(receiver, filter)
        Log.d("RegisterUtility", "BroadcastReceiver registered for Classic Bluetooth discovery")

        onDispose {
            context.unregisterReceiver(receiver)
            Log.d("RegisterUtility", "BroadcastReceiver unregistered")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register Utility", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            PrincipalBottomNavigationBar(navController)
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Start scanning button
                    Text("Select Bluetooth Sensor", style = MaterialTheme.typography.titleMedium)
                    Button(onClick = { scanForDevices(context) }) {
                        Icon(imageVector = Icons.Outlined.Bluetooth, contentDescription = "Scan")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan for Devices")
                    }

                    // Show progress indicator while scanning
                    if (isScanning) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Text("Scanning...", style = MaterialTheme.typography.bodySmall)
                    }
                }

                if (availableDevices.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, MaterialTheme.colorScheme.primary)
                                .padding(16.dp)
                                .heightIn(max = 250.dp) // Added height constraint
                        ) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(availableDevices) { device ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                Log.d("RegisterUtility", "Selected device: ${device.name ?: "Unnamed Device"} (${device.address})")
                                                selectedSensor = device
                                            }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${device.name ?: "Unnamed Device"} (${device.address})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                selectedSensor?.let {
                    item {
                        Text(
                            text = "Selected Sensor: ${it.name ?: "Unnamed Device"} (${it.address})",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = utilityName,
                        onValueChange = { utilityName = it },
                        label = { Text("Utility Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = utilityType,
                            onValueChange = {},
                            label = { Text("Utility Type") },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable { expanded = !expanded }
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        utilityType = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            if (utilityName.text.isNotBlank() && utilityType.isNotBlank() && selectedSensor != null) {
                                viewModel.registerUtility(
                                    name = utilityName.text,
                                    type = utilityType,
                                    sensor = selectedSensor!!.address,
                                    onSuccess = {
                                        Toast.makeText(context, "Utility registered successfully", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    },
                                    onError = {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = utilityName.text.isNotBlank() && utilityType.isNotBlank() && selectedSensor != null
                    ) {
                        Text("Register Utility")
                    }
                }
            }
        }
    )
}
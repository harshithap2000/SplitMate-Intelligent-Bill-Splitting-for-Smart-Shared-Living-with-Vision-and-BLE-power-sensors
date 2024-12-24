package com.example.splitmategamma.dashboard.bill.ui

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.splitmategamma.dashboard.bill.repository.BillRepository
import com.example.splitmategamma.dashboard.bill.viewmodel.BillViewModel
import com.example.splitmategamma.dashboard.bill.viewmodel.BillViewModelFactory
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadBillScreen(navController: NavController) {
    val context = LocalContext.current
    val apiService = ApiService.create()
    val billRepository = BillRepository(apiService)
    val viewModel: BillViewModel = viewModel(factory = BillViewModelFactory(billRepository))

    val token = PreferenceManager.getUserToken() ?: ""
    val houseId = PreferenceManager.getSelectedHouseId() ?: ""
    var totalAmount by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf("Select Month") }
    var expanded by remember { mutableStateOf(false) }
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var pdfFile: File? by remember { mutableStateOf(null) }

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                selectedPdfUri = it
                pdfFile = getFileFromUri(context, it)
                pdfFile?.let { file ->
                    Log.d("UploadBillScreen", "PDF selected: ${file.name}")
                    Toast.makeText(context, "PDF selected: ${file.name}", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Log.e("UploadBillScreen", "Failed to load PDF")
                    Toast.makeText(context, "Failed to load PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    val coroutineScope = rememberCoroutineScope()
    val uploadSuccess by viewModel.uploadSuccess.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Bill") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Total Amount Input
                OutlinedTextField(
                    value = totalAmount,
                    onValueChange = { totalAmount = it },
                    label = { Text("Total Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown for selecting billing month
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedMonth,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Month") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        months.forEach { month ->
                            DropdownMenuItem(
                                text = { Text(month) },
                                onClick = {
                                    selectedMonth = month
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // PDF Selection Button
                Button(
                    onClick = { pdfLauncher.launch("application/pdf") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select PDF")
                }

                selectedPdfUri?.let {
                    Text(text = "PDF selected: ${pdfFile?.name}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Upload Button
                Button(
                    onClick = {
                        if (token.isNotBlank() && houseId.isNotBlank() && pdfFile != null) {
                            val formattedDate = convertMonthToUTC(selectedMonth)
                            Log.d("UploadBillScreen", "Token: $token")
                            Log.d("UploadBillScreen", "House ID: $houseId")
                            Log.d("UploadBillScreen", "Total Amount: $totalAmount")
                            Log.d("UploadBillScreen", "Chosen Date: $formattedDate")
                            Log.d("UploadBillScreen", "PDF File Name: ${pdfFile?.name}")

                            if (formattedDate != null) {
                                coroutineScope.launch {
                                    viewModel.uploadBill(
                                        token = token,
                                        houseId = houseId,
                                        totalAmount = totalAmount,
                                        chosenDate = formattedDate,
                                        pdfFile = pdfFile!!
                                    )
                                }
                            } else {
                                Log.e("UploadBillScreen", "Invalid month selection")
                                Toast.makeText(context, "Please select a valid month", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.e("UploadBillScreen", "Missing input fields or PDF file")
                            Toast.makeText(context, "Fill all fields before submitting", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upload Bill")
                }

                // Display success or error message
                LaunchedEffect(uploadSuccess) {
                    uploadSuccess?.let {
                        if (it) {
                            Log.d("UploadBillScreen", "Bill uploaded successfully")
                            Toast.makeText(context, "Bill uploaded successfully!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                }

                LaunchedEffect(error) {
                    error?.let { errorMessage ->
                        Log.e("UploadBillScreen", "Error: $errorMessage")
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        viewModel.clearError()
                    }
                }
            }
        }
    )
}

// Helper function to convert selected month to UTC format
fun convertMonthToUTC(selectedMonth: String): String? {
    return try {
        val monthIndex = SimpleDateFormat("MMMM", Locale.ENGLISH).parse(selectedMonth)?.month ?: return null
        val calendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, monthIndex)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(calendar.time)
    } catch (e: Exception) {
        Log.e("UploadBillScreen", "Error formatting date: ${e.localizedMessage}")
        null
    }
}

// Helper function to get a file from URI
fun getFileFromUri(context: Context, uri: Uri): File? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst()) {
            val name = it.getString(nameIndex)
            val file = File(context.cacheDir, name)
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Log.d("UploadBillScreen", "File created successfully: $name")
            } catch (e: Exception) {
                Log.e("UploadBillScreen", "Error creating file from URI: ${e.localizedMessage}")
            }
            return file
        }
    }
    return null
}

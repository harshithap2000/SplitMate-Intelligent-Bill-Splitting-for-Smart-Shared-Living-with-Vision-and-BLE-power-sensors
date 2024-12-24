package com.example.splitmategamma.dashboard.bill.ui

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.splitmategamma.R
import com.example.splitmategamma.dashboard.bill.model.BillResponse
import com.example.splitmategamma.dashboard.bill.viewmodel.BillViewModel
import com.example.splitmategamma.dashboard.bill.repository.BillRepository
import com.example.splitmategamma.dashboard.bill.viewmodel.BillViewModelFactory
import com.example.splitmategamma.dashboard.principalTenant.common.PrincipalBottomNavigationBar
import com.example.splitmategamma.dashboard.principalTenant.tenantManagement.model.Tenant
import com.example.splitmategamma.dashboard.regularTenant.common.RegularBottomNavigationBar
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager
import com.example.splitmategamma.network.PreferenceManager.getUserRole
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BillScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService = ApiService.create()
    val billRepository = BillRepository(apiService)
    val viewModel: BillViewModel = viewModel(factory = BillViewModelFactory(billRepository))

    val billResponse by viewModel.billResponse.collectAsState()
    val error by viewModel.error.collectAsState()
    val payBillStatus by viewModel.payBillStatus.collectAsState()
    val tenants by viewModel.tenants.collectAsState(initial = emptyList())

    var isLoading by remember { mutableStateOf(true) }
    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    val userRole = getUserRole(context)

    // Fetch the bill for the selected month when it changes
    LaunchedEffect(selectedMonth) {
        isLoading = true
        val chosenDate = LocalDate.now().withMonth(selectedMonth).withDayOfMonth(1).toString() + "T00:00:00Z"
        val token = PreferenceManager.getUserToken() ?: ""
        val houseId = PreferenceManager.getSelectedHouseId() ?: ""
        viewModel.fetchBill(token, houseId, chosenDate)
        viewModel.fetchTenants(token, houseId) // Fetch tenant list
        isLoading = false
    }

    error?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bill Management", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    if (userRole == "p") {
                        IconButton(onClick = {
                            navController.navigate("upload_bill_screen")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Upload Bill",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            if (userRole == "p") {
                PrincipalBottomNavigationBar(navController)
            } else {
                RegularBottomNavigationBar(navController)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bill),
                    contentDescription = "Bill Icon",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                // Month Selector Dropdown
                MonthSelectorDropdown(selectedMonth) { selectedMonth = it }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )

                // Bill Details Card
                Card(
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total: $${String.format("%.2f", billResponse?.tenantBill?.totalAmount ?: 0.0)}",
                                fontSize = 28.sp,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "Status: ${billResponse?.tenantBill?.status ?: "Unknown"}",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        IconButton(onClick = {
                            val token = PreferenceManager.getUserToken() ?: ""
                            val houseId = PreferenceManager.getSelectedHouseId() ?: ""
                            val billingDate = LocalDate.now().withMonth(selectedMonth).withDayOfMonth(1).toString() + "T00:00:00Z"

                            coroutineScope.launch {
                                val file = viewModel.downloadBill(token, houseId, billingDate)
                                file?.let {
                                    Toast.makeText(context, "Download complete: ${it.name}", Toast.LENGTH_SHORT).show()
                                } ?: run {
                                    Toast.makeText(context, "Failed to download file", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download Bill",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("utility_usage_screen/$selectedMonth") },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Text("Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    if (billResponse?.tenantBill?.totalAmount != 0.0 && billResponse?.tenantBill?.status != "paid") {
                        Button(
                            onClick = {
                                val token = PreferenceManager.getUserToken() ?: ""
                                val houseId = PreferenceManager.getSelectedHouseId() ?: ""
                                val chosenDate = LocalDate.now().withMonth(selectedMonth).withDayOfMonth(1).toString() + "T00:00:00Z"

                                coroutineScope.launch {
                                    isLoading = true // Show loading indicator
                                    viewModel.payBill(token, houseId, chosenDate)
                                    isLoading = false // Hide loading indicator
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = MaterialTheme.shapes.medium,
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
                            Text("Pay Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Tenant List with Pending Bills Filtered by Month
                Spacer(modifier = Modifier.height(16.dp))

                // Check if there are any tenants with a role other than 'p' and a pending status
                val eligibleTenants = tenants.filter { tenant ->
                    val billMonth = billResponse?.tenantBill?.billingPeriod?.start?.substring(5, 7)?.toIntOrNull()
                    val isCorrectMonth = billMonth == selectedMonth
                    val isPending = billResponse?.tenantBill?.status == "pending" // Check for pending status
                    val isTenantAllowed = tenant.role != "p" // Ensure only non-principal tenants are shown
                    isCorrectMonth && isTenantAllowed && isPending
                }

                if (eligibleTenants.isNotEmpty()) {
                    Text(
                        text = "Tenants with Pending Bills",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(top = 8.dp)
                    ) {
                        items(eligibleTenants) { tenant ->
                            TenantCard(tenant, viewModel, billResponse)
                        }
                    }
                }
            }

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun TenantCard(tenant: Tenant, viewModel: BillViewModel, billResponse: BillResponse?) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var buttonText by remember { mutableStateOf("Remind") }
    val primaryColor = MaterialTheme.colorScheme.primary // Extract color outside of remember
    var buttonColor by remember { mutableStateOf(primaryColor) }

    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = tenant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "Pending: ₹${billResponse?.tenantBill?.totalAmount}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        val token = PreferenceManager.getUserToken() ?: return@launch
                        val notificationRequest = mapOf(
                            "toTenantId" to tenant._id,
                            "message" to "Please pay the bill."
                        )

                        viewModel.sendManualNotification(
                            token,
                            notificationRequest,
                            onSuccess = {
                                buttonText = "Sent"
                                buttonColor = Color.Green
                                Toast.makeText(context, "Notification sent successfully", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { errorMessage ->
                                buttonText = "Failed"
                                buttonColor = Color.Red
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Text(buttonText)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthSelectorDropdown(selectedMonth: Int, onMonthSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val months = (1..12).map { month -> LocalDate.of(2024, month, 1).month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = months[selectedMonth - 1],
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Month") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.width(200.dp).menuAnchor()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            months.forEachIndexed { index, monthName ->
                DropdownMenuItem(
                    text = { Text(monthName) },
                    onClick = {
                        onMonthSelected(index + 1)
                        expanded = false
                    }
                )
            }
        }
    }
}
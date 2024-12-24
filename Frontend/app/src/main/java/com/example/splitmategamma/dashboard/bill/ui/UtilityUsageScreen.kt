package com.example.splitmategamma.dashboard.bill.ui

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.splitmategamma.dashboard.bill.repository.BillRepository
import com.example.splitmategamma.dashboard.bill.viewmodel.BillViewModel
import com.example.splitmategamma.dashboard.bill.viewmodel.BillViewModelFactory
import com.example.splitmategamma.dashboard.principalTenant.common.PrincipalBottomNavigationBar
import com.example.splitmategamma.dashboard.regularTenant.common.RegularBottomNavigationBar
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager
import com.example.splitmategamma.network.PreferenceManager.getUserRole
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UtilityUsageScreen(navController: NavController, selectedMonth: Int) {
    val context = LocalContext.current
    val apiService = ApiService.create()

    val billRepository = BillRepository(apiService)
    val viewModel: BillViewModel = viewModel(factory = BillViewModelFactory(billRepository))
    val billResponse by viewModel.billResponse.collectAsState()
    val error by viewModel.error.collectAsState()

    val userRole = getUserRole(context)

    // Fetch the bill for the selected month using selectedMonth parameter
    LaunchedEffect(selectedMonth) {
        val chosenDate = LocalDate.now().withMonth(selectedMonth).withDayOfMonth(1).toString() + "T00:00:00Z"
        val token = PreferenceManager.getUserToken()
        val houseId = PreferenceManager.getSelectedHouseId()
        if (houseId != null) {
            viewModel.fetchBill(token, houseId, chosenDate)
        }
    }

    error?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Utility Usage", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onSurface)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (billResponse == null) {
                Text("Loading utility data...", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                val utilities = billResponse?.tenantBill?.utilities ?: emptyList()
                if (utilities.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(utilities) { utility ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable {
                                        // Navigate to the details screen with the utility name and chosen date
                                        navController.navigate("utility_details_screen/${utility.utilityName}/$selectedMonth")
                                    },
                                shape = MaterialTheme.shapes.medium,
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(
                                                painter = painterResource(id = getUtilityIcon(utility.utilityName)),
                                                contentDescription = utility.utilityName,
                                                modifier = Modifier.size(64.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(
                                                text = utility.utilityName,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = "$${String.format("%.2f", utility.totalCost)}",
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text("No utility data available for the selected month.", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}

fun getUtilityIcon(utilityName: String): Int {
    return when (utilityName.lowercase()) {
        "microwave" -> R.drawable.ic_microwave
        "fridge" -> R.drawable.ic_fridge
        "tv" -> R.drawable.ic_tv
        "laptop" -> R.drawable.ic_laptop
        else -> R.drawable.ic_default_utility
    }
}
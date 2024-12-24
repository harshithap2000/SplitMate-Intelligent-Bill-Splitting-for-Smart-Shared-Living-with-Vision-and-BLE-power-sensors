package com.example.splitmategamma.dashboard.bill.ui

import android.graphics.Color
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.splitmategamma.dashboard.bill.model.Usage
import com.example.splitmategamma.dashboard.bill.repository.BillRepository
import com.example.splitmategamma.dashboard.bill.viewmodel.BillViewModel
import com.example.splitmategamma.dashboard.bill.viewmodel.BillViewModelFactory
import com.example.splitmategamma.dashboard.principalTenant.common.PrincipalBottomNavigationBar
import com.example.splitmategamma.dashboard.regularTenant.common.RegularBottomNavigationBar
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager
import com.example.splitmategamma.network.PreferenceManager.getUserRole
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityDetailsScreen(navController: NavController, utilityName: String, selectedMonth: Int) {
    val context = LocalContext.current
    val apiService = ApiService.create()
    val billRepository = BillRepository(apiService)
    val viewModel: BillViewModel = viewModel(factory = BillViewModelFactory(billRepository))
    val billResponse by viewModel.billResponse.collectAsState()
    val error by viewModel.error.collectAsState()

    // Fetch bill for the selected month using chosenDate parameter
    val chosenDate = LocalDate.now().withMonth(selectedMonth).withDayOfMonth(1).toString() + "T00:00:00Z"
    val userRole = getUserRole(context)

    LaunchedEffect(chosenDate) {
        val token = PreferenceManager.getUserToken()
        val houseId = PreferenceManager.getSelectedHouseId()
        if (houseId != null) {
            viewModel.fetchBill(token, houseId, chosenDate)
        }
    }

    // Extract utility records for the selected utilityName
    val utilityRecords = billResponse?.tenantBill?.utilities?.find { it.utilityName == utilityName }?.usageRecords ?: emptyList()

    error?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$utilityName Usage Details", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (utilityRecords.isEmpty()) {
                Text(
                    text = "No usage data available for $utilityName",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 18.sp
                )
            } else {
                UsageGraphMPAndroidChart(usageRecords = utilityRecords)

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(utilityRecords) { record ->
                        UsageRecordCard(record = record)
                    }
                }
            }
        }
    }
}

@Composable
fun UsageGraphMPAndroidChart(usageRecords: List<Usage>) {
    AndroidView(factory = { context ->
        BarChart(context).apply {
            val entries = usageRecords.mapIndexed { index, record ->
                BarEntry(index.toFloat(), record.amount.toFloat())
            }

            val dataSet = BarDataSet(entries, "Usage Data").apply {
                color = Color.BLUE
                valueTextColor = Color.BLACK
                valueTextSize = 12f
            }

            val barData = BarData(dataSet)
            data = barData

            description.isEnabled = false
            axisRight.isEnabled = false

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.labelRotationAngle = -45f
            xAxis.valueFormatter = IndexAxisValueFormatter(usageRecords.map { it.date })

            axisLeft.granularity = 10f
            axisLeft.axisMinimum = 0f

            animateY(1000)
        }
    },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

@Composable
fun UsageRecordCard(record: Usage) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = record.date, fontSize = 16.sp)
            Text(text = "$${String.format("%.2f", record.amount)}", fontSize = 16.sp)
        }
    }
}
package com.example.splitmategamma.dashboard.regularTenant.home.ui

import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.splitmategamma.R
import com.example.splitmategamma.core.Routes
import com.example.splitmategamma.dashboard.bill.model.BillResponse
import com.example.splitmategamma.dashboard.bill.repository.BillRepository
import com.example.splitmategamma.dashboard.bill.viewmodel.BillViewModel
import com.example.splitmategamma.dashboard.bill.viewmodel.BillViewModelFactory
import com.example.splitmategamma.dashboard.regularTenant.common.RegularBottomNavigationBar
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegularDashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val viewModel: BillViewModel = viewModel(factory = BillViewModelFactory(BillRepository(ApiService.create())))

    val billResponse by viewModel.billResponse.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }

    // Fetch data based on selected month
    LaunchedEffect(selectedMonth) {
        val token = PreferenceManager.getUserToken() ?: ""
        val houseId = PreferenceManager.getSelectedHouseId() ?: ""
        val chosenDate = LocalDate.now().withMonth(selectedMonth).withDayOfMonth(1).toString() + "T00:00:00Z"
        if (token.isNotEmpty()) {
            viewModel.fetchBill(token, houseId, chosenDate)
        }
    }

    error?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        viewModel.clearError()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Regular Dashboard",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("principal_notification") }) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            RegularBottomNavigationBar(navController = navController)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Month Selector Dropdown
                MonthSelectorDropdown(selectedMonth) { selectedMonth = it }

                Spacer(modifier = Modifier.height(16.dp))

                // Total Bill Section with click navigation
                billResponse?.tenantBill?.let { tenantBill ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Routes.BILLING)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.LightGray),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Your Total Bill",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$${String.format("%.2f", tenantBill.totalAmount)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Utility Usage Overview Dropdown
                var selectedUtility by remember { mutableStateOf(billResponse?.tenantBill?.utilities?.firstOrNull()?.utilityName ?: "Select Utility") }
                val utilities = billResponse?.tenantBill?.utilities?.map { it.utilityName } ?: emptyList()

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedUtility,
                        onValueChange = { },
                        label = { Text("Select Utility") },
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
                        utilities.forEach { utility ->
                            DropdownMenuItem(
                                text = { Text(utility) },
                                onClick = {
                                    selectedUtility = utility
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Utility Usage Graph Section (Bar Chart)
                UtilityUsageBarGraph(selectedUtility, billResponse)

                Spacer(modifier = Modifier.height(16.dp))

                // Additional Info Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.LightGray),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "About Splitmate Gamma",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "SplitMate Gamma is designed to help tenants and homeowners manage utility bills and track their usage effortlessly. " +
                                    "The dashboard provides an overview of total bills and trends in electricity usage, making it easier to stay on top of monthly expenses.",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthSelectorDropdown(selectedMonth: Int, onMonthSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val months = (1..12).map { month ->
        LocalDate.of(2024, month, 1).month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
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

@Composable
fun UtilityUsageBarGraph(selectedUtility: String, billResponse: BillResponse?) {
    val context = LocalContext.current
    val utilityUsage = billResponse?.tenantBill?.utilities?.find { it.utilityName == selectedUtility }?.usageRecords ?: emptyList()

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(12.dp),
        factory = {
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                setBackgroundColor(Color.WHITE)
                setDrawGridBackground(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = Color.DKGRAY
                    textSize = 10f
                    granularity = 1f
                    setLabelCount(5, true)
                    labelRotationAngle = -45f
                    setAvoidFirstLastClipping(true)
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = Color.LTGRAY
                    textColor = Color.DKGRAY
                    textSize = 12f
                }

                axisRight.isEnabled = false

                setExtraOffsets(10f, 10f, 10f, 10f)
            }
        },
        update = { barChart ->
            val entries = utilityUsage.mapIndexed { index, record ->
                BarEntry(index.toFloat(), record.amount.toFloat())
            }

            val barDataSet = BarDataSet(entries, selectedUtility).apply {
                color = Color.BLUE
                valueTextColor = Color.BLACK
                valueTextSize = 12f
            }

            val barData = BarData(barDataSet).apply {
                barWidth = 0.8f
                setValueTextColor(Color.DKGRAY)
                setValueTextSize(12f)
            }

            barChart.apply {
                data = barData
                xAxis.valueFormatter = IndexAxisValueFormatter(utilityUsage.map { it.date })
                animateY(1000)
                invalidate()
            }
        }
    )
}
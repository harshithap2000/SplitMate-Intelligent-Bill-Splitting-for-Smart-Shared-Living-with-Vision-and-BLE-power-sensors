package com.example.splitmategamma.dashboard.principalTenant.home.ui

import android.graphics.Color
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.splitmategamma.auth.model.House
import com.example.splitmategamma.core.Routes
import com.example.splitmategamma.dashboard.bill.repository.BillRepository
import com.example.splitmategamma.dashboard.bill.viewmodel.BillViewModel
import com.example.splitmategamma.dashboard.bill.viewmodel.BillViewModelFactory
import com.example.splitmategamma.dashboard.bill.model.Usage
import com.example.splitmategamma.dashboard.principalTenant.common.PrincipalBottomNavigationBar
import com.example.splitmategamma.dashboard.principalTenant.home.repository.PrincipalRepository
import com.example.splitmategamma.dashboard.principalTenant.home.viewmodel.PrincipalViewModel
import com.example.splitmategamma.dashboard.principalTenant.home.viewmodel.PrincipalViewModelFactory
import com.example.splitmategamma.network.ApiService
import com.example.splitmategamma.network.PreferenceManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalDashboardScreen(navController: NavController, houseId: String?) {
    val context = LocalContext.current
    PreferenceManager.initialize(context)

    val apiService = ApiService.create()
    val billRepository = BillRepository(apiService)
    val viewModel: BillViewModel = viewModel(factory = BillViewModelFactory(billRepository))

    val housingRepository = PrincipalRepository(apiService)
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    val principalViewModel: PrincipalViewModel = ViewModelProvider(
        viewModelStoreOwner!!,
        PrincipalViewModelFactory(housingRepository)
    )[PrincipalViewModel::class.java]

    val billResponse by viewModel.billResponse.collectAsState()
    val error by viewModel.error.collectAsState()

    // State to manage houses and selected house
    val houses by principalViewModel.houses.collectAsState(initial = emptyList())
    var selectedHouse by remember { mutableStateOf<House?>(null) }
    var houseDropdownExpanded by remember { mutableStateOf(false) }

    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }

    // Fetch houses and bill when screen is launched
    LaunchedEffect(Unit) {
        val token = PreferenceManager.getUserToken() ?: ""
        if (token.isNotEmpty()) {
            principalViewModel.fetchHouses(token)
        }
    }

    // Monitor changes to houses and selectedHouseId to update the selectedHouse
    LaunchedEffect(houses, houseId, selectedMonth) {
        val selectedHouseId = houseId ?: PreferenceManager.getSelectedHouseId()
        if (houses.isNotEmpty() && selectedHouseId != null) {
            val house = houses.find { it._id == selectedHouseId }
            if (house != null) {
                selectedHouse = house
                PreferenceManager.saveSelectedHouseId(selectedHouseId) // Save the selected house ID
                val token = PreferenceManager.getUserToken() ?: ""
                val chosenDate = LocalDate.now().withMonth(selectedMonth).withDayOfMonth(1).toString() + "T00:00:00Z"
                if (token.isNotEmpty()) {
                    viewModel.fetchBill(token, selectedHouse!!._id, chosenDate)
                }
            }
        }
    }

    error?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { houseDropdownExpanded = !houseDropdownExpanded }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedHouse?.name ?: "Select House",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedHouse?.address ?: "Tap to select a house",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Edit House",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            navController.navigate(Routes.HOUSE_LIST)
                                        }
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = houseDropdownExpanded,
                        onDismissRequest = { houseDropdownExpanded = false }
                    ) {
                        houses.forEach { house ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(house.name, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(house.address, style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = {
                                    selectedHouse = house
                                    houseDropdownExpanded = false
                                    navController.navigate("principal_dashboard/${house._id}") {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("principal_notification") }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.shadow(8.dp)
            )
        },
        bottomBar = { PrincipalBottomNavigationBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MonthSelectorDropdown(selectedMonth) { month ->
                    selectedMonth = month
                }

                if (billResponse != null) {
                    // Total Bill Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.LightGray)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Bill",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "$${String.format("%.2f", billResponse?.houseBill?.totalHouse ?: 0.0)}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            item {
                if (billResponse != null) {
                    // Usage Trend Graph Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.LightGray)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Usage Trend",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            if (billResponse?.houseBill?.electricUsage?.isNotEmpty() == true) {
                                PrincipalUsageLineChart(
                                    electricityUsage = billResponse?.houseBill?.electricUsage ?: emptyList()
                                )
                            } else {
                                Text(
                                    text = "No data available",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                // App Description Section
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
    }
}

@Composable
fun PrincipalUsageLineChart(electricityUsage: List<Usage>) {
    val electricityEntries = electricityUsage.mapIndexed { index, usage ->
        Entry(index.toFloat(), usage.amount.toFloat())
    }

    if (electricityEntries.isEmpty()) return

    AndroidView(factory = { context ->
        LineChart(context).apply {
            val dataSet = LineDataSet(electricityEntries, "Electricity").apply {
                color = Color.BLUE
                valueTextColor = Color.BLACK
                lineWidth = 3f
                setCircleColor(Color.BLUE)
                circleRadius = 4f
                mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            }

            this.data = LineData(listOf(dataSet))
            this.description = Description().apply {
                text = "Electricity Usage"
                textSize = 14f
            }
            this.setTouchEnabled(true)
            this.setPinchZoom(true)
            this.animateX(1000)

            this.xAxis.apply {
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                textSize = 12f
            }

            this.axisLeft.apply {
                axisMinimum = 0f
                textSize = 12f
            }

            this.axisRight.isEnabled = false
            this.legend.isEnabled = true
            this.legend.textSize = 12f
            this.setDrawGridBackground(false)
            this.invalidate()  // Refresh the chart
        }
    }, modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .padding(8.dp))
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
